package com.muhammadminhaz.cartservice.scheduler;


import com.muhammadminhaz.cartservice.dto.CartRedisModel;
import com.muhammadminhaz.cartservice.kafka.CartPersistentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class CartExpiryScheduler {

    private final CartPersistentEventPublisher cartPersistentEventPublisher;

    // Keep track of scheduled tasks per customer
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    // Single-threaded executor for simplicity, can use pool
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    /**
     * Schedule cart persistence after delayMinutes
     */
    public void scheduleCartPersistence(String customerId, CartRedisModel cart, long delayMinutes) {
        // Cancel previous scheduled task if exists
        ScheduledFuture<?> existingTask = scheduledTasks.get(customerId);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }

        // Schedule new task
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            cartPersistentEventPublisher.publishCartEvent(cart);
            scheduledTasks.remove(customerId);
        }, delayMinutes, TimeUnit.MINUTES);

        scheduledTasks.put(customerId, future);
    }
}

