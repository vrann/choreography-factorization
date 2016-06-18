package com.vrann.Factorization;

import com.vrann.Matrix.DataWriter;
import com.vrann.Matrix.Multiplicator;

/**
 * Created by etulika on 6/13/16.
 */
public class L10Processor {

    private double[][] A10;
    private double[][] U00I;
    private double[][] L10;

    public L10Processor(double[][] A10, double[][] U00I) {
        this.A10 = A10;
        this.U00I = U00I;
    }

    public void calculate() throws Exception {
        L10 = Multiplicator.multiply(A10, U00I);
    }

    public double[][] getL10() {
        return L10;
    }
}
