package com.bookstore.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
//@Primary
@ConditionalOnProperty(name = "features.stock.calculation", havingValue = "fixed")
public class FixedStockService implements Serializable, StockService {
    @Override
    public int calculateStock(Long bookId) {
        return (int)(bookId * 10) % 100;
    }
}
