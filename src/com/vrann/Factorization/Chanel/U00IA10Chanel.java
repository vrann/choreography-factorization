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
public class U00IA10Chanel {

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message messageU00I = driver.getMessageFor(Chanels.L10);
        if (messageU00I == null) {
            return;
        }

        //listen for the messages from queue
        Message messageA10 = driver.getMessageFor(Chanels.A10);
        if (messageA10 == null) {
            return;
        }

        JSONObject dataU00I = new JSONObject(new JSONTokener(messageU00I.getBody()));
        int KU00I = Integer.parseInt(dataU00I.get("K").toString());

        JSONObject dataA10 = new JSONObject(new JSONTokener(messageA10.getBody()));
        int KA10 = Integer.parseInt(dataA10.get("K").toString());

        if (KU00I != KA10) {
            return;
        }

        String sourceAddressU00I = dataU00I.get("address").toString();
        int RA10 = Integer.parseInt(dataA10.get("R").toString());
        int JA10 = Integer.parseInt(dataA10.get("J").toString());
        String sourceAddressA10 = dataA10.get("address").toString();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(KA10));
        map.put("R", Integer.toString(RA10));
        map.put("J", Integer.toString(JA10));
        map.put("sourceAddressU00I", sourceAddressU00I);
        map.put("sourceAddressA10", sourceAddressA10);

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.calculateL10, reply);
        driver.delete(Chanels.U00I, messageU00I.getReceiptHandle());
        driver.delete(Chanels.A10, messageA10.getReceiptHandle());
    }
}
