package com.vrann.Matrix;

import com.vrann.Choreography.SetupConfig;
//import no.uib.cipr.matrix.DenseMatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by etulika on 6/12/16.
 */
public class DataWriter {

    private String matrixFileName;

    public static void writeMatrix(String matrixFileName, double[][] matrix, MatrixType type) throws Exception {
        String dirName = SetupConfig.get().getLocalDataDir();
        DataWriter writer = new DataWriter(matrixFileName, dirName);
        switch (type) {
            case A:
                writer.write(matrix);
                break;
            case L:
                writer.writeL(matrix);
                break;
            case U:
                writer.writeU(matrix);
                break;
        }
    }

    public DataWriter(String matrixFileName, String basePath) {
        this.matrixFileName = basePath + matrixFileName;
    }

    public void write(double[][] matrix) throws Exception {
        if (matrix.length == 0) {
            throw new Exception("Matrix is empty");
        }
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(matrixFileName), false));
        outputWriter.write(numRows + " " + numCols + "\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                outputWriter.write(Double.toString(matrix[i][j]));
                outputWriter.write(" ");
            }
            outputWriter.write("\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public void writeL(double[][] matrix) throws Exception {
        if (matrix.length == 0) {
            throw new Exception("Matrix is empty");
        }
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(matrixFileName), false));
        outputWriter.write(numRows + " " + numCols + "\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j <= i; j++) {
                outputWriter.write(Double.toString(matrix[i][j]));
                outputWriter.write(" ");
            }
            outputWriter.write("\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public void writeU(double[][] matrix) throws Exception {
        if (matrix.length == 0) {
            throw new Exception("Matrix is empty");
        }
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(matrixFileName), false));
        outputWriter.write(numRows + " " + numCols + " U\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix[i].length; j++) {
                outputWriter.write(Double.toString(matrix[i][j]));
                outputWriter.write(" ");
            }
            outputWriter.write("\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

//    public void write(DenseMatrix matrix) throws Exception {
//        int numRows = matrix.numRows();
//        int numCols = matrix.numColumns();
//        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(matrixFileName), false));
//        outputWriter.write(numRows + " " + numCols + "\n");
//        for (int i = 0; i < numRows; i++) {
//            for (int j = 0; j < numCols; j++) {
//                double value = matrix.get(i, j);
//                outputWriter.write(Double.toString(value));
//                outputWriter.write(" ");
//            }
//            outputWriter.write("\n");
//        }
//        outputWriter.flush();
//        outputWriter.close();
//    }
}
