package com.company;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class ThreadSafeArrayList<T> implements List<T> {
    private final List<T> list = new ArrayList<>();
    ReadWriteLock reentrantLock = new ReentrantReadWriteLock();

    @Override
    public int size() {
        return doWithReadLock(list::size);
    }

    @Override
    public boolean isEmpty() {
        return doWithReadLock(list::isEmpty);
    }

    @Override
    public boolean contains(Object o) {
        return doWithReadLock(() -> list.contains(o));
    }

    @Override
    public Iterator<T> iterator() {
        return doWithReadLock(list::iterator);
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return doWithReadLock(() -> list.toArray(a));
    }

    @Override
    public boolean add(T t) {
        return doWithWriteLock(() -> {
            boolean add = list.add(t);
            System.out.println(Thread.currentThread().getName() + " " + list);
            return add;
        });
    }

    @Override
    public boolean remove(Object o) {
        return doWithWriteLock(() -> list.remove(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return doWithReadLock(() -> list.containsAll(c));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return doWithReadLock(() -> list.addAll(c));
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return doWithReadLock(() -> list.addAll(index, c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return doWithReadLock(() -> list.removeAll(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return doWithWriteLock(() -> list.retainAll(c));
    }

    @Override
    public void clear() {
        reentrantLock.writeLock().lock();
        try {
            list.clear();
        } finally {
            reentrantLock.writeLock().unlock();
        }
    }

    @Override
    public T get(int index) {
        return doWithReadLock(() -> list.get(index));
    }

    @Override
    public T set(int index, T element) {
        return doWithWriteLock(() -> list.set(index, element));
    }

    @Override
    public void add(int index, T element) {
        reentrantLock.writeLock().lock();
        try {
            list.add(index, element);
        } finally {
            reentrantLock.writeLock().unlock();
        }
    }

    @Override
    public T remove(int index) {
        return doWithWriteLock(() -> list.remove(index));
    }

    @Override
    public int indexOf(Object o) {
        return doWithReadLock(() -> list.indexOf(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return doWithReadLock(() -> list.lastIndexOf(o));
    }

    @Override
    public ListIterator<T> listIterator() {
        return doWithWriteLock(list::listIterator);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return doWithWriteLock(() -> list.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return doWithWriteLock(() -> list.subList(fromIndex, toIndex));
    }

    @Override
    public String toString() {
        return doWithReadLock(list::toString);
    }

    private <R> R doWithWriteLock(Supplier<R> supplier) {
        this.reentrantLock.writeLock().lock();
        try {
            return supplier.get();
        } finally {
            this.reentrantLock.writeLock().unlock();
        }
    }

    private <R> R doWithReadLock(Supplier<R> supplier) {
        this.reentrantLock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            this.reentrantLock.readLock().unlock();
        }
    }
}
