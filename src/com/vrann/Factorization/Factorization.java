package com.vrann.Factorization;

import com.vrann.Service.DataReader;

/**
 * Created by etulika on 6/13/16.
 */
public class Factorization {

    public static void main(String[] args) {

        /*try {
            String matrixName = "A00";
            if (args.length >= 2) {
                matrixName = args[1];
            }
            int K = 0;
            //DataReader dr = new DataReader(matrixName);
            switch (args[0]) {
                case "factorize":
                    A00Processor A00 = new A00Processor(DataReader.getMatrix(matrixName));
                    A00.calculate();
                    break;
                case "calculateL10":
                    L10Processor l10 = new L10Processor(DataReader.getMatrix(matrixName), DataReader.getMatrix("U00I-" + K), K);
                    l10.calculate();
                    break;
                case "calculateU01":
                    U01Processor u01 = new U01Processor(DataReader.getMatrix(matrixName), DataReader.getMatrix("L00I-" + K), K);
                    u01.calculate();
                    break;
                case "calculateA11":
                    A11Processor a11 = new A11Processor(DataReader.getMatrix(matrixName), DataReader.getMatrix("L10-" + K + "-0"), DataReader.getMatrix("U01-0-" + K), K);
                    a11.calculate();
                    break;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
    }
}
