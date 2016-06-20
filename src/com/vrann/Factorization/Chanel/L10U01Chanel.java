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
public class L10U01Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageL10 = driver.getMessageFor(Chanels.L10);
        if (messageL10 == null) {
            return;
        }

        //listen for the messages from queue
        MessageInterface messageU01 = driver.getMessageFor(Chanels.U01);
        if (messageU01 == null) {
            return;
        }

        JSONObject dataL10 = new JSONObject(new JSONTokener(messageL10.getBody()));
        int KL10 = Integer.parseInt(dataL10.get("K").toString());

        JSONObject dataU01 = new JSONObject(new JSONTokener(messageU01.getBody()));
        int KU01 = Integer.parseInt(dataU01.get("K").toString());

        if (KL10 != KU01) {
            return;
        }

        String sourceAddressL10 = dataL10.get("address").toString();
        int RU01 = Integer.parseInt(dataU01.get("R").toString());
        int JU01 = Integer.parseInt(dataU01.get("J").toString());
        int JL10 = Integer.parseInt(dataL10.get("J").toString());
        String sourceAddressU01 = dataU01.get("address").toString();

        System.out.printf("process L10U01 %s %s \n", JL10, JU01);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(KU01));
        map.put("R", Integer.toString(RU01));
        map.put("I", Integer.toString(JL10));
        map.put("J", Integer.toString(JU01));
        map.put("sourceAddressL10", sourceAddressL10);
        map.put("sourceAddressU01", sourceAddressU01);

        JSONObject reply = new JSONObject(map);
        driver.send(Chanels.L10U01, reply);

        driver.delete(Chanels.U01, messageL10.getId());
        driver.delete(Chanels.L10, messageU01.getId());
    }
}
