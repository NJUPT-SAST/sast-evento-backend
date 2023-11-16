package sast.evento.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public String permission(@RequestParam ActionState type,
                             @RequestParam(required = false) Integer eventId,
                             @RequestParam(required = false) List<String> permission) {
        //异常丑陋的代码，随他去吧，反正是测试用的（
        UserModel userModel = HttpInterceptor.userHolder.get();
        final List<String> adminPermission = ActionRegister.actionName2action.values().stream()
                .filter(a -> a.getActionState().equals(ActionState.ADMIN))
                .map(Action::getMethodName).toList();
        final List<String> managerPermission = ActionRegister.actionName2action.values().stream()
                .filter(a -> a.getActionState().equals(ActionState.MANAGER))
                .map(Action::getMethodName).toList();
        switch (type) {
            case ADMIN -> {
                List<String> addedPermission = permission.isEmpty() ? adminPermission : permission;
                if (permissionService.getUserAdminPermissions(userModel.getId()).isEmpty()) {
                    permissionService.addAdmin(addedPermission, userModel.getId());
                } else {
                    permissionService.updateAdminPermission(addedPermission, userModel.getId());
                }
            }
            case MANAGER -> {
                if (eventMapper.exists(Wrappers.lambdaQuery(Event.class).eq(Event::getId, eventId))) {
                    List<String> addedPermission = permission.isEmpty() ? managerPermission : permission;
                    if (permissionService.getUserManagerPermissions(eventId, userModel.getId()).isEmpty()) {
                        permissionService.addManager(eventId, addedPermission, userModel.getId());
                    } else {
                        permissionService.updateManagerPermission(eventId, addedPermission, userModel.getId());
                    }
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
