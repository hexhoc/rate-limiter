package org.example.ratelimiter.aspect;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.ratelimiter.annotation.RateLimit;
import org.example.ratelimiter.controller.TooManyRequestsException;
import org.example.ratelimiter.util.HttpServletUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@Order(-8)
public class RateLimitAspect {

    @Resource
    private RedissonClient redissonClient;

    private static final String KEY_PRE = "rule-engine:rate-limit:{%s}";

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String value;
        switch (rateLimit.type()) {
            case IP:
                value = HttpServletUtils.getRequest().getRemoteAddr();
                break;
            case URL:
                value = HttpServletUtils.getRequest().getRequestURI();
                break;
            case USER:
                value = "";
//                UserData userData = Context.getCurrentUser();
//                if (userData == null) {
//                    throw new RuntimeException("user not found!");
//                }
//                value = userData.getId().toString();
                break;
            case URL_IP:
                HttpServletRequest request = HttpServletUtils.getRequest();
                value = request.getRequestURI() + request.getRemoteAddr();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        String key = String.format(KEY_PRE, value);
        log.info("rate limiter with type:{},key:{}", rateLimit.type(), key);
        this.executor(key, rateLimit);
        return joinPoint.proceed();
    }

    /**
     * Current limiting actuator
     *
     * @param key       redis key
     * @param rateLimit Rate Parameters
     */
    private void executor(String key, RateLimit rateLimit) throws TooManyRequestsException {
        // Limit time interval
        long refreshInterval = rateLimit.refreshInterval();
        // Limit the number of times available within a time interval
        long limit = rateLimit.limit();
        // Time Unit
        RateIntervalUnit rateIntervalUnit = rateLimit.rateIntervalUnit();
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // Initialize the RateLimiter state and store the configuration in the Redis server
        if (!rateLimiter.isExists()) {
            boolean trySetRate = rateLimiter.trySetRate(RateType.OVERALL, limit, refreshInterval, rateIntervalUnit);
            log.info("初始化RateLimiter的状态:{}", trySetRate);
            // The current limiting data is saved for 10 days
            rateLimiter.expire(10, TimeUnit.DAYS);
        }
        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException();
        }
    }

}
