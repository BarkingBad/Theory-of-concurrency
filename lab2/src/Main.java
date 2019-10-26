package lab2.src;

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

class CountingSemafor {
    private boolean _stan = true;
    private int _size = 0;

    Semafor s1 = new Semafor(true);
    Semafor s2 = new Semafor(false);

    public CountingSemafor(int size) {
        _size = size;
    }

    public void P() {
        s1.P();
        _size--;
        if(_size < 0 ) {
            s1.V();
            s2.P();
        } else {
            s1.V();
        }
    }

    public void V() {
        s1.P();
        _size++;
        if(_size <= 0) {
            s2.V();
            s1.V();
        } else {
            s1.V();
        }
    }
}


class Counter {
    private int _val;
    public Counter(int n) {
        _val = n;
    }
    public void inc() {
        _val++;
    }
    public void dec() {
        _val--;
    }
    public int value() {
        return _val;
    }
}

class IThread extends Thread {

    private Counter counter;
    private CountingSemafor semafor;

    public IThread(Counter c, CountingSemafor s) {
        counter = c;
        semafor = s;
    }

    public void run() {
        for (int i = 0; i < 1000000; i++) {
            System.out.println("i " + i);
            semafor.P();
            counter.inc();
            semafor.V();
        }
    }
}

class DThread extends Thread {

    private Counter counter;
    private CountingSemafor semafor;

    public DThread(Counter c, CountingSemafor s) {

        counter = c;
        semafor = s;
    }
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            System.out.println("d " + i);
            semafor.P();
            counter.dec();
            semafor.V();


        }
    }
}




public class Main {

    public static void main(String[] args) {

        CountingSemafor s = new CountingSemafor(1);
        Counter c = new Counter(0);
        IThread t1 = new IThread(c, s);
        DThread t2 = new DThread(c, s);


        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Ooops, something went wrong " + e.getMessage());
        }

        System.out.println(c.value());

    }
}
