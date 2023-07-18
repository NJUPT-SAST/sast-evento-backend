package sast.evento.interceptor;

import com.auth0.jwt.interfaces.Claim;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sast.evento.dataobject.Action;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Permission;
import sast.evento.model.UserProFile;
import sast.evento.service.ActionService;
import sast.evento.service.PermissionService;
import sast.evento.service.impl.SastLinkServiceCacheAble;
import sast.evento.utils.JwtUtil;

import java.util.Map;


/**
 * @Title HttpInterceptor
 * @Description 获取访问信息, 从数据库获取API限制信息*,获取用户信息*,从数据库获取用户权限*,
 * @Description 解析TOKEN信息获取本地用户登录信息, 与API信息联合判断是否拦截
 * @Author feelMoose
 * @Date 2023/7/14 16:27
 */
@Slf4j//todo 针整点权限的日志
@Component
public class HttpInterceptor implements HandlerInterceptor {
    @Resource
    private ActionService actionService;
    @Resource
    private SastLinkServiceCacheAble sastLinkServiceCacheAble;
    @Resource
    private PermissionService permissionService;
    @Resource
    private JwtUtil jwtUtil;
    public static ThreadLocal<UserProFile> userProFileHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String URL = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader("TOKEN");
        Action action = actionService.getActionByAPI(URL, method);
        if (action == null || !action.getIsVisible()) {
            throw new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST);
        } else if (action.getIsPublic()) {
            return true;
        }
        Map<String, Claim> map = jwtUtil.getClaims(token);
        String userId = map.get("uid").asString();//todo 更改token载荷内容
        Permission permission = permissionService.getPermission(userId);
        if (!permissionService.checkPermission(permission, action)) {
            throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
        }
        UserProFile userProFile = getUserProFileById(userId);
        userProFileHolder.set(userProFile);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) {
        userProFileHolder.remove();
    }


    @Cacheable(value = "userProFile", key = "#userId")
    public UserProFile getUserProFileById(String userId) {
        UserProFile userProFile = sastLinkServiceCacheAble.getUserProFile(userId);
        return userProFile;
    }

}
