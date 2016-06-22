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
public class AggregatedL10U01Chanel {
    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        List<MessageInterface> messagesU01 = driver.getAllMessagesFor(Chanels.aggregatedU01);
        List<MessageInterface> messagesL10 = driver.getAllMessagesFor(Chanels.aggregatedL10);


        List<JSONObject> u01messages = new ArrayList<>();
        for (MessageInterface messageU01: messagesU01) {
            JSONArray data = new JSONArray(new JSONTokener(messageU01.getBody()));
            for (int i = 0; i < data.length(); i++) {
                JSONObject u01 = data.getJSONObject(i);
                u01messages.add(u01);
            }
        }

        List<JSONObject> l10messages = new ArrayList<>();
        for (MessageInterface messageL10: messagesL10) {
            JSONArray data = new JSONArray(new JSONTokener(messageL10.getBody()));
            for (int i = 0; i < data.length(); i++) {
                JSONObject l10 = data.getJSONObject(i);
                l10messages.add(l10);
            }

        }

        for (JSONObject u01: u01messages) {
            int KU01 = Integer.parseInt(u01.get("K").toString());
            int JU01 = Integer.parseInt(u01.get("J").toString());
            int RU01 = Integer.parseInt(u01.get("R").toString());
            String addressU01 = u01.get("address").toString();
            JSONObject usedU01 = (JSONObject)u01.get("usedWith");

            for (JSONObject l10: l10messages) {
                int KL10 = Integer.parseInt(l10.get("K").toString());
                int JL10 = Integer.parseInt(l10.get("J").toString());
                String addressL10 = l10.get("address").toString();
                JSONObject usedL10 = (JSONObject)l10.get("usedWith");


                if (KU01 == KL10 && !usedU01.has(Integer.toString(JL10)) && !usedL10.has(Integer.toString(JU01))) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("K", Integer.toString(KU01));
                    map.put("R", Integer.toString(RU01));
                    map.put("I", Integer.toString(JL10));
                    map.put("J", Integer.toString(JU01));
                    map.put("sourceAddressL10", addressL10);
                    map.put("sourceAddressU01", addressU01);

                    //usedU01++;
                    //usedL10++;
                    usedU01.put(Integer.toString(JL10), true);
                    usedL10.put(Integer.toString(JU01), true);
                    u01.put("usedWith", usedU01);
                    l10.put("usedWith", usedL10);

                    JSONObject reply = new JSONObject(map);
                    driver.send(Chanels.L10U01, reply);

                    if (u01messages.size() > 0 && l10messages.size() > 0) {
                        System.out.printf("process aggregate L10 and U01 I:%s J:%s \n", JL10, JU01);
                    }
                }
            }
        }



        List<JSONObject> u01messagesReply = new ArrayList<>();
        for (JSONObject u01: u01messages) {
            int RU01 = Integer.parseInt(u01.get("R").toString());
            int KU01 = Integer.parseInt(u01.get("K").toString());
            JSONObject usedU01 = (JSONObject)u01.get("usedWith");

            //int usedU01 = Integer.parseInt(u01.get("usedTimes").toString());
            if (usedU01.length() < RU01 - KU01 -1) {
                u01messagesReply.add(u01);
            } else {
                System.out.printf("Remove matrix used enough U %s %s %s \n", KU01, u01.get("J"), usedU01);
            }
        }

        List<JSONObject> l10messagesReply = new ArrayList<>();
        for (JSONObject l10: l10messages) {
            int RL10 = Integer.parseInt(l10.get("R").toString());
            int KL10 = Integer.parseInt(l10.get("K").toString());
            JSONObject usedL10 = (JSONObject)l10.get("usedWith");
            //int usedL10 = Integer.parseInt(l10.get("usedTimes").toString());
            if (usedL10.length() < RL10 - KL10 -1) {
                l10messagesReply.add(l10);
            } else {
                System.out.printf("Remove matrix used enough L %s %s %s \n", KL10, l10.get("J"), usedL10);
            }
        }

        if (u01messagesReply.size() > 0) {
            JSONArray aggregatedMessage = new JSONArray(u01messagesReply);
            driver.send(Chanels.aggregatedU01, aggregatedMessage);
        }

        if (l10messagesReply.size() > 0) {
            JSONArray aggregatedMessage = new JSONArray(l10messagesReply);
            driver.send(Chanels.aggregatedL10, aggregatedMessage);
        }

        for (MessageInterface messageU01: messagesU01) {
            driver.delete(Chanels.aggregatedU01, messageU01.getId());
        }

        for (MessageInterface messageL10: messagesL10) {
            driver.delete(Chanels.aggregatedL10, messageL10.getId());
        }
    }
}
