package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(generator = "orders_id_generator")
    @SequenceGenerator(name = "orders_id_generator", sequenceName = "orders_id_seq", allocationSize = 1)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<Ticket> tickets;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "active_until")
    private ZonedDateTime active_until;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public Order() {
    }

    public Order(BigDecimal totalPrice, ZonedDateTime active_until, Status status) {
        this.totalPrice = totalPrice;
        this.active_until = active_until;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ZonedDateTime getActive_until() {
        return active_until;
    }

    public void setActive_until(ZonedDateTime active_until) {
        this.active_until = active_until;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        NEW, CANCELLED, CONFIRMED
    }
}