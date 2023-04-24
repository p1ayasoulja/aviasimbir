package com.example.aviasimbir.service;

import com.example.aviasimbir.exceptions.NoSuchIdException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OrderActiveService {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final OrderService orderService;
    @Value("${order.active.timeout.minutes}")
    private int orderActiveTimeoutInMinutes;

    @Lazy
    public OrderActiveService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Установка таймера валидности заказа
     *
     * @param id идентификатор билета
     */
    public void scheduleCancelOrderActive(Long id) {
        executorService.schedule(() -> {
            try {
                orderService.cancelOrderActive(id);
            } catch (NoSuchIdException e) {
                throw new RuntimeException(e);
            }
        }, orderActiveTimeoutInMinutes, TimeUnit.MINUTES);
    }
}
