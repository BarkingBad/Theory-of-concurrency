package lab9.src.zad1;

import org.jcsp.lang.*;

class Producer implements CSProcess {
    private One2OneChannelInt channel;
    private int start;

    public Producer(final One2OneChannelInt out, int start) {
        channel = out;
        this.start = start;
    }

    public void run() {
        int item;
        for(int k = start; k < start+100; k++) {
            item = k;
            channel.out().write(item);
        }
        channel.out().write(-1);
        System.out.println("Producer " + start + " ended.");
    }
}

class Consumer implements CSProcess {
    private One2OneChannelInt in;
    private One2OneChannelInt req;
    public Consumer(final One2OneChannelInt req, final One2OneChannelInt in) {
        this.req = req;
        this.in = in;
    }
    public void run() {
        int item;
        while (true){
            req.out().write(0);
            item = in.in().read();
            if (item < 0)
                break;
            System.out.println(item);
        }
        System.out.println("Consumer ended.");
    }
}

class Buffer implements CSProcess {
    private One2OneChannelInt[] in;
    private One2OneChannelInt[] req;
    private One2OneChannelInt[] out;
    private int[] buffer = new int[10];

    int hd = -1;
    int tl = -1;
    public Buffer (final One2OneChannelInt[] in, final One2OneChannelInt[] req, final One2OneChannelInt[] out) {
        this.in = in;
        this.req = req;
        this.out = out;
    }

    public void run () {
        final Guard[] guards = { in[0].in(), in[1].in(), req[0].in(), req[1].in() };
        final Alternative alt = new Alternative(guards);
        int countdown = 4;
        while (countdown > 0) {
            int index = alt.select();
            switch (index) {
                case 0:
                case 1:
                    if (hd < tl + 11) {
                        int item = in[index].in().read();
                        if (item < 0)
                            countdown--;
                        else {
                            hd++;
                            buffer[hd%buffer.length] = item;
                        }
                    }
                    break;
                case 2:
                case 3:
                    if (tl < hd) {
                        req[index-2].in().read();
                        tl++;
                        int item = buffer[tl%buffer.length];
                        out[index-2].out().write(item);
                    }
                    else if (countdown <= 2) {
                        req[index-2].in().read();
                        out[index-2].out().write(-1);
                        countdown--;
                    }
                    break;
            }
        }
        System.out.println("Buffer ended.");
    }
}

public final class Main {

    public static void main(String[] args) {

        new Main();
    }

    public Main() {

        StandardChannelIntFactory factory = new StandardChannelIntFactory();

        final One2OneChannelInt[] prodChan = { factory.createOne2One(), factory.createOne2One() };
        final One2OneChannelInt[] consReq = { factory.createOne2One(), factory.createOne2One() };
        final One2OneChannelInt[] consChan = { factory.createOne2One(), factory.createOne2One() };
        CSProcess[] procList = {
                new Producer(prodChan[0], 0),
                new Producer(prodChan[1], 100),
                new Buffer(prodChan, consReq, consChan),
                new Consumer(consReq[0], consChan[0]),
                new Consumer(consReq[1], consChan[1])
        };
        Parallel par = new Parallel(procList);
        par.run();
    }
}

