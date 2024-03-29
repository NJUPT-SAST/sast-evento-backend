package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.feellmoose.enums.Organization;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ActionState;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Permission;
import sast.evento.entitiy.User;
import sast.evento.mapper.PermissionMapper;
import sast.evento.mapper.UserMapper;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.PermissionService;
import sast.evento.service.PermissionServiceCacheAble;

import java.util.*;
import java.util.stream.Collectors;

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
    public static final List<String> defaultManagerPermission = Arrays.asList(
            "getAllManagerPermissionsAsTree", "getAllManagerPermissions",
            "getUserManagerPermissions", "getUserManagerPermissAsList");
    public static final List<String> defaultAdminPermission = Arrays.asList(
            "getUserAdminPermissions", "getAllAdminPermissions",
            "getAllAdminPermissionsAsTree", "getUserAdminPermissAsList",
            "getFeedback", "getStates", "getActionList", "getTypes",
            "getDepartments", "getLocations", "getAdmins", "getDir",
            "getUrls","eventAuthcodeGenerate");

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
    public void addAdmin(List<String> methodNames, String userId) {
        methodNames.addAll(defaultAdminPermission);
        Permission permission = new Permission(null, 0, userId, methodNames, null);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteAdmin(String userId) {
        permissionServiceCacheAble.deletePermission(userId, 0);
    }

    @Override
    public Page<User> getAdmins(Integer num,Integer size) {
        List<String> userIds =  permissionMapper.selectList(Wrappers.lambdaQuery(Permission.class)
                        .eq(Permission::getEventId,0)).stream()
                .map(Permission::getUserId).toList();
        return userMapper.selectPage(new Page<>(num,size),Wrappers.lambdaQuery(User.class)
                .in(User::getId,userIds));
    }

    @Override
    public void updateAdminPermission(List<String> methodNames, String userId) {
        methodNames.addAll(defaultAdminPermission);
        Permission permission = new Permission(null, 0, userId, methodNames, null);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserAdminPermissions(String userId) {
        return permissionServiceCacheAble.getPermission(userId, 0).getMethodNames().stream()
                .filter(methodName -> !defaultAdminPermission.contains(methodName))
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public List<String> getUserAdminPermissAsList(String userId) {
        return permissionServiceCacheAble.getPermission(userId, 0).getMethodNames().stream()
                .filter(methodName -> !defaultAdminPermission.contains(methodName))
                .toList();
    }

    @Override
    public void addManager(Integer eventId, List<String> methodNames, String userId) {
        methodNames.addAll(defaultManagerPermission);
        Permission permission = new Permission(null, eventId, userId, methodNames, null);
        permissionServiceCacheAble.addPermission(permission);
    }

    @Override
    public void deleteManager(Integer eventId, String userId) {
        permissionServiceCacheAble.deletePermission(userId, eventId);
    }

    @Override
    public Page<User> getManagers(Integer eventId,Integer num,Integer size) {
        List<String> userIds =  permissionMapper.selectList(Wrappers.lambdaQuery(Permission.class)
                .eq(Permission::getEventId,eventId)).stream()
                .map(Permission::getUserId).toList();
        return userMapper.selectPage(new Page<>(num,size),Wrappers.lambdaQuery(User.class)
                .in(User::getId,userIds));
    }

    @Override
    public void updateManagerPermission(Integer eventId, List<String> methodNames, String userId) {
        methodNames.addAll(defaultManagerPermission);
        Permission permission = new Permission(null, eventId, userId, methodNames, null);
        permissionServiceCacheAble.updatePermission(permission);
    }

    @Override
    public List<Action> getUserManagerPermissions(Integer eventId, String userId) {
        return permissionServiceCacheAble.getPermission(userId, eventId).getMethodNames().stream()
                .filter(methodName -> !defaultManagerPermission.contains(methodName))
                .map(methodName -> ActionRegister.actionName2action.get(methodName))
                .toList();
    }

    @Override
    public List<String> getUserManagerPermissAsList(Integer eventId, String userId) {
        return permissionServiceCacheAble.getPermission(userId, eventId).getMethodNames().stream()
                .filter(methodName -> !defaultManagerPermission.contains(methodName))
                .toList();
    }

    @Override
    public List<Integer> getManageEvent(String userId) {
        return permissionMapper.selectList(Wrappers.lambdaQuery(Permission.class)
                .eq(Permission::getUserId,userId)).stream()
                .map(Permission::getEventId)
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
        return permissionServiceCacheAble.getPermission(userId, eventId)
                .getMethodNames().stream()
                .anyMatch(methodName::equals);
    }

    private TreeDataNode getNode(String description, String methodName) {
        return new AntDesignTreeDataNode(description, methodName, null);
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
                                    getNode(action.getDescription(), action.getMethodName())
                            );
                        },
                        Map::putAll);
        return nodeMap.keySet().stream()
                .collect(
                        ArrayList::new,
                        (treeDataNodes, s) -> {
                            TreeDataNode node = getNode(s, s);
                            node.addChildren(nodeMap.get(s));
                            treeDataNodes.add(node);
                        },
                        List::addAll);
    }

    @Override
    public Page<User> getUsers(Integer current, Integer size) {
        return userMapper.selectPage(new Page<>(current,size),Wrappers.lambdaQuery(User.class));
    }

    @Override
    public Page<User> searchUsers(String keyword, Integer current, Integer size){
        List<Integer> orgs = Arrays.stream(Organization.values())
                .filter(organization -> organization.getOrg().contains(keyword))
                .map(Organization::getId).toList();
        return userMapper.selectPage(new Page<>(current,size),Wrappers.lambdaQuery(User.class)
                .like(User::getStudentId,keyword).or().like(User::getNickname,keyword).or()
                .in(User::getOrganization,orgs).orderByDesc(User::getId));
    }
}
