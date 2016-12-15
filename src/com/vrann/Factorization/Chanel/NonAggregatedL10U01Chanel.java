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
                driver.requeue(Chanels.U01, message);
            }
            return;
        }

        List<JSONObject> u01messages = new ArrayList<>();
        for (MessageInterface messageU01: messagesU01) {
            u01messages.add(new JSONObject(new JSONTokener(messageU01.getBody())));
            driver.delete(Chanels.U01, messageU01.getId());
        }

        List<JSONObject> l10messages = new ArrayList<>();
        for (MessageInterface messageL10: messagesL10) {
            l10messages.add(new JSONObject(new JSONTokener(messageL10.getBody())));
            driver.delete(Chanels.L10, messageL10.getId());
        }

        HashMap<String, JSONObject> u01map = new HashMap<>();
        for (JSONObject u01: u01messages) {
            String KU01 = u01.get("K").toString();
            String JU01 = u01.get("J").toString();
            JSONObject usedU01 = new JSONObject();
            if (u01.has("usedWith")) {
                usedU01 = (JSONObject) u01.get("usedWith");
            }
            u01.put("usedWith", usedU01);
            //System.out.print(u01.get("K").toString() + ":" + u01.get("J").toString() + "~");
            u01map.put(JU01, u01);
        }
//        System.out.println("u01map length" + u01map.size());
//        System.out.println("l10messages length" + l10messages.size());
        for (JSONObject l10: l10messages) {
            String KL10 = l10.get("K").toString();
            JSONObject usedL10 = new JSONObject();
            if (l10.has("usedWith")) {
                usedL10 = (JSONObject) l10.get("usedWith");
            }
            l10.put("usedWith", usedL10);
//            System.out.println(usedL10.names());

            for (String UJ: u01map.keySet()) {
                //System.out.print(UJ + " ");
                JSONObject u01 = u01map.get(UJ);
                String UK = u01.get("K").toString();
                String JL10 = l10.get("J").toString();
                if (!usedL10.has(UJ) && Integer.valueOf(UK) == Integer.valueOf(KL10)) {
                    if (((JSONObject)u01.get("usedWith")).has(JL10)) {
//                        throw new Exception(
//                                String.format("L matrix was used with U while U wasn't used with L: %s %s %s %s %s",
//                                        usedL10,
//                                        u01.get("usedWith"),
//                                        JL10,
//                                        UJ,
//                                        UK
//                                )
//                        );
                    }
                    String addressL10 = l10.get("address").toString();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("K", KL10);
                    map.put("R", l10.get("R").toString());
                    map.put("I", JL10);
                    map.put("J", UJ);
                    map.put("sourceAddressL10", addressL10);
                    map.put("sourceAddressU01", u01.get("address").toString());

                    JSONObject usedU01 = (JSONObject) u01.get("usedWith");
                    usedU01.put(JL10, true);
                    usedL10.put(UJ, true);

                    u01.put("usedWith", usedU01);
                    l10.put("usedWith", usedL10);

                    JSONObject reply = new JSONObject(map);
                    driver.send(Chanels.L10U01, reply);
                    System.out.println("L10 U01 aggregated: " + JL10 + " " + UJ );
//                    if (u01messages.size() > 0 && l10messages.size() > 0) {
//                        System.out.printf("process aggregate L10 and U01 I:%s J:%s \n", JL10, u01.get("J").toString());
//                    }
                } else {
//                    System.out.println("key is used already: " + UJ + " " + UK + " " + KL10);
                }
            }
//            System.out.println();
        }


        for (JSONObject u01: u01messages) {
            int RU01 = Integer.parseInt(u01.get("R").toString());
            int KU01 = Integer.parseInt(u01.get("K").toString());
            JSONObject usedU01 = (JSONObject)u01.get("usedWith");
            if (usedU01.length() < RU01 - KU01 - 1) {
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
