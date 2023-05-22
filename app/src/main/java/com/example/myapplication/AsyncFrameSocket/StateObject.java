package com.example.myapplication.AsyncFrameSocket;

import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;

public class StateObject {
    final private int BUFFER_SIZE = 327680;

    private AsynchronousSocketChannel worker;
    private byte[] buffer;

    public StateObject(AsynchronousSocketChannel worker) {
        this.worker = worker;
        this.buffer = new byte[BUFFER_SIZE];
    }

    public AsynchronousSocketChannel getWorker() {
        return this.worker;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getBufferSize() {
        return this.BUFFER_SIZE;
    }
}
