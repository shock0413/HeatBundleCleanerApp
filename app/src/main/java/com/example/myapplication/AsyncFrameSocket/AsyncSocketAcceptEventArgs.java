package com.example.myapplication.AsyncFrameSocket;

import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;

public class AsyncSocketAcceptEventArgs extends EventArgs {
    private AsynchronousSocketChannel conn;

    public AsyncSocketAcceptEventArgs(AsynchronousSocketChannel conn) {
        this.conn = conn;
    }

    public AsynchronousSocketChannel getWorker() {
        return this.conn;
    }
}
