package com.example.myapplication.AsyncFrameSocket;

public class AsyncSocketSendEventArgs extends EventArgs {
    private int id = 0;
    private int sendBytes;

    public AsyncSocketSendEventArgs(int id, int sendBytes) {
        this.id = id;
        this.sendBytes = sendBytes;
    }

    public int getSendBytes() {
        return this.sendBytes;
    }

    public int getID() {
        return this.id;
    }
}
