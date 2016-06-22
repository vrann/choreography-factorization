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
public class A11CalculatedChanel {
    public void process() throws Exception {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        List<MessageInterface> messagesA11 = driver.getAllMessagesFor(Chanels.A11calculated);
        if (messagesA11.size() == 0 ) {
            return;
        }

        MessageInterface firstMessage = messagesA11.get(0);
        JSONObject firstMessageData = new JSONObject(new JSONTokener(firstMessage.getBody()));
        int R = Integer.parseInt(firstMessageData.get("R").toString());
        int I = Integer.parseInt(firstMessageData.get("I").toString());
        int J = Integer.parseInt(firstMessageData.get("J").toString());
        int K = Integer.parseInt(firstMessageData.get("K").toString());

        int[][] usedI = new int[R][R];
        int[][] usedJ = new int[R][R];
        usedI[K][I] = 1;
        usedJ[K][J] = 1;

        for (int i = 1; i < messagesA11.size(); i++) {
            MessageInterface currentMessage = messagesA11.get(i);
            JSONObject currentMessageData = new JSONObject(new JSONTokener(currentMessage.getBody()));

            int currentI = Integer.parseInt(currentMessageData.get("I").toString());
            int currentJ = Integer.parseInt(currentMessageData.get("J").toString());
            int currentK = Integer.parseInt(currentMessageData.get("K").toString());
            usedI[currentK][currentI]++;
            usedJ[currentK][currentJ]++;
        }

        System.out.printf("Processing removal of U01, L10 \n");

        List<MessageInterface> messagesU01 = driver.getAllMessagesFor(Chanels.U01);
        for (MessageInterface messageU01: messagesU01) {
            JSONObject data = new JSONObject(new JSONTokener(messageU01.getBody()));
            int U01K = Integer.parseInt(data.get("K").toString());
            int U01J = Integer.parseInt(data.get("J").toString());
            if (usedJ[U01K][U01J] == (R - K - 1)) {
                System.out.printf("U01 %s %s \n", K, J);
                driver.delete(Chanels.U01, messageU01.getId());
            }
        }

        List<MessageInterface> messagesL01 = driver.getAllMessagesFor(Chanels.L10);
        for (MessageInterface messageL10: messagesL01) {
            JSONObject data = new JSONObject(new JSONTokener(messageL10.getBody()));
            int L10K = Integer.parseInt(data.get("K").toString());
            int L10I = Integer.parseInt(data.get("J").toString());
            if (usedI[L10K][L10I] == (R - K - 1)) {
                System.out.printf("L10 %s %s \n", K, I);
                driver.delete(Chanels.L10, messageL10.getId());
            }
        }
    }
}
