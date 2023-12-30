package sast.evento.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import sast.evento.model.UserModel;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.PermissionService;
import sast.evento.service.UserService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserService userService;

    @OperateLog("获取所有后台管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/admin/all")
    public List<Action> getAllAdminPermissions() {
        return permissionService.getAllAdminPermissions();
    }


    @OperateLog("获取所有后台管理权限")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
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
    @DefaultActionState(value = ActionState.MANAGER, group = "permission")
    @GetMapping("/manager/treeData")
    public List<TreeDataNode> getAllManagerPermissionsAsTree(@RequestParam @EventId Integer eventId) {
        return permissionService.getAllManagerPermissionsAsTree();
    }

    @OperateLog("删除后台管理者")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
    @DeleteMapping("/admin")
    public String deleteAdmin(@RequestParam(required = false) String userId,
                              @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        permissionService.deleteAdmin(userId);
        return "ok";
    }

    @OperateLog("获取后台管理者列表")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
    @GetMapping("/admins")
    public Map<String, Object> getAdmins(@RequestParam(required = false,defaultValue = "1")Integer current,
                                         @RequestParam(required = false,defaultValue = "10")Integer size) {
        Page<User> userPage = permissionService.getAdmins(current, size);
        return Map.of("users", userPage.getRecords(), "total", userPage.getTotal());
    }

    @OperateLog("添加后台管理者")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
    @PostMapping("/admin")
    public String addAdmin(@RequestParam List<String> methodNames,
                           @RequestParam(required = false) String userId,
                           @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        permissionService.addAdmin(methodNames, userId);
        return "ok";
    }

    @OperateLog("编辑后台管理者权限")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
    @PutMapping("/admin")
    public String putAdmin(@RequestParam List<String> methodNames,
                           @RequestParam(required = false) String userId,
                           @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        permissionService.updateAdminPermission(methodNames, userId);
        return "ok";
    }

    @OperateLog("获取用户具有后台管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/admin/user")
    public List<Action> getUserAdminPermissions(@RequestParam(required = false) String userId,
                                                @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        return permissionService.getUserAdminPermissions(userId);
    }

    @OperateLog("获取用户具有的后台管理权限")
    @DefaultActionState(value = ActionState.ADMIN, group = "permission")
    @GetMapping("/admin/user/list")
    public List<String> getUserAdminPermissAsList(@RequestParam(required = false) String userId,
                                                  @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        return permissionService.getUserAdminPermissAsList(userId);
    }

    @OperateLog("获取用户对某活动管理权限")
    @DefaultActionState(ActionState.INVISIBLE)
    @GetMapping("/event/manager/user")
    public List<Action> getUserManagerPermissions(@RequestParam @EventId Integer eventId,
                                                  @RequestParam(required = false) String userId,
                                                  @RequestParam(required = false) String studentId) {
        checkEventId(eventId);
        userId = checkUser(userId, studentId);
        return permissionService.getUserManagerPermissions(eventId, userId);
    }

    @OperateLog("获取用户对某活动管理的权限")
    @DefaultActionState(value = ActionState.MANAGER, group = "permission")
    @GetMapping("/event/manager/user/list")
    public List<String> getUserManagerPermissAsList(@RequestParam @EventId Integer eventId,
                                                    @RequestParam(required = false) String userId,
                                                    @RequestParam(required = false) String studentId) {
        checkEventId(eventId);
        userId = checkUser(userId, studentId);
        return permissionService.getUserManagerPermissAsList(eventId, userId);
    }

    @OperateLog("删除活动管理者")
    @DefaultActionState(value = ActionState.MANAGER, group = "permission")
    @DeleteMapping(value = "/event/manager")
    public String deleteManager(@RequestParam @EventId Integer eventId,
                                @RequestParam(required = false) String userId,
                                @RequestParam(required = false) String studentId) {
        checkEventId(eventId);
        userId = checkUser(userId, studentId);
        permissionService.deleteManager(eventId, userId);
        return "ok";
    }

    @OperateLog("编辑活动管理者权限")
    @DefaultActionState(value = ActionState.MANAGER, group = "permission")
    @PutMapping(value = "/event/manager")
    public String putManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam(required = false) String userId,
                             @RequestParam(required = false) String studentId) {
        checkEventId(eventId);
        userId = checkUser(userId, studentId);
        permissionService.updateManagerPermission(eventId, methodNames, userId);
        return "ok";
    }

    @OperateLog("添加活动管理者")
    @DefaultActionState(value = ActionState.MANAGER, group = "permission")
    @PostMapping(value = "/event/manager")
    public String addManager(@RequestParam List<String> methodNames,
                             @RequestParam @EventId Integer eventId,
                             @RequestParam(required = false) String userId,
                             @RequestParam(required = false) String studentId) {
        checkEventId(eventId);
        userId = checkUser(userId, studentId);
        permissionService.addManager(eventId, methodNames, userId);
        return "ok";
    }

    @OperateLog("获取活动管理者列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/event/managers")
    public Map<String, Object> getManagers(@RequestParam @EventId Integer eventId,
                                           @RequestParam(required = false,defaultValue = "1")Integer current,
                                           @RequestParam(required = false,defaultValue = "10")Integer size) {
        checkEventId(eventId);
        Page<User> userPage = permissionService.getManagers(eventId, current, size);
        return Map.of("users", userPage.getRecords(), "total", userPage.getTotal());
    }

    @OperateLog("获取用户具有哪些活动的管理权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/manager/events")
    public List<Integer> getManageEvent(@RequestParam(required = false) String userId,
                                        @RequestParam(required = false) String studentId) {
        userId = checkUser(userId, studentId);
        return permissionService.getManageEvent(userId);
    }

    @OperateLog("获取用户自身后台管理权限用于条件渲染")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/admin/self")
    public List<String> getSelfAdminPermission() {
        UserModel user = HttpInterceptor.userHolder.get();
        return permissionService.getUserAdminPermissAsList(user.getId());
    }

    @OperateLog("获取用户自身活动管理权限用于条件渲染")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping(value = "/event/manager/self")
    public List<String> getSelfManagerPermission(@RequestParam @EventId Integer eventId) {
        checkEventId(eventId);
        UserModel user = HttpInterceptor.userHolder.get();
        return permissionService.getUserManagerPermissAsList(eventId, user.getId());
    }

    @OperateLog("分页查询所有用户")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping(value = "/users")
    public Map<String, Object> getUsers(@RequestParam(required = false,defaultValue = "1")Integer current,
                                           @RequestParam(required = false,defaultValue = "10")Integer size) {
        Page<User> userPage = permissionService.getUsers(current, size);
        return Map.of("users", userPage.getRecords(), "total", userPage.getTotal());
    }

    @OperateLog("模糊查询用户")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping(value = "/users/search")
    public Map<String, Object> searchUsers(@RequestParam String keyword,
                                           @RequestParam(required = false,defaultValue = "1")Integer current,
                                           @RequestParam(required = false,defaultValue = "10")Integer size){
        Page<User> userPage = permissionService.searchUsers(keyword, current, size);
        return Map.of("users", userPage.getRecords(), "total", userPage.getTotal());
    }

    private String checkUser(String userId, String studentId) {
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        if (studentId != null && !studentId.isEmpty()) {
            User user = userService.getUserByStudentId(studentId);
            if(user == null || user.getId() == null){
                throw new LocalRunTimeException(ErrorEnum.STUDENT_NOT_BIND);
            }
            return user.getId();
        }
        throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid userId or studentId");
    }

    private void checkEventId(Integer eventId) {
        if (eventId == null || eventId <= 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid eventId, eventId should be greater than 0");
        }
    }

}
