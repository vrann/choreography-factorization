package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Factorization.U01Processor;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class CalculateU01Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface message = driver.getMessageFor(Chanels.calculateU01);
        if (message != null) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            String sourceAddresL00I = data.get("sourceAddressL00I").toString();
            System.out.printf("process calculateU10 %s %s \n", K, J);

            double[][] L00I = DataDriver.get(sourceAddresL00I, String.format("LI/LI-%s", K));

            String sourceAddressA01 = data.get("sourceAddressA01").toString();
            double[][] A01 = DataDriver.get(sourceAddressA01, String.format("A/A-%s-%s", K, J));

            U01Processor u01Processor = new U01Processor(A01, L00I);
            u01Processor.calculate();
            DataWriter.writeMatrix(String.format("U/U-%s-%s", K, J), u01Processor.getU01(), MatrixType.U);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("address", SetupConfig.get().getNetworkAddress());
            map.put("K", Integer.toString(K));
            map.put("R", Integer.toString(R));
            map.put("J", Integer.toString(J));

            JSONObject reply = new JSONObject(map);
            driver.send(Chanels.U01, reply);

            driver.delete(Chanels.calculateU01, message.getId());
        }
    }
}
