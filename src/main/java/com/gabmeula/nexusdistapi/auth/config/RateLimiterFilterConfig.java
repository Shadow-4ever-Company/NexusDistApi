package com.gabmeula.nexusdistapi.auth.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.gabmeula.nexusdistapi.filters.IpRateLimiterFilter;
import com.gabmeula.nexusdistapi.ratelimiter.RateLimiter;
import com.gabmeula.nexusdistapi.ratelimiter.InMemoryRateLimiter;

@Configuration
public class RateLimiterFilterConfig {

    @Bean
    public RateLimiter rateLimiter() {
        return new InMemoryRateLimiter();
    }

    @Bean
    public FilterRegistrationBean<IpRateLimiterFilter> ipRateLimiterFilterRegistration(RateLimiter rateLimiter) {
        FilterRegistrationBean<IpRateLimiterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new IpRateLimiterFilter(rateLimiter));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
