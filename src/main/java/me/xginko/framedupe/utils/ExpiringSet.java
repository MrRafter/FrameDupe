package me.xginko.framedupe.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 */
public final class ExpiringSet<E> extends AbstractSet<E> implements Set<E> {

    private final Cache<E, Object> cache;
    private static final Object PRESENT = new Object(); // Dummy value to associate with an Object in the backing Cache

    public ExpiringSet(long duration, TimeUnit unit) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration, unit).build();
    }

    public ExpiringSet(Duration duration) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration).build();
    }

    @Override
    public int size() {
        return this.cache.asMap().size();
    }

    @Override
    public boolean isEmpty() {
        return this.cache.asMap().isEmpty();
    }

    @Override
    public boolean contains(Object item) {
        return this.cache.getIfPresent((E) item) != null;
    }

    @Override
    public Iterator<E> iterator() {
        return this.cache.asMap().keySet().iterator();
    }

    @Override
    public Object [] toArray() {
        return this.cache.asMap().keySet().toArray();
    }

    @Override
    public <T> T [] toArray(T [] a) {
        return this.cache.asMap().keySet().toArray(a);
    }

    @Override
    public boolean add(E item) {
        boolean containedBefore = this.contains(item);
        this.cache.put(item, PRESENT);
        return !containedBefore;
    }

    @Override
    public boolean remove(Object o) {
        boolean containedBefore = this.contains(o);
        this.cache.invalidate((E) o);
        return containedBefore;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object o : collection) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean changed = false;
        for (E o : collection) {
            if (this.add(o)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        for (E e : this.cache.asMap().keySet()) {
            if (!collection.contains(e) && this.remove(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (E e : this.cache.asMap().keySet()) {
            if (this.remove(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }
}