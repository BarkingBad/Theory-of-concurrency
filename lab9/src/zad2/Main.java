package lab9.src.zad2;

import org.jcsp.lang.*;

class Producer implements CSProcess {

    private ChannelInputInt[] fromBuffer;
    private ChannelOutputInt[] toBuffer;
    private int n;

    public Producer (ChannelInputInt[] fromBuffer, ChannelOutputInt[] toBuffer, int n) {
        this.fromBuffer = fromBuffer;
        this.toBuffer = toBuffer;
        this.n = n;
    }

    @Override
    public void run(){

        while(true){
            for(int i = 0; i < n; i++) {
                fromBuffer[i].read();
                toBuffer[i].write(i);
            }
        }
    }
}

class Consumer implements CSProcess {
    private ChannelInputInt[] channels;
    private int n;

    public Consumer (final ChannelInputInt[] in, int n) {
        channels = in;
        this.n = n;
    }

    @Override
    public void run(){

        while(true) {
            for(int i = 0; i < n; i++) {
                int x = channels[i].read();
                System.out.println(x);

            }
        }
    }
}



class Buffer implements CSProcess {
    private One2OneChannelInt producerChannels;
    private ChannelOutputInt toConsumer;

    public Buffer(One2OneChannelInt producerChannels, ChannelOutputInt toConsumer) {
        this.producerChannels = producerChannels;
        this.toConsumer = toConsumer;
    }

    @Override
    public void run() {
        while(true){
            producerChannels.out().write(1);
            int x = producerChannels.in().read();
            toConsumer.write(x);
        }
    }
}

class Main {
    private One2OneChannelInt[] producerChannels;
    private One2OneChannelInt[] consumerChannels;
    private Consumer consumer;
    private Producer producer;
    private Buffer[] buf;

    Main(int n){
        producerChannels = Channel.one2oneIntArray(n);
        consumerChannels = Channel.one2oneIntArray(n);

        ChannelInputInt[] producerIn = Channel.getInputArray(producerChannels);
        ChannelOutputInt[] producerOut = Channel.getOutputArray(producerChannels);
        ChannelInputInt[] consumerIn = Channel.getInputArray(consumerChannels);
        ChannelOutputInt[] consumerOut = Channel.getOutputArray(consumerChannels);

        consumer= new Consumer(consumerIn, n);
        producer = new Producer(producerIn, producerOut, n);
        buf = new Buffer[n];
        for(int i = 0; i < buf.length; i++){
            buf[i] = new Buffer(producerChannels[i], consumerOut[i]);
        }
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