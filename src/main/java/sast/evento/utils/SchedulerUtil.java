package sast.evento.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 22:35
 */
@Slf4j
@Component
public class SchedulerUtil {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
    private static final StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static final Map<String, JobKey> jobName2jobKey = new HashMap<>();
    private static final Map<String, TriggerKey> jobName2TriggerKey = new HashMap<>();


    private static Scheduler getScheduler() throws SchedulerException{
        return schedulerFactory.getScheduler();
    }


    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, String cron) throws SchedulerException{
        Scheduler scheduler = getScheduler();
        if (scheduler.isShutdown()) {
            throw new RuntimeException("Please contact admin to start the scheduler first.");
        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName)
                .setJobData(jobDataMap)
                .build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        jobName2jobKey.put(jobName, jobDetail.getKey());
        jobName2TriggerKey.put(jobName, trigger.getKey());
    }


    public static void resetJobCron(String jobName, String cron) throws Exception{
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = getTriggerKey(jobName);
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        if (!trigger.getCronExpression().equalsIgnoreCase(cron)) {
            trigger.setCronExpression(cron);
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }


    public static void removeJob(String jobName) throws SchedulerException{
        Scheduler scheduler = getScheduler();
        scheduler.pauseTrigger(getTriggerKey(jobName));
        scheduler.unscheduleJob(getTriggerKey(jobName));
        scheduler.deleteJob(getJobKey(jobName));
        jobName2jobKey.remove(jobName);
        jobName2TriggerKey.remove(jobName);
    }


    public static void shutdownScheduler() throws SchedulerException{
        Scheduler scheduler = getScheduler();
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            log.info("Scheduler shut down.");
        } else {
            log.info("Scheduler has shut down.");
        }
    }


    public static void startScheduler() throws SchedulerException{
        Scheduler scheduler = getScheduler();
        if (scheduler.isShutdown()) {
            scheduler.start();
            log.info("Scheduler start.");
        } else {
            log.info("Scheduler has start.");
        }
    }


    public static Boolean isShutdown()throws SchedulerException {
        return getScheduler().isShutdown();
    }

    public static TriggerKey getTriggerKey(String jobName)throws RuntimeException{
        if (!jobName2TriggerKey.containsKey(jobName)) {
            throw new RuntimeException("Trigger not exist in jobName2TriggerKeyMap.");
        }
        return jobName2TriggerKey.get(jobName);
    }

    public static JobKey getJobKey(String jobName) throws RuntimeException{
        if (!jobName2jobKey.containsKey(jobName)) {
            throw new RuntimeException("Job not exist in jobName2jobKeyMap.");
        }
        return jobName2jobKey.get(jobName);
    }

    public static Set<String> getAllName() {
        return jobName2jobKey.keySet();
    }

}