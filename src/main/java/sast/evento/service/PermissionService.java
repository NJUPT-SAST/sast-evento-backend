package sast.evento.service;

import sast.evento.model.Action;
import sast.evento.entitiy.Permission;

import java.nio.charset.StandardCharsets;
import java.util.*;

public interface PermissionService {
    /* 对Permission基本操作，设置缓存 */
    Permission addPermission(Permission permission);
    void deletePermission(String userId, Integer eventId);
    Permission getPermission(String userId, Integer eventId);
    Permission updatePermission(Permission permission);

    /* 对Permission拓展操作，不设置缓存 */
    void deleteCommonPermission(String userId);
    Permission getCommonPermission(String userId);
    List<String> getValidMethodNamesByEventId(String userId, Integer eventId);
    List<String> getValidCommonMethodNamesByEventId(String userId);
    Boolean checkPermission(String userId, Integer eventId, String methodName);

}