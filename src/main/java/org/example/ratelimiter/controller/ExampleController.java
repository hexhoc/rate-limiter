package org.example.ratelimiter.controller;

import org.example.ratelimiter.annotation.RateLimit;
import org.example.ratelimiter.enums.RateLimitEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @RateLimit(limit = 6, refreshInterval = 60L, type = RateLimitEnum.IP)
    @GetMapping("/example")
    public String example() {
        // acquire a permit before processing the request
        return "HELLO WORLD";
    }

}
