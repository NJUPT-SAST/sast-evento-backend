package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.model.UserProFile;
import sast.evento.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 14:12
 */
@Service
public class SastLinkServiceCacheAble {
    //todo 对接Sast_Link_SDK
    @Resource
    private JwtUtil jwtUtil;

    public String login(String userId,String code){
        //存储用来获取用户信息的token


        //发放给客户端的token
        Map<String,String> map = new HashMap<>();
        return jwtUtil.generateToken(map);
    }

    public UserProFile getUserProFile(String userId) {
        return null;
    }


}
