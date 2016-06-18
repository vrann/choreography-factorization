package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.DataDriver;
import com.vrann.Factorization.A11Processor;
import com.vrann.Factorization.Chanels;
import com.vrann.Matrix.DataWriter;

import java.util.HashMap;

/**
 * Created by etulika on 6/16/16.
 */
public class A11L10U01Chanel {

    private String address;
    private String basePath;

    public A11L10U01Chanel(String address, String basePath) {
        this.address = address;
        this.basePath = basePath;
    }

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message messageL10U01 = driver.getMessageFor(Chanels.L10U01);
        if (messageL10U01 == null) {
            return;
        }

        //listen for the messages from queue
        Message messageA11 = driver.getMessageFor(Chanels.A11);
        if (messageA11 == null) {
            return;
        }

        JSONObject dataL10U01 = new JSONObject(new JSONTokener(messageL10U01.getBody()));
        int K = Integer.parseInt(dataL10U01.get("K").toString());
        int J = Integer.parseInt(dataL10U01.get("J").toString());
        int I = Integer.parseInt(dataL10U01.get("I").toString());

        JSONObject dataA11 = new JSONObject(new JSONTokener(messageA11.getBody()));
        int KA11 = Integer.parseInt(dataA11.get("K").toString());
        int JA11 = Integer.parseInt(dataA11.get("J").toString());

        if (I != KA11 || J != JA11) {
            return;
        }

        String sourceAddressL10 = dataL10U01.get("sourceAddressL10").toString();
        int R = Integer.parseInt(dataL10U01.get("R").toString());
        String sourceAddressU01 = dataL10U01.get("sourceAddressU01").toString();
        String sourceAddressA11 = dataA11.get("address").toString();

        DataDriver dataDriverL10 = new DataDriver(sourceAddressL10, address, basePath);
        double[][] L10 = dataDriverL10.get(String.format("L/L-%s-%s", I, K));

        DataDriver dataDriverU01 = new DataDriver(sourceAddressU01, address, basePath);
        double[][] U01 = dataDriverU01.get(String.format("U/U-%s-%s", K, J));

        DataDriver dataDriverA11 = new DataDriver(sourceAddressA11, address, basePath);
        double[][] A11 = dataDriverA11.get(String.format("A/A-%s-%s", I, J));

        A11Processor a11Processor = new A11Processor(A11, L10, U01);
        DataWriter A11writer = new DataWriter(String.format("A/A-%s-%s", I, J), basePath);
        A11writer.write(a11Processor.getA11());

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(I));
        map.put("R", Integer.toString(R));
        map.put("J", Integer.toString(J));
        map.put("address", address);
        JSONObject reply = new JSONObject(map);

        if (I == J && I == K + 1) {
            driver.send(Chanels.A00, reply);
        } else if (I == K + 1) {
            driver.send(Chanels.A01, reply);
        } else if (J == K + 1) {
            driver.send(Chanels.A10, reply);
        } else {
            driver.send(Chanels.A11, reply);
        }

        driver.delete(Chanels.L10U01, messageL10U01.getReceiptHandle());
        driver.delete(Chanels.A11, messageA11.getReceiptHandle());

    }
}
