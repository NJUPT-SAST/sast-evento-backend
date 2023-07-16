package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sast.evento.service.SastLinkService;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:31
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private SastLinkService sastLinkService;

    @RequestMapping("/login")
    public String login(@RequestParam(defaultValue = "")String userName,@RequestParam(defaultValue = "")String password){
        return sastLinkService.login(userName, password);
    }

}
