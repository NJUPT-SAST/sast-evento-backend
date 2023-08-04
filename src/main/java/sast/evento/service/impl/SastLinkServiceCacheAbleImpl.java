package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sast.evento.model.UserProFile;
import sast.evento.service.SastLinkServiceCacheAble;
import sast.evento.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 14:12
 */
@Service
public class SastLinkServiceCacheAbleImpl implements SastLinkServiceCacheAble {
    /* 带缓存的SastLink登录服务 */

    /* 在这里调用SastLinkSDK的方法并缓存信息 */
    //todo 对接Sast_Link_SDK
    @Resource
    private JwtUtil jwtUtil;
    @Override
    public String linkLogin(String code){
        /* 存储用来获取用户信息的 access_token */
        Map<String,String> map = new HashMap<>();
        map.put("code",code);


        String userId = "";
        Map<String,String> payload = new HashMap<>();
        map.put("userId",userId);
        return jwtUtil.generateToken(payload);
    }

    @Override
    public String wxLogin(String code) {
        return null;
    }
    @Override
    public String logout(String userId,String code){
        return null;
    }
    @Cacheable(value = "userProFile", key = "#userId")
    public UserProFile getUserProFile(String userId) {
        return null;
    }

    @CachePut(value = "userProFile", key = "#userId")
    public UserProFile updateUserProFile(String userId,UserProFile userProFile) {
        return userProFile;
    }


}
