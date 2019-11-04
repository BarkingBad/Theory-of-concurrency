package lab4.src.system;

import java.util.Random;

class Producer extends Thread {
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

class Consumer extends Thread {
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

    public Buffer(int size) {
        _size = size;
        buffer = new int[size];
    }

    private boolean canPut() {
        return iterator + 1 < _size;
    }

    public synchronized void put(int i) {
        while (!canPut()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Ooops, something went wrong " + e.getMessage());
            }
        }
        iterator++;
        buffer[iterator] = i;
        notify();
    }

    private boolean canGet() {
        return iterator >= 0;
    }

    public synchronized int get() {
        while (!canGet()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Ooops, something went wrong " + e.getMessage());
            }
        }
        int k = buffer[iterator];
        iterator--;
        notify();
        return k;
    }
}


public class Main {

    public static void main(String[] args) {

        int M = 4;
        int m = 10;
        int n = m;
        Random rn = new Random();
        int checker = 0;
        int newValue;
        for(int res = 0; res < 100;) {
            checker = 0;
            Buffer buffer = new Buffer(2*M);
            long begin = System.nanoTime();

            Producer producer[] = new Producer[m];
            Consumer consumer[] = new Consumer[n];
            for (int i = 0; i < m; i++) {
                newValue = rn.nextInt(M) + 1;
                checker += newValue;
                producer[i] = new Producer(buffer, M, newValue);
                producer[i].start();
            }
            for (int i = 0; i < n; i++) {
                newValue = rn.nextInt(M) + 1;
                checker -= newValue;
                consumer[i] = new Consumer(buffer, M, newValue);
                consumer[i].start();
            }
            if (checker < 0 || checker > 2 * M) {
                continue;
            }
            for (int i = 0; i < m; i++) {
                try {
                    producer[i].join();
                } catch (InterruptedException e) {
                    System.out.println("Ooops, something went wrong " + e.getMessage());
                }
            }
            for (int i = 0; i < n; i++) {
                try {
                    consumer[i].join();
                } catch (InterruptedException e) {
                    System.out.println("Ooops, something went wrong " + e.getMessage());
                }
            }
            long end = System.nanoTime();
            System.out.println(end - begin);
            res++;
        }
    }
}