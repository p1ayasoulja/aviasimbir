package com.example.aviasimbir.entity;

import javax.persistence.*;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight")
    private Flight flight;
    @Column(name = "price")
    private double price;
    @Column(name = "reserved")
    private Boolean reserved;
    @Column(name = "sold")
    private Boolean sold;
    @Column(name = "commission")
    private Boolean commission;

    public Ticket() {
    }

    public Ticket( Flight flight, double price, Boolean reserved, Boolean sold,Boolean commission) {
        this.id = id;
        this.flight = flight;
        this.price = price;
        this.reserved = reserved;
        this.sold = sold;
        this.commission=commission;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
}
