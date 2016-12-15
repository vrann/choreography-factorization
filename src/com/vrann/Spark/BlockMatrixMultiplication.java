package com.vrann.Spark;

//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.ml.linalg.Matrices;
//import org.apache.spark.ml.linalg.Matrix;
//import org.apache.spark.mllib.linalg.distributed.BlockMatrix;
//import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
//import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
//import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import com.vrann.Service.DataReader;

public class BlockMatrixMultiplication {

//    public void multiply()throws Exception {
//        double[][] matrix  = DataReader.getMatrix("/Users/etulika/Projects/java/choreography/experiments/matrix/A/A-0-0");
//        //RDD rdd = new JavaRDD<>()
//        //matrix.
//        JavaRDD<MatrixEntry> entries = new JavaRDD<MatrixEntry>(matrix);
//        for (int i = 0; i < matrix.length; i++) {
//            for (int j = 0; j < matrix[i].length; j++) {
//                MatrixEntry entry = new MatrixEntry(i, j, matrix[i][j]);
//                entries.wrapRDD(entry);
//            }
//        }
//        BlockMatrix
//        JavaRDD<MatrixEntry> entries =// a JavaRDD of matrix entries
//        // Create a CoordinateMatrix from a JavaRDD<MatrixEntry>.
//        CoordinateMatrix mat = new CoordinateMatrix(entries.rdd());
//
//        // Get its size.
//        long m = mat.numRows();
//        long n = mat.numCols();
//
//        // Convert it to an IndexRowMatrix whose rows are sparse vectors.
//        IndexedRowMatrix indexedRowMatrix = mat.toIndexedRowMatrix();
//    }


}
