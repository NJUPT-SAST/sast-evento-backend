package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Permission;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.PermissionMapper;
import sast.evento.service.PermissionServiceCacheAble;
import sast.evento.utils.JsonUtil;

import java.util.Optional;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/4 9:54
 */
@Service
public class PermissionServiceCacheAbleImpl implements PermissionServiceCacheAble {
    /* 权限服务 */
    @Resource
    private PermissionMapper permissionMapper;
    @Override
    @CachePut(value = "permission", key = "#permission.userId +#permission.eventId")
    public Permission addPermission(Permission permission) {
        if(permission.getId()!=null){
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"Invalid param. Empty id is needed");
        }
        checkValidMethods(permission);
        permissionMapper.insert(permission.updateUpTime());
        return permission;
    }

    @Override
    @CacheEvict(value = "permission", key = "#userId +#eventId")
    public void deletePermission(String userId,Integer eventId)  {
        permissionMapper.delete(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getUserId,userId)
                .and(wrapper -> wrapper.eq(Permission::getEventId,eventId)));
    }

    @Override
    @Cacheable(value = "permission", key = "#userId +#eventId")
    public Permission getPermission(String userId,Integer eventId) {
        Permission permission = permissionMapper.selectOne(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getUserId,userId)
                .and(wrapper -> wrapper.eq(Permission::getEventId,eventId)));
        if (permission == null) {
            throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR,"No valid permission exist");
        }
        return permission;
    }

    @Override
    @CachePut(value = "permission",key = "#permission.userId +#permission.eventId")
    public Permission updatePermission(Permission permission) {
        checkValidMethods(permission);
        permissionMapper.updatePermission(permission.getUserId(),
                permission.getEventId(),
                JsonUtil.toJson(permission.getMethodNames()),
                permission.updateUpTime().getUpdateTime());
        return permission;
    }

    private static void checkValidMethods(Permission permission){
        /* check and put valid methodNames */
        Optional.ofNullable(permission.getMethodNames()).ifPresent(
                methodNames -> permission.setMethodNames(methodNames.stream()
                        .filter(ActionRegister.actionNameSet::contains)
                        .distinct()
                        .toList())
        );
    }
}
