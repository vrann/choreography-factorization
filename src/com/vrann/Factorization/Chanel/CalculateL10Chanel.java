package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.DataDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Matrix.DataWriter;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class CalculateL10Chanel {

    private String address;
    private String basePath;

    public CalculateL10Chanel(String address, String basePath) {
        this.address = address;
        this.basePath = basePath;
    }

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message message = driver.getMessageFor(Chanels.calculateL10);
        if (message != null) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            String sourceAddressU00I = data.get("sourceAddressU00I").toString();

            DataDriver dataDriverU00I = new DataDriver(sourceAddressU00I, address, basePath);
            double[][] U00I = dataDriverU00I.get(String.format("UI/UI-%s", K));

            String sourceAddressA10 = data.get("sourceAddressA10").toString();
            DataDriver dataDriverA10 = new DataDriver(sourceAddressA10, address, basePath);
            double[][] A10 = dataDriverA10.get(String.format("A/A-%s-%s", J, K));

            L10Processor l10Processor = new L10Processor(A10, U00I);
            l10Processor.calculate();
            DataWriter L10writer = new DataWriter(String.format("L/L-%s-%s", J, K), basePath);
            L10writer.write(l10Processor.getL10());

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("address", this.address);
            map.put("K", Integer.toString(K));
            map.put("R", Integer.toString(R));
            map.put("J", Integer.toString(J));

            JSONObject reply = new JSONObject(map);
            driver.send(Chanels.L10, reply);

            driver.delete(Chanels.calculateL10, message.getReceiptHandle());
        }
    }
}
