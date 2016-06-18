package com.vrann.Factorization;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.Chanel.AWSSQSDriver;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class InitdataCommand {

    public static void main(String[] args) {
        AWSSQSDriver driver = new AWSSQSDriver();

        int R = Integer.parseInt(args[0]);
        int M = Integer.parseInt(args[1]);
        for (int K = 0; K < R; K++) {
            for (int J = 0; J < R; J++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("M", Integer.toString(M));
                map.put("N", Integer.toString(M));
                map.put("K", Integer.toString(K));
                map.put("J", Integer.toString(J));
                map.put("R", Integer.toString(R));


                JSONObject reply = new JSONObject(map);
                driver.send(Chanels.generate, reply);
            }
        }
    }

}
