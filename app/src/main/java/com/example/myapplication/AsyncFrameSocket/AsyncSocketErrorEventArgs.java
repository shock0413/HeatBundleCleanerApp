package com.example.myapplication.AsyncFrameSocket;

public class AsyncSocketErrorEventArgs extends EventArgs {
    private Exception exception;
    private int id = 0;

    public AsyncSocketErrorEventArgs(int id, Exception exception) {
        this.id = id;
        this.exception = exception;
    }

    public Exception getAsyncSocketException() {
        return this.exception;
    }

    public int getID() {
        return this.id;
    }
}
