package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "promocode")
public class Promocode {
    @Id
    @GeneratedValue(generator = "promocode_id_generator")
    @SequenceGenerator(name = "promocode_id_generator", sequenceName = "promocode_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "expiration_date")
    private ZonedDateTime expiration_date;
    @Column(name = "uses")
    private Long uses;
    @Column(name = "max_uses")
    private Long max_uses;
    @Column(name = "discount")
    private Long discount;

    public Promocode() {
    }

    public Promocode(String code, ZonedDateTime expiration_date, Long max_uses, Long discount) {
        this.code = code;
        this.expiration_date = expiration_date;
        this.max_uses = max_uses;
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZonedDateTime getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(ZonedDateTime expiration_date) {
        this.expiration_date = expiration_date;
    }

    public Long getUses() {
        return uses;
    }

    public void setUses(Long uses) {
        this.uses = uses;
    }

    public Long getMax_uses() {
        return max_uses;
    }

    public void setMax_uses(Long max_uses) {
        this.max_uses = max_uses;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }
}
