package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.UserModel;
import sast.evento.service.LoginService;
import sast.sastlink.sdk.exception.SastLinkException;

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

    @OperateLog("link登录")
    @PostMapping("/login/link")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> linkLogin(@RequestParam String code,
                                         @RequestParam Integer type) {
        if (code == null || code.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid code");
        }
        try {
            return loginService.linkLogin(code,type);
        } catch (SastLinkException e) {
            throw new LocalRunTimeException(ErrorEnum.SAST_LINK_SERVICE_ERROR, e.getMessage());
        }
    }

    @OperateLog("微信登录")
    @PostMapping("/login/wx")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> wxLogin(@RequestParam String code) {
        return loginService.wxLogin(code);
    }

    @OperateLog("绑定学号")
    @PostMapping("/bind/student")
    @DefaultActionState(ActionState.LOGIN)
    public String bindStudentId(@RequestParam String studentId){
        UserModel user = HttpInterceptor.userHolder.get();
        loginService.bindStudent(user.getId(),studentId);
        return "ok";
    }

    @OperateLog("获取key")
    @GetMapping("/login/key")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> getKey(@RequestParam String studentId){
        return loginService.getKeyForLogin(studentId);
    }

    @OperateLog("绑定密码")
    @PostMapping("/bind/pwd")
    @DefaultActionState(ActionState.LOGIN)
    public Map<String, Object> bindPassword(@RequestParam String password) {
        UserModel user = HttpInterceptor.userHolder.get();
        return loginService.bindPassword(user.getStudentId(), password);
    }

    @OperateLog("密码登录")
    @PostMapping("/login/pwd")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> bindPassword(@RequestParam String studentId, @RequestParam String password) {
        return loginService.loginByPassword(studentId, password);
    }

    @OperateLog("登出")
    @GetMapping("/logout")
    @DefaultActionState(ActionState.LOGIN)
    public String logout() {
        UserModel user = HttpInterceptor.userHolder.get();
        try {
            loginService.logout(user.getId());
        } catch (SastLinkException e) {
            throw new LocalRunTimeException(ErrorEnum.SAST_LINK_SERVICE_ERROR, e.getMessage());
        }
        return "ok";
    }


}