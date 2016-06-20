package com.vrann.Choreography;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.io.*;

/**
 * Created by etulika on 6/18/16.
 */
public class SetupConfig {

    private String remoteFileDriver = "rsync";
    private String queueDriver = "SQS";
    private String localDataDir = "/Users/etulika/Projects/java/matrix";
    private String remoteDataDir = "/matrix/";
    private String remoteUser = "ec2-user";
    private String keyPath = "/Users/etulika/.ssh/eugene-east.pem";
    private String awsCredentialsPath = "~/.aws/credentials";
    private String chanlesBasePath = "chanels/";
    private String networkAddress = "127.0.0.1";

    private SetupConfig() throws IOException, JSONException {
        this("conf/setup.json");
    }

    private SetupConfig(String configFileName) throws IOException, JSONException {
        File f = new File(configFileName);
        if (!f.exists()) {
            System.out.println("Setup config does not exists, falls back to defaults");
        }
        BufferedReader configReader = new BufferedReader(new FileReader(configFileName));
        String line;
        StringBuilder jsonString = new StringBuilder();
        while ((line = configReader.readLine()) != null) {
            jsonString.append(line);
        }
        configReader.close();

        JSONObject config = new JSONObject(jsonString.toString());
        if (config.has("remoteFileDriver")) {
            remoteFileDriver = config.get("remoteFileDriver").toString();
        }

        if (config.has("queueDriver")) {
            queueDriver = config.get("queueDriver").toString();
        }

        if (config.has("localDataDir")) {
            localDataDir = config.get("localDataDir").toString();
        }

        if (config.has("remoteDataDir")) {
            remoteDataDir = config.get("remoteDataDir").toString();
        }

        if (config.has("remoteUser")) {
            remoteUser = config.get("remoteUser").toString();
        }

        if (config.has("keyPath")) {
            keyPath = config.get("keyPath").toString();
        }

        if (config.has("awsCredentialsPath")) {
            awsCredentialsPath = config.get("awsCredentialsPath").toString();
        }

        if (config.has("chanelsBasePath")) {
            chanlesBasePath = config.get("chanelsBasePath").toString();
        }

        if (config.has("networkAddress")) {
            networkAddress = config.get("networkAddress").toString();
        }
    }

    private static SetupConfig config;

    public static SetupConfig get() throws Exception {
        if (config == null) {
            config = new SetupConfig();
        }
        return config;
    }

    public String getRemoteFileDriver() {
        return remoteFileDriver;
    }

    public String getChanelDriver() {
        return queueDriver;
    }

    public String getLocalDataDir() {
        return localDataDir;
    }

    public String getRemoteDataDir() {
        return remoteDataDir;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public String getChanelsBasePath() {
        return chanlesBasePath;
    }

    public String getAwsCredentialsPath() {
        return awsCredentialsPath;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

}
