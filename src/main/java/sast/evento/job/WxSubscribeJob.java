package sast.evento.job;


import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.UserDepartmentSubscribe;
import sast.evento.mapper.ParticipateMapper;
import sast.evento.model.wxServiceDTO.AccessTokenResponse;
import sast.evento.service.DepartmentService;
import sast.evento.service.EventService;
import sast.evento.service.WxService;
import sast.evento.service.impl.SubscribeMessageServiceImpl;
import sast.evento.service.impl.WxServiceImpl;
import sast.evento.utils.SpringContextUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 22:11
 */

// 定时任务
@Slf4j
public class WxSubscribeJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (SubscribeMessageServiceImpl.getIsOpen()) {
            log.info("Wx subscribe task start");

            Integer eventId = jobExecutionContext.getMergedJobDataMap().getInt("eventId");
            DepartmentService departmentService = SpringContextUtil.getBean(DepartmentService.class);
            ParticipateMapper participateMapperBean = SpringContextUtil.getBean(ParticipateMapper.class);
            EventService eventService = SpringContextUtil.getBean(EventService.class);
            WxService wxService = SpringContextUtil.getBean(WxServiceImpl.class);

            Set<String> openIds = new HashSet<>(participateMapperBean.selectSubscribeOpenIds(eventId));
            List<Integer> departmentIds = eventService.getEvent(eventId).getDepartments().stream()
                    .map(Department::getId).toList();
            Set<String> subscribeDepartmentOpenIds = departmentService.getSubscribeDepartmentUser(departmentIds)
                    .stream().map(UserDepartmentSubscribe::getOpenId).collect(Collectors.toSet());
            openIds.addAll(subscribeDepartmentOpenIds);

            /* 任意时刻发起调用获取到的 access_token 有效期至少为 5 分钟 */
            AccessTokenResponse accessTokenResponse = wxService.getStableToken();
            long date = System.currentTimeMillis() + accessTokenResponse.getExpires_in();
            for (String openId : openIds) {
                if (System.currentTimeMillis() > date) {
                    accessTokenResponse = wxService.getStableToken();
                    date = System.currentTimeMillis() + accessTokenResponse.getExpires_in();
                }
                wxService.seedSubscribeMessage(eventId, accessTokenResponse.getAccess_token(), openId);
            }

            log.info("Wx subscribe task end");


        }
    }
}

