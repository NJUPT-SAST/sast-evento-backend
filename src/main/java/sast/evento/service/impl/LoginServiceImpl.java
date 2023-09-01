package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.UserMapper;
import sast.evento.service.LoginService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.JwtUtil;
import sast.evento.utils.RedisUtil;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
import sast.sastlink.sdk.model.response.RefreshResponse;
import sast.sastlink.sdk.service.SastLinkService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private UserMapper userMapper;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    private static final String ACCESS_TOKEN = "access_token:";
    private static final long ACCESS_EXPIRE = 10000;
    private static final String REFRESH_TOKEN = "refresh_token:";
    private static final long REFRESH_EXPIRE = 20000;
    private static final String USER_INFO = "user_info:";
    private static final long USER_INFO_EXPIRE = 1000;

    @Override
    public Map<String, Object> linkLogin(String code) throws SastLinkException {
        Map<String, Object> data = login(code);
        UserInfo userInfo = (UserInfo) data.get("userInfo");
        userMapper.ignoreInsertUser(userInfo.getUserId(), userInfo.getWechatId(), userInfo.getEmail());
        return data;
    }

    @Override
    public Map<String, Object> wxLogin(String email, String password, String code_challenge, String code_challenge_method, String openId) throws SastLinkException {
        String token = sastLinkService.login(email, password);
        String code = sastLinkService.authorize(token, code_challenge, code_challenge_method);
        sastLinkService.logout(token);
        /* 临时登录一下，登完就退 */
        Map<String, Object> data = login(code);
        UserInfo userInfo = (UserInfo) data.get("userInfo");
        userMapper.ignoreInsertUser(userInfo.getUserId(), openId, userInfo.getEmail());
        return data;
    }

    private Map<String, Object> login(String code) throws SastLinkException {
        AccessTokenResponse accessTokenResponse = sastLinkService.accessToken(code);
        UserInfo userInfo = sastLinkService.userInfo(accessTokenResponse.getAccess_token());
        String userId = userInfo.getUserId();
        Map<String, String> payload = new HashMap<>();
        payload.put("user_id", userId);
        String token = jwtUtil.generateToken(payload);
        redisUtil.set(ACCESS_TOKEN + userId, accessTokenResponse.getAccess_token(), ACCESS_EXPIRE);
        redisUtil.set(REFRESH_TOKEN + userId, accessTokenResponse.getRefresh_token(), REFRESH_EXPIRE);
        redisUtil.set("TOKEN:" + userId, token, jwtUtil.expiration);
        redisUtil.set(USER_INFO + userId, JsonUtil.toJson(userInfo), USER_INFO_EXPIRE);
        return Map.of("token", token, "userInfo", userInfo);
    }

    @Override
    public Map<String, String> wxRegister(String email) throws SastLinkException {
        return Map.of("register_ticket", sastLinkService.sendCaptcha(email));
    }

    @Override
    public boolean checkCaptcha(String ticket, String captcha, String password) throws SastLinkException {
        return sastLinkService.checkCaptchaAndRegister(captcha, ticket, password);
    }

    @Override
    public void logout(String userId) throws SastLinkException {
        redisUtil.del("TOKEN:" + userId, ACCESS_TOKEN + userId, REFRESH_TOKEN + userId, USER_INFO + userId);
    }

    @Override
    public UserInfo getUserInfo(String userId) throws SastLinkException {
        UserInfo userInfo;
        String userInfoJson = (String) Optional.ofNullable(redisUtil.get(USER_INFO + userId)).orElse("");
        if (!userInfoJson.isEmpty()) {
            userInfo = JsonUtil.fromJson(userInfoJson, UserInfo.class);
            return userInfo;
        }
        String accessToken = (String) Optional.ofNullable(redisUtil.get(ACCESS_TOKEN + userId))
                .orElse("");
        if (accessToken.isEmpty()) {
            String refreshToken = (String) Optional.ofNullable(redisUtil.get(REFRESH_TOKEN + userId))
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "login has expired, please login first"));
            RefreshResponse refreshResponse = sastLinkService.refresh(refreshToken);
            redisUtil.set(ACCESS_TOKEN + userId, refreshResponse.getAccessToken(), ACCESS_EXPIRE);
            redisUtil.set(REFRESH_TOKEN + userId, refreshResponse.getRefreshToken(), REFRESH_EXPIRE);
        }
        userInfo = sastLinkService.userInfo(accessToken);
        redisUtil.set(USER_INFO + userId, JsonUtil.toJson(userInfo), USER_INFO_EXPIRE);
        return userInfo;
    }

    @Override
    public void checkLoginState(String userId, String token) {
        String localToken = String.valueOf(redisUtil.get("TOKEN:" + userId));
        if (!token.equals(localToken)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "login has expired, please login first");
        }
    }
}
