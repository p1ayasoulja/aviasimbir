package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Order;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.repo.OrderRepository;
import com.example.aviasimbir.repo.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final PromocodeService promocodeService;
    private final OrderActiveService orderActiveService;

    @Value("${order.active.timeout.minutes}")
    private Long orderActiveTimeoutInMinutes;

    public OrderService(TicketRepository ticketRepository, OrderActiveService orderActiveService,
                        PromocodeService promocodeService, OrderRepository orderRepository) {
        this.ticketRepository = ticketRepository;
        this.orderActiveService = orderActiveService;
        this.promocodeService = promocodeService;
        this.orderRepository = orderRepository;
    }

    /**
     * Создать заказ на покуаку билетов
     *
     * @param tickets_id идентификаторы билетов
     * @param promocode  промокод
     * @return заказ
     * @throws WrongPromocodeException ошибка применения промокода
     * @throws CreateOrderException    оишбка создания заказа
     * @throws TicketReservedException ошибка уже забронированного бронирования билета
     */
    @Transactional
    public Order createOrder(List<Long> tickets_id, String promocode) throws WrongPromocodeException, CreateOrderException, TicketReservedException, PlaneAlreadyLeftException {
        if (tickets_id.isEmpty()) {
            throw new CreateOrderException("List of tickets is empty");
        }
        if (!promocodeService.isPromocodeValid(promocode)) {
            throw new WrongPromocodeException("Promocode is expired or not available");
        }
        List<Ticket> tickets = ticketRepository.findTicketsById(tickets_id);
        Order order = new Order(BigDecimal.ZERO, ZonedDateTime.now().plusMinutes(orderActiveTimeoutInMinutes), Order.Status.NEW);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Ticket ticket : tickets) {
            if (ticket.getFlight().getDepartureTime().isBefore(ZonedDateTime.now())) {
                throw new PlaneAlreadyLeftException("Ticket " + ticket.getId() + " is not actual");
            }
            if (ticket.getReserved()) {
                throw new TicketReservedException("Ticket " + ticket.getId() + " already reserved or sold");
            }
            ticket.setReserved(true);
            ticket.setOrder(order);
            totalPrice = totalPrice.add(ticket.getPrice());
        }
        orderActiveService.scheduleCancelOrderActive(order.getId());
        totalPrice = totalPrice.multiply(BigDecimal.ONE.subtract((promocodeService.getDiscount(promocode)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        log.info("IN createOrder - Order created with id {}", order.getId());
        return order;
    }

    /**
     * Отменить заказ
     *
     * @param id идентификатор заказа
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    @Transactional
    public void cancelOrderActive(Long id) throws NoSuchIdException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Order with id " + id + " was not found"));
        if (order.getActive_until().isAfter(ZonedDateTime.now()) && order.getStatus() == Order.Status.NEW) {
            order.setActive_until(null);
            order.setStatus(Order.Status.CANCELLED);
            List<Ticket> tickets = ticketRepository.findTicketsByOrder(order);
            for (Ticket ticket : tickets) {
                ticket.setReserved(false);
                ticket.setOrder(null);
            }
            orderRepository.save(order);
            log.info("IN cancelOrderActive - Order cancelled");
        }
    }

    /**
     * Поменять статус заказа
     *
     * @param id     идентификатор заказа
     * @param status статус заказа
     * @throws NoSuchIdException  ошибка неверного идентификатора заказа
     * @throws BadStatusException ошибка ввода недоступного статуса заказа
     */
    @Transactional
    public void changeOrderStatus(Long id, String status) throws NoSuchIdException, BadStatusException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Order with id " + id + " was not found"));
        if (status.isEmpty()) {
            throw new BadStatusException("Not available status");
        }
        if (!order.getStatus().equals(Order.Status.NEW)) {
            throw new BadStatusException("Order status can not be changed");
        }
        List<Ticket> tickets = ticketRepository.findTicketsByOrder(order);
        if (status.equals(Order.Status.CANCELLED.name())) {
            for (Ticket ticket : tickets) {
                ticket.setOrder(null);
                ticket.setReserved(false);
            }
            order.setStatus(Order.Status.CANCELLED);
        }
        if (status.equals(Order.Status.CONFIRMED.name())) {
            for (Ticket ticket : tickets) {
                ticket.setSold(true);
            }
            order.setStatus(Order.Status.CONFIRMED);
        }
        orderRepository.save(order);
        log.info("IN changeOrderStatus - Order status changed to {}", status);
    }

    /**
     * Получить список всех заказов
     *
     * @return список заказов
     */
    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
