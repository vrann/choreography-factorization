package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.DataDriver;
import com.vrann.Factorization.A00Processor;
import com.vrann.Factorization.Chanels;
import com.vrann.Matrix.DataWriter;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class A00Chanel {

    private String address;
    private String basePath;

    public A00Chanel(String address, String basePath) {

        this.address = address;
        this.basePath = basePath;
    }

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message message = driver.getMessageFor(Chanels.A00);
        if (message != null) {
            /*
             {
                "K": 0,
                "R": 5,
                "address": "52.207.217.226"
             }
             */
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            if (K != 0 || J != 0) {
                throw new Exception("This is not A00 matrix");
            }
            String sourceAddress = data.get("address").toString();

            DataDriver dataDriver = new DataDriver(sourceAddress, address, basePath);
            double[][] A00 = dataDriver.get(String.format("A/A-%s-%s", K, K));

            A00Processor a00processor = new A00Processor(A00);
            a00processor.calculate();
            a00processor.getL00();
            a00processor.getU00();
            a00processor.getL00I();
            a00processor.getU00I();

            DataWriter L00 = new DataWriter(String.format("L/L-%s-%s", K, K), basePath);
            L00.writeL(a00processor.getL00());

            DataWriter U00 = new DataWriter(String.format("U/U-%s-%s", K, K), basePath);
            U00.writeU(a00processor.getU00());

            if (K == R) {
                //last element processed, terminate
                driver.send(Chanels.terminate, new JSONObject());
                return;
            }

            DataWriter L00I = new DataWriter(String.format("LI/LI-%s", K), basePath);
            L00I.writeL(a00processor.getL00I());

            DataWriter U00I = new DataWriter(String.format("UI/UI-%s", K), basePath);
            U00I.writeU(a00processor.getU00I());

            for (int j = K; j < R; j++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("address", this.address);
                map.put("K", Integer.toString(K));
                map.put("R", Integer.toString(R));

                JSONObject reply = new JSONObject(map);
                driver.send(Chanels.U00I, reply);
                driver.send(Chanels.L00I, reply);
            }
            driver.delete(Chanels.A00, message.getReceiptHandle());
        }
    }
}
