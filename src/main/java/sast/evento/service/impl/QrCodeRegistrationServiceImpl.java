package sast.evento.service.impl;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.springframework.stereotype.Service;
import sast.evento.job.CodeRefreshJob;
import sast.evento.job.CodeRefreshTriggerListener;
import sast.evento.service.BaseRegistrationService;
import sast.evento.service.QrCodeRegistrationService;
import sast.evento.utils.SchedulerUtil;

import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/29 14:50
 */
@Service
public class QrCodeRegistrationServiceImpl implements QrCodeRegistrationService {
    private static final String jobGroupName = "job_qr_code_registration";
    private static final String triggerGroupName = "trigger_qr_code_registration";
    private static final long duration = 60000;
    private static final String refreshCron = "0 0/1 * * * ? *";//每分钟更新

    @Resource
    BaseRegistrationService baseRegistrationService;

    /* 手动关闭任务 */
    @Override
    @SneakyThrows
    public void close(Integer eventId) {
        String stringEventId = String.valueOf(eventId);
        SchedulerUtil.removeJob(stringEventId,jobGroupName,stringEventId,triggerGroupName);
        baseRegistrationService.deleteCode(eventId);
    }

    /* 查看任务是否关闭 */
    @Override
    @SneakyThrows
    public Boolean isClose(Integer eventId) {
        JobKey jobKey = new JobKey(String.valueOf(eventId),jobGroupName);
        return SchedulerUtil.isShutdown() || !SchedulerUtil.getScheduler().checkExists(jobKey);
    }

    @Override
    @SneakyThrows
    public BufferedImage getRegistrationQrCode(Integer eventId) {
        /* 访问自动开启服务:访问自动开启(服务开启条件状态下) */
        JobKey jobKey = new JobKey(String.valueOf(eventId),jobGroupName);
        String stringEventId = String.valueOf(eventId);
        if(!SchedulerUtil.getScheduler().checkExists(jobKey)){
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("eventId",eventId);
            baseRegistrationService.refreshCode(eventId);
            SchedulerUtil.addRepeatJob(stringEventId,jobGroupName,stringEventId,triggerGroupName, CodeRefreshJob.class,jobDataMap,refreshCron,new Date(),new Date(System.currentTimeMillis()+duration));
            SchedulerUtil.addTriggerListener(stringEventId,triggerGroupName,new CodeRefreshTriggerListener(eventId));
        }else {
            SchedulerUtil.resetRepeatJob(stringEventId,triggerGroupName,null,null,new Date(System.currentTimeMillis()+duration));
        }
        return baseRegistrationService.getQrCode(eventId);
    }

    @Override
    @SneakyThrows
    public Boolean checkRegistrationCode(Integer eventId,String registrationCode) {
        return baseRegistrationService.getCode(eventId).equals(registrationCode);
    }

}
