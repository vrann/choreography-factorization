package com.vrann.Choreography.Chanel;

import com.amazonaws.services.sqs.model.Message;
import com.vrann.Choreography.MessageInterface;

/**
 * Created by etulika on 6/18/16.
 */
public class AwsMessageWrapper implements MessageInterface {

    private Message sqsMessage;

    public AwsMessageWrapper(Message message) {
        sqsMessage = message;
    }

    public String getBody() {
        return sqsMessage.getBody();
    }

    public String getId() {
        return sqsMessage.getReceiptHandle();
    }
}
