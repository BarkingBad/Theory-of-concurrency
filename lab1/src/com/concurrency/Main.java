package com.concurrency;


import java.util.HashMap;
import java.util.Map;


class Semaphore {

    private int mutex = 1;

    public void acquire() {
        try {
            Thread.sleep(0,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(mutex <= 0){
            try {
                Thread.sleep(0,10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mutex--;



    }

    public void release() {
        try {
            Thread.sleep(0,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mutex++;
        try {
            Thread.sleep(0,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Main {

    static int counter = 0;

    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();

        Semaphore s = new Semaphore();

        for(int j = 0; j < 5; j++) {
            System.out.println(j);
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        s.acquire();
                        counter++;
                        s.release();
                    }
                }
            });

            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        s.acquire();
                        counter--;
                        s.release();
                    }
                }
            });
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                System.out.println("Ooops, something went wrong " + e.getMessage());
            }
            if(map.get(counter) == null) {
                map.put(counter, 1);
            } else {

                map.put(counter, map.get(counter) + 1);
            }

            counter = 0;
        }

        map.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
    }
}
