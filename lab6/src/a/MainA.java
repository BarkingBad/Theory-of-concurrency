package lab6.src.a;


import java.util.concurrent.locks.ReentrantLock;

class Widelec {
    private ReentrantLock lock = new ReentrantLock();

    public void podnies() {
        lock.lock();
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
            leftFork.podnies();
            rightFork.podnies();

            // jedzenie
            ++_licznik;
            if (_licznik % 100 == 0) {
                System.out.println("Filozof: " + Thread.currentThread() +
                        "jadlem " + _licznik + " razy");
            }
            // koniec jedzenia
            leftFork.odloz();
            rightFork.odloz();
        }
    }
}

public class MainA {
    public static void main(String[] args) {
        Widelec[] widelce = new Widelec[5];
        Filozof[] filozofowie = new Filozof[5];
        widelce[0] = new Widelec();
        for(int i = 0; i < 5; i++) {
            if(i != 4)
            widelce[i+1] = new Widelec();
            filozofowie[i] = new Filozof(widelce[i%5], widelce[(i+1)%5]);
        }

        for(int i = 0; i < 5; i++) {
            filozofowie[i].start();
        }
    }
}
