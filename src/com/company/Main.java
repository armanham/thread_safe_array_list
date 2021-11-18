package com.company;

import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        List<Integer> list = new ThreadSafeArrayList<>();
        Runnable runnable = () -> {
            while (list.size() < 10) {
                list.add(random.nextInt(100));
            }
        };

        List<Thread> threadList = List.of(
                new Thread(runnable, "Thread 1"),
                new Thread(runnable, "Thread 2"),
                new Thread(runnable, "Thread 3"));

        for (Thread thread : threadList) {
            thread.start();
        }
        Thread.sleep(10000);
    }
}
