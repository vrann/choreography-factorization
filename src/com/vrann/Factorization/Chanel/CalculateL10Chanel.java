package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Math.Matrix;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class CalculateL10Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface message = driver.getMessageFor(Chanels.calculateL10);
        if (message != null) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            System.out.printf("process calculateL10 %s %s \n", K, J);
            String sourceAddressU00I = data.get("sourceAddressU00I").toString();

            double[][] U00I = DataDriver.get(sourceAddressU00I, String.format("UI/UI-%s", K));

            String sourceAddressA10 = data.get("sourceAddressA10").toString();
            double[][] A10 = DataDriver.get(sourceAddressA10, String.format("A/A-%s-%s", J, K));

            L10Processor l10Processor = new L10Processor(A10, U00I);
            l10Processor.calculate();
            DataWriter.writeMatrix(String.format("L/L-%s-%s", J, K), l10Processor.getL10(), MatrixType.L);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("address", SetupConfig.get().getNetworkAddress());
            map.put("K", Integer.toString(K));
            map.put("R", Integer.toString(R));
            map.put("J", Integer.toString(J));

            JSONObject reply = new JSONObject(map);
            driver.send(Chanels.L10, reply);

            driver.delete(Chanels.calculateL10, message.getId());
        }
    }
}
