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
    private Integer price;
    @Column(name = "reserved")
    private Boolean reserved;
    @Column(name = "sold")
    private Boolean sold;
    @Column(name = "commission")
    private Boolean commission;

    public Ticket() {
    }

    public Ticket(Flight flight, Integer price, Boolean reserved, Boolean sold, Boolean commission) {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
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

    @Override
    public String toString() {
        return "Ticket with " +
                "id = " + id;
    }
}
