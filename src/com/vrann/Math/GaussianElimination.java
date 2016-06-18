package com.vrann.Math;

/**
 * Created by etulika on 5/7/16.
 */
public class GaussianElimination {

    private double[][] A;

    public GaussianElimination(double[][] A) {
        this.A = A;
    }

    public double[][] toUpperTriangular()
    {
        int N = A.length;
        //System.out.println(toString());
        for (int n = 0; n < N-1; n++) {
            if (n >= A[n].length) {
                break;
            }
            System.out.println(toString());
            double pivot = A[n][n];
            int pivotRow = n;
            for (int i = n; i < N; i++) {
                if (A[i][n] > pivot) {
                    pivot = A[i][n];
                    pivotRow = i;
                }
            }
            if (pivotRow != n) {
                double[] currentRow = A[n];
                A[n] = A[pivotRow];
                A[pivotRow] = currentRow;
            }
            System.out.println(toString());
            for (int i = n + 1; i < N; i++) {
                if(Math.abs(A[n][n]) <= 1.0E-10D) {
                    throw new RuntimeException("Matrix is singular or nearly singular");
                }
                double factor = (-1) * (A[i][n] / A[n][n]);
                for (int j = 0; j < A[i].length; j++) {
                    A[i][j] = A[i][j] + A[n][j]*factor;
                }
                //System.out.println(toString());
            }
        }
        //System.out.println(toString());
        return A;
    }

    public double[] solve()
    {
        toUpperTriangular();
        System.out.println(toString());
        double[] result = new double[A.length];
        for (int i = A.length - 1; i > 0; i--) {
            int ncol = A[i].length;
            double sum = 0.0;
            for (int j = i+1; j < ncol-1; j++) {
                sum += A[i][j] * result[j];
            }
            result[i] = (A[i][ncol - 1] - sum) / A[i][i];
        }
        return result;
    }

    public String toString() {
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

}
