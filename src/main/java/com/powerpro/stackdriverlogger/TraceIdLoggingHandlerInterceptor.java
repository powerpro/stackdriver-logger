package com.powerpro.stackdriverlogger;

import static org.springframework.util.StringUtils.hasText;

import com.google.cloud.logging.TraceLoggingEnhancer;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TraceIdLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

    public static final String TRACE_ID_ATTR = "x-cloud-trace-context";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final String traceId = (String) request.getAttribute(TRACE_ID_ATTR);
        if (hasText(traceId)) {
            TraceLoggingEnhancer.setCurrentTraceId(traceId);
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        TraceLoggingEnhancer.setCurrentTraceId(null);
        super.afterCompletion(request, response, handler, ex);
    }

}
