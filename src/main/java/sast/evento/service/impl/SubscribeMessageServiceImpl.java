package sast.evento.service.impl;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.job.WxSubscribeJob;
import sast.evento.service.SubscribeMessageService;
import sast.evento.utils.SchedulerUtil;

import java.util.Date;

/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 16:27
 */

@Slf4j
@Service
public class SubscribeMessageServiceImpl implements SubscribeMessageService {
    private static final String jobGroupName = "job_wx_subscribe";
    private static final String triggerGroupName = "trigger_wx_subscribe";
    @Getter
    private static Boolean isOpen = true;

    /* 开启任务 */
    public void open() {
        isOpen = true;
    }

    /* 关闭任务 */
    public void close() {
        isOpen = false;
    }

    /* 查看任务是否关闭 */
    @SneakyThrows
    public Boolean isClose() {
        return (!isOpen) || SchedulerUtil.isShutdown();
    }

    /* 添加定时读取并发送活动提醒任务 */
    @SneakyThrows
    public void addWxSubScribeJob(Integer eventId, Date startTime) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR,"Wx subscribe message service is close.");
        }
        String cron = SchedulerUtil.simpleDateFormat.format(startTime);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("eventId", eventId);
        String stringEventId = String.valueOf(eventId);
        SchedulerUtil.addJob(stringEventId, jobGroupName, stringEventId, triggerGroupName, WxSubscribeJob.class, jobDataMap, cron);
    }

    /* 更新任务时间 */
    @SneakyThrows
    public void updateWxSubScribeJob(Integer eventId, Date startTime) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR,"Wx subscribe message service is close.");
        }
        String cron = SchedulerUtil.simpleDateFormat.format(startTime);
        SchedulerUtil.resetJobCron(String.valueOf(eventId),triggerGroupName, cron);
    }

    /* 删除任务 */
    @SneakyThrows
    public void removeWxSubScribeJob(Integer eventId) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR,"Wx subscribe message service is close.");
        }
        String stringEventId = String.valueOf(eventId);
        SchedulerUtil.removeJob(stringEventId,jobGroupName,stringEventId,triggerGroupName);
    }

}
