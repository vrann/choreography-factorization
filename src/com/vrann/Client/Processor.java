package com.vrann.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by etulika on 6/4/16.
 */
public class Processor {

    private String role;

    public Processor(String role) {
        this.role = role;
    }

    public String send(String command)
    {
        Runtime rt = Runtime.getRuntime();
        StringBuffer sb = new StringBuffer();
        try {
            String fullCommand = "java -cp out/production/gaussian com.vrann.Service " + role + " " + command;
            System.out.println(fullCommand);
            Process pr = rt.exec(fullCommand);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while((line = input.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            input.close();
        } catch (Exception e) {
            sb.append("failure\n");
            sb.append(e.getStackTrace());
        }
        return sb.toString();
    }

}
