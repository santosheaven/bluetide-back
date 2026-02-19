package com.bluetide.services.job;

import com.bluetide.services.models.Invoice;
import com.bluetide.services.service.InvoiceService;
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
 * Quartz job that runs daily to find unpaid invoices and creates notifications.
 */
@Component
public class UnpaidInvoiceReminderJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(UnpaidInvoiceReminderJob.class);

    private final InvoiceService invoiceService;
    private final NotificationService notificationService;
    private final MessageSource messageSource;

    public UnpaidInvoiceReminderJob(InvoiceService invoiceService,
                                    NotificationService notificationService,
                                    MessageSource messageSource) {
        this.invoiceService = invoiceService;
        this.notificationService = notificationService;
        this.messageSource = messageSource;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("UnpaidInvoiceReminderJob – checking unpaid invoices…");
        List<Invoice> all = invoiceService.findAll();
        Locale locale = Locale.forLanguageTag("es");
        int created = 0;
        for (Invoice inv : all) {
            if (inv.getPaymentDate() == null) {
                String message = messageSource.getMessage("email.invoice.pending",
                        new Object[]{inv.getAmount(), inv.getServiceType()}, locale);
                notificationService.createNotification(
                        null,
                        "UNPAID_INVOICE",
                        message,
                        inv.getId()
                );
                created++;
            }
        }
        log.info("UnpaidInvoiceReminderJob – created {} reminders", created);
    }
}
