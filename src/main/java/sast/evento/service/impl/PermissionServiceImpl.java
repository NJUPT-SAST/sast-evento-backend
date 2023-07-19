package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.Action;
import sast.evento.entitiy.UserPermission;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.UserPermissionMapper;
import sast.evento.model.Permission;
import sast.evento.service.PermissionService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:46
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Override
    @CachePut(value = "permission", key = "#userId")
    public Permission addPermission(String userId, Permission permission) {
        checkUserState(userId);
        permission.setVersion(new Date());
        userPermissionMapper.insert(new UserPermission(userId, permission));
        return permission;
    }

    @Override
    @CachePut(value = "permission", key = "#userId")
    public Permission clearPermission(String userId) {
        checkUserState(userId);
        Permission permission = Permission.getDefault();
        userPermissionMapper.updateById(new UserPermission(userId, permission));
        return permission;
    }

    @Override
    @Cacheable(value = "permission", key = "#userId")
    public Permission getPermission(String userId) {
        UserPermission userPermission = userPermissionMapper.selectById(userId);
        if (userPermission == null) {
            throw new LocalRunTimeException(ErrorEnum.USER_ALREADY_EXIST, "Please check if user has been already log in.");
        }
        Permission permission = userPermission.getPermission();
        return permission;
    }

    @Override
    @CachePut(value = "permission", key = "#userId")
    public Permission updatePermission(String userId, Permission permission) {
        checkUserState(userId);
        permission.setVersion(new Date());
        userPermissionMapper.updateById(new UserPermission(userId, permission));
        return permission;
    }

    @Override
    public void checkUserState(String userId) {
        getPermission(userId);
    }

    @Override
    public Permission.Statement getStatementByEventId(String userId, String eventId) {
        return getAllStatement(userId).stream()
                .filter(statement -> eventId.equals(statement.getResource()))
                .findAny()
                .orElse(null);
    }

    @Override
    public Permission.Statement getBaseStatement(String userId) {
        return getStatementByEventId(userId, "all");
    }

    @Override
    public List<Permission.Statement> getAllStatement(String userId) {
        return getPermission(userId).getStatements();
    }

    @Override
    public List<Action> getAllValidActionByEventId(String userId, String eventId) {
        Set<Action> actions = new HashSet<>();
        actions.addAll(getValidBaseAction(userId));
        actions.addAll(getValidActionByEventId(userId, eventId));
        return actions.stream().toList();
    }

    @Override
    public List<Action> getValidBaseAction(String userId) {
        return getValidActionByEventId(userId, "all");
    }

    @Override
    public List<Action> getValidActionByEventId(String userId, String eventId) {
        return getAllStatement(userId).stream()
                .filter(statement -> statement.getConditions() != null && statement.getConditions().after(new Date()))
                .filter(statement -> statement.getResource().equals(eventId))
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR))
                .getActions();
    }

    @Override
    public Boolean clearBaseStatement(String userId) {
        return clearStatementByEventId(userId, "all");
    }

    @Override
    public Boolean clearStatementByEventId(String userId, String eventId) {
        Permission.Statement statement = new Permission.Statement();
        statement.setResource(eventId);
        return updateStatementByEventId(userId, eventId, statement);
    }

    @Override
    public Boolean clearAllStatement(String userId) {
        clearPermission(userId);
        return true;
    }

    @Override
    public Boolean updateBaseStatement(String userId, Permission.Statement statement) {
        return updateStatementByEventId(userId, "all", statement);
    }

    @Override
    public Boolean updateStatementByEventId(String userId, String eventId, Permission.Statement statement) {
        if (!statement.getResource().equals(eventId)) {
            throw new LocalRunTimeException(ErrorEnum.STATEMENT_ERROR, "Invalid eventId.");
        }
        Permission permission = getPermission(userId);
        if (permission.getStatements().stream()
                .map(Permission.Statement::getResource)
                .anyMatch(eventId::equals)) {
            permission.setStatements(permission.getStatements().stream()
                    .map(s -> s.getResource().equals(eventId) ? statement : s)
                    .collect(Collectors.toList()));
        } else {
            permission.getStatements().add(statement);
        }
        updatePermission(userId, permission);
        return true;
    }

    @Override
    public Boolean checkPermission(Permission permission, Action accessAction) {
        return permission.getStatements().stream().anyMatch(statement -> checkStatement(statement, accessAction));
    }

    private Boolean checkStatement(Permission.Statement statement, Action accessAction) {
        if (statement.getConditions() != null && statement.getConditions().after(new Date())) {
            return false;
        }
        if (statement.getResource().equals("all") || statement.getResource().equals(accessAction.getActionName())) {
            return statement.getActions().stream()
                    .map(Action::getId)
                    .anyMatch(needCheck -> checkAction(needCheck, accessAction));
        }
        return false;
    }

    private Boolean checkAction(Integer needCheck, Action accessAction) {
        return accessAction.getId().equals(needCheck);
    }
}
