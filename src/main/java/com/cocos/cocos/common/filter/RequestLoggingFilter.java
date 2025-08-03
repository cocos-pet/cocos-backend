package com.cocos.cocos.common.filter;

import com.cocos.cocos.common.interceptor.HibernateQueryCountInterceptor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_METHOD = "method";
    private static final String MDC_URI = "uri";
    private static final String MDC_IP = "ip";
    private static final String MDC_CONCURRENT = "concurrent";
    private static final String MDC_SQL_COUNT = "sqlCount";
    private static final String MDC_DURATION = "duration";
    private static final String MDC_STATUS = "status";

    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    private final AtomicInteger concurrentRequests = new AtomicInteger(0);
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain chain)
            throws ServletException, IOException {

        final long startTime = System.currentTimeMillis();
        final int current = concurrentRequests.incrementAndGet();
        final ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            MDC.put(MDC_METHOD, request.getMethod());
            MDC.put(MDC_URI, request.getRequestURI());
            MDC.put(MDC_IP, request.getRemoteAddr());
            MDC.put(MDC_CONCURRENT, String.valueOf(current));

            chain.doFilter(request, wrappedResponse);

        } finally {
            try {
                handleAfterRequest(request, wrappedResponse, startTime);
            } finally {
                concurrentRequests.decrementAndGet();
                MDC.clear();
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    private void handleAfterRequest(@NonNull final HttpServletRequest request,
                                    @NonNull final ContentCachingResponseWrapper wrappedResponse,
                                    final long startTime) {
        final long duration = System.currentTimeMillis() - startTime;

        final int sqlCount = Optional.ofNullable(request.getAttribute(HibernateQueryCountInterceptor.QUERY_COUNT_ATTR))
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(0);

        MDC.put(MDC_SQL_COUNT, String.valueOf(sqlCount));
        MDC.put(MDC_DURATION, String.valueOf(duration));

        final int status = wrappedResponse.getStatus();
        final ErrorResponseInfo errorInfo = parseErrorResponse(wrappedResponse);

        MDC.put(MDC_STATUS, errorInfo.code != null ? errorInfo.code : String.valueOf(status));

        if (status >= 400) {
            log.warn("Request failed: {}",
                    Optional.ofNullable(errorInfo.message).orElse("Unknown error"));
        } else {
            log.info("Request completed successfully");
        }
    }

    private ErrorResponseInfo parseErrorResponse(@NonNull final ContentCachingResponseWrapper wrappedResponse) {
        try {
            final String body = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!body.isBlank() && body.trim().startsWith("{")) {
                final JsonNode json = objectMapper.readTree(body);
                final String code = json.has(CODE) ? json.get(CODE).asText() : null;
                final String message = json.has(MESSAGE) ? json.get(MESSAGE).asText() : null;
                return new ErrorResponseInfo(code, message);
            }
        } catch (final Exception e) {
            log.debug("응답 본문 파싱 실패: {}", e.getMessage());
        }
        return new ErrorResponseInfo(null, null);
    }

    private record ErrorResponseInfo(String code, String message) {}
}
