package lab6.src.b;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Widelec {
    private ReentrantLock lock = new ReentrantLock();

    public boolean podnies() {
        return lock.tryLock();
    }

    public void odloz() {
        lock.unlock();
    }
}

class Filozof extends Thread {
    private int _licznik = 0;

    private Widelec leftFork;
    private Widelec rightFork;

    public Filozof(Widelec left, Widelec right) {
        leftFork = left;
        rightFork = right;
    }

    public void run() {
        while (true) {
            if(leftFork.podnies()) {
                if(rightFork.podnies()) {
                    ++_licznik;
                    if (_licznik % 100 == 0) {
                        System.out.println("Filozof: " + Thread.currentThread() +
                                "jadlem " + _licznik + " razy");
                    }
                    leftFork.odloz();
                    rightFork.odloz();
                } else {
                    leftFork.odloz();
                }
            }
        }
    }
}

public class MainB {
    public static void main(String[] args) throws Exception {
        Widelec[] widelce = new Widelec[5];
        Filozof[] filozofowie = new Filozof[5];
        widelce[0] = new Widelec();
        for(int i = 0; i < 5; i++) {
            if(i != 4)
            widelce[i+1] = new Widelec();
            filozofowie[i] = new Filozof(widelce[i%5], widelce[(i+1)%5]);
        }
        long start = System.nanoTime();
        for(int i = 0; i < 5; i++) {
            filozofowie[i].start();
        }
        for(int i = 0; i < 5; i++) {
            filozofowie[i].join();
        }
        long exit = System.nanoTime();
        System.out.println(exit - start);


    }
}
