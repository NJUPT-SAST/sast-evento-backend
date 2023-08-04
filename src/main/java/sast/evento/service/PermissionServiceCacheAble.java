package sast.evento.service;

import sast.evento.entitiy.Permission;

public interface PermissionServiceCacheAble {
    /* 对Permission基本操作，设置缓存 */
    Permission addPermission(Permission permission);

    void deletePermission(String userId, Integer eventId);

    Permission getPermission(String userId, Integer eventId);

    Permission updatePermission(Permission permission);
}
