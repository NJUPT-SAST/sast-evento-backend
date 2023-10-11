package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.UserMapper;
import sast.evento.model.wxServiceDTO.JsCodeSessionResponse;
import sast.evento.service.LoginService;
import sast.evento.service.WxService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.JwtUtil;
import sast.evento.utils.RedisUtil;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
import sast.sastlink.sdk.service.SastLinkService;

import java.util.HashMap;
import java.util.Map;

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
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getLinkId,userInfo.getUserId()));
        if(user == null){
            user = new User();
            user.setLinkId(userInfo.getUserId());
        }
        user.setEmail(userInfo.getEmail());
        userMapper.insert(user);
        String token = addTokenInCache(user);
        return Map.of("token", token, "userInfo", user);
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
        String token = addTokenInCache(user);
        return Map.of("unionid", jsCodeSessionResponse.getUnionid(),"userInfo",user,"token",token);
    }

    private String addTokenInCache(User user){
        String userId = user.getId();
        String token = generateToken(userId);
        redisUtil.set(TOKEN + userId, token, jwtUtil.expiration);
        return token;
    }

    private String generateToken(String userId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("user_id", userId);
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

    @Override
    public User getLocalUser(String userId) {
        String json = (String) redisUtil.get(USER + userId);
        User user = null;
        if (json == null) {
            user = userMapper.selectById(userId);
            if (user == null) {
                throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "user not exist");
            }
            redisUtil.set(USER + userId, JsonUtil.toJson(user));
        } else {
            user = JsonUtil.fromJson(json, User.class);
        }
        return user;
    }

}
