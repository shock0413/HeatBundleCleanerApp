package com.example.myapplication.AsyncFrameSocket;

import java.util.ArrayList;

final public class Event <TEventArgs extends EventArgs> {
    private ArrayList<IEventHandler<TEventArgs>> observerList = new ArrayList<>();

    public void raiseEvent(Object sender, TEventArgs e) {
        for (IEventHandler<TEventArgs> handler : this.observerList) {
            handler.eventReceived(sender, e);
        }
    }

    public void addEventHandler(IEventHandler<TEventArgs> handler) {
        this.observerList.add(handler);
    }

    public void removeEventHandler(IEventHandler<TEventArgs> handler) {
        this.observerList.remove(handler);
    }
}
