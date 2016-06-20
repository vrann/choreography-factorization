package com.vrann.Factorization.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.*;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.A00Processor;
import com.vrann.Factorization.Chanels;
import com.vrann.Math.Matrix;
import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.MatrixType;

import java.util.HashMap;

/**
 * Created by etulika on 6/15/16.
 */
public class A00Chanel {

    public void process() throws Exception
    {
        ChanelInterface driver = new ChanelFactory().getChanelDriver();

        //listen for the messages from queue
        MessageInterface message = driver.getMessageFor(Chanels.A00);
        if (message != null) {
            /*
             {
                "K": 0,
                "R": 5,
                "address": "52.207.217.226"
             }
             */
            JSONObject data = new JSONObject(new JSONTokener(message.getBody()));
            int K = Integer.parseInt(data.get("K").toString());
            System.out.printf("process A00 %s \n", K);

            int R = Integer.parseInt(data.get("R").toString());
            int J = Integer.parseInt(data.get("J").toString());
            if (K != 0 || J != 0) {
                throw new Exception("This is not A00 matrix");
            }
            String sourceAddress = data.get("address").toString();

            double[][] A00 = DataDriver.get(sourceAddress, String.format("A/A-%s-%s", K, K));

            A00Processor a00processor = new A00Processor(A00);
            a00processor.calculate();
            a00processor.getL00();
            a00processor.getU00();
            a00processor.getL00I();
            a00processor.getU00I();

            DataWriter.writeMatrix(String.format("L/L-%s-%s", K, K), a00processor.getL00(), MatrixType.L);
            DataWriter.writeMatrix(String.format("U/U-%s-%s", K, K), a00processor.getU00(), MatrixType.U);

            if (K == R) {
                //last element processed, terminate
                driver.send(Chanels.terminate, new JSONObject());
                return;
            }

            DataWriter.writeMatrix(String.format("LI/LI-%s", K), a00processor.getL00I(), MatrixType.L);
            DataWriter.writeMatrix(String.format("UI/UI-%s", K), a00processor.getU00I(), MatrixType.U);

            for (int j = K + 1; j < R; j++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("address", SetupConfig.get().getNetworkAddress());
                map.put("K", Integer.toString(K));
                map.put("R", Integer.toString(R));

                JSONObject reply = new JSONObject(map);
                driver.send(Chanels.U00I, reply);
                driver.send(Chanels.L00I, reply);
            }
            driver.delete(Chanels.A00, message.getId());
        }
    }
}
