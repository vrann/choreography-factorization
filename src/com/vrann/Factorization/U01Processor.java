package com.vrann.Factorization;

import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.Multiplicator;

/**
 * Created by etulika on 6/13/16.
 */
public class U01Processor {
    private double[][] A01;
    private double[][] L00I;
    private double[][] U01;

    public U01Processor(double[][] A01, double[][] L00I) {
        this.A01 = A01;
        this.L00I = L00I;
    }

    public void calculate() throws Exception {
        U01 = Multiplicator.multiply(A01, L00I);
    }

    public double[][] getU01() {
        return U01;
    }
}
