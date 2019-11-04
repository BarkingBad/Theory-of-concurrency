package lab4.src.concurrency;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Producer implements Runnable {
    private Buffer _buf;
    private int upperBound;
    private int value;
    public Producer(Buffer buf, int upperBound, int value) {
        _buf = buf;
        this.upperBound = upperBound;
        this.value = value;
    }

    public void run() {
        for (int i = 0; i < value; ++i) {
            _buf.put(i);
        }
    }
}

class Consumer implements Runnable {
    private Buffer _buf;
    private int upperBound;
    private int value;
    public Consumer(Buffer buf, int upperBound, int value) {
        _buf = buf;
        this.upperBound = upperBound;
        this.value = value;
    }

    public void run() {
        for (int i = 0; i < value; ++i) {
            _buf.get();
        }
    }
}

class Buffer {

    private int _size;
    private int iterator = -1;
    private int[] buffer;
    private Lock lock = new ReentrantLock();

    public Buffer(int size) {
        _size = size;
        buffer = new int[size];
    }

    private boolean canPut() {
        return iterator + 1 < _size;
    }

    public void put(int i) {
        lock.lock();
        while (!canPut()) {
            lock.unlock();
            lock.lock();
        }
        iterator++;
        buffer[iterator] = i;
        lock.unlock();
    }

    private boolean canGet() {
        return iterator >= 0;
    }

    public int get() {
        lock.lock();
        while (!canGet()) {
            lock.unlock();
            lock.lock();
        }
        int k = buffer[iterator];
        iterator--;
        lock.unlock();
        return k;
    }
}

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int M = 1;
        int m = 10;
        int n = m;

        Random rn = new Random();

        int checker = 0;
        int newValue;
        for(int res = 0; res < 1;) {
            checker = 0;
            Buffer buffer = new Buffer(2 * M);
            long begin = System.nanoTime();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(m + n);
            for (int i = 0; i < m; i++) {
                newValue = rn.nextInt(M) + 1;
                checker += newValue;
                executor.execute(new Producer(buffer, M, newValue));
            }
            for (int i = 0; i < n; i++) {
                newValue = rn.nextInt(M) + 1;
                checker -= newValue;
                executor.execute(new Consumer(buffer, M, newValue));
            }
            executor.shutdown();
            if (checker < 0 || checker > 2 * M) {
                executor.awaitTermination(0, TimeUnit.SECONDS);
                executor.shutdownNow();
                continue;
            }

            long end = System.nanoTime();
            System.out.println(end - begin);
            executor.shutdownNow();
            res++;
        }
    }
}