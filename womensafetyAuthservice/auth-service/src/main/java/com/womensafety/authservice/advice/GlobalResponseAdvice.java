package com.womensafety.authservice.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Optional: Skip wrapping for certain controllers/packages
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType contentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        //  Detect already-wrapped error manually
        if (body instanceof ResponseWrapper<?> wrapper) {
            String status = wrapper.getStatus();
            if ("ERROR".equalsIgnoreCase(status) || "FAILURE".equalsIgnoreCase(status)) {
                return wrapper;  // do NOT re-wrap
            }
        }

        //  Avoid wrapping generic error maps (like validation maps)
        if (body instanceof java.util.Map map && map.containsKey("status") && map.get("status").equals(400)) {
            return body; // Don't wrap validation responses
        }

        // Normal response — wrap in SUCCESS
        return new ResponseWrapper<>(LocalDateTime.now(), "SUCCESS", "Request processed", body);
    }
}
