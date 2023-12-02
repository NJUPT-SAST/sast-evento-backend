package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.common.enums.Platform;
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

    /**
     * 使用sast-link第三方登录，由于多端，进行两端的分别配置
     * @param code sast-link验证code
     * @param type web端或客户端
     * @return Map
     */
    @OperateLog("link登录")
    @PostMapping("/login/link")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> linkLogin(@RequestParam String code,
                                         @RequestParam Integer type,
                                         @RequestParam(required = false,defaultValue = "false") Boolean update) {
        if (code == null || code.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid code");
        }
        try {
            return loginService.linkLogin(code,type,update);
        } catch (SastLinkException e) {
            throw new LocalRunTimeException(ErrorEnum.SAST_LINK_SERVICE_ERROR, e.getMessage());
        }
    }

    /**
     * 使用weChat第三方登录
     * @param code weChat验证code
     * @return Map
     */
    @OperateLog("微信登录")
    @PostMapping("/login/wx")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> wxLogin(@RequestParam String code) {
        return loginService.wxLogin(code);
    }

    /**
     * weChat登录后绑定学号
     * @param studentId 学号
     * @return Map
     */
    @OperateLog("绑定学号")
    @PostMapping("/bind/student")
    @DefaultActionState(ActionState.LOGIN)
    public Map<String,Object> bindStudentId(@RequestParam String studentId,
                                            @RequestParam(required = false,defaultValue = "false") Boolean force){
        UserModel user = HttpInterceptor.userHolder.get();
        return loginService.bindStudentOnWechat(user.getId(),studentId,force);

    }

    /**
     * 获取授权给新设备登录的ticket
     * @param studentId 学号
     * @return Map
     */
    @OperateLog("获取ticket")
    @PostMapping("/login/ticket/get")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> getTicket(@RequestParam String studentId,
                                         @RequestParam(required = false) String ticket){
        return loginService.getLoginTicket(studentId,ticket);
    }

    /**
     * 新设备获取ticket后使用学号登录
     * @param ticket 登录令牌
     * @return ok
     */
    @OperateLog("检查ticket并登录")
    @PostMapping("/login/ticket")
    @DefaultActionState(ActionState.LOGIN)
    public String loginByTicket(@RequestParam String ticket){
        UserModel user = HttpInterceptor.userHolder.get();
        loginService.checkTicket(user.getStudentId(), ticket);
        return "ok";
    }

    /**
     * 给已经使用第三方登陆的用户绑定密码或者修改密码
     * @param password 密码
     * @return Map
     */
    @OperateLog("绑定密码")
    @PostMapping("/bind/password")
    @DefaultActionState(ActionState.LOGIN)
    public String bindPassword(@RequestParam String password) {
        UserModel user = HttpInterceptor.userHolder.get();
        loginService.bindPassword(user.getStudentId(), password);
        return "ok";
    }

    /**
     * 学号密码登录
     * @param studentId 学号
     * @param password 密码
     * @return Map
     */
    @OperateLog("密码登录")
    @PostMapping("/login/password")
    @DefaultActionState(ActionState.PUBLIC)
    public Map<String, Object> loginByPassword(@RequestParam String studentId, @RequestParam String password) {
        return loginService.loginByPassword(studentId, password);
    }

    /**
     * 登出
     * @return ok
     */
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