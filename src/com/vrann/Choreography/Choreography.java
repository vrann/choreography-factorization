package com.vrann.Choreography;

/**
 * Created by etulika on 6/15/16.
 */
public class Choreography {

    public static void main(String[] args) {
        EventLoop loop = new EventLoop();
        String address = args[0];
        String basePath = args[1];
        loop.listenChannels(address, basePath);
    }
}
