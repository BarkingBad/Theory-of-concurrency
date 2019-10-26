package lab3.src.thirdimplementation;

class Semafor {
    private boolean _stan = true;
    private int _czeka = 0;

    public Semafor(boolean stan) {
        _stan = stan;
    }

    public synchronized void P() {
        _czeka++;
        while(_stan == false) {
            try{
                this.wait();
            } catch(InterruptedException e) {
                System.out.println("Ooops, something went wrong " + e.getMessage());
            }
        }
        _czeka--;
        _stan = false;
    }

    public synchronized void V() {
        if(_czeka > 0) {
            this.notify();
        }
        _stan = true;
    }
}

class Producer extends Thread {
    private Buffer _buf;
    private Semafor _sem;

    public Producer(Buffer buf) {
        _buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            boolean res = _buf.put(i);
            if(res == false) {
                i--;
            }
        }
    }
}

class Consumer extends Thread {
    private Buffer _buf;
    private Semafor _sem;

    public Consumer(Buffer buf) {
        _buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            int res = _buf.get();
            if(res == -1) {
                i--;
            } else {
                System.out.println(res);
            }
        }
    }
}

class Buffer {

    private int _size;
    private int iterator = -1;
    private int[] buffer;
    Semafor semafor = new Semafor(true);

    public Buffer(int size) {
        _size = size;
        buffer = new int[size];
    }

    private boolean canPut() {
        return iterator + 1 < _size;
    }

    public boolean put(int i) {
        boolean res;
        semafor.P();
        if (!canPut()) {
            res = false;
        } else {
            res = true;
            iterator++;
            buffer[iterator] = i;
        }
        semafor.V();
        return res;
    }

    private boolean canGet() {
        return iterator >= 0;
    }

    public int get() {
        int res;
        semafor.P();

        if (!canGet()) {
            res = -1;
        } else {
            res = buffer[iterator];
            iterator--;
        }
        semafor.V();
        return res;
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
