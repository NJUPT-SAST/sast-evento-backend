package sast.evento.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/20 18:38
 */
@Slf4j
@Component
public class ActionRegister implements BeanFactoryAware, CommandLineRunner {
    public ListableBeanFactory beanFactory;
    public static Map<String, Action> actionName2action = new HashMap<>();
    public static Set<String> actionNameSet;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public void run(String... args) {
        Map<String, Object> beansWithRestController = beanFactory.getBeansWithAnnotation(RestController.class);
        Map<String, Object> beansWithController = beanFactory.getBeansWithAnnotation(Controller.class);
        Set<Class> set = beansWithController.values().stream().map(Object::getClass).collect(Collectors.toSet());
        set.addAll(beansWithRestController.values().stream().map(Object::getClass).collect(Collectors.toSet()));
        for (Class c : set) {
            Method[] declaredMethods = c.getDeclaredMethods();
            for (Method m : declaredMethods) {
                OperateLog ano = AnnotatedElementUtils.findMergedAnnotation(m, OperateLog.class);
                RequestMapping r = AnnotatedElementUtils.findMergedAnnotation(m, RequestMapping.class);
                DefaultActionState d = AnnotatedElementUtils.findMergedAnnotation(m, DefaultActionState.class);
                if (r == null) {
                    continue;
                }
                if (d == null) {
                    if (m.getName().equals("error") || m.getName().equals("errorHtml")) {
                        continue;
                    }
                    throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "run failed,the annotation defaultActionState is needed on Mapping method");
                }
                String methodName = m.getName();
                actionName2action.put(methodName, new Action(m.getName(), d.group(), d.value(), ano.value()));
            }
        }
        actionNameSet = actionName2action.keySet();
        //log.info("Scan of action is over. Final actionName2action map is:{}", actionName2action);
        log.info("Scan of action is over. Final actionName set is:{} total action num:{}", actionNameSet, actionNameSet.size());
    }
}