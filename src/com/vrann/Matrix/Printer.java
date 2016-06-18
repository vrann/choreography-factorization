package com.vrann.Matrix;

/**
 * Created by etulika on 6/4/16.
 */
public class Printer {
    public static String print(double[][] A) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                sb.append(A[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String formatColumn(double[] C) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < C.length; i++) {
                sb.append(C[i]);
                sb.append(" ");
        }
        return sb.toString();
    }
}
