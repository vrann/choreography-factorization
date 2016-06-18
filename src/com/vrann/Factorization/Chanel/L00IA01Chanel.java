package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class L00IA01Chanel {

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message messageL00I = driver.getMessageFor(Chanels.L00I);
        if (messageL00I == null) {
            return;
        }

        //listen for the messages from queue
        Message messageA01 = driver.getMessageFor(Chanels.A01);
        if (messageA01 == null) {
            return;
        }

        JSONObject dataL00I = new JSONObject(new JSONTokener(messageL00I.getBody()));
        int KU00I = Integer.parseInt(dataL00I.get("K").toString());

        JSONObject dataA01 = new JSONObject(new JSONTokener(messageA01.getBody()));
        int KA01 = Integer.parseInt(dataA01.get("K").toString());

        if (KU00I != KA01) {
            return;
        }

        String sourceAddressL00I = dataL00I.get("address").toString();
        int RA01 = Integer.parseInt(dataA01.get("R").toString());
        int JA01 = Integer.parseInt(dataA01.get("J").toString());
        String sourceAddressA01 = dataA01.get("address").toString();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(KA01));
        map.put("R", Integer.toString(RA01));
        map.put("J", Integer.toString(JA01));
        map.put("sourceAddressL00I", sourceAddressL00I);
        map.put("sourceAddressA01", sourceAddressA01);

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.calculateU01, reply);
        driver.delete(Chanels.L00I, messageL00I.getReceiptHandle());
        driver.delete(Chanels.A01, messageA01.getReceiptHandle());
    }
}
