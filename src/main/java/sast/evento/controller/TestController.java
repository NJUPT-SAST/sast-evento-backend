package sast.evento.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.feellmoose.test.TestSastLinkServiceAdapter;
import fun.feellmoose.test.data.Token;
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
import sast.evento.entitiy.Event;
import sast.evento.entitiy.Permission;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.mapper.EventMapper;
import sast.evento.mapper.PermissionMapper;
import sast.evento.model.Action;
import sast.evento.model.UserModel;
import sast.evento.service.PermissionService;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@ConditionalOnProperty(prefix = "test", name = "challenge")
@RequestMapping("/test")
public class TestController {
    @Value("${test.challenge}")
    private String challenge;
    @Value("${test.method}")
    private String method;

    @Resource
    private TestSastLinkServiceAdapter sastLinkService;
    @Resource
    private TestSastLinkServiceAdapter sastLinkServiceWeb;
    @Resource
    private TestSastLinkServiceAdapter sastLinkServiceMobileDev;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private EventMapper eventMapper;


    @OperateLog("link登录")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/linklogin")
    public String linkLogin(@RequestParam Integer type,
                            @RequestParam String email,
                            @RequestParam String password) {
        TestSastLinkServiceAdapter service = switch (type) {
            case 0 -> sastLinkService;
            case 1 -> sastLinkServiceWeb;
            case 2 -> sastLinkServiceMobileDev;
            default -> throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error link client type value: " + type);
        };
        if (challenge.isEmpty() || method.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR);
        }
        try {
            Token token = service.login(email, password);
            return service.authorize(token.getLoginToken(), challenge, method);
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
        UserModel userModel = HttpInterceptor.userHolder.get();
        switch (type) {
            case ADMIN -> {
                List<String> adminPermission = permissionService.getAllAdminPermissions().stream().map(Action::getMethodName).collect(Collectors.toList());
                List<String> addedPermission = permission.isEmpty() ? adminPermission : permission;
                if (!permissionMapper.exists(Wrappers.lambdaQuery(Permission.class)
                        .eq(Permission::getUserId, userModel.getId())
                        .and(wrapper -> wrapper.eq(Permission::getEventId, 0)))) {
                    permissionService.addAdmin(addedPermission, userModel.getId());
                } else {
                    permissionService.updateAdminPermission(addedPermission, userModel.getId());
                }
            }
            case MANAGER -> {
                List<String> managerPermission = permissionService.getAllManagerPermissions().stream().map(Action::getMethodName).collect(Collectors.toList());
                if (eventMapper.exists(Wrappers.lambdaQuery(Event.class).eq(Event::getId, eventId))) {
                    List<String> addedPermission = permission.isEmpty() ? managerPermission : permission;
                    if (!permissionMapper.exists(Wrappers.lambdaQuery(Permission.class)
                            .eq(Permission::getUserId, userModel.getId())
                            .and(wrapper -> wrapper.eq(Permission::getEventId, eventId)))) {
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
