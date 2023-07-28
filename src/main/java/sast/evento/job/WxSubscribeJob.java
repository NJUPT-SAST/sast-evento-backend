package sast.evento.job;


import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.client.RestTemplate;
import sast.evento.mapper.ParticipateMapper;
import sast.evento.service.impl.SubscribeMessageServiceImpl;
import sast.evento.utils.SpringContextUtil;


/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 22:11
 */

@Slf4j
public class WxSubscribeJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if(SubscribeMessageServiceImpl.getIsOpen()){
            log.info("Wx subscribe task start.");

            Integer eventId = jobExecutionContext.getMergedJobDataMap().getInt("eventId");
            ParticipateMapper participateMapperBean =  SpringContextUtil.getBean(ParticipateMapper.class);
            RestTemplate restTemplateBean = SpringContextUtil.getBean(RestTemplate.class);
            //todo 发送微信订阅任务




            log.info("Wx subscribe task end.");


        }
    }
}

