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
    public  MessageInterface getMessageFor(Chanels chanel) throws Exception;

    public List<MessageInterface> getAllMessagesFor(Chanels chanel) throws Exception;

    public void send(Chanels chanel, JSONObject data) throws Exception;

    public void send(Chanels chanel, JSONArray data) throws Exception;

    public void delete(Chanels chanel, String messageId) throws Exception;
}
