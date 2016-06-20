package com.vrann.Choreography;

import com.vrann.Choreography.Chanel.AWSSQSDriver;
import com.vrann.Choreography.Chanel.FileDriver;

/**
 * Created by etulika on 6/18/16.
 */
public class ChanelFactory {

    public ChanelInterface getChanelDriver() throws Exception {
        switch (SetupConfig.get().getChanelDriver()) {
            case "File":
                return new FileDriver(SetupConfig.get().getChanelsBasePath());
            default:
            case "SQS":
                return new AWSSQSDriver(SetupConfig.get().getAwsCredentialsPath());
        }
    }
}
