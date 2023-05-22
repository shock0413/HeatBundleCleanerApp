package com.example.myapplication.AsyncFrameSocket;

import com.example.myapplication.LogManager;

public class AsyncSocketClass {
    protected  int id;

    public Event<AsyncSocketErrorEventArgs> onError = new Event<>();
    public Event<AsyncSocketReceiveEventArgs> onReceive = new Event<>();
    public Event<AsyncSocketConnectionEventArgs> onConnect = new Event<>();
    public Event<AsyncSocketConnectionEventArgs> onClose = new Event<>();
    public Event<AsyncSocketSendEventArgs> onSend = new Event<>();
    public Event<AsyncSocketAcceptEventArgs> onAccept = new Event<>();

    public AsyncSocketClass() {
        this.id = -1;
    }

    public AsyncSocketClass(int id) {
        this.id = id;
    }

    final public int ID = this.id;

    protected void errorOccured(AsyncSocketErrorEventArgs e) {
        LogManager.e("[AsyncSocketClass] [errorOccured] " + e.getAsyncSocketException().getMessage());

        Event<AsyncSocketErrorEventArgs> handler = onError;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }

    protected void connected(AsyncSocketConnectionEventArgs e) {
        Event<AsyncSocketConnectionEventArgs> handler = onConnect;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }

    protected void closed(AsyncSocketConnectionEventArgs e) {
        Event<AsyncSocketConnectionEventArgs> handler = onConnect;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }

    protected void send(AsyncSocketSendEventArgs e) {
        Event<AsyncSocketSendEventArgs> handler = onSend;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }

    protected void received(AsyncSocketReceiveEventArgs e) {
        Event<AsyncSocketReceiveEventArgs> handler = onReceive;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }

    protected void accepted(AsyncSocketAcceptEventArgs e) {
        Event<AsyncSocketAcceptEventArgs> handler = onAccept;

        if (handler != null) {
            handler.raiseEvent(this, e);
        }
    }
}
