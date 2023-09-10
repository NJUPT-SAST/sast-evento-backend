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
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.PermissionService;

import java.util.List;


@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @OperateLog("获取所有后台管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/admin/all")
    public List<Action> getAllAdminPermissions() {
        return permissionService.getAllAdminPermissions();
    }


    @OperateLog("获取所有后台管理权限")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @GetMapping("/admin/treeData")
    public List<TreeDataNode> getAllAdminPermissionsAsTree() {
        return permissionService.getAllAdminPermissionsAsTree();
    }

    @OperateLog("获取所有活动管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/manager/all")
    public List<Action> getAllManagerPermissions(@RequestParam @EventId Integer eventId) {
        return permissionService.getAllManagerPermissions();
    }

    @OperateLog("获取所有活动管理权限")
    @DefaultActionState(value = ActionState.MANAGER,group = "permission")
    @GetMapping("/manager/treeData")
    public List<TreeDataNode> getAllManagerPermissionsAsTree(@RequestParam @EventId Integer eventId) {
        return permissionService.getAllManagerPermissionsAsTree();
    }

    @OperateLog("删除后台管理者")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @DeleteMapping("/admin")
    public String deleteAdmin(@RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.deleteAdmin(userId);
        return "ok";
    }

    @OperateLog("获取后台管理者列表")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @GetMapping("/admins")
    public List<User> getAdmins() {
        return permissionService.getAdmins();
    }

    @OperateLog("添加后台管理者")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @PostMapping("/admin")
    public String addAdmin(@RequestParam List<String> methodNames,
                           @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.addAdmin(methodNames, userId);
        return "ok";
    }

    @OperateLog("编辑后台管理者权限")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @PutMapping("/admin")
    public String putAdmin(@RequestParam List<String> methodNames,
                           @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.updateAdminPermission(methodNames, userId);
        return "ok";
    }

    @OperateLog("获取用户具有后台管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/admin/user")
    public List<Action> getUserAdminPermissions(@RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        return permissionService.getUserAdminPermissions(userId);
    }

    @OperateLog("获取用户具有的后台管理权限")
    @DefaultActionState(value = ActionState.ADMIN,group = "permission")
    @GetMapping("/admin/user/list")
    public List<String> getUserAdminPermissAsList(@RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        return permissionService.getUserAdminPermissAsList(userId);
    }

    @OperateLog("获取用户对某活动管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/event/manager/user")
    public List<Action> getUserManagerPermissions(@RequestParam @EventId Integer eventId,
                                                  @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        return permissionService.getUserManagerPermissions(eventId, userId);
    }

    @OperateLog("获取用户对某活动管理的权限")
    @DefaultActionState(value = ActionState.MANAGER,group = "permission")
    @GetMapping("/event/manager/user/list")
    public List<String> getUserManagerPermissAsList(@RequestParam @EventId Integer eventId,
                                                    @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        return permissionService.getUserManagerPermissAsList(eventId, userId);
    }

    @OperateLog("删除活动管理者")
    @DefaultActionState(value = ActionState.MANAGER,group = "permission")
    @DeleteMapping(value = "/event/manager")
    public String deleteManager(@RequestParam @EventId Integer eventId,
                                @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.deleteManager(eventId, userId);
        return "ok";
    }

    @OperateLog("编辑活动管理者权限")
    @DefaultActionState(value = ActionState.MANAGER,group = "permission")
    @PutMapping(value = "/event/manager")
    public String putManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.updateManagerPermission(eventId, methodNames, userId);
        return "ok";
    }

    @OperateLog("添加活动管理者")
    @DefaultActionState(value = ActionState.MANAGER,group = "permission")
    @PostMapping(value = "/event/manager")
    public String addManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        permissionService.addManager(eventId, methodNames, userId);
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
    public List<Integer> getManageEvent(@RequestParam String userId) {
        if (userId.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parameter userId is required, and at least one");
        }
        return permissionService.getManageEvent(userId);
    }

    @OperateLog("获取用户自身后台管理权限用于条件渲染")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/admin/self")
    public List<String> getSelfAdminPermission() {
        User user = HttpInterceptor.userHolder.get();
        return permissionService.getUserAdminPermissAsList(user.getUserId());
    }

    @OperateLog("获取用户自身活动管理权限用于条件渲染")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/event/manager/self")
    public List<String> getSelfManagerPermission(@RequestParam @EventId Integer eventId) {
        User user = HttpInterceptor.userHolder.get();
        return permissionService.getUserManagerPermissAsList(eventId, user.getUserId());
    }

}
