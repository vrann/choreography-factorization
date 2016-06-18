package com.vrann.Client;

import java.io.*;
import java.util.Random;

public class RandomMatrixGenerator {

    public static void main(String[] args) {
        try {
            int N = 10000;
            String matrixName = "matrix";
            if (args.length > 0) {
                System.out.println(args.length);
                System.out.println(args[0]);
                N = Integer.parseInt(args[0]);
            }
            if (args.length > 1) {
                matrixName = args[1];
            }
            generateRandomMatrix(matrixName, N, N);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateRandomMatrix(String matrixFileName, int numCols, int numRows) throws IOException
    {
        int[][] matrix = new int[numRows][numCols];
        Random rand = new Random();

        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(matrixFileName));
        outputWriter.write(numRows + " " + numCols);

        for (int i = 0; i < numRows; ++i) {
            outputWriter.write("\n");
            for (int j = 0; j < numCols; ++j) {
                matrix[i][j] = rand.nextInt(100);
                outputWriter.write(Integer.toString(matrix[i][j]));
                outputWriter.write(" ");
            }
        }

        outputWriter.flush();
        outputWriter.close();
    }
}
