package com.bookstore.bean;

import com.bookstore.aspect.TrackPerformance;
import com.bookstore.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("session")
public class AsyncStockBean implements Serializable {
    private final Map<Long, CompletableFuture<Integer>> calculations = new ConcurrentHashMap<>();
    private final Map<Long, Integer> results = new ConcurrentHashMap<>();
    final Map<Long, Boolean> status = new ConcurrentHashMap<>();

    @Autowired
    private StockService stockService;

    @TrackPerformance
    public void calculateStock(Long bookId) {
        status.put(bookId, true);

        calculations.put(bookId, CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                return stockService.calculateStock(bookId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            } finally {
                status.put(bookId, false);
            }
        }));

        calculations.get(bookId).thenAccept(result -> {
            results.put(bookId, result);
            status.put(bookId, false);
        });
    }

    public boolean isCalculating(Long bookId) {
        return status.getOrDefault(bookId, false);
    }

    public Integer getResult(Long bookId) {
        return results.get(bookId);
    }
}