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
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.model.UserProFile;
import sast.evento.service.ActionService;
import sast.evento.service.PermissionService;
import sast.evento.service.impl.SastLinkServiceCacheAble;
import sast.evento.utils.JwtUtil;

import java.lang.reflect.Method;
import java.util.Map;


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
        if(handler instanceof ResourceHttpRequestHandler){
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        String token = request.getHeader("TOKEN");
        Action action = actionService.getAction(method.getName());
        if (action == null || !action.getIsVisible()) {
            throw new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST);
        } else if (action.getIsPublic()) {
            return true;
        }
        Map<String, Claim> map = jwtUtil.getClaims(token);
        String userId = map.get("user_id").asString();
        EventId eventAnno = AnnotatedElementUtils.findMergedAnnotation(method, EventId.class);
        if (eventAnno != null) {
            String eventId = request.getParameter(eventAnno.name());
            if (eventId == null) {
                throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "EventId in requestParam should not be null.");
            }
            if (!permissionService.checkPermission(userId, Integer.parseInt(eventId), action.getMethodName())) {
                throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
            }
        } else {
            if (!permissionService.checkPermission(userId, 0, action.getMethodName())) {
                throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
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
