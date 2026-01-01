package com.womensafety.authservice.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter implements jakarta.servlet.Filter {
    private static final String HDR = "x-trace-id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;

        String traceId = http.getHeader(HDR);
        if (traceId == null || traceId.isBlank()) traceId = UUID.randomUUID().toString();

        MDC.put("traceId", traceId);
        httpRes.setHeader(HDR, traceId);
        try { chain.doFilter(req, res); }
        finally { MDC.remove("traceId"); }
    }
}