package com.vrann.Service;

import com.vrann.Math.LU;
import com.vrann.Matrix.Printer;

/**
 * Created by etulika on 6/4/16.
 */
public class Service {

    private String[] roles;

    private String[] commands;

    private void send(String role, String command) throws Exception
    {
        /*boolean localSend = false;
        for (String myRole: roles) {
            if (role == myRole) {
                execute(role, command, data);
                return;
            }
        }
        Processor p = new Processor();
        p.send(role, command, data);*/
    }


    public void execute(String role, String command) throws Exception
    {
        //ProcessTemplate pt = new ProcessTemplate(new DataReader("matrix"));
        switch (command) {
            case "pivot":

            case "factorize":
                /*double[][] block = pt.getMatrixBlock(role);
                System.out.println(Printer.print(block));
                LU lu = new LU(block, 2);
                System.out.println(Printer.print(lu.getL()));
                System.out.println(Printer.print(lu.getU()));*/
                break;
            default:
                throw new Exception("Unsupported Command");
        }

    }

    public static void main(String args[]) {
        String role = args[0];
        String command = args[1];
        System.out.println(role);
        System.out.println(command);
        Service s = new Service();
        try {
            s.execute(role, command);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
