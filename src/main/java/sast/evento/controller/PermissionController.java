package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.service.PermissionService;

import java.util.List;


@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @OperateLog("获取所有admin权限")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admin/all")
    public List<Action> getAllAdminPermissions() {
        return permissionService.getAllAdminPermissions();
    }

    @OperateLog("获取所有manager权限")
    @DefaultActionState(ActionState.MANAGER)
    @GetMapping("/manager/all")
    public List<Action> getAllManagerPermissions(@RequestParam @EventId Integer eventId) {
        return permissionService.getAllManagerPermissions();
    }

    @OperateLog("删除后台管理者")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/admin")
    public String deleteAdmin(@RequestParam(required = false) String studentId,
                              @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.deleteAdmin(userId, studentId);
        return "ok";
    }

    @OperateLog("获取后台管理者列表")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admins")
    public List<User> getAdmins() {
        return permissionService.getAdmins();
    }

    @OperateLog("添加后台管理者")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/admin")
    public String addAdmin(@RequestParam List<String> methodNames,
                           @RequestParam(required = false) String studentId,
                           @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.addAdmin(methodNames, userId, studentId);
        return "ok";
    }

    @OperateLog("编辑后台管理者权限")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/admin")
    public String putAdmin(@RequestParam List<String> methodNames,
                           @RequestParam(required = false) String studentId,
                           @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.updateAdminPermission(methodNames, userId, studentId);
        return "ok";
    }

    @OperateLog("获取用户具有admin权限")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admin/user")
    public List<Action> getUserAdminPermissions(@RequestParam(required = false) String studentId,
                                                @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        return permissionService.getUserAdminPermissions(userId, studentId);
    }

    @OperateLog("获取用户对某活动，manager权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/event/manager/user")
    public List<Action> getUserManagerPermissions(@RequestParam @EventId Integer eventId,
                                                  @RequestParam(required = false) String studentId,
                                                  @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        return permissionService.getUserManagerPermissions(eventId, userId, studentId);
    }

    @OperateLog("删除活动管理者")
    @DefaultActionState(ActionState.MANAGER)
    @DeleteMapping(value = "/event/manager")
    public String deleteManager(@RequestParam @EventId Integer eventId,
                                @RequestParam(required = false) String studentId,
                                @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.deleteManager(eventId, userId, studentId);
        return "ok";
    }

    @OperateLog("编辑活动管理者权限")
    @DefaultActionState(ActionState.MANAGER)
    @PutMapping(value = "/event/manager")
    public String putManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam(required = false) String studentId,
                             @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.updateManagerPermission(eventId, methodNames, userId, studentId);
        return "ok";
    }

    @OperateLog("添加活动管理者")
    @DefaultActionState(ActionState.MANAGER)
    @PostMapping(value = "/event/manager")
    public String addManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam(required = false) String studentId,
                             @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        permissionService.addManager(eventId, methodNames, userId, studentId);
        return "ok";
    }

    @OperateLog("获取活动管理者列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/event/managers")
    public List<User> getManagers(@RequestParam @EventId Integer eventId) {
        return permissionService.getManagers(eventId);
    }

    @OperateLog("获取用户具有哪些活动的管理权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/manager/events")
    public List<Integer> getManageEvent(@RequestParam(required = false) String studentId,
                                        @RequestParam(required = false) String userId) {
        if (userId.isEmpty() && studentId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId or studentId is required, and at least one.");
        }
        return permissionService.getManageEvent(userId, studentId);
    }

}
