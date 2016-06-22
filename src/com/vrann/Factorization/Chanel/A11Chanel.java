package com.vrann.Factorization.Chanel;

import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Factorization.A11Processor;
import com.vrann.Factorization.Chanels;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;
import java.util.List;

/**
 * Created by etulika on 6/20/16.
 */
public class A11Chanel {
    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface message = driver.getMessageFor(Chanels.A11);
        if (message == null) {
            return;
        }

        List<MessageInterface> messagesU01 = driver.getAllMessagesFor(Chanels.U01);
        if (messagesU01.size() == 0) {
            return;
        }

        JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
        int K = Integer.parseInt(data.get("K").toString());
        int J = Integer.parseInt(data.get("J").toString());
        int I = Integer.parseInt(data.get("I").toString());
        System.out.printf("A11: %s %s \n", I, J);

        MessageInterface messageU01 = null;
        JSONObject dataU01 = null;
        for (MessageInterface currentMessage: messagesU01) {
            JSONObject currentData = new JSONObject(new JSONTokener(currentMessage.getBody()));
            int currentK = Integer.parseInt(currentData.get("K").toString());
            int currentJ = Integer.parseInt(currentData.get("J").toString());
            if (currentK == K && currentJ == J) {
                messageU01 = currentMessage;
                dataU01 = currentData;
                break;
            }
        }

        if (messageU01 == null) {
            return;
        }

        List<MessageInterface> messagesL10 = driver.getAllMessagesFor(Chanels.L10);
        if (messagesL10.size() == 0) {
            return;
        }

        MessageInterface messageL10 = null;
        JSONObject dataL10 = null;
        for (MessageInterface currentMessage: messagesL10) {
            JSONObject currentData = new JSONObject(new JSONTokener(currentMessage.getBody()));
            int currentK = Integer.parseInt(currentData.get("K").toString());
            int currentI = Integer.parseInt(currentData.get("J").toString());
            if (currentK == K && currentI == I) {
                messageL10 = currentMessage;
                dataL10 = currentData;
                break;
            }
        }

        if (messageL10 == null) {
            return;
        }

        System.out.printf("Processing A11 %s %s \n", I, J);

        String sourceAddressL10 = dataL10.get("address").toString();
        int R = Integer.parseInt(dataL10.get("R").toString());
        String sourceAddressU01 = dataU01.get("address").toString();
        String sourceAddressA11 = data.get("address").toString();

        double[][] L10 = DataDriver.get(sourceAddressL10, String.format("L/L-%s-%s", I, K));

        double[][] U01 = DataDriver.get(sourceAddressU01, String.format("U/U-%s-%s", K, J));

        double[][] A11 = DataDriver.get(sourceAddressA11, String.format("A/A-%s-%s", I, J));

        A11Processor a11Processor = new A11Processor(A11, L10, U01);
        a11Processor.calculate();
        DataWriter.writeMatrix(String.format("A/A-%s-%s", I, J), a11Processor.getA11(), MatrixType.A);

        //increase K to go to the next iteration
        K = K + 1;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("K", Integer.toString(K));
        map.put("R", Integer.toString(R));
        map.put("I", Integer.toString(I));
        map.put("J", Integer.toString(J));
        map.put("address", SetupConfig.get().getNetworkAddress());
        JSONObject reply = new JSONObject(map);

        if (I == J && I == K) {
            driver.send(Chanels.A00, reply);
        } else if (I == K) {
            driver.send(Chanels.A01, reply);
        } else if (J == K) {
            driver.send(Chanels.A10, reply);
        } else {
            driver.send(Chanels.A11, reply);
        }

        driver.send(Chanels.A11calculated, data);
        driver.delete(Chanels.A11, message.getId());
    }
}
