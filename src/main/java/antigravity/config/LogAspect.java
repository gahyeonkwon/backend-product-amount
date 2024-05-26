package antigravity.config;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;


@Aspect
@Slf4j
@Component
public class LogAspect {
    @Pointcut("execution(* antigravity..*Controller.*(..))") // 모든 conroller 에 대해 지정
    public void controller() {
    }

    @Around("controller()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String controllerName = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {

            params.put("controller", controllerName);
            params.put("method", methodName);

        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }

        log.info("method: {}.{}", params.get("controller") ,params.get("method"));
        Object result = joinPoint.proceed();

        stopWatch.stop();
        log.info("taken_time: {} ", stopWatch.prettyPrint());

        return result;
    }

}
