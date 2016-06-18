package com.vrann.Client;

import com.vrann.Client.Processor;

/**
 * Created by etulika on 5/31/16.
 */
public class ParallelProcessor {
    public static void main(String args[]) {
        /*Processor[][] processors = new Processor[][]{
                new Processor[] {new Processor("p11"), new Processor("p12"), new Processor("p13"), new Processor("p14")},
                new Processor[] {new Processor("p21"), new Processor("p22"), new Processor("p23"), new Processor("p24")},
                new Processor[] {new Processor("p31"), new Processor("p32"), new Processor("p33"), new Processor("p34")},
                new Processor[] {new Processor("p41"), new Processor("p42"), new Processor("p43"), new Processor("p44")},
        };*/
        Processor[][] processors = new Processor[][]{
                new Processor[] {new Processor("p11"), new Processor("p12"), new Processor("p13"), new Processor("p14")},
        };


        String result = processors[0][0].send("factorize");
        System.out.println(result);
    }
}
