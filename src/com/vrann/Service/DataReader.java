package com.vrann.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by etulika on 6/4/16.
 */
public class DataReader {

    public static boolean exists(String matrixFileName)
    {
        File f = new File(matrixFileName);
        if (!f.exists() || f.isDirectory()) {
            return false;
        }
        return true;
    }

    public static double[][] getMatrix(String matrixFileName) throws Exception
    {
        if (!exists(matrixFileName)) {
            throw new Exception("Matrix does not exists");
        }
        BufferedReader br = new BufferedReader(new FileReader(matrixFileName));
        String line;
        line = br.readLine();
        String[] dimensions = line.split("\\s+");
        double[][] matrix = new double[Integer.parseInt(dimensions[0])][Integer.parseInt(dimensions[1])];
        boolean isUMatrix = false;
        if (dimensions.length > 2 && dimensions[2] == "U") {
            isUMatrix = true;
        }

        int i = 0;
        while ((line = br.readLine()) != null) {
            int j = 0;
            if (isUMatrix) {
                j = i;
            }
            line = line.trim();

            String[] row = line.split("\\s+");
            for (String item: row) {
                matrix[i][j] = Double.parseDouble(item);
                j++;
            }
            i++;
        }
        return matrix;
    }
}
