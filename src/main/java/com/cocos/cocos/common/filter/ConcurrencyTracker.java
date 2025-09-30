package com.cocos.cocos.common.filter;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ConcurrencyTracker {

    private final AtomicInteger concurrentRequests = new AtomicInteger(0);

    public void increment() {
        concurrentRequests.incrementAndGet();
    }

    public void decrement() {
        concurrentRequests.decrementAndGet();
    }

    public int getCurrent() {
        return concurrentRequests.get();
    }
}
