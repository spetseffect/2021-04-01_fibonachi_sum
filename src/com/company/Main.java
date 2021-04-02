package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main {
    private static int prevp = 0;
    private static int prevn = 1;
    private static int count = 2;

    private static void WaitForFinishOfExecutor(ExecutorService executor) {
        while (!executor.isTerminated()) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int newValues() {
        var tmp = prevn;
        prevn += prevp;
        prevp = tmp;
        count++;
        return prevn;
    }

    public static void main(String[] args) {
        AtomicInteger sum = new AtomicInteger(1);
        ReentrantLock lock = new ReentrantLock();
        ExecutorService exec = Executors.newFixedThreadPool(4);
        Runnable task = () -> {
            while (count < 40) {
                sum.addAndGet(newValues());
                System.out.printf("prevp = %d, prevn = %d, sum = %d, count = %d%n", prevp, prevn, sum.get(), count);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.printf("prevp = %d, prevn = %d, sum = %d, count = %d%n", prevp, prevn, sum.get(), count);
        exec.submit(task);
        exec.submit(task);
        exec.submit(task);
        exec.submit(task);
        exec.shutdown();
        WaitForFinishOfExecutor(exec);
        System.out.println(sum);
    }
}
