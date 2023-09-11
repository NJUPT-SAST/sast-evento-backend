package sast.evento;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.stereotype.Component;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.DepartmentMapper;
import sast.evento.mapper.SubscribeDepartmentMapper;
import sast.evento.job.EventStateUpdateJob;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.SemiTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.service.CodeService;
import sast.evento.service.EventStateScheduleService;
import sast.evento.service.LocationService;
import sast.evento.service.LoginService;

import sast.evento.utils.*;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
import sast.sastlink.sdk.model.response.RefreshResponse;
import sast.sastlink.sdk.service.SastLinkService;
import sast.sastlink.sdk.service.impl.RestTemplateSastLinkService;
import sast.evento.utils.*;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static sast.sastlink.sdk.enums.GrantType.REFRESH_TOKEN;
import static sast.sastlink.sdk.enums.SastLinkApi.ACCESS_TOKEN;

@SpringBootTest
class SastEventoBackendApplicationTests {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LocationService locationService;
    @Resource
    private RestTemplateSastLinkService sastLinkServiceWeb;

    @Resource
    private SubscribeDepartmentMapper subscribeDepartmentMapper;

    @Resource
    private EventStateScheduleService eventStateScheduleService;

    @SneakyThrows
    @Test
    void eventSSTest() {
        String dateStr = "2023-09-05 22:25:59";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SchedulerUtil.startScheduler();
            eventStateScheduleService.scheduleJob(47, dateFormat.parse(dateStr), 2);
            Thread.sleep(1000);
            eventStateScheduleService.scheduleJob(46, dateFormat.parse(dateStr), 3);
            Thread.sleep(60000);
        } catch (ParseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void wxSubscribe() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key1", "");
        dataMap.put("key2", String.valueOf(111));
        dataMap.put("key3", String.valueOf(111));
        WxSubscribeRequest wxSubscribeRequest = new WxSubscribeRequest();
        wxSubscribeRequest.setData(WxSubscribeRequest.getData(dataMap));
        System.out.println(JsonUtil.toJson(wxSubscribeRequest));
        System.out.println(JsonUtil.toJson(new AccessTokenRequest()));
    }

    @Test
    void RedisTest() {
        System.out.println(JsonUtil.toJson(locationService.getLocations()));
    }

    @Test
    void SastLinkTest() {
        String token = sastLinkServiceWeb.login("","");
        String code = sastLinkServiceWeb.authorize(token,"","");
        System.out.println(code);




    }
    @Test
    void subscribeDepartmentMapperTest() {
        System.out.println(subscribeDepartmentMapper.selectSubscribeDepartmentUser(List.of(1, 10, 2, 3, 4)));
    }
    void combineTest() throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + 5000);
        JobDataMap jobDataMapForUtil = new JobDataMap();
        jobDataMapForUtil.put("eventId", 56);
        jobDataMapForUtil.put("state", 3);
//        SchedulerUtil.startScheduler();
        SchedulerUtil.addOneTimeJob("44", "update_not_start_state_job_group", "44", "update_not_start_state_trigger_group", EventStateUpdateJob.class, jobDataMapForUtil, date);
        JobDataMap jobDataMapForDirect = new JobDataMap();
        jobDataMapForDirect.put("eventId", 57);
        jobDataMapForDirect.put("state", 2);
        System.out.println("Util HashCode: " + SchedulerUtil.getScheduler().hashCode());
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        System.out.println("Direct HashCode: " + scheduler.hashCode());
        scheduler.start();
//        Scheduler scheduler = SchedulerUtil.getScheduler();
//        scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(EventStateUpdateJob.class)
                .withIdentity("37", "update_not_start_state_job_group")
                .setJobData(jobDataMapForDirect)
                .build();
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("37", "update_not_start_state_trigger_group")
                .startAt(date)
                .build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
