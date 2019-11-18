package lab6.src.c;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Lokaj extends Thread {
    ReentrantLock lock = new ReentrantLock();
    Queue<Filozof> queue = new ConcurrentLinkedQueue<>();
    AtomicInteger forks = new AtomicInteger(5);

    public void run() {
        while (true) {
            if(!queue.isEmpty() && forks.get() >= 2) {
                lock.lock();
                Filozof filozof = queue.peek();
                queue.remove();
                forks.addAndGet(-2);
                filozof.s.release();
                lock.unlock();
            }
        }
    }

    void dajWidelce(Filozof filozof) {
        lock.lock();
        try {
            filozof.s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queue.add(filozof);
        lock.unlock();
    }

    void odbierz(Filozof filozof) {
        lock.lock();
        forks.addAndGet(2);
        filozof.s.release();
        lock.unlock();
    }
}

class Filozof extends Thread {
    Semaphore s = new Semaphore(1);
    private int _licznik = 0;
    Lokaj l;

    Filozof(Lokaj l) {
        this.l = l;
    }

    public void run() {
        while (true) {
            l.dajWidelce(this);
            try {
                s.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++_licznik;
            if (_licznik % 100 == 0) {
                System.out.println("Filozof: " + Thread.currentThread() +
                        "jadlem " + _licznik + " razy");
            }
            l.odbierz(this);
        }
    }
}

public class MainC {
    public static void main(String[] args) throws Exception {
        Lokaj lokaj = new Lokaj();
        Filozof[] filozofowie = new Filozof[5];
        for(int i = 0; i < 5; i++) {
            filozofowie[i] = new Filozof(lokaj);
        }
        long start = System.nanoTime();
        lokaj.start();
        for(int i = 0; i < 5; i++) {
            filozofowie[i].start();
        }



        long exit = System.nanoTime();
        System.out.println(exit - start);


        for(int i = 0; i < 5; i++) {
            filozofowie[i].join();
        }

    }
}
