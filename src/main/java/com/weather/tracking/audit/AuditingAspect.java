package com.weather.tracking.audit;

import com.weather.tracking.entity.AuditLogEntry;
import com.weather.tracking.enums.ActionStatus;
import com.weather.tracking.enums.AuditAction;
import com.weather.tracking.repository.AuditLogEntryRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Aspect
@Component
public class AuditingAspect {
    private final AuditLogEntryRepository auditLogEntryRepository;
    private final RequestContextHolder requestContextHolder;

    public AuditingAspect(final AuditLogEntryRepository auditLogEntryRepository,
                          final RequestContextHolder requestContextHolder) {
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.requestContextHolder = requestContextHolder;
    }

    @Pointcut("@annotation(com.weather.tracking.audit.Auditable)")
    public void auditableMethods() {}

    @Around("auditableMethods()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Auditable auditable = signature.getMethod().getAnnotation(Auditable.class);
        AuditAction auditableAction = auditable.action();
        AuditLogEntry entry = new AuditLogEntry(auditable.action(), requestContextHolder.getUserEmail());

        try {
            Object result = joinPoint.proceed();
            entry.setStatus(ActionStatus.SUCCESS);
            entry.setTimeFinished(ZonedDateTime.now());
            return result;
        } catch (Exception ex) {
            entry.setStatus(ActionStatus.FAILED);
            entry.setTimeFinished(ZonedDateTime.now());
            entry.setAdditionalContext(String.format("The command: %s failed due to: %s", auditableAction.name(), ex.getMessage()));
            throw ex;
        } finally {
            auditLogEntryRepository.save(entry);
        }
    }
}


