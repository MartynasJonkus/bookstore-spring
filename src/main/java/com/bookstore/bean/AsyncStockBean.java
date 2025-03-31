package com.bookstore.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Named
@SessionScoped
public class AsyncStockBean implements Serializable {
    private final Map<Long, CompletableFuture<Integer>> calculations = new ConcurrentHashMap<>();
    private final Map<Long, Integer> results = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> status = new ConcurrentHashMap<>();

    public void calculateStock(Long bookId) {
        status.put(bookId, true);

        calculations.put(bookId, CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                return new Random().nextInt(100);
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