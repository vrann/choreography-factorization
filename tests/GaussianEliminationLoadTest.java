import com.vrann.Client.RandomMatrixGenerator;
import com.vrann.Math.GaussianElimination;
import com.vrann.Service.DataReader;
import junit.framework.TestCase;

/**
 * Created by etulika on 5/10/16.
 */
public class GaussianEliminationLoadTest extends TestCase {

    public void testSolveLoad() throws Exception {
        RandomMatrixGenerator.generateRandomMatrix("matrix", 16, 16);

        double matrix[][] = DataReader.getMatrix("matrix");
        GaussianElimination elimination = new GaussianElimination(
                matrix
        );
        double[] result = elimination.solve();
        for (double element: result) {
            System.out.print(element + " ");
        }
    }
}
