package com.gabmeula.nexusdistapi.filters;

import java.io.IOException;

import com.gabmeula.nexusdistapi.ratelimiter.RateLimiter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IpRateLimiterFilter implements Filter {

    private final RateLimiter rateLimiter;

    public IpRateLimiterFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String clientIP = this.resolveClientIp(req);

        if (rateLimiter.isOverLimit(clientIP)) {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Too Many Requests\"}"); // opcional
            return;
        }
        chain.doFilter(request, response);

    }

    private String resolveClientIp(HttpServletRequest request) {
        String fowarded = request.getHeader("X-Forwarded-For");

        if (fowarded != null && !fowarded.isBlank()) {
            return fowarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();

    }

}