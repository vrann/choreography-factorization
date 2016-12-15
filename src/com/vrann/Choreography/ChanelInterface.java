package com.vrann.Choreography;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.vrann.Factorization.Chanels;

import java.util.List;

/**
 * Created by etulika on 6/18/16.
 */
public interface ChanelInterface {
    MessageInterface getMessageFor(Chanels chanel) throws Exception;

    List<MessageInterface> getAllMessagesFor(Chanels chanel) throws Exception;

    void send(Chanels chanel, JSONObject data) throws Exception;

    void send(Chanels chanel, JSONArray data) throws Exception;

    void delete(Chanels chanel, String messageId) throws Exception;

    void requeue(Chanels chanel, MessageInterface message) throws Exception;
}
