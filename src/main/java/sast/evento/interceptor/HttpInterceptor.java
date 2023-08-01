package sast.evento.interceptor;

import com.auth0.jwt.interfaces.Claim;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import sast.evento.annotation.EventId;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.model.UserProFile;
import sast.evento.service.ActionService;
import sast.evento.service.PermissionService;
import sast.evento.service.SastLinkServiceCacheAble;
import sast.evento.utils.JwtUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;


/**
 * @Title HttpInterceptor
 * @Description 获取访问信息, 获取API限制信息, 获取用户信息*,根据请求从数据库获取用户权限*,
 * @Description 解析TOKEN信息获取本地用户登录信息, 与API信息联合判断是否拦截
 * @Author feelMoose
 * @Date 2023/7/14 16:27
 */
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
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        String token = request.getHeader("TOKEN");
        Action action = Optional.ofNullable(actionService.getAction(method.getName()))
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST, "method not exist."));
        String userId = null;
        switch (action.getActionState()) {
            /* 鉴权状态可拓展 */
            case INVISIBLE -> throw new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST, "method inVisible.");
            case PUBLIC -> {
                return true;
            }
            case LOGIN -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
            }
            case MANAGER -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
                EventId eventAnno = Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(method, EventId.class))
                        .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "annotation EventId on requestParam is needed."));
                String eventId = Optional.ofNullable(request.getParameter(eventAnno.name()))
                        .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "eventId in requestParam should not be null."));
                if (!permissionService.checkPermission(userId, Integer.parseInt(eventId), action.getMethodName())) {
                    throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
                }
            }
            case ADMIN -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
                if (!permissionService.checkPermission(userId, 0, action.getMethodName())) {
                    throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
                }
            }
        }
        UserProFile userProFile = sastLinkServiceCacheAble.getUserProFile(userId);//todo 等待对接sastLink
        userProFileHolder.set(userProFile);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) {
        userProFileHolder.remove();
    }
}
