package sast.evento.aop;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sast.evento.annotation.OperateLog;
import sast.evento.interceptor.LogInterceptor;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@Aspect
@Component
@Slf4j
public class OperateLogAspect {
    private final static Set<String> EXCLUDE_SET;

    static {
        EXCLUDE_SET = new HashSet<>();
        EXCLUDE_SET.add("password");
    }

    @Pointcut("@annotation(sast.evento.annotation.OperateLog)")
    public void operateLog() {
    }

    @Around("operateLog() && @annotation(anno)")
    public Object aroundMethod(ProceedingJoinPoint proceedingPoint, OperateLog anno) throws Throwable {
        Optional.ofNullable(LogInterceptor.logHolder.get()).ifPresent(
                traceLog -> log.info(traceLog
                        .setDescription(anno.value())
                        .setFinishTime(System.currentTimeMillis())
                        .toLogFormat(true))
        );
        return proceedingPoint.proceed();
    }


    @AfterThrowing(pointcut = "operateLog()&&@annotation(anno)", throwing = "exception")
    public void throwHandler(JoinPoint joinPoint, OperateLog anno, Throwable exception) {
        Signature sg = joinPoint.getSignature();
        Field[] declaredFields1 = sg.getDeclaringType().getDeclaredFields();
        // 获取当前抛出异常类使用@Resource注入的bean
        List<String> classList = Arrays.stream(declaredFields1)
                .filter(field -> field.getAnnotation(Resource.class) != null)
                .map(Field::getType)
                .map(Class::getSimpleName).toList();
        HashSet<String> classSet = new HashSet<>(classList);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取当前抛出异常的方法名称和类
        String eleMethodName = signature.getMethod().getName();
        String typeName = signature.getDeclaringTypeName();

        // 获取抛出异常的stackTrace
        StackTraceElement[] stackTrace = exception.getStackTrace();
        // 拿到需要的stackTrace，只拿以下两种 stackTrace
        // 注入的bean和controller方法
        List<StackTraceElement> stackTraceList = Arrays.stream(stackTrace).filter(ele -> {
                    if (eleMethodName.equals(ele.getMethodName()) && typeName.equals(ele.getClassName()))
                        return true;
                    if (ele.getFileName() != null)
                        return classSet.stream().anyMatch(ele.getFileName()::contains);
                    return false;
                }
        ).toList();
        // 格式处理
        ArrayList<String> errMsg = new ArrayList<>();
        for (StackTraceElement ele : stackTraceList) {
            String fileName = ele.getFileName();
            String methodName = ele.getMethodName();
            int lineNumber = ele.getLineNumber();
            String errTrace = fileName + " | " + methodName + " | line:" + lineNumber + " | " + exception.getLocalizedMessage();
            errMsg.add(errTrace);
        }
        Optional.ofNullable(LogInterceptor.logHolder.get()).ifPresent(
                traceLog -> log.error(traceLog
                        .setDescription(anno.value())
                        .setFinishTime(System.currentTimeMillis())
                        .setStackTrace(StringUtils.collectionToCommaDelimitedString(errMsg))
                        .toLogFormat(false))
        );
    }
}
