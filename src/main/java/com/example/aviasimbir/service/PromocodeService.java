package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Promocode;
import com.example.aviasimbir.exceptions.WrongPromocodeException;
import com.example.aviasimbir.repo.PromocodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class PromocodeService {
    private final PromocodeRepository promocodeRepository;

    public PromocodeService(PromocodeRepository promocodeRepository) {
        this.promocodeRepository = promocodeRepository;
    }

    /**
     * Получить процент скидки промокода
     *
     * @param code промокод
     * @return процент скидки
     * @throws WrongPromocodeException ошибка применения промокода
     */
    @Transactional
    public BigDecimal getDiscount(String code) throws WrongPromocodeException {
        Promocode promocode = promocodeRepository.findByCode(code)
                .orElseThrow(() -> new WrongPromocodeException("Promocode " + code + " does not exist"));
        Long uses = promocode.getUses();
        promocode.setUses(uses + 1);
        promocodeRepository.save(promocode);
        log.info("IN getDiscount - Discount: {} successfully found", promocode.getDiscount());
        return BigDecimal.valueOf(promocode.getDiscount());
    }

    /**
     * Проверка работоспособности промокода
     *
     * @param code текст промокода
     * @return флаг работоспособности
     */
    public Boolean isPromocodeValid(String code) throws WrongPromocodeException {
        Promocode promocode = promocodeRepository.findByCode(code)
                .orElseThrow(() -> new WrongPromocodeException("Promocode " + code + " does not exist"));
        if (promocode.getExpiration_date().isBefore(ZonedDateTime.now())) {
            return false;
        }
        return promocode.getUses() < promocode.getMax_uses();
    }
}
