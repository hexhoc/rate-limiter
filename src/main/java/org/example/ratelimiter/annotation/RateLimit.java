package org.example.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example.ratelimiter.enums.RateLimitEnum;
import org.redisson.api.RateIntervalUnit;

/**
 * 〈One sentence function description〉<br>
 *  Interface current limiting
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * The number of requests in each cycle, by default, an IP address can only request this interface once within 60 seconds
     *
     * @return int
     */
    long limit() default 1L;

    /**
     * Trigger within cycle time
     *
     * @return int
     */
    long refreshInterval() default 60L;

    /**
     * Current limiting type, default is based on IP limit
     *
     * @return RateLimitEnum
     */
    RateLimitEnum type() default RateLimitEnum.IP;

    /**
     * Time Unit
     *
     * @return RateIntervalUnit
     */
    RateIntervalUnit rateIntervalUnit() default RateIntervalUnit.SECONDS;
}