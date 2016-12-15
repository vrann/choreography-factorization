package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class U00IA10Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageU00IA10 = driver.getMessageFor(Chanels.A10U00I);
        if (messageU00IA10 == null) {
            return;
        }
        JSONObject data = new JSONObject(new JSONTokener(messageU00IA10.getBody()));
        int K = Integer.parseInt(data.get("K").toString());
        int I = Integer.parseInt(data.get("I").toString());
        System.out.printf("process U00IA10 %s %s \n", K, I);

        String sourceAddressU00I = data.get("sourceAddressU00I").toString();
        int R = Integer.parseInt(data.get("R").toString());
        String sourceAddressA10 = data.get("sourceAddressA10").toString();

        double[][] U00I = DataDriver.get(sourceAddressU00I, String.format("UI/UI-%s", K));
        double[][] A10 = DataDriver.get(sourceAddressA10, String.format("A/A-%s-%s", I, K));

        L10Processor l10Processor = new L10Processor(A10, U00I);
        l10Processor.calculate();
        DataWriter.writeMatrix(String.format("L/L-%s-%s", I, K), l10Processor.getL10(), MatrixType.L);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("address", SetupConfig.get().getNetworkAddress());
        map.put("K", Integer.toString(K));
        map.put("R", Integer.toString(R));
        map.put("J", Integer.toString(I));

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.L10, reply);
        driver.delete(Chanels.A10U00I, messageU00IA10.getId());
    }

    public void process2() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageA10 = driver.getMessageFor(Chanels.A10);
        if (messageA10 == null) {
            return;
        }

        //listen for the messages from queue
        MessageInterface messageU00I = driver.getMessageFor(Chanels.U00I);
        if (messageU00I == null) {
            driver.send(Chanels.A10, new JSONObject(new JSONTokener(messageA10.getBody())));
            driver.delete(Chanels.A10, messageA10.getId());
            return;
        }

        JSONObject dataU00I = new JSONObject(new JSONTokener(messageU00I.getBody()));
        int KU00I = Integer.parseInt(dataU00I.get("K").toString());

        JSONObject dataA10 = new JSONObject(new JSONTokener(messageA10.getBody()));
        int KA10 = Integer.parseInt(dataA10.get("K").toString());
        int IA10 = Integer.parseInt(dataA10.get("I").toString());
        int JA10 = Integer.parseInt(dataA10.get("J").toString());

        if (KU00I != KA10) {
            System.out.printf("-- different K in U00 and A10 %s %s \n", KU00I, KA10);
            driver.send(Chanels.A10, new JSONObject(new JSONTokener(messageA10.getBody())));
            driver.delete(Chanels.A10, messageA10.getId());
            driver.send(Chanels.U00I, new JSONObject(new JSONTokener(messageU00I.getBody())));
            driver.delete(Chanels.U00I, messageU00I.getId());
            return;
        }

        System.out.printf("process U00IA10 %s %s \n", KU00I, IA10);

        String sourceAddressU00I = dataU00I.get("address").toString();
        int RA10 = Integer.parseInt(dataA10.get("R").toString());
        String sourceAddressA10 = dataA10.get("address").toString();

        double[][] U00I = DataDriver.get(sourceAddressU00I, String.format("UI/UI-%s", KU00I));
        double[][] A10 = DataDriver.get(sourceAddressA10, String.format("A/A-%s-%s", IA10, KA10));

        L10Processor l10Processor = new L10Processor(A10, U00I);
        l10Processor.calculate();
        DataWriter.writeMatrix(String.format("L/L-%s-%s", IA10, KA10), l10Processor.getL10(), MatrixType.L);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("address", SetupConfig.get().getNetworkAddress());
        map.put("K", Integer.toString(KA10));
        map.put("R", Integer.toString(RA10));
        map.put("J", Integer.toString(IA10));

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.L10, reply);

        driver.delete(Chanels.U00I, messageU00I.getId());
        driver.delete(Chanels.A10, messageA10.getId());
    }
}
