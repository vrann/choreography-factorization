package com.vrann.Factorization.Chanel;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Factorization.Chanels;

import java.util.HashMap;
import java.util.List;

public class U00IA10AggregatorChannel {
    public void process() throws Exception {

        MessageSet u00iSet = new MessageSet(Chanels.U00I, "K");
        if (u00iSet.size() == 0) {
            return;
        }
        MessageSet a10Set = new MessageSet(Chanels.A10, "I");
        if (a10Set.size() == 0) {
            u00iSet.requeue();
            return;
        }

        List<JSONObject[]> pairsNotUsedTogether = u00iSet.intersect(a10Set);

        for (JSONObject[] pair: pairsNotUsedTogether) {
            JSONObject u00i = pair[0];
            JSONObject a10 = pair[1];

            String I = a10.get("I").toString();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("K", u00i.get("K").toString());
            map.put("R", u00i.get("R").toString());
            map.put("I", I);
            map.put("sourceAddressA10", a10.get("address").toString());
            map.put("sourceAddressU00I", u00i.get("address").toString());

            JSONObject reply = new JSONObject(map);
            ChanelInterface driver = new ChanelFactory().getChanelDriver();
            driver.send(Chanels.A10U00I, reply);
            System.out.println("U00I A10 aggregated: " + u00i.get("K").toString() + " " + I);
        }
        u00iSet.resendMessages();
        a10Set.resendMessages(1);
    }
}
