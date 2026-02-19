package com.bluetide.services.config;

import com.bluetide.services.job.MaintenanceReminderJob;
import com.bluetide.services.job.UnpaidInvoiceReminderJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz scheduler configuration.
 * Schedules recurring jobs for maintenance reminders and unpaid invoice alerts.
 */
@Configuration
public class QuartzConfig {

    // ── Maintenance Reminder (daily at 08:00) ──

    @Bean
    public JobDetail maintenanceReminderJobDetail() {
        return JobBuilder.newJob(MaintenanceReminderJob.class)
                .withIdentity("maintenanceReminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger maintenanceReminderTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(maintenanceReminderJobDetail())
                .withIdentity("maintenanceReminderTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(8, 0))
                .build();
    }

    // ── Unpaid Invoice Reminder (daily at 09:00) ──

    @Bean
    public JobDetail unpaidInvoiceReminderJobDetail() {
        return JobBuilder.newJob(UnpaidInvoiceReminderJob.class)
                .withIdentity("unpaidInvoiceReminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger unpaidInvoiceReminderTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(unpaidInvoiceReminderJobDetail())
                .withIdentity("unpaidInvoiceReminderTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(9, 0))
                .build();
    }
}

