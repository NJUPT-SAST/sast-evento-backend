package sast.evento.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import sast.evento.annotation.OperateLog;
import sast.evento.config.ActionRegister;
import sast.evento.model.TraceLog;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Title LogInterceptor
 * @Description 为该请求线程配置日志
 * @Author feelMoose
 * @Date 2023/7/14 16:20
 */

@Component
public class LogInterceptor implements HandlerInterceptor {
    public static ThreadLocal<TraceLog> logHolder = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        MDC.put("TRACE_ID", UUID.randomUUID().toString());
        TraceLog preTraceLog = new TraceLog();
        preTraceLog.setUri(request.getRequestURI());
        preTraceLog.setMethod(request.getMethod());
        preTraceLog.setDescription(ActionRegister.actionName2action.get(method.getName()).getDescription());
        preTraceLog.setStartTime(System.currentTimeMillis());
        logHolder.set(preTraceLog);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) {
        logHolder.remove();
        MDC.clear();
    }
}
