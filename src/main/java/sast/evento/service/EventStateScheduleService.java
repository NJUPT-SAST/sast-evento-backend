package sast.evento.service;

import java.util.Date;

/**
 * @Author: Love98
 * @Date: 8/26/2023 8:41 PM
 */
public interface EventStateScheduleService {
    /**
     * 添加定时任务：修改EventState
     * @param eventId 活动Id
     * @param startTime 任务开始时间
     * @param state 修改后的活动状态 1：未开始 2：签到中 3：进行中 5：已结束
     */
    void scheduleJob(Integer eventId, Date startTime, Integer state);

    /**
     * 删除定时任务
     * @param eventId 活动Id
     * @param state 修改后的活动状态 1：未开始 2：签到中 3：进行中 5：已结束
     */
    void removeJob(Integer eventId, Integer state);

    void removeJobs(Integer eventId);

    /**
     * 更新定时任务的时间
     * @param eventId 活动Id
     * @param startTime 更新后任务开始时间
     * @param state 修改后的活动状态 1：未开始 2：签到中 3：进行中 5：已结束
     */
    void updateJob(Integer eventId, Date startTime, Integer state);

//    Boolean pauseJob(String jobName);
//
//    Boolean resumeJob(String jobName);
//
//    Boolean triggerJob(String jobName);
//
//    Boolean isJobExist(String jobName);
}
