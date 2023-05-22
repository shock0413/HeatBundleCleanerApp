package com.example.myapplication.AsyncFrameSocket;

public interface IEventHandler <TEventArgs extends EventArgs> {
    public void eventReceived(Object sender, TEventArgs e);
}
