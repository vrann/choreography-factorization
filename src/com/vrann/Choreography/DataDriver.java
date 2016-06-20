package com.vrann.Choreography;

import com.vrann.Service.DataReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by etulika on 6/15/16.
 */
public class DataDriver {

    public static double[][] get(String sourceAddress, String matrixFileName) throws Exception
    {
        SetupConfig config = SetupConfig.get();

        String local = config.getLocalDataDir() + matrixFileName;
        String remote = config.getLocalDataDir() + matrixFileName;

        if (sourceAddress.equals(config.getNetworkAddress()) || DataReader.exists(local)) {
            return DataReader.getMatrix(local);
        }

        String[] rsyncCommand = new String[] {
                "rsync",
                "-e",
                String.format("ssh -o StrictHostKeyChecking=no -i %s", config.getKeyPath()),
                String.format("%s@%s:%s", config.getRemoteUser(), sourceAddress, remote),
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
        if (exitVal > 0) {
            errorDescriptionBuilder.append("Process exitValue:" + exitVal);
            throw new Exception(errorDescriptionBuilder.toString());
        } else {
            System.out.println(errorDescriptionBuilder.toString());
        }
        br.close();

        if (DataReader.exists(local)) {
            return DataReader.getMatrix(local);
        }

        throw new Exception(String.format("Matrix file does not exists %s", local));
    }
}
