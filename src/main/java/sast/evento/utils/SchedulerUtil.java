package sast.evento.utils;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 22:35
 */
@Slf4j
public class SchedulerUtil {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
    private static final StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();

    public static Scheduler getScheduler() throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    static {
        try {
            getScheduler().start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, String cron) throws SchedulerException {
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
    }

    public static void addRepeatJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, String cron, Date start, Date end) throws SchedulerException {
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
                .startAt(start)
                .endAt(end)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static void resetJobCron(String triggerName, String triggerGroupName, String cron) throws Exception {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        if (!trigger.getCronExpression().equalsIgnoreCase(cron)) {
            trigger.setCronExpression(cron);
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    public static void resetRepeatJob(String triggerName, String triggerGroupName, String cron, Date start, Date end) throws Exception {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        if (!trigger.getCronExpression().equalsIgnoreCase(cron)) {
            trigger.setCronExpression(cron);
        }
        if (start != null) {
            trigger.setStartTime(start);
        }
        if (end != null) {
            trigger.setEndTime(end);
        }
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    public static void addJobListener(String jobName, String jobGroup, JobListener listener) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
        scheduler.getListenerManager().addJobListener(listener, matcher);
    }

    public static void removeJobListener(String name, String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
        Scheduler scheduler = getScheduler();
        scheduler.getListenerManager().removeJobListenerMatcher(name, matcher);
    }

    public static void addTriggerListener(String triggerName, String triggerGroup, TriggerListener listener) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        Matcher<TriggerKey> matcher = KeyMatcher.keyEquals(triggerKey);
        scheduler.getListenerManager().addTriggerListener(listener, matcher);
    }

    public static void removeTriggerListener(String name, String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        Matcher<TriggerKey> matcher = KeyMatcher.keyEquals(triggerKey);
        Scheduler scheduler = getScheduler();
        scheduler.getListenerManager().removeTriggerListenerMatcher(name, matcher);
    }

    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (!isJobExist(jobName, jobGroupName, triggerName, triggerGroupName)) {
            log.info("Job: {} is not exist.", jobName);
            return;
        }
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(new JobKey(jobName, jobGroupName));
    }

    public static Boolean isJobExist(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        return scheduler.checkExists(new JobKey(jobName, jobGroupName)) && scheduler.checkExists(new TriggerKey(triggerName, triggerGroupName));
    }

    public static void shutdownScheduler() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            log.info("Scheduler shut down.");
        } else {
            log.info("Scheduler has shut down.");
        }
    }


    public static void startScheduler() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (scheduler.isShutdown()) {
            scheduler.start();
            log.info("Scheduler start.");
        } else {
            log.info("Scheduler has start.");
        }
    }

    public static Boolean isShutdown() throws SchedulerException {
        return getScheduler().isShutdown();
    }

}