package com.powerpro.stackdriverlogger;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class LogController {

    @RequestMapping
    public Map<String, String> hello(HttpServletRequest request) {
        RequestLogger requestLogger = RequestLogger.builder()
            .includeClientInfo(true)
            .includeHeaders(true)
            .build();
        requestLogger.debug(request);

        Map<String, String> info = new HashMap<>();
        info.put("threadId", Long.toString(Thread.currentThread().getId()));
        info.put("traceId", (String) request.getAttribute("x-cloud-trace-context"));
        log.debug("\n==========INFO===========\n{}\n", info);
        return info;
    }

}
