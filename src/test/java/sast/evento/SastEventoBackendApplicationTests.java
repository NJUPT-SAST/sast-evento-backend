package sast.evento;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import sast.evento.job.EventStateUpdateJob;
import sast.evento.mapper.SubscribeDepartmentMapper;
import sast.evento.service.EventStateScheduleService;
import sast.evento.service.LocationService;
import sast.evento.service.LoginService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.RedisUtil;
import sast.evento.utils.SchedulerService;
import sast.sastlink.sdk.service.SastLinkService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static sast.evento.utils.SchedulerService.simpleDateFormatPattern;

@SpringBootTest
class SastEventoBackendApplicationTests {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SchedulerService schedulerService;

    @Resource
    private LocationService locationService;
    @Resource
    private SastLinkService sastLinkService;

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
            schedulerService.startScheduler();
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
            System.out.println(new SimpleDateFormat(simpleDateFormatPattern).format(dateFormat.parse(dateStr)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void oneTimeJobTest() throws SchedulerException {
        for (int i = 2; i < 43; i++) {
            Date date = new Date();
            date.setTime(date.getTime() + 5000000);
            JobDataMap jobDataMapForUtil = new JobDataMap();
            jobDataMapForUtil.put("eventId", i);
            jobDataMapForUtil.put("state", 3);
            schedulerService.startScheduler();
            schedulerService.addJob(String.valueOf(i), "update_not_start_state_job_group", String.valueOf(i), "update_not_start_state_trigger_group", EventStateUpdateJob.class, jobDataMapForUtil, date);
        }
        try {
            Thread.sleep(70000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    void oneTimeJobDirectAddingTest() throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + 50000);
        JobDataMap jobDataMapForDirect = new JobDataMap();
        jobDataMapForDirect.put("eventId", 37);
        jobDataMapForDirect.put("state", 2);
//        Scheduler scheduler = schedulerService.getScheduler();
//        scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(EventStateUpdateJob.class)
                .withIdentity("44", "update_not_start_state_job_group")
                .setJobData(jobDataMapForDirect)
                .build();
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("44", "update_not_start_state_trigger_group")
                .startAt(date)
                .build();
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
//        schedulerService.startScheduler();
        schedulerService.addJob("44", "update_not_start_state_job_group", "44", "update_not_start_state_trigger_group", EventStateUpdateJob.class, jobDataMapForUtil, date);
        JobDataMap jobDataMapForDirect = new JobDataMap();
        jobDataMapForDirect.put("eventId", 57);
        jobDataMapForDirect.put("state", 2);
        System.out.println("Util HashCode: " + schedulerService.getScheduler().hashCode());
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        System.out.println("Direct HashCode: " + scheduler.hashCode());
        scheduler.start();
//        Scheduler scheduler = schedulerService.getScheduler();
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
