package lab5.src.ReadersWriters;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Reader extends Thread {

    private Semaphore resourceAccess;
    private Semaphore readCountAccess;
    private Semaphore serviceQueue;
    private AtomicInteger readerCount;

    public Reader(Semaphore resourceAccess, Semaphore readCountAccess, Semaphore serviceQueue, AtomicInteger readerCount) {
        this.resourceAccess = resourceAccess;
        this.readCountAccess = readCountAccess;
        this.serviceQueue = serviceQueue;
        this.readerCount = readerCount;
    }

    @Override
    public void run() {
        try {
            serviceQueue.acquire();
            readCountAccess.acquire();

            if (readerCount.get() == 0) {
                resourceAccess.acquire();
            }
            readerCount.addAndGet(1);
            serviceQueue.release();
            readCountAccess.release();
            //System.out.println("Thread "+Thread.currentThread().getName() + " is READING");
            Thread.sleep(500);
            //System.out.println("Thread "+Thread.currentThread().getName() + " leaves");

            readCountAccess.acquire();
            readerCount.addAndGet(-1);
            if(readerCount.get() == 0) {
                resourceAccess.release();
            }
            readCountAccess.release();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Writer extends Thread {

    private Semaphore resourceAccess;
    private Semaphore readCountAccess;
    private Semaphore serviceQueue;

    public Writer(Semaphore resourceAccess, Semaphore readCountAccess, Semaphore serviceQueue) {
        this.resourceAccess = resourceAccess;
        this.readCountAccess = readCountAccess;
        this.serviceQueue = serviceQueue;
    }

    @Override
    public void run() {
        try {
            serviceQueue.acquire();
            resourceAccess.acquire();
            serviceQueue.release();
            //System.out.println("Thread "+Thread.currentThread().getName() + " is WRITING");
            Thread.sleep(500);
            //System.out.println("Thread "+Thread.currentThread().getName() + " leaves");
            resourceAccess.release();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}

public class Main {

    public static void main(String[] args) throws Exception {

        Semaphore resourceAccess = new Semaphore(1);
        Semaphore readCountAccess = new Semaphore(1);
        Semaphore serviceQueue = new Semaphore(1);
        AtomicInteger readCount = new AtomicInteger(0);


        for(int k = 1; k < 10; k++) {
            int readersCount = k*10;
            int writersCount = k;
            Reader[] readers = new Reader[readersCount];
            Writer[] writers = new Writer[writersCount];

            long start = System.nanoTime();

            for (int j = 0; j < k; j++) {
                for (int i = 0; i < readersCount / k; i++) {
                    readers[j * 10 + i] = new Reader(resourceAccess, readCountAccess, serviceQueue, readCount);
                    readers[j * 10 + i].start();
                }

                for (int i = 0; i < writersCount / k; i++) {
                    writers[j] = new Writer(resourceAccess, readCountAccess, serviceQueue);
                    writers[j].start();
                }
            }

            for (int i = 0; i < readersCount; i++) {
                readers[i].join();
            }

            for (int i = 0; i < writersCount; i++) {
                writers[i].join();
            }

            long end = System.nanoTime();

            System.out.println(end - start);
        }
    }
}