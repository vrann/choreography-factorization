package com.vrann.Choreography;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanel.*;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.Roles;

import java.util.HashMap;

/**
 * Created by etulika on 6/13/16.
 */
public class EventLoop {

    public void listenChannels(String[] roles) throws Exception {
        boolean flag = true;

        //Main event loop of the handler
        while (flag) {
            try {
                if ((new TerminateChanel()).process()) {
                    flag = false;
                    break;
                }

                if (roles.length == 0) {
                    (new GenerateChanel()).process();
                    (new A00Chanel()).process();
                    (new U00IA10Chanel()).process();
                    (new L00IA01Chanel()).process();
                    (new A11L10U01AggregatorChannel()).process();
                    (new NonAggregatedL10U01Chanel()).process();
                    (new AggregatedA11L10U01Chanel()).process();
                } else {
                    for (String role: roles) {
                        if (role.equals(Roles.Mapper.name())) {
                            //process mapping channels, fast
                            (new NonAggregatedL10U01Chanel()).process();
                            (new A11L10U01AggregatorChannel()).process();
                        } else if (role.equals(Roles.Calculator.name())) {
                            //process calculation channels, takes longer
                            (new GenerateChanel()).process();
                            (new A00Chanel()).process();
                            (new U00IA10Chanel()).process();
                            (new L00IA01Chanel()).process();
                            (new AggregatedA11L10U01Chanel()).process();
                        }
                    }
                }

                System.out.print(".");
            } catch (Exception e) {
                e.printStackTrace();

                ChanelInterface driver = new ChanelFactory().getChanelDriver();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("error", e.getMessage());
                driver.send(Chanels.terminate, new JSONObject(map));
                System.out.println("Error Message: " + e.getMessage());
            }
        }
    }
}
