package com.vrann.Factorization;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class InitdataCommand {

    public static void main(String[] args) {
        try {
            ChanelInterface driver = new ChanelFactory().getChanelDriver();

            int R = Integer.parseInt(args[0]);
            int M = Integer.parseInt(args[1]);
            int K = 0;
                for (int I = 0; I < R; I++) {
                for (int J = 0; J < R; J++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("M", Integer.toString(M));
                    map.put("N", Integer.toString(M));
                    map.put("I", Integer.toString(I));
                    map.put("J", Integer.toString(J));
                    map.put("R", Integer.toString(R));
                    map.put("K", Integer.toString(K));

                    JSONObject reply = new JSONObject(map);
                    driver.send(Chanels.generate, reply);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
