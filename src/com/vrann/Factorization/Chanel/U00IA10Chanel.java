package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Factorization.Chanels;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class U00IA10Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageU00I = driver.getMessageFor(Chanels.U00I);
        if (messageU00I == null) {
            return;
        }

        //listen for the messages from queue
        MessageInterface messageA10 = driver.getMessageFor(Chanels.A10);
        if (messageA10 == null) {
            return;
        }

        JSONObject dataU00I = new JSONObject(new JSONTokener(messageU00I.getBody()));
        int KU00I = Integer.parseInt(dataU00I.get("K").toString());

        JSONObject dataA10 = new JSONObject(new JSONTokener(messageA10.getBody()));
        int KA10 = Integer.parseInt(dataA10.get("K").toString());
        int IA10 = Integer.parseInt(dataA10.get("I").toString());
        int JA10 = Integer.parseInt(dataA10.get("J").toString());

        System.out.printf("process U00IA10 %s %s \n", KU00I, IA10);

        if (KU00I != JA10) {
            return;
        }

        String sourceAddressU00I = dataU00I.get("address").toString();
        int RA10 = Integer.parseInt(dataA10.get("R").toString());
        String sourceAddressA10 = dataA10.get("address").toString();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(KU00I));
        map.put("R", Integer.toString(RA10));
        map.put("J", Integer.toString(IA10));
        map.put("sourceAddressU00I", sourceAddressU00I);
        map.put("sourceAddressA10", sourceAddressA10);

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.calculateL10, reply);
        driver.delete(Chanels.U00I, messageU00I.getId());
        driver.delete(Chanels.A10, messageA10.getId());
    }
}
