package com.vrann.Math;

import com.vrann.Matrix.Multiplicator;

/**
 * Created by etulika on 6/3/16.
 */
public class LU {
    private double[][] A;

    private double[][] L;

    private double[][] P;

    private double[][] U;

    private int blocksize = 2;

    public LU(double[][] A, int blocksize) throws Exception
    {
        if (A.length == 0) {
            throw new Exception("Matrix is empty");
        }
        this.A = A;
        U = new double[A.length][A.length];
        L = new double[A.length][A.length];

        this.blocksize = blocksize;

        factorize();
    }

    private double[][] copyArray(double[][] Input)
    {
        int width = 0;
        if (Input.length == 0) {
            return new double[0][0];
        } else {
            width = Input[0].length;
        }
        double[][] result = new double[Input.length][width];
        for (int i = 0; i < Input.length; i++) {
            result[i] = Input[i].clone();
        }
        return result;
    }

    public double[][] getInverse() throws Exception
    {
        return Substitution.backSubstitution(U, P);
    }

    private void factorize() throws Exception
    {
        if (A.length > blocksize) {
            double[][] U00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                for (int j = 0; j < blocksize; j++) {
                    U00[i][j] = A[i][j];
                }
            }

            double[][] A01 = new double[blocksize][A.length - blocksize];
            for (int i = 0; i < blocksize; i++) {
                for (int j = blocksize; j < A[0].length; j++) {
                    A01[i][j-blocksize] = A[i][j];
                }
            }
            double[][] A10 = new double[A.length - blocksize][blocksize];
            for (int i = blocksize; i < A[0].length; i++) {
                for (int j = 0; j < blocksize; j++) {
                    A10[i - blocksize][j] = A[i][j];
                }
            }
            double[][] L00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                L00[i][i] = 1;
            }

            double[][] P00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                P00[i][i] = 1;
            }

            factorizeBlock(U00, L00);
            for (int i = 0; i < U00.length; i++) {
                for (int j = 0; j < U00[i].length; j++) {
                    U[i][j] = U00[i][j];
                    L[i][j] = L00[i][j];
                }
            }

            //Calculate inverted matrix for L and U
            double[][] L00I = Substitution.forwardSubstitutionIdentity(L00);
            double[][] U00I = Substitution.backSubstitutionIdentity(U00);

            double[][] L10 = Multiplicator.multiply(A10, U00I);
            double[][] U01 = Multiplicator.multiply(L00I, A01);
            for (int i = 0; i < L10.length; i++) {
                for (int j = 0; j < L10[i].length; j++) {
                    U[j][blocksize+i] = U01[j][i];
                    L[blocksize+i][j] = L10[i][j];
                }
            }


            double[][] A11 = new double[A.length - blocksize][A.length - blocksize];
            for (int i = blocksize; i < A.length; i++) {
                for (int j = blocksize; j < A[i].length; j++) {
                    A11[i - blocksize][j-blocksize] = A[i][j];
                }
            }

            double[][] substract = Multiplicator.multiply(L10, U01);
            for (int i = 0; i < A11.length; i++) {
                for (int j = 0; j < A11[i].length; j++) {
                    A11[i][j] -= substract[i][j];
                }
            }

            int blockNum = (A.length - blocksize) / blocksize;
            for (int a = 0; a < blockNum; a++) {
                for (int b = 0; b < blockNum; b++) {
                    System.out.printf("Block %s, %s", a, b);
                }
            }

            LU lu = new LU(A11, blocksize);

            double[][] L1 = lu.getL();
            double[][] U1 = lu.getU();
            for (int i = 0; i < L1.length; i++) {
                for (int j = 0; j < L1[i].length; j++) {
                    U[blocksize+i][blocksize+j] = U1[i][j];
                    L[blocksize+i][blocksize+j] = L1[i][j];
                }
            }
        } else if (A.length == blocksize) {
            double[][] U00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                for (int j = 0; j < blocksize; j++) {
                    U00[i][j] = A[i][j];
                }
            }

            double[][] L00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                L00[i][i] = 1;
            }

            /*double[][] P00 = new double[blocksize][blocksize];

            for (int i = 0; i < blocksize; i++) {
                P00[i][i] = 1;
            }*/

            factorizeBlock(U00, L00);
            this.L = L00;
            //this.P = P00;
            this.U = U00;
        }
    }

    private void factorizeBlock(double[][] U, double[][] L) {
        int N = U.length;
        for (int n = 0; n < N-1; n++) {
            if (n >= U[n].length) {
                break;
            }

            /*double pivot = U[n][n];
            int pivotRow = n;
            for (int i = n; i < N; i++) {
                if (U[i][n] > pivot) {
                    pivot = U[i][n];
                    pivotRow = i;
                }
            }
            if (pivotRow != n) {
                double[] currentRow = U[n];
                double[] currentLRow = L[n];
                U[n] = U[pivotRow];
                U[pivotRow] = currentRow;
                L[n] = L[pivotRow];
                L[pivotRow] = currentLRow;
            }*/

            for (int i = n + 1; i < N; i++) {
                if (Math.abs(U[n][n]) <= 1.0E-10D) {
                    U[n][n] = 1.0E-10D;
                    //throw new RuntimeException("Matrix is singular or nearly singular");
                }
                double factor = (U[i][n] / U[n][n]);
                for (int j = 0; j < U[i].length; j++) {
                    U[i][j] = U[i][j] - U[n][j] * factor;
                    //P[i][j] = P[i][j] - P[n][j] * factor;
                }
                L[i][n] = factor;
            }

        }
    }

    public double[][] getL()
    {
        return L;
    }

    public double[][] getU()
    {
        return U;
    }
}
