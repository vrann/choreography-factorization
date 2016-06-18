package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Client.RandomMatrixGenerator;
import com.vrann.Factorization.Chanels;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class GenerateChanel {
    private String address;
    private String basePath;

    public GenerateChanel(String address, String basePath) {
        this.address = address;
        this.basePath = basePath;
    }

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message messageGenerate = driver.getMessageFor(Chanels.generate);
        if (messageGenerate == null) {
            return;
        }

        JSONObject dataGenerate = new JSONObject(new JSONTokener(messageGenerate.getBody()));
        int M = Integer.parseInt(dataGenerate.get("M").toString());
        int N = Integer.parseInt(dataGenerate.get("N").toString());
        int K = Integer.parseInt(dataGenerate.get("K").toString());
        int J = Integer.parseInt(dataGenerate.get("J").toString());
        int R = Integer.parseInt(dataGenerate.get("R").toString());

        RandomMatrixGenerator.generateRandomMatrix(basePath + "/matrix/A/A-" + K + "-" + J, M, N);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(K));
        map.put("R", Integer.toString(R));
        map.put("J", Integer.toString(J));
        map.put("address", address);
        JSONObject reply = new JSONObject(map);

        if (K == J && K == 0) {
            driver.send(Chanels.A00, reply);
        } else if (K == 0) {
            driver.send(Chanels.A01, reply);
        } else if (J == 0) {
            driver.send(Chanels.A10, reply);
        } else {
            driver.send(Chanels.A11, reply);
        }

        driver.delete(Chanels.generate, messageGenerate.getReceiptHandle());
    }
}
