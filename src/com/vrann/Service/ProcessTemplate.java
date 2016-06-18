package com.vrann.Service;

/**
 * Created by etulika on 6/4/16.
 */
public class ProcessTemplate {

    private DataReader dataReader;

    public ProcessTemplate (DataReader dataReader) {
        this.dataReader = dataReader;
    }

    public void getMatrixBlock(String processRole) throws Exception
    {
        //double[][] matrix = DataReader.getMatrix(dataReader.matrixFileName);
        //int N = matrix.length;
        /*int processesRowNum = 4;
        int blockSize = (int)Math.floor(N/processesRowNum);
        System.out.println(blockSize);

        double[][] block = new double[blockSize][blockSize];

        switch (processRole) {
            case "p11":
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        //block[i][j] = matrix[i][j];
                    }
                }
                break;
            case "p12":
                break;
            case "p13":
                break;
            case "p21":
                break;
            case "p22":
                break;
            case "p23":
                break;
            case "p31":
                break;
            case "p32":
                break;
            case "p33":
                break;
            default:
                throw new Exception("Unidentified process role");
        }
        return block;*/
    }
}
