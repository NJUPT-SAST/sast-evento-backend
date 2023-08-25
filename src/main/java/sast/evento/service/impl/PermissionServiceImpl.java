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
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.PermissionService;
import sast.evento.service.PermissionServiceCacheAble;

import java.util.*;

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
    /* 设置所有不需要管理的默认权限 */
    public static final List<String> defaultManagerPermission = List.of(
            "getAllManagerPermissionsAsTree", "getAllManagerPermissions",
            "getUserManagerPermissions", "getUserManagerPermissAsList");
    public static final List<String> defaultAdminPermission = List.of(
            "getUserAdminPermissions", "getAllAdminPermissions",
            "getAllAdminPermissionsAsTree", "getUserAdminPermissAsList");

    @Override
    public List<Action> getAllAdminPermissions() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.ADMIN))
                .filter(action -> !defaultAdminPermission.contains(action.getMethodName()))
                .toList();
    }

    @Override
    public List<TreeDataNode> getAllAdminPermissionsAsTree() {
        return toTreeData(getAllAdminPermissions());
    }

    @Override
    public List<Action> getAllManagerPermissions() {
        return ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.MANAGER))
                .filter(action -> !defaultManagerPermission.contains(action.getMethodName()))
                .toList();
    }

    @Override
    public List<TreeDataNode> getAllManagerPermissionsAsTree() {
        return toTreeData(getAllManagerPermissions());
    }

    @Override
    public void addAdmin(List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        Permission permission = new Permission();
        methodNames.addAll(defaultAdminPermission);
        permission.setMethodNames(methodNames);
        permission.setEventId(0);
        permission.setUserId(userId);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteAdmin(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
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
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        methodNames.addAll(defaultAdminPermission);
        permission.setEventId(0);
        permission.setUserId(userId);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserAdminPermissions(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, 0).getMethodNames().stream()
                .filter(methodName -> !defaultAdminPermission.contains(methodName))
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public List<String> getUserAdminPermissAsList(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, 0).getMethodNames().stream()
                .filter(methodName -> !defaultAdminPermission.contains(methodName))
                .toList();
    }

    @Override
    public void addManager(Integer eventId, List<String> methodNames, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        methodNames.addAll(defaultManagerPermission);
        permission.setEventId(eventId);
        permission.setUserId(userId);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteManager(Integer eventId, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
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
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        Permission permission = new Permission();
        permission.setMethodNames(methodNames);
        methodNames.addAll(defaultManagerPermission);
        permission.setEventId(eventId);
        permission.setUserId(userId);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserManagerPermissions(Integer eventId, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, eventId).getMethodNames().stream()
                .filter(methodName -> !defaultManagerPermission.contains(methodName))
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public List<String> getUserManagerPermissAsList(Integer eventId, String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
        }
        return permissionServiceCacheAble.getPermission(userId, eventId).getMethodNames().stream()
                .filter(methodName -> !defaultManagerPermission.contains(methodName))
                .toList();
    }

    @Override
    public List<Integer> getManageEvent(String userId, String studentId) {
        if (userId.isEmpty()) {
            userId = Optional.ofNullable(getUserByStudentId(studentId).getUserId())
                    .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "studentId no exist"));
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

    private TreeDataNode getNode(String title, String value, String key) {
        //todo 对接前端客户端
        return new AntDesignTreeDataNode(title, value, null);
        //return new SemiTreeDataNode(title,value,key);
    }

    public List<TreeDataNode> toTreeData(List<Action> actions) {
        HashMap<String, List<TreeDataNode>> nodeMap = actions.stream()
                .collect(
                        HashMap::new,
                        (map, action) -> {
                            String groupName = action.getGroup();
                            if (!map.containsKey(groupName)) map.put(groupName, new ArrayList<>());
                            map.get(groupName).add(
                                    getNode(action.getDescription(), action.getMethodName(), action.getMethodName())
                            );
                        },
                        Map::putAll);
        return nodeMap.keySet().stream()
                .collect(
                        ArrayList::new,
                        (treeDataNodes, s) -> {
                            TreeDataNode node = getNode(s, s, s);
                            node.addChildren(nodeMap.get(s));
                            treeDataNodes.add(node);
                        },
                        List::addAll);
    }

    private User getUserByStudentId(String studentId) {
        return Optional.ofNullable(userMapper.selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getStudentId, studentId)))
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no such studentId, please add first"));
    }

}