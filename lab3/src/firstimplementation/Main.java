package lab3.src.firstimplementation;

class Producer extends Thread {
    private Buffer _buf;

    public Producer(Buffer buf) {
        _buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            _buf.put(i);
        }
    }
}

class Consumer extends Thread {
    private Buffer _buf;

    public Consumer(Buffer buf) {
        _buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            System.out.println(_buf.get());
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

        Buffer buffer = new Buffer(4);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            System.out.println("Ooops, something went wrong " + e.getMessage());
        }
    }
}
