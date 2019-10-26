package lab3.src.streambuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

class ProducerConsumer extends Thread {
    private Buffer _buf;
    private int streamPriority;
    private Function<Integer, Integer> _func;
    public ProducerConsumer(Buffer buf, int sp, Function<Integer, Integer> func) {
        _buf = buf;
        streamPriority = sp;
        _func = func;
    }

    public void run() {
        for (int i = 0; i < _buf.get_size(); ++i) {
            _buf.execute(i, streamPriority, _func);
        }
    }
}

class BufferCell {
    private int payload;
    private int expectedPriority = 0;

    public synchronized void operateOn(int streamPriority, Function<Integer, Integer> func) {
        while (streamPriority != expectedPriority) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Ooops, something went wrong " + e.getMessage());
            }
        }
        payload = func.apply(payload);
        expectedPriority++;
        notifyAll();
    }
}

class Buffer {

    private int _size;
    BufferCell[] bufferCells;
    public Buffer(int size) {
        _size = size;
        bufferCells = new BufferCell[size];
        for(int i = 0; i < size; i++) {
            bufferCells[i] = new BufferCell();
        }
    }

    public void execute(int i, int streamPriority, Function<Integer, Integer> func) {
        bufferCells[i].operateOn(streamPriority, func);
    }

    public int get_size() {
        return _size;
    }
}

public class Main {
    public static void main(String[] args) {

        Buffer buffer = new Buffer(100);
        ArrayList<Function<Integer, Integer>> functions = new ArrayList<>(Arrays.asList(
            x -> {
                Random rn = new Random();
                int randomNumber = rn.nextInt(100) + 1;
                System.out.println("Producing number: " + randomNumber);
                return randomNumber;
            },
            x -> x / 2,
            x -> x * 5,
            x -> x % 13,
            x -> x + 2,
            x -> x << 2,
            x -> {
                System.out.println("Consuming number: " + x);
                return x;
            }
        ));
        int countOfWorkers = functions.size();
        ProducerConsumer[] workers = new ProducerConsumer[countOfWorkers];

        for(int i = 0; i < countOfWorkers; i++) {
            workers[i] = new ProducerConsumer(buffer, i, functions.get(i));
            workers[i].start();
        }

        try {
            for(int i = 0; i < countOfWorkers; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Ooops, something went wrong " + e.getMessage());
        }
    }
}
