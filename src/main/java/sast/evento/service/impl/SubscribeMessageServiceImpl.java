package sast.evento.service.impl;


import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.job.WxSubscribeJob;
import sast.evento.service.SubscribeMessageService;
import sast.evento.utils.SchedulerService;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sast.evento.utils.SchedulerService.simpleDateFormatPattern;

/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 16:27
 */

@Slf4j
@Service
public class SubscribeMessageServiceImpl implements SubscribeMessageService {
    /* 可以定时发送消息的微信订阅消息服务 */

    /* 流程如下：
     * 创建活动时创建定时任务
     * 修改活动时修改定时任务时间
     * 取消活动或删除活动时删除定时任务
     *
     * 定时任务会在startTime开始读取数据库中订阅某活动的用户（只允许从微信订阅,一定有openId）
     * 并获取微信stable_access_token,根据模板id和一定的格式发送消息
     *
     * 可以选择是否开启这一功能
     */
    private static final String jobGroupName = "job_wx_subscribe";
    private static final String triggerGroupName = "trigger_wx_subscribe";
    @Getter
    private static Boolean isOpen = true;
    @Resource
    private SchedulerService schedulerService;

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
        return (!isOpen) || schedulerService.isShutdown();
    }

    /* 添加定时读取并发送活动提醒任务 */
    @SneakyThrows
    public void addWxSubScribeJob(Integer eventId, Date startTime) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR, "Wx subscribe message service is close");
        }
        String cron = new SimpleDateFormat(simpleDateFormatPattern).format(startTime);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("eventId", eventId);
        String stringEventId = String.valueOf(eventId);
        schedulerService.addJob(stringEventId, jobGroupName, stringEventId, triggerGroupName, WxSubscribeJob.class, jobDataMap, cron);
    }

    /* 更新任务时间 */
    @SneakyThrows
    public void updateWxSubScribeJob(Integer eventId, Date startTime) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR, "Wx subscribe message service is close");
        }
        String cron = new SimpleDateFormat(simpleDateFormatPattern).format(startTime);
        if (!schedulerService.resetJobTrigger(String.valueOf(eventId), triggerGroupName, cron)) {
            addWxSubScribeJob(eventId, startTime);
        }
    }

    /* 删除任务 */
    @SneakyThrows
    public void removeWxSubScribeJob(Integer eventId) {
        if (isClose()) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR, "Wx subscribe message service is close.");
        }
        String stringEventId = String.valueOf(eventId);
        schedulerService.removeJob(stringEventId, jobGroupName, stringEventId, triggerGroupName);
    }

}
