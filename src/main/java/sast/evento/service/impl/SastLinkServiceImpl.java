package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.model.UserProFile;
import sast.evento.service.SastLinkService;
import sast.evento.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 14:12
 */
@Service
public class SastLinkServiceImpl implements SastLinkService {
    //todo 对接Sast_Link
    @Resource
    private JwtUtil jwtUtil;
    public String login(String userName,String password){
        Map<String,String> map = new HashMap<>();
        return jwtUtil.generateToken(map);
    }

    @Override
    public UserProFile getUserProFile(String userId) {
        return null;
    }


}
