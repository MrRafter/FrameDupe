package me.MrRafter.framedupe.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class ExpiringSet<E> {

    private final Cache<E, Object> cache;
    private static final Object PRESENT = new Object();

    public ExpiringSet(long duration, TimeUnit unit) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration, unit).build();
    }

    public ExpiringSet(Duration duration) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration).build();
    }

    public void add(E item) {
        this.cache.put(item, PRESENT);
    }

    public boolean contains(E item) {
        return this.cache.getIfPresent(item) != null;
    }
}