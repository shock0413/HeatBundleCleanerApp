package com.example.myapplication.AsyncFrameSocket;

public class AsyncSocketReceiveEventArgs extends EventArgs {
    private int id = 0;
    private int receiveBytes;
    private byte[] receiveData;

    public AsyncSocketReceiveEventArgs(int id, int receiveBytes, byte[] receiveData) {
        this.id = id;
        this.receiveBytes = receiveBytes;
        this.receiveData = receiveData;
    }

    public int getID() {
        return this.id;
    }

    public int getReceiveBytes() {
        return this.receiveBytes;
    }

    public byte[] getReceiveData() {
        return this.receiveData;
    }
}
