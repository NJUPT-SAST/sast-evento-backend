package sast.evento.service.impl;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.job.EventStateUpdateJob;
import sast.evento.service.EventStateScheduleService;
import sast.evento.utils.SchedulerService;

import java.util.Date;

/**
 * @Author: Love98
 * @Date: 8/26/2023 8:29 PM
 */
@Slf4j
@Service
public class EventStateScheduleServiceImpl implements EventStateScheduleService {

    @Resource
    private SchedulerService schedulerService;
    private static final String notStartStateJobGroupName = "update_not_start_state_job_group";
    private static final String checkingInStateJobGroupName = "update_checking_in_state_job_group";
    private static final String inProgressStateJobGroupName = "update_in_process_state_job_group";
    private static final String endedStateJobGroupName = "update_ended_state_job_group";

    private static final String notStartStateTriggerGroupName = "update_not_start_state_trigger_group";
    private static final String checkingInStateTriggerGroupName = "update_checking_in_state_trigger_group";
    private static final String inProgressStateTriggerGroupName = "update_in_process_state_trigger_group";
    private static final String endedStateTriggerGroupName = "update_ended_state_trigger_group";

    @SneakyThrows
    public void scheduleJob(Integer eventId, Date startTime, Integer state) {
        log.info("scheduleJob: eventId: {}, startTime: {}, state: {}", eventId, startTime, state);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("eventId", eventId);
        String stringEventId = String.valueOf(eventId);
        jobDataMap.put("state", state);
        switch (state) {
            case 1 ->
                    schedulerService.addJob(stringEventId, notStartStateJobGroupName, stringEventId, notStartStateTriggerGroupName, EventStateUpdateJob.class, jobDataMap, startTime);
            case 2 ->
                    schedulerService.addJob(stringEventId, checkingInStateJobGroupName, stringEventId, checkingInStateTriggerGroupName, EventStateUpdateJob.class, jobDataMap, startTime);
            case 3 ->
                    schedulerService.addJob(stringEventId, inProgressStateJobGroupName, stringEventId, inProgressStateTriggerGroupName, EventStateUpdateJob.class, jobDataMap, startTime);
            case 5 ->
                    schedulerService.addJob(stringEventId, endedStateJobGroupName, stringEventId, endedStateTriggerGroupName, EventStateUpdateJob.class, jobDataMap, startTime);
            default -> throw new LocalRunTimeException(ErrorEnum.SCHEDULER_ERROR);
        }
    }

    @SneakyThrows
    public void removeJob(Integer eventId, Integer state) {
        String stringEventId = String.valueOf(eventId);
        switch (state) {
            case 1 ->
                    schedulerService.removeJob(stringEventId, notStartStateJobGroupName, stringEventId, notStartStateTriggerGroupName);
            case 2 ->
                    schedulerService.removeJob(stringEventId, checkingInStateJobGroupName, stringEventId, checkingInStateTriggerGroupName);
            case 3 ->
                    schedulerService.removeJob(stringEventId, inProgressStateJobGroupName, stringEventId, inProgressStateTriggerGroupName);
            case 5 ->
                    schedulerService.removeJob(stringEventId, endedStateJobGroupName, stringEventId, endedStateTriggerGroupName);
            default -> throw new LocalRunTimeException(ErrorEnum.SCHEDULER_ERROR);
        }
    }

    @SneakyThrows
    public void removeJobs(Integer eventId) {
        String stringEventId = String.valueOf(eventId);
        schedulerService.removeJob(stringEventId, notStartStateJobGroupName, stringEventId, notStartStateTriggerGroupName);
        schedulerService.removeJob(stringEventId, checkingInStateJobGroupName, stringEventId, checkingInStateTriggerGroupName);
        schedulerService.removeJob(stringEventId, inProgressStateJobGroupName, stringEventId, inProgressStateTriggerGroupName);
        schedulerService.removeJob(stringEventId, endedStateJobGroupName, stringEventId, endedStateTriggerGroupName);
    }

    @SneakyThrows
    public void updateJob(Integer eventId, Date startTime, Integer state) {
        String stringEventId = String.valueOf(eventId);
        if (!switch (state) {
            case 1 -> schedulerService.resetJobTrigger(stringEventId, notStartStateTriggerGroupName, startTime);
            case 2 -> schedulerService.resetJobTrigger(stringEventId, checkingInStateTriggerGroupName, startTime);
            case 3 -> schedulerService.resetJobTrigger(stringEventId, inProgressStateTriggerGroupName, startTime);
            case 5 -> schedulerService.resetJobTrigger(stringEventId, endedStateTriggerGroupName, startTime);
            default -> throw new LocalRunTimeException(ErrorEnum.SCHEDULER_ERROR);
        }) {
            scheduleJob(eventId, startTime, state);
        }
    }

}
