package sast.evento.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @projectName: Test
 * @author: feelMoose
 * @date: 2023/7/26 22:35
 */
@Slf4j
@Component
public class SchedulerService {
    public static final String simpleDateFormatPattern = "ss mm HH dd MM ? yyyy";
    @Resource
    private StdScheduler schedulerFactoryBean;

    public Scheduler getScheduler() {
        return this.schedulerFactoryBean;
    }

    public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, String cron) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!scheduler.isStarted()) {
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

    public void addRepeatJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, String cron, Date start, Date end) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!scheduler.isStarted()) {
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

    public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends org.quartz.Job> jobClass, @Nullable JobDataMap jobDataMap, Date date) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!scheduler.isStarted()) {
            throw new RuntimeException("Please contact admin to start the scheduler first.");
        }
        log.info("""
                
                jobName: {}
                jobGroup: {}
                triggerName: {}
                triggerGroupName: {}""", jobName, jobGroupName, triggerName, triggerGroupName);

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName)
                .setJobData(jobDataMap)
                .build();
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName)
                .startAt(date)
                .build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);
    }


    public Boolean resetJobTrigger(String triggerName, String triggerGroupName, String cron) throws Exception {
        Scheduler scheduler = this.getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            log.error("resetRepeatJobFailed:" + triggerGroupName + ":" + triggerName);
            return false;
        }

        if (!trigger.getCronExpression().equalsIgnoreCase(cron)) {
            trigger.setCronExpression(cron);
            scheduler.rescheduleJob(triggerKey, trigger);
        }
        return true;
    }

    public Boolean resetJobTrigger(String triggerName, String triggerGroupName, Date date) throws Exception {
        Scheduler scheduler = this.getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            log.error("resetRepeatJobFailed:" + triggerGroupName + ":" + triggerName);
            return false;
        }

        if (!trigger.getStartTime().equals(date)) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(date)
                    .build();
            scheduler.rescheduleJob(triggerKey, simpleTrigger);
        }
        return true;
    }

    public Boolean resetRepeatJob(String triggerName, String triggerGroupName, String cron, Date start, Date end) throws Exception {
        Scheduler scheduler = this.getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            log.error("resetRepeatJobFailed:" + triggerGroupName + ":" + triggerName);
            return false;
        }

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
        return true;
    }

    public void addJobListener(String jobName, String jobGroup, JobListener listener) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
        scheduler.getListenerManager().addJobListener(listener, matcher);
    }

    public void removeJobListener(String name, String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
        Scheduler scheduler = this.getScheduler();
        scheduler.getListenerManager().removeJobListenerMatcher(name, matcher);
    }

    public void addTriggerListener(String triggerName, String triggerGroup, TriggerListener listener) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        Matcher<TriggerKey> matcher = KeyMatcher.keyEquals(triggerKey);
        scheduler.getListenerManager().addTriggerListener(listener, matcher);
    }

    public void removeTriggerListener(String name, String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        Matcher<TriggerKey> matcher = KeyMatcher.keyEquals(triggerKey);
        Scheduler scheduler = this.getScheduler();
        scheduler.getListenerManager().removeTriggerListenerMatcher(name, matcher);
    }

    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!isJobExist(jobName, jobGroupName, triggerName, triggerGroupName)) {
            log.info("Job: {} is not exist.", jobName);
            return;
        }
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(new JobKey(jobName, jobGroupName));
    }

    public Boolean isJobExist(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        return scheduler.checkExists(new JobKey(jobName, jobGroupName)) && scheduler.checkExists(new TriggerKey(triggerName, triggerGroupName));
    }

    public void shutdownScheduler() throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            log.info("Scheduler shut down.");
        } else {
            log.info("Scheduler has shut down.");
        }
    }


    public void startScheduler() throws SchedulerException {
        Scheduler scheduler = this.getScheduler();
        if (!scheduler.isStarted()) {
            scheduler.start();
            log.info("Scheduler start.");
        } else {
            log.warn("Scheduler has start.");
        }
    }

    public Boolean isShutdown() throws SchedulerException {
        return this.getScheduler().isShutdown();
    }

}