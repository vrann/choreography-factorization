package com.vrann.Factorization.Chanel;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Factorization.Chanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etulika on 6/20/16.
 */
public class NonAggregatedL10U01Chanel {
    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        List<MessageInterface> messagesU01 = driver.getAllMessagesFor(Chanels.U01);
        if (messagesU01.size() == 0) {
            return;
        }

        List<MessageInterface> messagesL10 = driver.getAllMessagesFor(Chanels.L10);
        if (messagesL10.size() == 0) {
            for (MessageInterface message: messagesU01) {
                driver.send(Chanels.U01, new JSONObject(new JSONTokener(message.getBody())));
                driver.delete(Chanels.U01, message.getId());
            }
            return;
        }

        List<JSONObject> u01messages = new ArrayList<>();
        for (MessageInterface messageU01: messagesU01) {
            u01messages.add(new JSONObject(new JSONTokener(messageU01.getBody())));
        }

        List<JSONObject> l10messages = new ArrayList<>();
        for (MessageInterface messageL10: messagesL10) {
            l10messages.add(new JSONObject(new JSONTokener(messageL10.getBody())));;
        }

        HashMap<String, JSONObject> u01map = new HashMap<>();
        for (JSONObject u01: u01messages) {
            String KU01 = u01.get("K").toString();
            JSONObject usedU01 = new JSONObject();
            if (u01.has("usedWith")) {
                usedU01 = (JSONObject) u01.get("usedWith");
            }
            u01.put("usedWith", usedU01);
            u01map.put(KU01, u01);
        }

        for (JSONObject l10: l10messages) {
            String KL10 = l10.get("K").toString();
            JSONObject usedL10 = new JSONObject();
            if (l10.has("usedWith")) {
                usedL10 = (JSONObject) l10.get("usedWith");
            }
            l10.put("usedWith", usedL10);
            if (u01map.containsKey(KL10)
                    && !((JSONObject)l10.get("usedWith")).has((String)u01map.get(KL10).get("K"))
                    && !((JSONObject)u01map.get(KL10).get("usedWith")).has(KL10)) {

                JSONObject u01 = u01map.get(KL10);
                String JL10 = l10.get("J").toString();
                String addressL10 = l10.get("address").toString();

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("K", KL10);
                map.put("R", l10.get("R").toString());
                map.put("I", JL10);
                map.put("J", u01.get("J").toString());
                map.put("sourceAddressL10", addressL10);
                map.put("sourceAddressU01", u01.get("address").toString());

                JSONObject usedU01 = (JSONObject) u01.get("usedWith");
                usedU01.put(JL10, true);
                usedL10.put(u01.get("J").toString(), true);

                u01.put("usedWith", usedU01);
                l10.put("usedWith", usedL10);

                JSONObject reply = new JSONObject(map);
                driver.send(Chanels.L10U01, reply);

                if (u01messages.size() > 0 && l10messages.size() > 0) {
                    System.out.printf("process aggregate L10 and U01 I:%s J:%s \n", JL10, u01.get("J").toString());
                }
            }
        }


        for (JSONObject u01: u01messages) {
            int RU01 = Integer.parseInt(u01.get("R").toString());
            int KU01 = Integer.parseInt(u01.get("K").toString());
            JSONObject usedU01 = (JSONObject)u01.get("usedWith");
            if (usedU01.length() < RU01 - KU01 -1) {
                driver.send(Chanels.U01, u01);
            } else {
                System.out.printf("Remove matrix used enough U %s %s %s \n", KU01, u01.get("J"), usedU01);
            }
        }

        for (JSONObject l10: l10messages) {
            int RL10 = Integer.parseInt(l10.get("R").toString());
            int KL10 = Integer.parseInt(l10.get("K").toString());
            JSONObject usedL10 = (JSONObject)l10.get("usedWith");
            if (usedL10.length() < RL10 - KL10 -1) {
                driver.send(Chanels.L10, l10);
            } else {
                System.out.printf("Remove matrix used enough L %s %s %s \n", KL10, l10.get("J"), usedL10);
            }
        }


        for (MessageInterface messageU01: messagesU01) {
            driver.delete(Chanels.U01, messageU01.getId());
        }

        for (MessageInterface messageL10: messagesL10) {
            driver.delete(Chanels.L10, messageL10.getId());
        }
    }
}
