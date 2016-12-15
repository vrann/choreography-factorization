package com.vrann.Factorization.Chanel;

import com.amazonaws.auth.policy.conditions.BooleanCondition;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Choreography.SetupConfig;
import com.vrann.Factorization.Chanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etulika on 6/21/16.
 */
public class A11L10U01AggregatorChannel {

    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        List<MessageInterface> messagesL10U01 = driver.getAllMessagesFor(Chanels.L10U01);
        if (messagesL10U01.size() == 0) {
            return;
        }

        List<MessageInterface> messagesA11 = driver.getAllMessagesFor(Chanels.A11);
        if (messagesA11.size() == 0) {
            for (MessageInterface message: messagesL10U01) {
                driver.requeue(Chanels.L10U01, message);
            }
            return;
        }

        HashMap<String, HashMap<String, String>> visitedCombinations = new HashMap<>();
        for (MessageInterface messageL10U01: messagesL10U01) {
            JSONObject l10u01 = new JSONObject(new JSONTokener(messageL10U01.getBody()));
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("K", l10u01.get("K").toString());
            map.put("I", l10u01.get("I").toString());
            map.put("J", l10u01.get("J").toString());
            map.put("R", l10u01.get("R").toString());
            map.put("sourceAddressL10", l10u01.get("sourceAddressL10").toString());
            map.put("sourceAddressU01", l10u01.get("sourceAddressU01").toString());
            String combinationKey = String.format("%s.%s.%s", map.get("K"), map.get("I"), map.get("J"));
            if (visitedCombinations.containsKey(combinationKey)) {
                //driver.delete(Chanels.L10U01, messageL10U01.getId());
//                System.out.printf("Visited combination L10U01 %s", combinationKey);
                break;
            }
            visitedCombinations.put(combinationKey, map);
        }

        HashMap<String, JSONObject> resendA11messages = new HashMap<>();
        HashMap<String, Boolean> visitedA11 = new HashMap<>();
        for (MessageInterface messageA11: messagesA11) {
            JSONObject a11 = new JSONObject(new JSONTokener(messageA11.getBody()));
            String K = a11.get("K").toString();
            String I = a11.get("I").toString();
            String J = a11.get("J").toString();
            String addressA11 = a11.get("address").toString();
            String combinationKey = String.format("%s.%s.%s", K, I, J);
            if (visitedA11.containsKey(combinationKey)) {
                //driver.delete(Chanels.A11, messageA11.getId());
//                System.out.printf("Visited combination A11 %s", combinationKey);
                break;
            }
            visitedA11.put(combinationKey, true);
            if (visitedCombinations.containsKey(combinationKey)) {
                HashMap aggregatedMessage = visitedCombinations.get(combinationKey);
                visitedCombinations.remove(combinationKey);
                aggregatedMessage.put("sourceAddressA11", addressA11);
                JSONObject reply = new JSONObject(aggregatedMessage);
                driver.send(Chanels.L10U01A11, reply);
                System.out.printf("generated A1L10U01 K:%s I:%s J:%s \n", K, I, J);
            } else {
                resendA11messages.put(combinationKey, a11);
            }
        }

        if (visitedCombinations.size() > 0) {
            for (HashMap<String, String> l10u01: visitedCombinations.values()) {
                driver.send(Chanels.L10U01, new JSONObject(l10u01));
            }
        }

        if (resendA11messages.size() > 0) {
            for (JSONObject a11: resendA11messages.values()) {
                driver.send(Chanels.A11, a11);
            }
        }

        for (MessageInterface messageL10U01: messagesL10U01) {
            driver.delete(Chanels.L10U01, messageL10U01.getId());
        }

        for (MessageInterface messageA11: messagesA11) {
            driver.delete(Chanels.A11, messageA11.getId());
        }

        //System.out.printf("A11 removed vs added: %s %s \n", messagesA11.size(), resendA11messages.size());

    }
}
