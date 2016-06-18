package com.vrann.Choreography;

import com.amazonaws.services.sqs.model.Message;
import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Factorization.Chanels;

/**
 * Created by etulika on 6/16/16.
 */
public class TerminateChanel {
    public boolean process() throws Exception
    {
        AWSSQSDriver driver = new AWSSQSDriver();

        //listen for the messages from queue
        Message message = driver.getMessageFor(Chanels.terminate);
        if (message != null) {
            return true;
        }
        return false;
    }
}
