package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.A11Processor;
import com.vrann.Factorization.Chanels;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;
import java.util.List;

/**
 * Created by etulika on 6/16/16.
 */
public class A11L10U01Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface messageL10U01 = driver.getMessageFor(Chanels.L10U01);
        if (messageL10U01 == null) {
            return;
        }

        //listen for the messages from queue
        List<MessageInterface> messagesA11 = driver.getAllMessagesFor(Chanels.A11);
        if (messagesA11.size() == 0) {
            driver.requeue(Chanels.L10U01, messageL10U01);
            return;
        }

        JSONObject dataL10U01 = new JSONObject(new JSONTokener(messageL10U01.getBody()));
        int K = Integer.parseInt(dataL10U01.get("K").toString());
        int J = Integer.parseInt(dataL10U01.get("J").toString());
        int I = Integer.parseInt(dataL10U01.get("I").toString());

        MessageInterface messageA11 = null;
        JSONObject dataA11 = null;
        for (MessageInterface message: messagesA11) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int KA11 = Integer.parseInt(data.get("K").toString());
            int JA11 = Integer.parseInt(data.get("J").toString());
            int IA11 = Integer.parseInt(data.get("I").toString());

            if (I == IA11 && J == JA11 && K == KA11) {
                messageA11 = message;
                dataA11 = data;
                break;
            }
        }

        if (messageA11 == null) {
            return;
        }

        System.out.printf("process A11L10U01 %s %s \n", K, J);
        String sourceAddressL10 = dataL10U01.get("sourceAddressL10").toString();
        int R = Integer.parseInt(dataL10U01.get("R").toString());
        String sourceAddressU01 = dataL10U01.get("sourceAddressU01").toString();
        String sourceAddressA11 = dataA11.get("address").toString();

        double[][] L10 = DataDriver.get(sourceAddressL10, String.format("L/L-%s-%s", I, K));

        double[][] U01 = DataDriver.get(sourceAddressU01, String.format("U/U-%s-%s", K, J));

        double[][] A11 = DataDriver.get(sourceAddressA11, String.format("A/A-%s-%s", I, J));

        A11Processor a11Processor = new A11Processor(A11, L10, U01);
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

        driver.delete(Chanels.L10U01, messageL10U01.getId());
        driver.delete(Chanels.A11, messageA11.getId());

    }
}
