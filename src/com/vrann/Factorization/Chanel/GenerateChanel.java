package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Choreography.SetupConfig;
import com.vrann.Client.RandomMatrixGenerator;
import com.vrann.Factorization.Chanels;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class GenerateChanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageGenerate = driver.getMessageFor(Chanels.generate);
        if (messageGenerate == null) {
            return;
        }

        JSONObject dataGenerate = new JSONObject(new JSONTokener(messageGenerate.getBody()));
        int M = Integer.parseInt(dataGenerate.get("M").toString());
        int N = Integer.parseInt(dataGenerate.get("N").toString());
        int K = Integer.parseInt(dataGenerate.get("K").toString());
        int I = Integer.parseInt(dataGenerate.get("I").toString());
        int J = Integer.parseInt(dataGenerate.get("J").toString());
        int R = Integer.parseInt(dataGenerate.get("R").toString());
        System.out.printf("process generate %s %s \n", I, J);

        RandomMatrixGenerator.generateRandomMatrix(SetupConfig.get().getLocalDataDir() + "/A/A-" + I + "-" + J, M, N);

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

        driver.delete(Chanels.generate, messageGenerate.getId());
    }
}
