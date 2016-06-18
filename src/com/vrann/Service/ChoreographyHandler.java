package com.vrann.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.vrann.Math.LU;
import com.vrann.Math.Substitution;
import com.vrann.Matrix.DataWriter;

import java.io.*;
import java.util.List;

/**
 * Created by etulika on 6/11/16.
 */
public class ChoreographyHandler {

    public static void main(String[] args) {

        boolean flag = true;

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setRegion(usEast1);
        System.out.println("Listing buckets");
        for (Bucket bucket : s3.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
        System.out.println();

        String A00queue = "";
        String L01U01ReadyQueue = "";
        String A11ReadyQueue = "";
        String A01A10ReadyQueue = "";
        String L00U00ReadyQueue = "";
        String L10ReadyQueue = "";
        String U01ReadyQueue = "";
        String ForA11ReadyQueue = "";
        String ForL10U01ReadyQueue = "";

        // List queues
        System.out.println("Listing all queues in your account.\n");
        for (String queueUrl: sqs.listQueues().getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);

            if (queueUrl.contains("A00-ready")) {
                A00queue = queueUrl;
            } else if (queueUrl.contains("L01U01-ready")) {
                L01U01ReadyQueue = queueUrl;
            } else if (queueUrl.contains("A11-ready")) {
                A11ReadyQueue = queueUrl;
            } else if (queueUrl.contains("A01A10-ready")) {
                A01A10ReadyQueue = queueUrl;
            } else if (queueUrl.contains("L00U00-ready")) {
                L00U00ReadyQueue = queueUrl;
            } else if (queueUrl.contains("L10-ready")) {
                L10ReadyQueue = queueUrl;
            } else if (queueUrl.contains("U01-ready")) {
                U01ReadyQueue = queueUrl;
            } else if (queueUrl.contains("for-A11-ready")) {
                ForA11ReadyQueue = queueUrl;
            } else if (queueUrl.contains("for-L10U01-ready")) {
                ForL10U01ReadyQueue = queueUrl;
            }
        }
        System.out.println();

        //Main event loop of the handler
        while (flag) {

            try {
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(A00queue);
                receiveMessageRequest.setMaxNumberOfMessages(1);
                List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
                if (messages.size() > 0) {
                    final long startTime = System.currentTimeMillis();
                    String K = messages.get(0).getBody();
                    System.out.printf("Message Body: %s", K);
                    //int K = Integer.getInteger(data);

                    String bucketName = "eugene-tulika-choreography-matrix";
                    String key = "A00-" + K;

                    System.out.printf("Downloading an object %s/%s \n", bucketName, key);
                    S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
                    InputStream in = object.getObjectContent();
                    byte[] buf = new byte[1024];

                    String dirName = "/home/ec2-user/";
                    //String dirName = "/Users/etulika/";

                    String fileA00 = "A00-" + K;
                    OutputStream out = new FileOutputStream(dirName + fileA00);
                    int count = 0;
                    while((count = in.read(buf)) != -1)
                    {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        out.write(buf, 0, count);
                    }
                    out.close();
                    in.close();
                    System.out.printf("Done. \n");

                    final long startCalcTime = System.currentTimeMillis();
                    double A00[][] = DataReader.getMatrix(dirName + fileA00);
                    LU luFactorization = new LU(A00, A00.length);
                    final long endCalcTime = System.currentTimeMillis();

                    String basePath = "matrix/";
                    String fileL00 = "L00-" + K;
                    (new DataWriter(dirName + fileL00, basePath)).write(luFactorization.getL());
                    System.out.printf("Uploading object to S3 %s \n", fileL00);
                    s3.putObject(new PutObjectRequest(bucketName, fileL00, new File(dirName + fileL00)));

                    String fileU00 = "U00-" + K;
                    (new DataWriter(dirName + fileU00, basePath)).write(luFactorization.getU());
                    System.out.printf("Uploading object to S3 %s \n", fileU00);
                    s3.putObject(new PutObjectRequest(bucketName, fileU00, new File(dirName + fileU00)));
                    System.out.printf("Done. \n");

                    //Calculate inverted matrix for L and U
                    String fileL00I = "L00I-" + K;
                    double[][] L00I = Substitution.forwardSubstitutionIdentity(luFactorization.getL());
                    (new DataWriter(dirName + fileL00I, basePath)).write(L00I);
                    System.out.printf("Uploading object to S3 %s \n", fileL00I);
                    s3.putObject(new PutObjectRequest(bucketName, fileL00I, new File(dirName + fileL00I)));
                    System.out.printf("Done. \n");

                    String fileU00I = "U00I-" + K;
                    double[][] U00I = Substitution.backSubstitutionIdentity(luFactorization.getU());
                    (new DataWriter(dirName + fileU00I, basePath)).write(U00I);
                    System.out.printf("Uploading object to S3 %s \n", fileU00I);
                    s3.putObject(new PutObjectRequest(bucketName, fileU00I, new File(dirName + fileU00I)));
                    System.out.printf("Done. \n");

                    System.out.println("Deleting a message.\n");
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    sqs.deleteMessage(new DeleteMessageRequest(A00queue, messageReceiptHandle));
                    System.out.printf("Done. \n");
                    final long endTime = System.currentTimeMillis();
                    System.out.printf("Total execution time. Wall %s, calc: %s \n",
                            (endTime - startTime), (endCalcTime - startCalcTime));
                }

                System.out.print(".");
            }  catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which means your request made it " +
                        "to Amazon SQS, but was rejected with an error response for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
                ase.printStackTrace();
                flag = false;
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which means the client encountered " +
                        "a serious internal problem while trying to communicate with SQS, such as not " +
                        "being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
                flag = false;
            } catch (Exception e) {
                System.out.println("Error Message:    " + e.getMessage());
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
    }

}
