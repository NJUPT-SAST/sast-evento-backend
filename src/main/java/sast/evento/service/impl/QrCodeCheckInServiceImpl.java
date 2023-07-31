package sast.evento.service.impl;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sast.evento.job.CodeRefreshJob;
import sast.evento.job.CodeRefreshTriggerListener;
import sast.evento.service.CodeService;
import sast.evento.service.QrCodeCheckInService;
import sast.evento.utils.SchedulerUtil;

import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/29 14:50
 */
@Service
public class QrCodeCheckInServiceImpl implements QrCodeCheckInService {
    private static final String jobGroupName = "job_qr_code_registration";
    private static final String triggerGroupName = "trigger_qr_code_registration";

    @Value("${evento.QrCode.duration}")
    private long duration;
    @Value("${evento.QrCode.refreshCron}")
    private String refreshCron;

    @Resource
    CodeService codeService;

    /* 手动关闭任务 */
    @Override
    @SneakyThrows
    public void close(Integer eventId) {
        String stringEventId = String.valueOf(eventId);
        SchedulerUtil.removeJob(stringEventId,jobGroupName,stringEventId,triggerGroupName);
        SchedulerUtil.removeTriggerListener(stringEventId,stringEventId,triggerGroupName);
        codeService.deleteCode(eventId);
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
    public BufferedImage getCheckInQrCode(Integer eventId) {
        /* 访问自动开启服务:访问自动开启(服务开启条件状态下) */
        JobKey jobKey = new JobKey(String.valueOf(eventId),jobGroupName);
        String stringEventId = String.valueOf(eventId);
        if(!SchedulerUtil.getScheduler().checkExists(jobKey)){
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("eventId",eventId);
            codeService.refreshCode(eventId);
            SchedulerUtil.addRepeatJob(stringEventId,jobGroupName,stringEventId,triggerGroupName, CodeRefreshJob.class,jobDataMap,refreshCron,new Date(),new Date(System.currentTimeMillis()+duration));
            SchedulerUtil.addTriggerListener(stringEventId,triggerGroupName,new CodeRefreshTriggerListener(eventId));
        }else {
            SchedulerUtil.resetRepeatJob(stringEventId,triggerGroupName,null,null,new Date(System.currentTimeMillis()+duration));
        }
        return codeService.getQrCode(eventId);
    }

    @Override
    @SneakyThrows
    public Boolean checkCode(Integer eventId,String registrationCode) {
        return codeService.getCode(eventId).equals(registrationCode);
    }

}
