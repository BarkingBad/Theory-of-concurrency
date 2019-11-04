package lab3.src.fourthimplementation;

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
            _buf.put(i);
            System.out.println("Producer " + super.getId() + " " + i);
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
            System.out.println("Consumer " + super.getId() + " " + _buf.get());
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

    public void put(int i) {
        semafor.P();
        while (!canPut()) {
            semafor.V();
            semafor.P();
        }
        iterator++;
        buffer[iterator] = i;
        semafor.V();
    }

    private boolean canGet() {
        return iterator >= 0;
    }

    public int get() {
        semafor.P();
        while (!canGet()) {
            semafor.V();
            semafor.P();
        }
        int res = buffer[iterator];
        iterator--;
        semafor.V();
        return res;
    }
}

public class Main {
    public static void main(String[] args) {

        Buffer buffer = new Buffer(4);



        int pSize = 4;
        int cSize = 4;

        Producer[] producer = new Producer[pSize];
        Consumer[] consumer = new Consumer[cSize];

        for(int i = 0; i < pSize; i++) {
            producer[i] = new Producer(buffer);
            producer[i].start();
        }

        for(int i = 0; i < cSize; i++) {
            consumer[i] = new Consumer(buffer);
            consumer[i].start();
        }

        try {
            for(int i = 0; i < pSize; i++) {
                producer[i].join();
            }

            for(int i = 0; i < cSize; i++) {
                consumer[i].join();
            }
        } catch(InterruptedException e) {
            System.out.println("Ooops, something went wrong " + e.getMessage());
        }

    }
}