package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping("/login")
    public String login(@RequestParam(defaultValue = "")String userId,
                        @RequestParam(defaultValue = "")String code){
        return sastLinkServiceCacheAble.login(userId, code);
    }

}
