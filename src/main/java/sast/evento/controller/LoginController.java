package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.UserProFile;
import sast.evento.service.LoginService;

import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:31
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private LoginService loginService;

    @OperateLog("Link登录")
    @PostMapping("/login/link")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> linkLogin(@RequestParam String code) {
        if (code == null || code.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid code");
        }
        return loginService.linkLogin(code);
    }

    @OperateLog("微信登录")
    @PostMapping("/login/wx")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> wxLogin(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String codeChallenge,
                                       @RequestParam String codeChallengeMethod,
                                       @RequestParam String openId) {
        if (email.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid email");
        }
        if (password.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid password");
        }
        if (openId.isEmpty()){
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid openId");
        }
        return loginService.wxLogin(email, password, codeChallenge, codeChallengeMethod,openId);
    }

    @OperateLog("登出")
    @GetMapping("/logout")
    @DefaultActionState(ActionState.LOGIN)
    public String logout() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        loginService.logout(userProFile.getUserId());
        return "ok";
    }


}
