package com.example.myapplication.AsyncFrameSocket;

public class AsyncSocketConnectionEventArgs extends EventArgs {
    private int id = 0;

    public AsyncSocketConnectionEventArgs(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}
