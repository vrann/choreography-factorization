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
public class U01AggregatorChanel {
    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        List<MessageInterface> messagesU01 = driver.getAllMessagesFor(Chanels.U01);
        List<JSONObject> aggregatedMessages = new ArrayList<>();
        if (messagesU01.size() > 0) {
            System.out.printf("process aggregate U01 \n");
        }
        for (MessageInterface message: messagesU01) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            JSONObject map  = new JSONObject();
            map.put("address", data.get("address").toString());
            map.put("K", data.get("K").toString());
            map.put("R", data.get("R").toString());
            map.put("J", data.get("J").toString());
            HashMap<String,Boolean> usedWith = new HashMap<>();
            map.put("usedWith", usedWith);
            aggregatedMessages.add(map);
        }

        if (aggregatedMessages.size() > 0) {
            JSONArray aggregatedMessage = new JSONArray(aggregatedMessages);
            driver.send(Chanels.aggregatedU01, aggregatedMessage);
        }

        for (MessageInterface message: messagesU01) {
            driver.delete(Chanels.U01, message.getId());
        }
    }
}
