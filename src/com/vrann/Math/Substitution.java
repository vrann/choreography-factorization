package com.vrann.Math;

/**
 * Created by etulika on 6/12/16.
 */
public class Substitution {


    public static double[][] forwardSubstitution(double[][] L, double[][] I) throws Exception
    {
        double[][] result = new double[L.length][L[0].length];
        for (int n = 0; n < L[0].length; n++) {
            double[] column = new double[I.length];
            for (int i = 0; i < I.length; i++) {
                column[i] = I[i][n];
            }

            for (int i = 0; i < L.length; i++) {
                if (column.length != L[i].length) {
                    throw new Exception("Incompatible length of Matrix and vector");
                }

                double sum = 0.0;
                for (int j = 0; j < i; j++) {
                    sum += L[i][j] * column[j];
                }
                column[i] = (column[i] - sum) / L[i][i];
            }

            for (int i = 0; i < I.length; i++) {
                result[i][n] = column[i];
            }
        }
        return result;
    }

    public static double[][] backSubstitution(double[][] U, double[][] I) throws Exception
    {
        double[][] result = new double[U.length][U[0].length];

        for (int n = 0; n < U[0].length; n++) {
            double[] column = new double[I.length];
            for (int i = 0; i < I.length; i++) {
                column[i] = I[i][n];
            }

            for (int i = U.length - 1; i >= 0; i--) {
                if (column.length != U[i].length) {
                    throw new Exception("Incompatible length of Matrix and vector");
                }

                double sum = 0.0;
                for (int j = i + 1; j < column.length; j++) {
                    sum += U[i][j] * column[j];
                }
                column[i] = (column[i] - sum) / U[i][i];
            }

            for (int i = 0; i < I.length; i++) {
                result[i][n] = column[i];
            }
        }
        return result;
    }

    private static double[][] getIdentity(int size)
    {
        double[][] identity = new double[size][size];
        for (int i = 0; i < identity.length; i++) {
            identity[i][i] = 1;
        }
        return identity;
    }

    public static double[][] backSubstitutionIdentity(double[][] U) throws Exception
    {
        return backSubstitution(U, getIdentity(U.length));
    }

    public static double[][] forwardSubstitutionIdentity(double[][] L) throws Exception
    {
        return forwardSubstitution(L, getIdentity(L.length));
    }
}
