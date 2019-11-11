package lab5.src.list;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WhollyBlockedNode {
    private Object o;
    private WhollyBlockedNode nextNode;

    WhollyBlockedNode(Object o) {
        this.o = o;
        this.nextNode = null;
    }

    boolean contains(Object o) {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(nextNode != null) {
            if (this.nextNode.o == o) {
                return true;
            } else {
                return nextNode.contains(o);
            }
        } else {
            return false;
        }
    }

    boolean remove(Object o) {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(this.nextNode != null) {
            if (nextNode.o == o) {
                this.nextNode = nextNode.nextNode;
                return true;
            } else {
                return nextNode.remove(o);
            }
        } else {
            return false;
        }
    }

    boolean add(Object o) {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(this.nextNode == null) {
            this.nextNode = new WhollyBlockedNode(o);
            return true;
        } else {
            return nextNode.add(o);
        }
    }
}

public class WhollyBlocked {

    public static void main(String[] args) throws InterruptedException {

        //Setup
        Object[] objects = new Object[100];
        Thread[] runner = new Thread[10];

        WhollyBlockedNode head = new WhollyBlockedNode(new Object());
        Lock lock = new ReentrantLock();
        //Adding to list
        long addingStart = System.nanoTime();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                objects[10 * i + j] = new Object();
            }
            final int bound = i;
            runner[i] = new Thread(() -> {
                for (int k = bound * 10; k < ((bound + 1) * 10); k++) {
                    lock.lock();
                    head.add(objects[k]);
                    lock.unlock();
                }
            });
            runner[i].start();
        }

        for (int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long addingEnd = System.nanoTime();

        //Containing

        Random rand = new Random();

        long containingStart = System.nanoTime();

        for (int i = 0; i < 10; i++) {
            runner[i] = new Thread(() -> {
                lock.lock();
                head.contains(objects[rand.nextInt(99)]);
                lock.unlock();
            });
            runner[i].start();
        }

        for (int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long containingEnd = System.nanoTime();
        //Removing
        long removingStart = System.nanoTime();

        for (int i = 0; i < 10; i++) {
            runner[i] = new Thread(() -> {
                lock.lock();
                head.remove(objects[rand.nextInt(99)]);
                lock.unlock();
            });
            runner[i].start();
        }

        for (int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long removingEnd = System.nanoTime();

        // Output

        System.out.println("Adding time: " + (addingEnd - addingStart) + "ns");
        System.out.println("Containing time: " + (containingEnd - containingStart) + "ns");
        System.out.println("Removing time: " + (removingEnd - removingStart) + "ns");
    }
}
