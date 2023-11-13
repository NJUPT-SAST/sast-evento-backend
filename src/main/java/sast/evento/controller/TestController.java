package sast.evento.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Event;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.mapper.EventMapper;
import sast.evento.model.Action;
import sast.evento.model.UserModel;
import sast.evento.service.PermissionService;
import sast.sastlink.sdk.service.SastLinkService;

import java.util.List;
import java.util.Objects;

@RestController
@ConditionalOnProperty(prefix = "test", name = "challenge")
@RequestMapping("/test")
public class TestController {
    @Value("${test.challenge}")
    private String challenge;
    @Value("${test.method}")
    private String method;

    @Resource
    private SastLinkService sastLinkService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private EventMapper eventMapper;


    @OperateLog("link登录")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/linklogin")
    public String linkLogin(@RequestParam String email,
                            @RequestParam String password) {
        if (challenge.isEmpty() || method.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR);
        }
        try {
            String token = sastLinkService.login(email, password);
            return sastLinkService.authorize(token, challenge, method);
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.SAST_LINK_SERVICE_ERROR, e.getMessage());
        }
    }

    @OperateLog("addPermissionForTest")
    @DefaultActionState(ActionState.LOGIN)
    @PostMapping("/permission")
    public String permission(@RequestParam ActionState type,
                             @RequestParam(required = false) Integer eventId,
                             @RequestParam(required = false) List<String> permission) {
        UserModel userModel = HttpInterceptor.userHolder.get();
        final List<String> adminPermission = ActionRegister.actionName2action.values().stream()
                .filter(a -> a.getActionState().equals(ActionState.ADMIN))
                .map(Action::getMethodName).toList();
        final List<String> managerPermission = ActionRegister.actionName2action.values().stream()
                .filter(a -> a.getActionState().equals(ActionState.MANAGER))
                .map(Action::getMethodName).toList();
        switch (type) {
            case ADMIN ->
                    permissionService.addAdmin(Objects.requireNonNullElse(permission, adminPermission), userModel.getId());
            case MANAGER -> {
                if (eventMapper.exists(Wrappers.lambdaQuery(Event.class).eq(Event::getId, eventId))) {
                    permissionService.addManager(eventId, Objects.requireNonNullElse(permission, managerPermission), userModel.getId());
                } else {
                    return "no such event";
                }
            }
            default -> {
                return "error type";
            }
        }
        return "ok";
    }

}
