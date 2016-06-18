package com.vrann.Choreography;

import com.vrann.Service.DataReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by etulika on 6/15/16.
 */
public class DataDriver {

    private String sourceAddress;

    private String currentAddress;

    private String dirName;

    private String user;

    private String keyName;

    public DataDriver(String sourceAddress, String currentAddress, String basePath) {
        this.sourceAddress = sourceAddress;
        this.currentAddress = currentAddress;
        this.dirName = basePath + "/matrix/";
        this.user = "ec2-user";
        this.keyName = basePath + "/.ssh/eugene-east.pem";
    }

    public double[][] get(String matrixFileName) throws Exception {
        String local = dirName + matrixFileName;
        String remote = dirName + matrixFileName;

        if (sourceAddress.equals(currentAddress) || DataReader.exists(local)) {
            return DataReader.getMatrix(local);
        }

        String[] rsyncCommand = new String[] {
                "rsync",
                "-e",
                String.format("ssh -o StrictHostKeyChecking=no -i %s", keyName),
                String.format("%s@%s:%s", user, sourceAddress, remote),
                local
        };

        StringBuilder rsync = new StringBuilder();
        for (String cmd: rsyncCommand) {
            rsync.append(cmd + " ");
        }
        System.out.println("Command: " + rsync);

        ProcessBuilder pb = new ProcessBuilder(rsyncCommand);
        Process p = pb.start();

        InputStream stderr = p.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder errorDescriptionBuilder = new StringBuilder();
        String errorLine;
        while ((errorLine = br.readLine()) != null) {
            errorDescriptionBuilder.append(errorLine + "\n");
        }
        int exitVal = p.waitFor();
        if (errorDescriptionBuilder.toString().length() > 0) {
            errorDescriptionBuilder.append("Process exitValue:" + exitVal);
            throw new Exception(errorDescriptionBuilder.toString());
        }

        if (DataReader.exists(local)) {
            return DataReader.getMatrix(local);
        }

        throw new Exception(String.format("Matrix file does not exists %s", local));
    }
}
