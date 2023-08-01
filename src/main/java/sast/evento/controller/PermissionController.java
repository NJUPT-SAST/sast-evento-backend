package sast.evento.controller;

import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.entitiy.User;
import sast.evento.model.Action;

import java.util.List;


@RestController
@RequestMapping("/permission")
public class PermissionController {

    @OperateLog("获取所有admin权限")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admin/all")
    public List<Action> getAllAdminPermissions() {
        return null;
    }

    @OperateLog("获取所有manager权限")
    @DefaultActionState(ActionState.MANAGER)
    @GetMapping("/manager/all")
    public List<Action> getAllManagerPermissions(@RequestParam @EventId Integer eventId) {
        return null;
    }

    @OperateLog("删除后台管理者")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/admin")
    public String deleteAdmin(@RequestParam(required = false) String studentId,
                              @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("获取后台管理者列表")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admins")
    public List<User> getAdmins() {
        return null;
    }

    @OperateLog("添加后台管理者")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/admin")
    public String addAdmin(@RequestParam List<String> methodNames,
                           @RequestParam(required = false) String studentId,
                           @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("编辑后台管理者权限")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/admin")
    public String updateAdmin(@RequestParam List<String> methodNames,
                              @RequestParam(required = false) String studentId,
                              @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("获取用户具有admin权限")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/admin/user")
    public List<Action> getAdminPermission(@RequestParam(required = false) String studentId,
                                           @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("获取用户对某活动，manager权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/event/manager/user")
    public List<Action> getEventPermission(@RequestParam @EventId Integer EventId,
                                           @RequestParam(required = false) String studentId,
                                           @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("删除活动管理者")
    @DefaultActionState(ActionState.MANAGER)
    @DeleteMapping(value = "/event/manager")
    public String deleteManager(@RequestParam @EventId Integer EventId,
                                @RequestParam(required = false) String studentId,
                                @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("编辑活动管理者权限")
    @DefaultActionState(ActionState.MANAGER)
    @PatchMapping(value = "/event/manager")
    public String PatchManager(@RequestParam List<String> methodNames,
                               @RequestParam @EventId Integer EventId,
                               @RequestParam(required = false) String studentId,
                               @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("添加活动管理者")
    @DefaultActionState(ActionState.MANAGER)
    @PostMapping(value = "/event/manager")
    public String addManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer EventId,
                             @RequestParam(required = false) String studentId,
                             @RequestParam(required = false) String userId) {
        return null;
    }

    @OperateLog("获取活动管理者列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/event/managers")
    public List<User> getManagers(@RequestParam @EventId Integer EventId) {
        return null;
    }

    @OperateLog("获取用户具有哪些活动的管理权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/manager/events")
    public List<Action> getUserManagers(@RequestParam(required = false) String studentId,
                                        @RequestParam(required = false) String userId) {
        return null;
    }

}
