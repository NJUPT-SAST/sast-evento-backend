package sast.evento.service;

import sast.evento.model.Action;
import sast.evento.model.Permission;

import java.util.List;

public interface PermissionService {
    /* 对Permission基本操作，设置缓存 */
    Permission getPermission(String userId);

    Permission addPermission(String userId, Permission permission);

    Permission clearPermission(String userId);

    Permission updatePermission(String userId, Permission permission);

    /* 提供高级服务 */
    void checkUserState(String userId);

    Permission.Statement getStatementByEventId(String userId, String eventId);

    Permission.Statement getBaseStatement(String userId);

    List<Permission.Statement> getAllStatement(String userId);

    List<Action> getAllValidActionByEventId(String userId, String eventId);

    List<Action> getValidBaseAction(String userId);

    List<Action> getValidActionByEventId(String userId, String eventId);

    Boolean clearBaseStatement(String userId);

    Boolean clearStatementByEventId(String userId, String eventId);

    Boolean clearAllStatement(String userId);

    Boolean updateBaseStatement(String userId, Permission.Statement statement);

    Boolean updateStatementByEventId(String userId, String eventId, Permission.Statement statement);

    Boolean checkPermissionByResouce(Permission permission, Action accessAction,String resource);

    Boolean checkPermission(Permission permission, Action accessAction);
}
