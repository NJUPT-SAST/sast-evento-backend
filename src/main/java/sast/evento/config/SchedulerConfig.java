package sast.evento.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.utils.SchedulerUtil;

/**
 * @Author: Love98
 * @Date: 9/9/2023 7:11 PM
 */
@Slf4j
@Configuration
public class SchedulerConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            SchedulerUtil.startScheduler();
        } catch (SchedulerException e) {
            throw new LocalRunTimeException(ErrorEnum.SCHEDULER_ERROR,"error start");
        }
    }
}
