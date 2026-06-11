package re.dgnl.it211_project.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    @Around("execution(* re.dgnl.it211_project.service.*.*(..)) || execution(* re.dgnl.it211_project.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceedResult = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;

        String methodName = joinPoint.getSignature().toShortString();

        log.info("{} đã chạy xong. Tổng thời gian xử lý: {} ms", methodName, executionTime);

        return proceedResult;
    }
}