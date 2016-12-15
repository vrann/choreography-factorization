package com.vrann.Matrix;

//import no.uib.cipr.matrix.DenseMatrix;

/**
 * Created by etulika on 6/6/16.
 */
public class Multiplicator {

    public static double[][] multiply(double[][] A, double[][] B) throws Exception {
        if (A[0].length != B.length) {
            throw new Exception("Incompatible Matrix sizes");
        }
        double[][] result = new double[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int k = 0; k < B[0].length; k++) {
                for (int j = 0; j < A[i].length; j++) {
                    result[i][k] += A[i][j] * B[j][k];
                }
            }
        }
        return result;
    }

//    public static DenseMatrix multiplyEffective(double[][] A, double[][] B)
//    {
//        DenseMatrix matrixA = new DenseMatrix(A);
//        DenseMatrix matrixB = new DenseMatrix(B);
//        DenseMatrix matrixC = new DenseMatrix(A.length, A.length);
//        matrixA.mult(matrixB, matrixC);
//        return matrixC;
//    }

    public static double[] multiplyVector(double[][] A, double[] C) {
        double[] result = new double[C.length];
        for (int i = 0; i < A.length; i++) {
            for (int k = 0; k < C.length; k++) {
                result[i] += A[i][k] * C[k];
            }
        }
        return result;
    }
}
