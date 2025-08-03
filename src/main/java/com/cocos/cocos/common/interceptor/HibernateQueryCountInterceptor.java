package com.cocos.cocos.common.interceptor;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class HibernateQueryCountInterceptor implements HandlerInterceptor {

    public static final String QUERY_COUNT_ATTR = "hibernate.query.count";
    private final EntityManager entityManager;

    private Statistics getStatistics() {
        return entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        getStatistics().clear();
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        request.setAttribute(QUERY_COUNT_ATTR, (int) getStatistics().getQueryExecutionCount());
    }
}
