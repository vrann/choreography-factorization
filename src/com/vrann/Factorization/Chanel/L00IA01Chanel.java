package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Factorization.U01Processor;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class L00IA01Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageL00IA01 = driver.getMessageFor(Chanels.A01L00I);
        if (messageL00IA01 == null) {
            return;
        }
        JSONObject data = new JSONObject(new JSONTokener(messageL00IA01.getBody()));
        int K = Integer.parseInt(data.get("K").toString());
        int J = Integer.parseInt(data.get("J").toString());
        System.out.printf("process L00IA01 %s %s \n", K, J);

        String sourceAddressL00I = data.get("sourceAddressL00I").toString();
        int R = Integer.parseInt(data.get("R").toString());
        String sourceAddressA01 = data.get("sourceAddressA01").toString();

        System.out.printf("process L00IA01 %s %s \n", K, J);

        double[][] L00I = DataDriver.get(sourceAddressL00I, String.format("LI/LI-%s", K));
        double[][] A01 = DataDriver.get(sourceAddressA01, String.format("A/A-%s-%s", K, J));

        U01Processor u01Processor = new U01Processor(A01, L00I);
        u01Processor.calculate();
        DataWriter.writeMatrix(String.format("U/U-%s-%s", K, J), u01Processor.getU01(), MatrixType.U);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("address", SetupConfig.get().getNetworkAddress());
        map.put("K", Integer.toString(K));
        map.put("R", Integer.toString(R));
        map.put("J", Integer.toString(J));

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.U01, reply);

        driver.delete(Chanels.A01L00I, messageL00IA01.getId());
    }

    public void process2() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageA01 = driver.getMessageFor(Chanels.A01);
        if (messageA01 == null) {
            return;
        }

        //listen for the messages from queue
        MessageInterface messageL00I = driver.getMessageFor(Chanels.L00I);
        if (messageL00I == null) {
            driver.requeue(Chanels.A01, messageA01);
            return;
        }

        JSONObject dataL00I = new JSONObject(new JSONTokener(messageL00I.getBody()));
        int KL00I = Integer.parseInt(dataL00I.get("K").toString());

        JSONObject dataA01 = new JSONObject(new JSONTokener(messageA01.getBody()));
        int KA01 = Integer.parseInt(dataA01.get("K").toString());

        if (KL00I != KA01) {
            System.out.printf("-- different K in L00 and A01 %s %s \n", KL00I, KA01);
            driver.requeue(Chanels.L00I, messageL00I);
            driver.requeue(Chanels.A01, messageA01);
            return;
        }

        String sourceAddressL00I = dataL00I.get("address").toString();
        int RA01 = Integer.parseInt(dataA01.get("R").toString());
        int JA01 = Integer.parseInt(dataA01.get("J").toString());
        String sourceAddressA01 = dataA01.get("address").toString();

        System.out.printf("process L00IA01 %s %s \n", KL00I, JA01);

        double[][] L00I = DataDriver.get(sourceAddressL00I, String.format("LI/LI-%s", KA01));
        double[][] A01 = DataDriver.get(sourceAddressA01, String.format("A/A-%s-%s", KA01, JA01));

        U01Processor u01Processor = new U01Processor(A01, L00I);
        u01Processor.calculate();
        DataWriter.writeMatrix(String.format("U/U-%s-%s", KA01, JA01), u01Processor.getU01(), MatrixType.U);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("address", SetupConfig.get().getNetworkAddress());
        map.put("K", Integer.toString(KL00I));
        map.put("R", Integer.toString(RA01));
        map.put("J", Integer.toString(JA01));

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.U01, reply);

        driver.delete(Chanels.L00I, messageL00I.getId());
        driver.delete(Chanels.A01, messageA01.getId());
    }
}
