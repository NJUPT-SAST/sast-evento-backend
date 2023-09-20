package sast.evento;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import sast.evento.mapper.SubscribeDepartmentMapper;
import sast.evento.job.EventStateUpdateJob;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.service.EventStateScheduleService;
import sast.evento.service.LocationService;
import sast.evento.service.LoginService;
import sast.evento.service.QrCodeCheckInService;
import sast.evento.utils.*;
import sast.sastlink.sdk.service.impl.RestTemplateSastLinkService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class SastEventoBackendApplicationTests {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LocationService locationService;
    @Resource
    private RestTemplateSastLinkService sastLinkService;

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
    void cronTest() {
        String dateStr = "2023-09-04 12:16:10";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            System.out.println(SchedulerUtil.simpleDateFormat.format(dateFormat.parse(dateStr)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void oneTimeJobTest() throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + 5000);
        JobDataMap jobDataMapForUtil = new JobDataMap();
        jobDataMapForUtil.put("eventId", 44);
        jobDataMapForUtil.put("state", 3);
        SchedulerUtil.startScheduler();
        SchedulerUtil.addJob("44", "update_not_start_state_job_group", "44", "update_not_start_state_trigger_group", EventStateUpdateJob.class, jobDataMapForUtil, date);
        try {
            Thread.sleep(7000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    void oneTimeJobDirectAddingTest() throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + 5000);
        JobDataMap jobDataMapForDirect = new JobDataMap();
        jobDataMapForDirect.put("eventId", 37);
        jobDataMapForDirect.put("state", 2);
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
//        Scheduler scheduler = SchedulerUtil.getScheduler();
//        scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(EventStateUpdateJob.class)
                .withIdentity("44", "update_not_start_state_job_group")
                .setJobData(jobDataMapForDirect)
                .build();
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("44", "update_not_start_state_trigger_group")
                .startAt(date)
                .build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void RedisTest() {
        System.out.println(JsonUtil.toJson(locationService.getLocations()));
    }

    @Test
    void SastLinkTest() {
        sastLinkService.login("","");
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
        SchedulerUtil.addJob("44", "update_not_start_state_job_group", "44", "update_not_start_state_trigger_group", EventStateUpdateJob.class, jobDataMapForUtil, date);
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
