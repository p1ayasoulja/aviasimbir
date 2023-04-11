package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(generator = "ticket_id_generator")
    @SequenceGenerator(name = "ticket_id_generator", sequenceName = "ticket_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight")
    private Flight flight;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "reserved")
    private Boolean reserved;
    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;
    @Column(name = "sold")
    private Boolean sold;
    @Column(name = "commission")
    private Boolean commission;

    public Ticket() {
    }

    public Ticket(Flight flight, BigDecimal price, Boolean reserved, Boolean sold, Boolean commission) {
        this.flight = flight;
        this.price = price;
        this.reserved = reserved;
        this.sold = sold;
        this.commission = commission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Boolean getCommission() {
        return commission;
    }

    public void setCommission(Boolean commission) {
        this.commission = commission;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    @Override
    public String toString() {
        return "Ticket with " +
                "id = " + id;
    }
}
