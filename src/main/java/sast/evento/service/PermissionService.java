package sast.evento.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    void addAdmin(List<String> methodNames, String userId);

    void deleteAdmin(String userId);

    Page<User> getAdmins(Integer num, Integer size);

    void updateAdminPermission(List<String> methodNames, String userId);

    List<Action> getUserAdminPermissions(String userId);

    List<String> getUserAdminPermissAsList(String userId);

    void addManager(Integer eventId, List<String> methodNames, String userId);

    void deleteManager(Integer eventId, String userId);

    Page<User> getManagers(Integer eventId,Integer num,Integer size);

    void updateManagerPermission(Integer eventId, List<String> methodNames, String userId);

    List<Action> getUserManagerPermissions(Integer eventId, String userId);

    List<String> getUserManagerPermissAsList(Integer eventId, String userId);

    List<Integer> getManageEvent(String userId);

    List<Action> getPublicActions();

    List<Action> getLoginAction();

    Boolean checkPermission(String userId, Integer eventId, String methodName);

}