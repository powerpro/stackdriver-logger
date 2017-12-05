package com.powerpro.stackdriverlogger;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Builder
@Getter
@Slf4j
public class RequestLogger {

    private static final String DEFAULT_MESSAGE_PREFIX = "\n";

    private static final String DEFAULT_MESSAGE_SUFFIX = "\n";

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;


    private final boolean includeQueryString;

    private final boolean includeClientInfo;

    private final boolean includeHeaders;

    private final boolean includePayload;

    @Builder.Default
    private final int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

    @Builder.Default
    private final String messagePrefix = DEFAULT_MESSAGE_PREFIX;

    @Builder.Default
    private final String messageSuffix = DEFAULT_MESSAGE_SUFFIX;

    public void debug(final HttpServletRequest request) {
        log.debug(createMessage(request, messagePrefix, messageSuffix));
    }

    /**
     * @see org.springframework.web.filter.AbstractRequestLoggingFilter
     */
    private String createMessage(final HttpServletRequest request, final String prefix, final String suffix) {
        StringBuilder msg = new StringBuilder(prefix);

        msg.append("\n==OVERVIEW==");
        msg.append("uri=").append(request.getRequestURI());
        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        msg.append("\nmethod=").append(request.getMethod());

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append("\nclient=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append("\nsession=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append("\nuser=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            String headers = Collections.list(request.getHeaderNames()).stream()
                    .map(k -> String.format("%s=%s", k, request.getHeader(k)))
                    .collect(Collectors.joining("\n", "\n", ""));
            msg.append("\n\n==HEADERS==").append(headers);
        }

        if (request.getMethod() != RequestMethod.GET.toString()) {
            String params = Collections.list(request.getParameterNames()).stream()
                    .map(k -> String.format("%s=%s", k, request.getParameter(k)))
                    .collect(Collectors.joining("\n", "\n", ""));
            msg.append("\n\n==PARAMS==").append(params);
        }

        if (isIncludePayload()) {
            ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    String payload;
                    int length = Math.min(buf.length, getMaxPayloadLength());
                    try {
                        payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                    }
                    catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                    msg.append("\n\n==PAYLOAD==").append(payload);
                }
            }
        }

        msg.append(suffix);
        return msg.toString();
    }

}
