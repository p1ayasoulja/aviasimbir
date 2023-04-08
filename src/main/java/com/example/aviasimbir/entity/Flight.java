package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class Flight {
    @Id
    @GeneratedValue(generator = "flight_id_generator")
    @SequenceGenerator(name = "flight_id_generator", sequenceName = "flight_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "plane")
    private Plane plane;
    @Column(name = "departure")
    private String departure;
    @Column(name = "destination")
    private String destination;
    @Column(name = "departure_time")
    private ZonedDateTime departureTime;
    @Column(name = "arrival_time")
    private ZonedDateTime arrivalTime;
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    public Flight() {
    }

    public Flight(Plane plane, String departure, String destination,
                  ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        this.plane = plane;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Flight with " +
                "id = " + id;
    }
}