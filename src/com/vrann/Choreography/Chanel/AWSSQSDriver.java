package com.vrann.Choreography.Chanel;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.json.JSONObject;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Factorization.Chanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etulika on 6/13/16.
 */
public class AWSSQSDriver implements ChanelInterface {

    private AmazonSQS sqsClient;

    private HashMap<String, String> queueUrls = new HashMap<>();

    public AWSSQSDriver(String credentialsFile) {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider(credentialsFile, "default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(String.format(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (%s), and is in valid format.", credentialsFile),
                    e);
        }

        sqsClient = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqsClient.setRegion(usEast1);


        for (String queueUrl: sqsClient.listQueues().getQueueUrls()) {
            for (Chanels chanel : Chanels.values()) {
                if (queueUrl.matches(".*/" + chanel.toString() + "$")) {
                    queueUrls.put(chanel.toString(), queueUrl);
                }
            }
        }
    }

    public MessageInterface getMessageFor(Chanels chanel) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrls.get(chanel.toString()));
        receiveMessageRequest.setMaxNumberOfMessages(1);
        try {
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
            if (messages.size() > 0) {
                return new AwsMessageWrapper(messages.get(0));
            }
        } catch (AmazonServiceException ase) {
            processServiceException(ase);
        } catch (AmazonClientException ace) {
            processClientException(ace);
        }
        return null;
    }

    public List<MessageInterface> getAllMessagesFor(Chanels chanel) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrls.get(chanel.toString()));
        receiveMessageRequest.setMaxNumberOfMessages(1);
        try {
            List<MessageInterface> result = new ArrayList<>();
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message: messages) {
                result.add(new AwsMessageWrapper(message));
            }
            return result;
        } catch (AmazonServiceException ase) {
            processServiceException(ase);
        } catch (AmazonClientException ace) {
            processClientException(ace);
        }
        return null;
    }

    public void send(Chanels chanel, JSONObject data) {
        sqsClient.sendMessage(new SendMessageRequest(queueUrls.get(chanel.toString()), data.toString()));
    }

    public void delete(Chanels chanel, String messageId) {
        sqsClient.deleteMessage(new DeleteMessageRequest(queueUrls.get(chanel.toString()), messageId));
    }

    private void processServiceException(AmazonServiceException ase) {

        System.out.println("Caught an AmazonServiceException, which means your request made it " +
                "to Amazon SQS, but was rejected with an error response for some reason.");
        System.out.println("Error Message:    " + ase.getMessage());
        System.out.println("HTTP Status Code: " + ase.getStatusCode());
        System.out.println("AWS Error Code:   " + ase.getErrorCode());
        System.out.println("Error Type:       " + ase.getErrorType());
        System.out.println("Request ID:       " + ase.getRequestId());
        ase.printStackTrace();
    }

    private void processClientException(AmazonClientException ace) {
        System.out.println("Caught an AmazonClientException, which means the client encountered " +
                "a serious internal problem while trying to communicate with SQS, such as not " +
                "being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
    }
}
