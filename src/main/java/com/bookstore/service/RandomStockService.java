package com.bookstore.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Random;

@Service
@ConditionalOnProperty(name = "features.stock.calculation", havingValue = "random")
public class RandomStockService implements Serializable, StockService {
    @Override
    public int calculateStock(Long bookId) {
        return new Random().nextInt(100);
    }
}