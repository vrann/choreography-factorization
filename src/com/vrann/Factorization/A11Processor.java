package com.vrann.Factorization;

import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.Multiplicator;

/**
 * Created by etulika on 6/13/16.
 */
public class A11Processor {
    private double[][] A11;
    private double[][] L10;
    private double[][] U01;

    public A11Processor(double[][] A11, double[][] L10, double[][] U01) {
        this.A11 = A11;
        this.L10 = L10;
        this.U01 = U01;
    }

    public void calculate() throws Exception {
        double[][] substract = Multiplicator.multiply(L10, U01);
        for (int i = 0; i < A11.length; i++) {
            for (int j = 0; j < A11[i].length; j++) {
                A11[i][j] -= substract[i][j];
            }
        }
    }

    public double[][] getA11() {
        return this.A11;
    }
}
