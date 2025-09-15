package com.cocos.cocos.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int ERROR_RESPONSE_CODE = 400;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final QueryCountInspector queryCountInspector;
    private final Timer timer;
    private final ConcurrencyTracker concurrencyTracker;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        timer.start();
        concurrencyTracker.increment();
        final ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, wrappedResponse);

            final String responseBody = new String(
                    wrappedResponse.getContentAsByteArray(),
                    StandardCharsets.UTF_8
            );
            final int status = wrappedResponse.getStatus();

            if (status >= ERROR_RESPONSE_CODE) {
                LOGGER.info("method={} uri={} duration={} count={} concurrency={} body={}",
                        request.getMethod(), request.getRequestURI(), timer.getLatencyMillis(),
                        queryCountInspector.getCount(), concurrencyTracker.getCurrent(), responseBody);
            } else {
                LOGGER.info("method={} uri={} duration={} count={} concurrency={}",
                        request.getMethod(), request.getRequestURI(), timer.getLatencyMillis(),
                        queryCountInspector.getCount(), concurrencyTracker.getCurrent());
            }
        } finally {
            queryCountInspector.clear();
            concurrencyTracker.decrement();

            wrappedResponse.copyBodyToResponse();
        }
    }
}
