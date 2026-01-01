package com.womensafety.authservice.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest req = (HttpServletRequest) request;

            // Add MDC context
            MDC.put("requestId", UUID.randomUUID().toString());

            // Add userId from token or default to anonymous
            String userId = req.getHeader("X-USER-ID");
            MDC.put("userId", userId != null ? userId : "anonymous");

            chain.doFilter(request, response);
        } finally {
            // Clear MDC to avoid leakage
            MDC.clear();
        }
    }
}
