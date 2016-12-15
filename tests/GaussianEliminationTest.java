//import com.vrann.Math.GaussianElimination;
//import junit.framework.TestCase;
//
///**
// * Created by etulika on 5/7/16.
// */
//public class GaussianEliminationTest extends TestCase {
//
//    public void axtestToUpperTriangular() throws Exception {
//
//        double[][] testA = new double[][] {
//            new double[] {
//                1, 3, 1, 9
//            }, new double[] {
//                1, 1, -1, 1
//            }, new double[] {
//                3, 11, 5, 35
//            }
//        };
//
//        GaussianElimination elimination = new GaussianElimination(
//                testA
//        );
//
//        double[][] resultMatrix = elimination.toUpperTriangular();
//        System.out.println(resultMatrix.length);
//        for (int i = 1; i < resultMatrix.length; i++) {
//            for (int j = 0; j < i; j++) {
//                assertEquals(0.0, resultMatrix[i][j]);
//            }
//        }
//    }
//
//    public void testSolve() throws Exception {
//        double[][] testA = new double[][] {
//        new double[] {
//                1, 2, 1, -1,5
//            }, new double[] {
//                3, 2, 4, 4, 16
//            }, new double[] {
//                4, 4, 3, 4, 22
//            }, new double[] {
//                2, 0, 1, 5, 15
//            }
//        };
//        GaussianElimination elimination = new GaussianElimination(
//                testA
//        );
//        double[] result = elimination.solve();
//        for (double element: result) {
//            System.out.print(element + " ");
//            System.out.println("");
//        }
//        double[] expected = new double[] {16, -6, -2, -3};
//        assertNotSame(expected, result);
//    }
//
//    public void atestSolve() throws Exception {
//        double[][] testA = new double[][] {
//            new double[] {
//                1, 2,
//            }, new double[] {
//                3, 2,
//            }, new double[] {
//                4, 4,
//            }, new double[] {
//                2, 0,
//            }
//        };
//        GaussianElimination elimination = new GaussianElimination(
//                testA
//        );
//        double[] result = elimination.solve();
//    }
//}