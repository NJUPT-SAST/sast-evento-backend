package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Permission;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.PermissionMapper;
import sast.evento.mapper.UserMapper;
import sast.evento.model.Action;
import sast.evento.service.PermissionService;
import sast.evento.service.PermissionServiceCacheAble;

import java.util.List;
import java.util.Optional;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:46
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionServiceCacheAble permissionServiceCacheAble;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public List<Action> getAllAdminPermissions() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.ADMIN))
                .toList();
    }

    @Override
    public List<Action> getAllManagerPermissions() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.MANAGER))
                .toList();
    }

    @Override
    public void addAdmin(List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        permission.setEventId(0);
        permission.setUserId(userId);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteAdmin(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        permissionServiceCacheAble.deletePermission(userId, 0);
    }

    @Override
    public List<User> getAdmins() {
        return permissionMapper.getUserHasPermissionByEvent(0);
    }

    @Override
    public void updateAdminPermission(List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        permission.setEventId(0);
        permission.setUserId(userId);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserAdminPermissions(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, 0).getMethodNames().stream()
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public void addManager(Integer eventId, List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        permission.setEventId(eventId);
        permission.setUserId(userId);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteManager(Integer eventId, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        permissionServiceCacheAble.deletePermission(userId, eventId);
    }

    @Override
    public List<User> getManagers(Integer eventId) {
        return permissionMapper.getUserHasPermissionByEvent(eventId);
    }

    @Override
    public void updateManagerPermission(Integer eventId, List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        permission.setEventId(eventId);
        permission.setUserId(userId);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserManagerPermissions(Integer eventId, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, eventId).getMethodNames().stream()
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public List<Integer> getManageEvent(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(()->new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"studentId no exist"));
        }
        return permissionMapper.getManageEvent(userId).stream()
                .filter(integer -> !integer.equals(0))
                .toList();
    }

    @Override
    public List<Action> getPublicActions() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.PUBLIC))
                .toList();
    }

    @Override
    public List<Action> getLoginAction() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.LOGIN))
                .toList();
    }

    @Override
    public Boolean checkPermission(String userId, Integer eventId, String methodName) {
        //todo 对接后更改
        return permissionServiceCacheAble.getPermission(userId, eventId)
                .getMethodNames().stream()
                .anyMatch(methodName::equals);
    }

    private User getUserByStudentId(String studentId) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getStudentId, studentId)))
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no such studentId, please add first"));
    }

}