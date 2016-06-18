package com.vrann;

import com.vrann.Client.RandomMatrixGenerator;
import com.vrann.Math.LU;
import com.vrann.Matrix.Multiplicator;
import com.vrann.Matrix.Printer;
import com.vrann.Service.DataReader;
import org.junit.Assert;

/**
 * Created by etulika on 6/3/16.
 */
public class LUTest {

    private double[][] testA;

    public void testGetInverse() throws Exception {
        double[][] testA = new double[][] {
                new double[] {3, 2},
                new double[] {2, 1}
        };
        LU luFactorization = new LU(testA, 2);
        double[][] result = Multiplicator.multiply(testA, luFactorization.getInverse());
        double[][] expected = new double[][] {
                new double[] {1, 0},
                new double[] {0, 1}
        };
        Assert.assertArrayEquals(expected, result);
    }

    public void testGetInverse3x3() throws Exception {
        double[][] testB = new double[][] {
                new double[] {11, 3, 2},
                new double[] {4, 12, 5},
                new double[] {10, 6, 7}
        };

        LU luFactorization = new LU(testB, 3);
        double[][] inverse = luFactorization.getInverse();
        System.out.println(Printer.print(inverse));
        double[][] result = Multiplicator.multiply(testB, inverse);
        double[][] expected = new double[][] {
                new double[] {1, 0, 0},
                new double[] {0, 1, 0},
                new double[] {0, 0, 1}
        };
        System.out.println(Printer.print(result));
        Assert.assertArrayEquals(expected, result);
    }

    public void getLU() throws Exception
    {
        double[][] testB = new double[][] {
                new double[] {6, 0, 2},
                new double[] {24, 1, 8},
                new double[] {-12, 1, -3}
        };
        LU luFactorization = new LU(testB, 4);
        System.out.println(Printer.print(Multiplicator.multiply(luFactorization.getL(), luFactorization.getU())));
    }

    public void testGetInverse4x4() throws Exception {
        double[][] testB = new double[][] {
                new double[] {5, 6, 13, 7},
                new double[] {3, 8, 9, 4},
                new double[] {11, 10, 12, 14},
                new double[] {15, 16, 17, 58}
        };

        LU luFactorization = new LU(testB, 4);
        double[][] inverse = luFactorization.getInverse();
        System.out.println(Printer.print(inverse));
        double[][] result = Multiplicator.multiply(testB, inverse);
        double[][] expected = new double[][] {
                new double[] {1, 0, 0, 0},
                new double[] {0, 1, 0, 0},
                new double[] {0, 0, 1, 0},
                new double[] {0, 0, 0, 1}
        };
        System.out.println(Printer.print(result));
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                Assert.assertTrue(Math.abs(expected[i][j] - result[i][j]) <= 1.0E-10D);
            }
        }
    }

    public void testAmountReadWriteOps()
    {
        int sum = 0;
        for (int K = 60; K > 0; K--) {
            sum += (4*K*K) - (2*K) +3;
            System.out.println(K);
        }
        System.out.println(sum);
    }

    @org.junit.Test
    public void testMultiplyExxective() throws Exception
    {
        double[][] testA = new double[][] {
                new double[] {5, 6, 1, 7},
                new double[] {3, 8, 9, 4},
                new double[] {11, 10, 12, 14},
                new double[] {15, 16, 17, 58}
        };

        double[][] testB = new double[][] {
                new double[] {5, 6, 1, 7},
                new double[] {3, 8, 9, 4},
                new double[] {2, 10, 12, 14},
                new double[] {3, 16, 17, 58}
        };
        Multiplicator.multiplyEffective(testA, testB);

        double[][] testAA = new double[][] {
                new double[] {5, 6, 13, 7, 2, 8},
                new double[] {3, 8, 9, 4, 1, 4},
                new double[] {11, 10, 12, 14, 2, 3},
                new double[] {15, 16, 17, 58, 7, 5},
                new double[] {2, 15, 7, 9, 1, 12},
                new double[] {4, 2, 3, 9, 1, 13}
        };

        LU luFactorization = new LU(testAA, testAA.length);
        Multiplicator.multiplyEffective(luFactorization.getL(), luFactorization.getU());

        //System.out.println(Printer.print(Multiplicator.multiply(testA, testB)));
    }


    public void testBlocked() throws Exception
    {
        double[][] testB = new double[][] {
                new double[] {5, 6, 13, 7, 2, 8},
                new double[] {3, 8, 9, 4, 1, 4},
                new double[] {11, 10, 12, 14, 2, 3},
                new double[] {15, 16, 17, 58, 7, 5},
                new double[] {2, 15, 7, 9, 1, 12},
                new double[] {4, 2, 3, 9, 1, 13}
        };

        LU luFactorization = new LU(testB, 2);
        System.out.println(Printer.print(Multiplicator.multiply(luFactorization.getL(), luFactorization.getU())));
    }


    public void setTestBlockedLoad() throws Exception
    {
        //RandomMatrixGenerator rmg = new RandomMatrixGenerator("test.matrix", 1000, 1000);
        //rmg.generateRandomMatrix();
        double matrix[][] = DataReader.getMatrix("test.matrix");
        LU luFactorization = new LU(matrix, 2);
        System.out.println(Printer.print(Multiplicator.multiply(luFactorization.getL(), luFactorization.getU())));
    }

    public void setTestMultiply() throws Exception
    {
        RandomMatrixGenerator.generateRandomMatrix("test.matrix.1M", 10000, 10000);
        //DataReader dr = new DataReader("test.matrix");
        //double matrix[][] = dr.getMatrix();
        //LU luFactorization = new LU(matrix, 2);
        //System.out.println(Printer.print(Multiplicator.multiply(matrix, matrix)));
    }


    public void testGetInverseBigger() throws Exception {
        double[][] testB = new double[][] {
                new double[] {1, 2, 1, -1},
                new double[] {3, 2, 4, 4},
                new double[] {4, 4, 3, 4},
                new double[] {2, 0, 1, 5}
        };

        double x = 1*(-5.25) + 2*3.125 + 0.5 + (-1) * 2;

        System.out.println(x);
        LU luFactorization = new LU(testB, 4);
        double[][] inverse = luFactorization.getInverse();
        System.out.println(Printer.print(inverse));
        double[][] result = Multiplicator.multiply(testB, inverse);
        double[][] expected = new double[][] {
                new double[] {1, 0, 0, 0},
                new double[] {0, 1, 0, 0},
                new double[] {0, 0, 1, 0},
                new double[] {0, 0, 0, 1}
        };
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                Assert.assertTrue(Math.abs(expected[i][j] - result[i][j]) <= 1.0E-10D);
            }
        }
        System.out.println(Printer.print(result));
        //Assert.assertArrayEquals(expected, result);
    }

    public void testGetL() throws Exception {
        double[][] testB = new double[][] {
                new double[] {1, 2, 1, -1},
                new double[] {3, 2, 4, 4},
                new double[] {4, 4, 3, 4},
                new double[] {2, 0, 1, 5}
        };
        LU luFactorization = new LU(testB, 2);
        double[][] L = luFactorization.getL();
    }


    public void testGetU() throws Exception {
        double[][] testB = new double[][] {
                new double[] {1, 2, 1, -1},
                new double[] {3, 2, 4, 4},
                new double[] {4, 4, 3, 4},
                new double[] {2, 0, 1, 5}
        };
        LU luFactorization = new LU(testB, 4);
        double[][] U = luFactorization.getU();
    }
}