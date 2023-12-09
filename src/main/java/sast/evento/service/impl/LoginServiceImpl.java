package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.entitiy.UserPassword;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.UserMapper;
import sast.evento.mapper.UserPasswordMapper;
import sast.evento.model.UserModel;
import sast.evento.model.wxServiceDTO.JsCodeSessionResponse;
import sast.evento.service.LoginService;
import sast.evento.service.WxService;
import sast.evento.utils.*;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.service.SastLinkService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static sast.evento.utils.JwtUtil.TOKEN;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 14:12
 */
@Service
public class LoginServiceImpl implements LoginService {
    /* 带缓存的SastLink登录服务 */
    @Resource
    private SastLinkService sastLinkService;
    @Resource
    private SastLinkService sastLinkServiceWeb;
    @Resource
    private SastLinkService sastLinkServiceMobileDev;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserPasswordMapper userPasswordMapper;
    @Resource
    private WxService wxService;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    private static final String LOGIN_TICKET = "ticket:";
    private static final String LOGIN_SUCCESS = "login:";
    private static final long LOGIN_TICKET_EXPIRE = 600;
    private static final long LOGIN_TIME_OUT = 30;

    /**
     * 这边逻辑和业务强耦合，建议先熟悉登陆流程再阅读代码
     */

    @Override
    @Transactional
    public Map<String, Object> linkLogin(String code, Integer type, Boolean updateUser) throws SastLinkException {
        SastLinkService service = switch (type) {
            case 0 -> sastLinkService;
            case 1 -> sastLinkServiceWeb;
            case 2 -> sastLinkServiceMobileDev;
            default -> throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error link client type value: " + type);
        };
        AccessToken accessToken = service.accessToken(code);
        UserInfo userInfo = service.user(accessToken.getAccessToken());
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getLinkId, userInfo.getUserId()));
        if (user == null) {
            //若无对应用户，查看是否存在有对应学号的账号
            User origin = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                    .eq(User::getStudentId, userInfo.getUserId()));
            if (origin != null) {
                //学号已经存在，直接绑定到该学号账号上
                origin.setLinkId(userInfo.getUserId());
                origin.setStudentId(userInfo.getUserId());
                userMapper.updateById(origin);
                user = origin;
            } else {
                //学号也不存在，创建新的账号
                user = new User();
                setCommonInfo(user, userInfo);
                user.setLinkId(userInfo.getUserId());
                user.setStudentId(userInfo.getUserId());//link默认直接绑定学号
                userMapper.insert(user);
            }
        } else if (updateUser) {
            setCommonInfo(user, userInfo);
            userMapper.updateById(user);
        }
        String token = addTokenInCache(user, false);
        return Map.of("token", token, "userInfo", user);
    }

    @Override
    @Transactional
    public Map<String, Object> wxLogin(String code) {
        //没有学号冲突的风险
        JsCodeSessionResponse jsCodeSessionResponse = wxService.login(code);
        String openId = jsCodeSessionResponse.getOpenid();
        if (openId == null || openId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SERVICE_ERROR, "wx login failed");
        }
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getOpenId, openId));
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setUnionId(jsCodeSessionResponse.getUnionid());
            userMapper.insert(user);
        }
        String token = addTokenInCache(user, false);
        return Map.of("unionid", jsCodeSessionResponse.getUnionid(), "userInfo", user, "token", token);
    }

    @Override
    public Map<String, Object> getLoginTicket(@Nullable String ticket) {
        if (ticket == null || ticket.isEmpty()) {
            String key = TicketUtil.generateKey();
            return Map.of("expireIn", LOGIN_TICKET_EXPIRE, "ticket", generateTicket(key));
        }
        String key = TicketUtil.getInfoFromTicket(ticket)[0];
        String localTicket = (String) redisUtil.get(LOGIN_TICKET + key);
        if (!ticket.equals(localTicket)) {
            redisUtil.del(LOGIN_TICKET + key);
            key = TicketUtil.generateKey();
            return Map.of("expireIn", LOGIN_TICKET_EXPIRE, "ticket", generateTicket(key));
        }
        String userJson = (String) redisUtil.get(LOGIN_SUCCESS + key);
        if (userJson == null || userJson.isEmpty()) {
            return Map.of("expireIn", redisUtil.getExpire(LOGIN_TICKET + key), "ticket", ticket);
        }
        User user = JsonUtil.fromJson(userJson, User.class);
        String token = addTokenInCache(user, false);
        redisUtil.del(LOGIN_TICKET + key, LOGIN_SUCCESS + key);
        return Map.of("token", token, "userInfo", user);
    }

    @Override
    public void checkLoginTicket(String ticket, String userId) {
        if (ticket == null || ticket.isEmpty())
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "ticket should not be null");
        String key = TicketUtil.getInfoFromTicket(ticket)[0];
        String localTicket = (String) redisUtil.get(LOGIN_TICKET + key);
        if (!ticket.equals(localTicket))
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "error ticket please try again");
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getId, userId));
        redisUtil.set(LOGIN_SUCCESS + key, JsonUtil.toJson(user));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        String salt = MD5Util.getSalt(5);
        UserPassword userPassword = userPasswordMapper.selectOne(Wrappers.lambdaQuery(UserPassword.class)
                .eq(UserPassword::getStudentId, studentId)
                .last("for update"));
        if (userPassword != null) {
            userPassword.setPassword(MD5Util.md5Encode(password, salt));
            userPassword.setSalt(salt);
            userPasswordMapper.updateById(userPassword);
        } else {
            userPassword = new UserPassword(null, studentId, MD5Util.md5Encode(password, salt), salt);
            userPasswordMapper.insert(userPassword);
        }
    }

    @Override
    public Map<String, Object> loginByPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        checkPassword(studentId, password);
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId));
        String token = addTokenInCache(user, false);
        return Map.of("token", token, "userInfo", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bindStudentOnWechat(String userId, String studentId, Boolean force) {
        //此时微信登陆成功已经默认创建新账号，需要将新账号删除并绑定至原有link账号
        //查看本地是否存在此学号
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId).last("for update"));
        if (user != null) {
            //若已经存在,则使用第一个账号(本账号已经绑定过也算在这里，所以只可以绑定一次学号，否则去联系管理员)
            if (force) {
                User del = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                        .eq(User::getId, userId).last("for update"));
                if (user.getOpenId() != null || del.getLinkId() != null) {
                    //微信已经绑定过学号也在这里报错
                    throw new LocalRunTimeException(ErrorEnum.ACCOUNT_HAS_BEEN_BIND, "please contact administrator");
                }
                user.setOpenId(del.getOpenId());
                user.setUnionId(del.getUnionId());
                user.setStudentId(studentId);
                userMapper.deleteById(userId);
                userMapper.updateById(user);
                String token = addTokenInCache(user, true);
                return Map.of("token", token, "userInfo", user);
            } else {
                throw new LocalRunTimeException(ErrorEnum.STUDENT_HAS_BEEN_BIND, "force an overwrite on new account or cancel operation");
            }
        }
        userMapper.bindStudentId(userId, studentId);
        user = userMapper.selectById(userId);
        String token = addTokenInCache(user, true);
        return Map.of("token", token, "userInfo", user);

    }

    @Override
    public void logout(String userId) throws SastLinkException {
        redisUtil.del(TOKEN + userId);
    }

    @Override
    public void checkLoginState(String userId, String token) {
        Object o = redisUtil.get(TOKEN + userId);
        if (o != null) {
            String localToken = String.valueOf(o);
            if (localToken.equals(token)) {
                return;
            }
        }
        throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "login has expired, please login first");
    }

    private void setCommonInfo(User local, UserInfo userInfo) {
        local.setAvatar(userInfo.getAvatar());
        local.setEmail(userInfo.getEmail());
        local.setNickname(userInfo.getNickname());
        local.setOrganization(userInfo.getOrg());
        local.setBiography(userInfo.getBio());
        local.setLink(userInfo.getLink());
    }

    private void checkPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        UserPassword userPassword = userPasswordMapper.selectOne(Wrappers.lambdaQuery(UserPassword.class)
                .eq(UserPassword::getStudentId, studentId));
        if (userPassword == null) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "please register first");
        }
        if (!MD5Util.md5Encode(password, userPassword.getSalt()).equals(userPassword.getPassword())) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "error password");
        }
    }

    private String addTokenInCache(User user, boolean update) {
        UserModel userModel = new UserModel(user.getId(), user.getStudentId(), user.getEmail());
        String token = generateToken(userModel);
        if (update) {
            redisUtil.set(TOKEN + user.getId(), token, jwtUtil.expiration);
        } else {
            if (!redisUtil.setnx(TOKEN + user.getId(), token, jwtUtil.expiration, TimeUnit.SECONDS)) {
                redisUtil.expire(TOKEN + user.getId(), jwtUtil.expiration);
                token = (String) redisUtil.get(TOKEN + user.getId());
            }
        }
        return token;
    }

    private String generateToken(UserModel user) {
        Map<String, String> payload = new HashMap<>();
        payload.put("user", JsonUtil.toJson(user));
        return jwtUtil.generateToken(payload);
    }

    private String getTicket(@Nonnull String ticket) {
        String key = TicketUtil.generateKey();
        return (!checkTicket(ticket)) ? generateTicket(key) : ticket;
    }

    private String generateTicket(String key) {
        int times = 0;
        String ticket = TicketUtil.generateTicket(key);
        while (!redisUtil.setnx(LOGIN_TICKET + key, ticket, LOGIN_TICKET_EXPIRE, TimeUnit.SECONDS) && times < 5) {
            key = TicketUtil.generateKey();
            ticket = TicketUtil.generateTicket(key);
            times++;
        }
        if (times == 5) throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "please try again later");
        return ticket;
    }

    private boolean checkTicket(String ticket) {
        String[] info = TicketUtil.getInfoFromTicket(ticket);
        String key = info[0];
        String localTicket = (String) redisUtil.get(LOGIN_TICKET + key);
        return localTicket != null && localTicket.equals(ticket);
    }


}
