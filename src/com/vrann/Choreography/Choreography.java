package com.vrann.Choreography;

/**
 * Created by etulika on 6/15/16.
 */
public class Choreography {

    public static void main(String[] args) {
        EventLoop loop = new EventLoop();
        try {
            loop.listenChannels(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
