package lab9.src.zad3;


import org.jcsp.lang.*;

class Producer implements CSProcess {

    private One2OneChannelInt producerChannel;

    public Producer (One2OneChannelInt producerChannel) {
        this.producerChannel = producerChannel;
    }

    @Override
    public void run(){
        int i = 0;
        while(true){
            producerChannel.in().read();
            producerChannel.out().write(i);
            i++;
        }
    }

}

class Consumer implements CSProcess {
    private One2OneChannelInt consumerChannels;
    private int n;

    public Consumer (final One2OneChannelInt consumerChannels, int n) {
        this.consumerChannels = consumerChannels;
        this.n = n;
    }

    @Override
    public void run(){

        while(true) {
            consumerChannels.out().write(1);
            int x = consumerChannels.in().read();
            System.out.println(x);
        }
    }
}



class Buffer implements CSProcess {
    private One2OneChannelInt producerChannel;
    private One2OneChannelInt consumerChannels;
    private int id;

    public Buffer(One2OneChannelInt producerChannel, One2OneChannelInt consumerChannels, int i) {
        this.producerChannel = producerChannel;
        this.consumerChannels = consumerChannels;
        this.id = i;
    }

    @Override
    public void run() {
        while(true){
            producerChannel.out().write(1);
            int x = producerChannel.in().read();
            consumerChannels.in().read();
            consumerChannels.out().write(x);
            System.out.println(x);

        }
    }
}

class Main {
    private One2OneChannelInt[] bufferChannels;
    private One2OneChannelInt consumerChannel;
    private One2OneChannelInt producerChannel;
    private Consumer consumer;
    private Producer producer;
    private Buffer[] buf;

    private StandardChannelIntFactory factory = new StandardChannelIntFactory();

    Main(int n){
        producerChannel = factory.createOne2One();
        consumerChannel = factory.createOne2One();


        consumer= new Consumer(consumerChannel, n);
        producer = new Producer(producerChannel);
        buf = new Buffer[n];
        bufferChannels = new One2OneChannelInt[n];


        bufferChannels[0] = factory.createOne2One();
        buf[0] = new Buffer(producerChannel, bufferChannels[0], 0);
        for(int i = 1; i < buf.length-1; i++){
            bufferChannels[i] = factory.createOne2One();
            buf[i] = new Buffer(bufferChannels[i-1], bufferChannels[i], i);
        }
        buf[n-1] = new Buffer(bufferChannels[n-2], consumerChannel, 9);
    }
    public static void main(String[] args){
        final int n = 10;
        Main main = new Main(n);
        Parallel parallel = new Parallel();
        for(Buffer b: main.buf){
            parallel.addProcess(b);
        }
        parallel.addProcess(main.consumer);
        parallel.addProcess(main.producer);
        parallel.run();
    }
}