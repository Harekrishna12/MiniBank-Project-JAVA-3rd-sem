package service;

import util.Command;

// Shared bounded buffer: producer calls put(), consumer calls take()
public class TransactionBuffer {
    private final Command[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public TransactionBuffer(int capacity) {
        buffer = new Command[capacity];
    }

    public synchronized void put(Command command) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[in] = command;
        in = (in + 1) % buffer.length;
        count++;
        notify();
    }

    public synchronized Command take() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        Command command = buffer[out];
        buffer[out] = null;
        out = (out + 1) % buffer.length;
        count--;
        notify();
        return command;
    }
}
