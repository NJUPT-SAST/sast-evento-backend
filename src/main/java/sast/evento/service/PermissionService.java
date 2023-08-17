package sast.evento.service;

import sast.evento.entitiy.User;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;

import java.util.List;

public interface PermissionService {
    /* 对Permission拓展操作，不设置缓存 */
    List<Action> getAllAdminPermissions();

    List<TreeDataNode> getAllAdminPermissionsAsTree();

    List<Action> getAllManagerPermissions();

    List<TreeDataNode> getAllManagerPermissionsAsTree();

    void addAdmin(List<String> methodNames, String userId, String studentId);

    void deleteAdmin(String userId, String studentId);

    List<User> getAdmins();

    void updateAdminPermission(List<String> methodNames, String userId, String studentId);

    List<Action> getUserAdminPermissions(String userId, String studentId);

    List<String> getUserAdminPermissAsList(String userId, String studentId);

    void addManager(Integer eventId, List<String> methodNames, String userId, String studentId);

    void deleteManager(Integer eventId, String userId, String studentId);

    List<User> getManagers(Integer eventId);

    void updateManagerPermission(Integer eventId, List<String> methodNames, String userId, String studentId);

    List<Action> getUserManagerPermissions(Integer eventId, String userId, String studentId);

    List<String> getUserManagerPermissAsList(Integer eventId, String userId, String studentId);

    List<Integer> getManageEvent(String userId, String studentId);

    List<Action> getPublicActions();

    List<Action> getLoginAction();

    Boolean checkPermission(String userId, Integer eventId, String methodName);

}