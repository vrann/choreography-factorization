package com.vrann.Factorization;

import com.vrann.Math.LU;
import com.vrann.Math.Substitution;
import com.vrann.Matrix.DataWriter;

/**
 * Created by etulika on 6/12/16.
 */
public class A00Processor {

    private double[][] matrixBlock;

    private double[][] L00;

    private double[][] U00;

    private double[][] L00I;

    private double[][] U00I;

    public A00Processor(double[][] A00) {
        this.matrixBlock = A00;
    }

    public void calculate() throws Exception {
        LU luFactorization = new LU(this.matrixBlock, this.matrixBlock.length);
        L00 = luFactorization.getL();
        U00 = luFactorization.getU();
        L00I = Substitution.forwardSubstitutionIdentity(luFactorization.getL());
        U00I = Substitution.backSubstitutionIdentity(luFactorization.getU());
    }

    public double[][] getL00() {
        return L00;
    }

    public double[][] getU00() {
        return U00;
    }

    public double[][] getL00I() {
        return L00I;
    }

    public double[][] getU00I() {
        return U00I;
    }
}