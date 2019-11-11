package lab5.src.list;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class FineGrainedNode {
    private Object o;
    private FineGrainedNode nextNode;
    Lock lock = new ReentrantLock();

    FineGrainedNode(Object o) {
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
            nextNode.lock.lock();
            if (this.nextNode.o == o) {
                this.lock.unlock();
                nextNode.lock.unlock();
                return true;
            } else {
                this.lock.unlock();
                return nextNode.contains(o);
            }
        } else {
            this.lock.unlock();
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
            this.nextNode.lock.lock();
            if (nextNode.o == o) {
                this.nextNode = nextNode.nextNode;
                this.lock.unlock();
                return true;
            } else {
                this.lock.unlock();
                return nextNode.remove(o);
            }
        } else {
            this.lock.unlock();
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
            this.nextNode = new FineGrainedNode(o);
            this.lock.unlock();
            return true;
        } else {
            this.nextNode.lock.lock();
            this.lock.unlock();
            return nextNode.add(o);
        }
    }
}


public class FineGrained {
    public static void main(String[] args) throws InterruptedException {

        //Setup
        FineGrainedNode head = new FineGrainedNode(new Object());
        Object[] objects = new Object[100];

        Thread[] runner = new Thread[10];

        //Adding to list
        long addingStart = System.nanoTime();

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                objects[10 * i + j] = new Object();
            }
            final int bound = i;
            runner[i] = new Thread( () -> {
                for(int k = bound*10; k < ((bound+1) * 10); k++) {
                    head.lock.lock();
                    head.add(objects[k]);
                }
            });
            runner[i].start();
        }

        for(int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long addingEnd = System.nanoTime();

        //Containing

        Random rand = new Random();

        long containingStart = System.nanoTime();

        for(int i = 0; i < 10; i++) {
            runner[i] = new Thread( () -> {
                head.lock.lock();
                head.contains(objects[rand.nextInt(99)]);
            });
            runner[i].start();
        }

        for(int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long containingEnd = System.nanoTime();
        //Removing
        long removingStart = System.nanoTime();

        for(int i = 0; i < 10; i++) {
            runner[i] = new Thread( () -> {
                head.lock.lock();
                head.remove(objects[rand.nextInt(99)]);
            });
            runner[i].start();
        }

        for(int i = 0; i < 10; i++) {
            runner[i].join();
        }

        long removingEnd = System.nanoTime();

        // Output

        System.out.println("Adding time: " + (addingEnd - addingStart));
        System.out.println("Containing time: " + (containingEnd - containingStart));
        System.out.println("Removing time: " + (removingEnd - removingStart));
    }
}
