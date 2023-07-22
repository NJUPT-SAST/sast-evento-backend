package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.OperateLog;
import sast.evento.service.impl.SastLinkServiceCacheAble;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:31
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private SastLinkServiceCacheAble sastLinkServiceCacheAble;
    @OperateLog("登录")
    @PostMapping ("/login")
    public String login(@RequestParam(defaultValue = "")String userId,
                      @RequestParam(defaultValue = "")String password){
        return sastLinkServiceCacheAble.login(userId, password);
    }

}
