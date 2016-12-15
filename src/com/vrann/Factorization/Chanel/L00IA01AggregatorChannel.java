package com.vrann.Factorization.Chanel;

import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Factorization.Chanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class L00IA01AggregatorChannel {
    public void process() throws Exception {

        MessageSet l00iSet = new MessageSet(Chanels.L00I, "K");
        if (l00iSet.size() == 0) {
            return;
        }
        MessageSet a01Set = new MessageSet(Chanels.A01, "J");
        if (a01Set.size() == 0) {
            l00iSet.requeue();
            return;
        }

        List<JSONObject[]> pairsNotUsedTogether = l00iSet.intersect(a01Set);

        for (JSONObject[] pair: pairsNotUsedTogether) {
            JSONObject l00i = pair[0];
            JSONObject a01 = pair[1];


            String J = a01.get("J").toString();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("K", l00i.get("K").toString());
            map.put("R", l00i.get("R").toString());
            map.put("J", J);
            map.put("sourceAddressA01", a01.get("address").toString());
            map.put("sourceAddressL00I", l00i.get("address").toString());

            JSONObject reply = new JSONObject(map);
            ChanelInterface driver = new ChanelFactory().getChanelDriver();
            driver.send(Chanels.A01L00I, reply);
            System.out.println("L00I A01 aggregated: " + l00i.get("K").toString() + " " + J);
        }

        l00iSet.resendMessages();
        a01Set.resendMessages(1);
    }
}
