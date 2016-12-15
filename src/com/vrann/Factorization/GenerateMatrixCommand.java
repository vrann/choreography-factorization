package com.vrann.Factorization;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.SetupConfig;
import com.vrann.Client.RandomMatrixGenerator;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class GenerateMatrixCommand {

    public static void main(String[] args) {
        try {
            ChanelInterface driver = new ChanelFactory().getChanelDriver();

            int R = Integer.parseInt(args[0]);
            int M = Integer.parseInt(args[1]);
            int clusterSize = Integer.parseInt(args[2]);
            int numInCluster = Integer.parseInt(args[3]);
            int count = (R / clusterSize);
            int startFrom = count * numInCluster;

            int K = 0;
            for (int I = startFrom; I < startFrom + count; I++) {
                for (int J = 0; J < R; J++) {
                    System.out.printf("process generate %s %s \n", I, J);
                    RandomMatrixGenerator.generateRandomMatrix(SetupConfig.get().getLocalDataDir() + "/A/A-" + I + "-" + J, M, M);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("K", Integer.toString(K));
                    map.put("R", Integer.toString(R));
                    map.put("I", Integer.toString(I));
                    map.put("J", Integer.toString(J));
                    map.put("address", SetupConfig.get().getNetworkAddress());
                    JSONObject reply = new JSONObject(map);

                    if (I == J && I == K) {
                        driver.send(Chanels.A00, reply);
                    } else if (I == 0) {
                        driver.send(Chanels.A01, reply);
                    } else if (J == 0) {
                        driver.send(Chanels.A10, reply);
                    } else {
                        driver.send(Chanels.A11, reply);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
