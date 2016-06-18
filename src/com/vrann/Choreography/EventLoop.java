package com.vrann.Choreography;

import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanel.*;
import com.vrann.Factorization.Chanels;

/**
 * Created by etulika on 6/13/16.
 */
public class EventLoop {

    public void listenChannels(String address, String basePath) {
        boolean flag = true;

        //Main event loop of the handler
        while (flag) {
            try {
                if ((new TerminateChanel()).process()) {
                    flag = false;
                    break;
                }

                //process mapping channels, fast
                (new U00IA10Chanel()).process();
                (new L00IA01Chanel()).process();
                (new L10U01Chanel()).process();

                //process calculation channels, takes longer
                (new GenerateChanel(address, basePath)).process();
                (new A00Chanel(address, basePath)).process();
                (new CalculateL10Chanel(address, basePath)).process();
                (new CalculateU01Chanel(address, basePath)).process();
                (new A11L10U01Chanel(address, basePath)).process();

                System.out.print(".");
            } catch (Exception e) {
                AWSSQSDriver driver = new AWSSQSDriver();
                driver.send(Chanels.terminate, new JSONObject());
                System.out.println("Error Message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
