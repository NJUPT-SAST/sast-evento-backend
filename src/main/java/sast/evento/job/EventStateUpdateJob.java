package sast.evento.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import sast.evento.common.enums.EventState;
import sast.evento.entitiy.Event;
import sast.evento.service.EventService;
import sast.evento.service.impl.EventServiceImpl;
import sast.evento.utils.SpringContextUtil;

/**
 * @Author: Love98
 * @Date: 8/26/2023 8:57 PM
 */
@Slf4j
public class EventStateUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Event state update task start");

        Integer eventId = jobExecutionContext.getMergedJobDataMap().getInt("eventId");
        int state = jobExecutionContext.getMergedJobDataMap().getInt("state");

        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.getEventState(state));
        EventService eventService = SpringContextUtil.getBean(EventServiceImpl.class);
        eventService.updateEvent(event);

        log.info("Event state update task end");
    }
}
