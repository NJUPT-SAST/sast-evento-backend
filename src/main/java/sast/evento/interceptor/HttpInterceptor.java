package sast.evento.interceptor;

import com.auth0.jwt.interfaces.Claim;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import sast.evento.annotation.EventId;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.service.ActionService;
import sast.evento.service.LoginService;
import sast.evento.service.PermissionService;
import sast.evento.utils.JwtUtil;
import sast.sastlink.sdk.model.UserInfo;

import java.lang.reflect.Method;
import java.util.Arrays;
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
    public static ThreadLocal<User> userHolder = new ThreadLocal<>();

    @Resource
    private ActionService actionService;
    @Resource
    private LoginService loginService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        String token = request.getHeader("TOKEN");
        if (method.getName().equals("error")) throw new LocalRunTimeException(ErrorEnum.INTERNAL_SERVER_ERROR);
        Action action = Optional.ofNullable(actionService.getAction(method.getName()))
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST, "unsupported service"));
        String userId = null;
        switch (action.getActionState()) {
            /* 鉴权状态可拓展 */
            case INVISIBLE -> throw new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST, "method inVisible");
            case PUBLIC -> {
                return true;
            }
            case LOGIN -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
                loginService.checkLoginState(userId, token);
            }
            case MANAGER -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
                loginService.checkLoginState(userId, token);
                EventId eventAnno = Arrays.stream(Optional.ofNullable(method.getParameters()).orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "eventId param is needed")))
                        .filter(param -> AnnotatedElementUtils.hasAnnotation(param, EventId.class))
                        .findAny()
                        .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "annotation EventId on requestParam is needed"))
                        .getAnnotation(EventId.class);
                int eventId;
                try {
                    String stringEventId = Optional.ofNullable(request.getParameter(eventAnno.name()))
                            .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "eventId in requestParam should not be null"));
                    eventId = Integer.parseInt(stringEventId);
                } catch (NumberFormatException e) {
                    throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid eventId");
                }
                if (!permissionService.checkPermission(userId, eventId, action.getMethodName())) {
                    throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
                }
            }
            case ADMIN -> {
                Map<String, Claim> map = jwtUtil.getClaims(token);
                userId = map.get("user_id").asString();
                loginService.checkLoginState(userId, token);
                if (!permissionService.checkPermission(userId, 0, action.getMethodName())) {
                    throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
                }
            }
        }
        UserInfo userInfo = loginService.getUserInfo(userId);
        userHolder.set(new User(userInfo.getUserId(), userInfo.getWechatId(), userInfo.getEmail()));
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, @Nullable Exception ex) {
        userHolder.remove();
    }
}
