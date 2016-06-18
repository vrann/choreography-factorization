package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.DataDriver;
import com.vrann.Factorization.Chanels;
import com.vrann.Factorization.L10Processor;
import com.vrann.Factorization.U01Processor;
import com.vrann.Matrix.DataWriter;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class CalculateU01Chanel {

    private String address;
    private String basePath;

    public CalculateU01Chanel(String address, String basePath) {
        this.address = address;
        this.basePath = basePath;
    }

    public void process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message message = driver.getMessageFor(Chanels.calculateU01);
        if (message != null) {
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            String sourceAddresL00I = data.get("sourceAddressL00I").toString();


            DataDriver dataDriverL00I = new DataDriver(sourceAddresL00I, address, basePath);
            double[][] L00I = dataDriverL00I.get(String.format("LI/LI-%s", K));

            String sourceAddressA01 = data.get("sourceAddressA01").toString();
            DataDriver dataDriverA01 = new DataDriver(sourceAddressA01, address, basePath);
            double[][] A01 = dataDriverA01.get(String.format("A/A-%s-%s", K, J));

            U01Processor u01Processor = new U01Processor(A01, L00I);
            u01Processor.calculate();
            DataWriter U01writer = new DataWriter(String.format("U/U-%s-%s", K, J), basePath);
            U01writer.write(u01Processor.getU01());

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("address", this.address);
            map.put("K", Integer.toString(K));
            map.put("R", Integer.toString(R));
            map.put("J", Integer.toString(J));

            JSONObject reply = new JSONObject(map);
            driver.send(Chanels.U01, reply);

            driver.delete(Chanels.calculateU01, message.getReceiptHandle());
        }
    }
}
