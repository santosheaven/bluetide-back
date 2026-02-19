package com.bluetide.services.job;

import com.bluetide.services.models.Maintenance;
import com.bluetide.services.service.MaintenanceService;
import com.bluetide.services.service.NotificationService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Quartz job that runs daily to find upcoming maintenance tasks and
 * creates notifications for the related inventory/property owners.
 */
@Component
public class MaintenanceReminderJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceReminderJob.class);

    private final MaintenanceService maintenanceService;
    private final NotificationService notificationService;
    private final MessageSource messageSource;

    public MaintenanceReminderJob(MaintenanceService maintenanceService,
                                  NotificationService notificationService,
                                  MessageSource messageSource) {
        this.maintenanceService = maintenanceService;
        this.notificationService = notificationService;
        this.messageSource = messageSource;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("MaintenanceReminderJob – checking upcoming maintenance…");
        List<Maintenance> upcoming = maintenanceService.findUpcoming();
        Locale locale = Locale.forLanguageTag("es");
        int created = 0;
        for (Maintenance m : upcoming) {
            String message = messageSource.getMessage("email.maintenance.upcoming",
                    new Object[]{m.getDescription()}, locale);
            notificationService.createNotification(
                    null,
                    "MAINTENANCE_REMINDER",
                    message,
                    m.getId()
            );
            created++;
        }
        log.info("MaintenanceReminderJob – created {} reminder notifications", created);
    }
}
