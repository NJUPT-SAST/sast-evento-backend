package sast.evento.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import sast.evento.service.BaseRegistrationService;
import sast.evento.utils.SpringContextUtil;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/29 16:15
 */
@Slf4j
public class CodeRefreshJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Integer eventId = jobExecutionContext.getMergedJobDataMap().getInt("eventId");
        BaseRegistrationService baseRegistrationServiceBean = SpringContextUtil.getBean(BaseRegistrationService.class);
        baseRegistrationServiceBean.refreshCode(eventId);
        log.info("refresh Qrcode. EvenId: {}",eventId);
    }
}
