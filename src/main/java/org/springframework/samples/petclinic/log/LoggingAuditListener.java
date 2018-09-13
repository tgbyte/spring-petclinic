package org.springframework.samples.petclinic.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static net.logstash.logback.argument.StructuredArguments.entries;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component
public class LoggingAuditListener
{
    private static final Logger logger = LoggerFactory.getLogger(LoggingAuditListener.class);

    @EventListener
    public void onAuditEvent(AuditApplicationEvent event)
    {
        final AuditEvent auditEvent = event.getAuditEvent();

        logger.info("Received audit event; {} {}",
                keyValue("type", auditEvent.getType()),
                keyValue("principal", auditEvent.getPrincipal()),
                entries(auditEvent.getData()));
    }
}
