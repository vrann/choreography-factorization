package com.vrann.Factorization.Chanel;

import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.ChanelFactory;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Factorization.Chanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageSet {

    private Chanels chanel;

    private List<JSONObject> messages = new ArrayList<>();

    HashMap<String, JSONObject> messageMap = new HashMap<>();

    private List<MessageInterface> messagesBodies;

    private boolean initialized = false;

    private ChanelInterface driver;

    private String key;

    public MessageSet(Chanels chanel, String key) throws Exception {
        this.chanel = chanel;
        driver = new ChanelFactory().getChanelDriver();
        this.key = key;
    }

    public List<JSONObject> get() throws Exception {
        if (!initialized) {
            init();
        }
        return messages;
    }

    public HashMap<String, JSONObject> getMap() throws Exception {
        if (!initialized) {
            init();
        }
        return messageMap;
    }

    public void requeue() throws Exception {
        if (!initialized || messagesBodies == null) {
            throw new Exception("Message Set is not initialized. Nothing ro requeue");
        }
        for (MessageInterface message: messagesBodies) {
            //message already deleted from the channel during init. Just re-sending
            driver.send(chanel, new JSONObject(new JSONTokener(message.getBody())));
            //driver.requeue(chanel, message);
        }
        return;
    }

    public int size() throws Exception {
        if (!initialized) {
            init();
        }
        return messages.size();
    }

    public List<JSONObject[]> intersect(MessageSet guest) throws Exception {
        if (!initialized) {
            init();
        }
        List<JSONObject[]> result = new ArrayList<>();
        HashMap<String, JSONObject> guestMap = guest.getMap();
//        System.out.println(messages.size());
//        System.out.println(guestMap.keySet());
        for (JSONObject message: messages) {
            for (String guestJ: guestMap.keySet()) {

                JSONObject guestMessage = guestMap.get(guestJ);
                int guestK = Integer.valueOf(guestMessage.get("K").toString());
                int messageK = Integer.valueOf(message.get("K").toString());

                if (guestK != messageK) {
                    continue;
                }

//                System.out.println(guestK);

                JSONObject usedMessage = (JSONObject) message.get("usedWith");
//                System.out.println(usedMessage);

                JSONObject usedGuest = (JSONObject) guestMessage.get("usedWith");
//                System.out.println(usedGuest);

                String messageJ = message.get(this.key).toString();
                if (!((JSONObject) message.get("usedWith")).has(guestJ)) {
                    if (((JSONObject) guestMessage.get("usedWith")).has(messageJ)) {
//                        throw new Exception(
//                                String.format(
//                                        "Guest matrix was used with Message %s, %s: %s while Message wasn't used with Guest J %s: %s; %s %s; %s",
//                                        chanel, key,
//                                        guestMessage.get("usedWith"),
//                                        guestJ,
//                                        message.get("usedWith"),
//                                        guestMessage.get("I"),
//                                        guestMessage.get("J"),
//                                        messageK
//                                )
//                        );
                    }
                    JSONObject[] pair = new JSONObject[] {message, guestMessage};
                    result.add(pair);



                    usedMessage.put(guestJ, true);
                    message.put("usedWith", usedMessage);
//                    System.out.println(message.get("usedWith"));


                    usedGuest.put(messageJ, true);
                    guestMessage.put("usedWith", usedGuest);
//                    System.out.println(guestMessage.get("usedWith"));
                }
            }
        }
        return result;
    }

    public void resendMessages() throws Exception {
        for (JSONObject message: messages) {
            int R = Integer.parseInt(message.get("R").toString());
            int K = Integer.parseInt(message.get("K").toString());
            int maxSentTimes = R - K - 1;
            resend(message, maxSentTimes);
        }
    }

    public void resendMessages(int maxSentTimes) throws Exception {
        for (JSONObject message: messages) {
            resend(message, maxSentTimes);
        }
    }

    private void resend(JSONObject message, int maxSentTimes) throws Exception {
        JSONObject usedMessage = (JSONObject)message.get("usedWith");
        if (usedMessage.length() < maxSentTimes) {
//            System.out.printf("Resend: matrix not used enough %s %s %s %s \n",
//                    chanel.toString(),
//                    message.get("K").toString(),
//                    message.get(this.key).toString(),
//                    usedMessage
//            );
            driver.send(chanel, message);
        } else {
            System.out.printf("Remove: matrix used enough %s %s %s %s \n",
                    chanel.toString(),
                    message.get("K").toString(),
                    message.get(this.key).toString(),
                    usedMessage
            );
        }
    }

    private void init() throws Exception {
        messagesBodies = driver.getAllMessagesFor(this.chanel);
        if (messagesBodies.size() == 0) {
            return;
        }

        for (MessageInterface messageBody: messagesBodies) {
            JSONObject message = new JSONObject(new JSONTokener(messageBody.getBody()));
            messages.add(message);

            String J = message.get(this.key).toString();
            JSONObject usedWith = new JSONObject();
            if (message.has("usedWith")) {
                usedWith = (JSONObject) message.get("usedWith");
            }
            message.put("usedWith", usedWith);
            messageMap.put(J, message);
            driver.delete(chanel, messageBody.getId());
        }

        initialized = true;
    }
}
