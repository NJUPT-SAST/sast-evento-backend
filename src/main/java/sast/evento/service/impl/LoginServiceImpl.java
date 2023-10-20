package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import sast.sastlink.sdk.enums.Organization;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
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
    private UserMapper userMapper;
    @Resource
    private UserPasswordMapper userPasswordMapper;
    @Resource
    private WxService wxService;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    private static final String LOGIN_KEY = "rsa:";
    private static final String LOGIN_TICKET = "ticket:";
    private static final String LOGIN_SUCCESS = "login:";
    private static final long LOGIN_KEY_EXPIRE = 600;
    private static final long LOGIN_TICKET_EXPIRE = 600;

    /**
     * 这边逻辑和业务强耦合，建议先熟悉登陆流程再阅读代码
     */

    @Override
    public Map<String, Object> linkLogin(String code, Integer type) throws SastLinkException {
        SastLinkService service = switch (type) {
            case 0 -> sastLinkService;
            case 1 -> sastLinkServiceWeb;
            default -> throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error link client type value: " + type);
        };
        AccessTokenResponse accessTokenResponse = service.accessToken(code);
        UserInfo userInfo = service.userInfo(accessTokenResponse.getAccess_token());
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getLinkId, userInfo.getUserId()));
        //查看学号是否冲突，若冲突则绑定至wx账号
        if (user == null) {
            //查看有对应学号的账号
            User origin = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                    .eq(User::getStudentId, userInfo.getUserId()));
            if (origin != null) {
                origin.setLinkId(userInfo.getUserId());
                origin.setStudentId(userInfo.getUserId());
                userMapper.updateById(origin);
            } else {
                user = new User();
                setCommonInfo(user, userInfo);
                user.setLinkId(userInfo.getUserId());
                user.setStudentId(userInfo.getUserId());//link默认直接绑定学号
                userMapper.insert(user);
            }
        }
        String token = addTokenInCache(user, false);
        return Map.of("token", token, "userInfo", user);
    }

    @Override
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
    public Map<String, Object> getKeyForLogin(String studentId) {
        studentId = studentId.toLowerCase();
        if (studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "student id should not be empty");
        }
        if (!userMapper.exists(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId))) {
            throw new LocalRunTimeException(ErrorEnum.STUDENT_NOT_BIND);
        }
        //生成公钥密钥
        Map<String, String> keyPair = RSAUtil.generateKey();
        String publicKeyStr = keyPair.get("publicKeyStr");
        String privateKeyStr = keyPair.get("privateKeyStr");
        redisUtil.set(LOGIN_KEY + studentId, privateKeyStr, LOGIN_KEY_EXPIRE);
        return Map.of("expireIn", LOGIN_KEY_EXPIRE,
                "str", publicKeyStr);
    }

    //未登录展示保持连接并等待（检查Ticket更改状态）
    @Override
    public Map<String, Object> getLoginTicket(String studentId, String ticket) {
        studentId = studentId.toLowerCase();
        if (!userMapper.exists(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId))) {
            throw new LocalRunTimeException(ErrorEnum.STUDENT_NOT_BIND);
        }
        String local = (String) redisUtil.get(LOGIN_TICKET + studentId);
        if (ticket != null && !ticket.isEmpty()) {
            String userJson = (String) redisUtil.get(LOGIN_SUCCESS + studentId);
            if (ticket.equals(local) && userJson != null) {
                User user = JsonUtil.fromJson(userJson, User.class);
                String token = addTokenInCache(user, false);
                redisUtil.del(LOGIN_TICKET + studentId, LOGIN_SUCCESS + studentId);
                return Map.of("token", token, "userInfo", user);
            }
        }
        if (local != null) {
            return Map.of("expireIn", redisUtil.getExpire(LOGIN_TICKET + studentId), "ticket", local);
        }
        ticket = TicketUtil.generateTicket();
        redisUtil.set(LOGIN_TICKET + studentId, ticket, LOGIN_TICKET_EXPIRE);
        return Map.of("expireIn", LOGIN_TICKET_EXPIRE, "ticket", ticket);
    }

    //检查Ticket更改状态
    @Override
    public void checkTicket(String studentId, String ticket) {
        studentId = studentId.toLowerCase();
        String localTicket = (String) redisUtil.get(LOGIN_TICKET + studentId);
        if (localTicket != null && localTicket.equals(ticket)) {
            redisUtil.del(LOGIN_TICKET + studentId);
        }
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId));
        redisUtil.set(LOGIN_SUCCESS + studentId, JsonUtil.toJson(user));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bindPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        String privateKeyStr = (String) redisUtil.get(LOGIN_KEY + studentId);
        if (privateKeyStr == null || privateKeyStr.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_EXPIRE, "login failed please try again");
        }
        try {
            password = RSAUtil.decryptByPrivateKey(password, privateKeyStr);
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "login failed please try again");
        }
        String salt = MD5Util.getSalt(5);
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId));
        if (user == null) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "please register first");
        }
        UserPassword userPassword = new UserPassword(null, studentId, MD5Util.md5Encode(password, salt), salt);
        userPasswordMapper.insert(userPassword);
        redisUtil.del(LOGIN_KEY + studentId);
        String token = addTokenInCache(user, false);
        return Map.of("token", token, "userInfo", user);
    }

    @Override
    public Map<String, Object> loginByPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        checkPassword(studentId, password);
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId, studentId));
        redisUtil.del(LOGIN_KEY + studentId);
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
        local.setOrganization((userInfo.getOrg() == null || userInfo.getOrg().isEmpty()) ? null : Organization.valueOf(userInfo.getOrg()).getId());
        local.setBiography(userInfo.getBio());
        local.setLink(userInfo.getLink());
    }

    private void checkPassword(String studentId, String password) {
        studentId = studentId.toLowerCase();
        String privateKeyStr = (String) redisUtil.get(LOGIN_KEY + studentId);
        if (privateKeyStr == null || privateKeyStr.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_EXPIRE, "login failed please try again");
        }
        try {
            password = RSAUtil.decryptByPrivateKey(password, privateKeyStr);
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR, "login failed please try again");
        }
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


}
