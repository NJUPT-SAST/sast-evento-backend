package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.common.enums.Platform;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.UserMapper;
import sast.evento.model.UserModel;
import sast.evento.model.wxServiceDTO.JsCodeSessionResponse;
import sast.evento.service.LoginService;
import sast.evento.service.WxService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.JwtUtil;
import sast.evento.utils.RedisUtil;
import sast.sastlink.sdk.enums.Organization;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
import sast.sastlink.sdk.service.SastLinkService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private WxService wxService;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    private static final String USER = "user:";

    @Override
    public Map<String, Object> linkLogin(String code, Integer type) throws SastLinkException {
        SastLinkService service = switch (type) {
            case 0 -> sastLinkService;
            case 1 -> sastLinkServiceWeb;
            default -> throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error link client type value: " + type);
        };
        AccessTokenResponse accessTokenResponse = service.accessToken(code);
        UserInfo userInfo = service.userInfo(accessTokenResponse.getAccess_token());
//        User user = getUserFrom2Client(userInfo);
        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getLinkId, userInfo.getUserId()))).orElse(new User());
        setCommonInfo(user,userInfo);
        if(user.getId() == null){
            userMapper.insert(user);
        }else {
            userMapper.updateById(user);
        }
        String token = addTokenInCache(user,Platform.SastLink);
        return Map.of("token", token, "userInfo", user);
    }

    private void setCommonInfo(User local,UserInfo userInfo){
        local.setAvatar(userInfo.getAvatar());
        local.setEmail(userInfo.getEmail());
        local.setNickname(userInfo.getNickname());
        System.out.println("userInfo org    :  "+userInfo.getOrg());
        local.setOrganization((userInfo.getOrg() == null||userInfo.getOrg().isEmpty())?null: Organization.valueOf(userInfo.getOrg()).getId());
        local.setBiography(userInfo.getBio());
        local.setLink(userInfo.getLink());
        //local.setHide(userInfo.getHide());
    }

    @Override
    public Map<String, Object> wxLogin(String code) {
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
            userMapper.insert(user);
        }
        String token = addTokenInCache(user,Platform.WeChat);
        return Map.of("unionid", jsCodeSessionResponse.getUnionid(),"userInfo",user,"token",token);
    }

    @Override
    public void bindStudent(String userId,String studentId){
        userMapper.bindStudentId(userId,studentId);
    }

    private String addTokenInCache(User user, Platform platform){
        UserModel userModel =new UserModel(user.getId(),user.getStudentId(),user.getEmail(),platform.name());
        String token = generateToken(userModel);
        redisUtil.set(TOKEN + user.getId(), token, jwtUtil.expiration);
        return token;
    }

    private String generateToken(UserModel user) {
        Map<String, String> payload = new HashMap<>();
        payload.put("user", JsonUtil.toJson(user));
        return jwtUtil.generateToken(payload);
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
            if (!localToken.isEmpty()) {
                return;
            }
        }
        throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "login has expired, please login first");
    }


    private User getUserFrom2Client(UserInfo userInfo){
        User user = null;
        //若存在两个匹配项优先使用link账号
        //使用link登录时，若检测到已经存在一个wx账号，就直接合并到link的账号上
        //若wx账号与link的wx账号不一致则为两个账号
        if(getUserWeChatId(userInfo)!=null&&!getUserWeChatId(userInfo).isEmpty()) {
            //从本地寻找匹配账号
            List<User> userList = userMapper.selectList(Wrappers.lambdaQuery(User.class)
                    .eq(User::getLinkId, userInfo.getUserId())
                    .or(wrapper -> wrapper.eq(User::getOpenId, getUserWeChatId(userInfo))));
            user = switch (userList.size()){
                case 0 -> new User(userInfo);//直接使用link登录情况
                case 1 -> {
                    User local = userList.get(0);
                    String localLinkId = local.getLinkId();
                    if(localLinkId!=null&&localLinkId.equals(userInfo.getUserId())){
                        setCommonInfo(local,userInfo);
                        yield local;//使用link二次登录
                    }
                    String localOpenId = local.getOpenId();
                    if(localOpenId!=null&&localOpenId.equals(getUserWeChatId(userInfo))){
                        setCommonInfo(local,userInfo);
                        local.setLinkId(localLinkId);
                        yield local;//使用wx登录后转而使用link登录
                    }
                    throw new LocalRunTimeException(ErrorEnum.INTERNAL_SERVER_ERROR);
                }
                default -> userList.stream()
                            .filter(u -> u.getLinkId().equals(userInfo.getUserId()))
                            .findAny()
                            .orElse(null);//使用link登录发现有wx和link分别对应两个及以上个账号
            };
        }else {
        //从本地寻找匹配账号
            User local = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                    .eq(User::getLinkId, userInfo.getUserId()));
            String localLinkId = local.getLinkId();
        String localOpenId = local.getOpenId();
            if(localLinkId!=null&&localLinkId.equals(userInfo.getUserId())){
                setCommonInfo(local,userInfo);//使用link二次登录
            }
            else if (localOpenId!=null&&localOpenId.equals(getUserWeChatId(userInfo))) {
                setCommonInfo(local,userInfo);
                local.setLinkId(localLinkId);//使用wx登录后转而使用link登录
            }
            user = local;
        }
        return user;
    }

    private String getUserWeChatId(UserInfo userInfo){
        return null;
    }



}
