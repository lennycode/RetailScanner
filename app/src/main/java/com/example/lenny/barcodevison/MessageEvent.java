package com.example.lenny.barcodevison;


import org.greenrobot.eventbus.EventBus;


public class MessageEvent  <T>{
    public String message;
    public T packet;

    public MessageEvent (T s){
        packet = s;

    }

}
