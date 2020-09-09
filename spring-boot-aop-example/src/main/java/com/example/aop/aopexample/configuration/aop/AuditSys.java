package com.example.aop.aopexample.configuration.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class AuditSys {
    @Pointcut("execution(public * (@com.example.aop.aopexample.configuration.aop.common.Auditable *).*(..))")
    public void isAuditForClass() {
        // call by AOP
    }

    @Pointcut("execution(@com.example.aop.aopexample.configuration.aop.common.Auditable * *(..))")
    public void isAuditForMethod() {
        // call by AOP
    }

    /**
     * Method will add log statement of audit of the methods of class in call which
     * are annotated with @RoAudit
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("isAuditForClass() || isAuditForMethod()")
    // Around("execution(* *(..)) &&
    // @annotation(com.mastercontrol.ro.config.LogExecTime)")
    public Object processAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        Integer totalSize = 0;
        stopWatch.start();
        Object val = joinPoint.proceed();
        if (val instanceof Collection) {
            totalSize = ((Collection<?>) val).size();
        }
        stopWatch.stop();
        logAudit(joinPoint, stopWatch, totalSize);
        return val;
    }

    private void logAudit(ProceedingJoinPoint joinPoint, StopWatch stopWatch, Integer totalSize) {
        Date dateLog = new Date();
        String ip = "";
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
            ip = getClientIP(request);
        }
        // IP valid
        validIP(ip);

        String classCall = joinPoint.getTarget().getClass().getName();
        String method = joinPoint.getSignature().getName();
        String user = getCurrentUser();

        log.info("*****************************Audit Info****************************************");
//        log.info("Domain name: {}", TenantContext.getDomainInfo().getDomainName());
        log.info("Class name: {}", classCall);
        log.info("Method call: {}", method);
        if (totalSize > 0) {
            log.info("Total items: {}", totalSize);
        }
      
        log.info("Execution time: {}ms", stopWatch.getTotalTimeMillis());
        log.info("User performed: {}", user);
        log.info("IP call: {}", ip);
        log.info("Time call: {}", dateLog.toString());

        log.info("*******************************************************************************");
    }

    private String getCurrentUser() {
        /*try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!StringUtils.isBlank(currentUser)) {
                return currentUser;
            }

        } catch (Exception e) {
            return "UNKNOWN";

        }*/

        return "UNKNOWN";
    }

    private String getClientIP(HttpServletRequest req) {
        String ip = req.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = req.getRemoteAddr();
        }
        if (ip == null) {
            ip = req.getHeader("x-forwarded-for");
        }
        if (ip == null) {
            ip = req.getHeader("X-Forwarded-For");
        }
        return ip;
    }

    private boolean validIP(String ip) {
        /*
         * Set<String> ips = appConfig.getTrustIPs(); Iterator<String> it =
         * ips.iterator(); String trustIp = null; while (it.hasNext()) { trustIp =
         * it.next(); if (trustIp.equals(ip)) { return true; } }
         */
        return true;
    }

}
