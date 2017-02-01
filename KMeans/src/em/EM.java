package em;

import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public class EM implements ClusteringAlg {

    @Override
    public Cluster[] doClustering(Point[] data, ClusteringAlgConf conf) {

        int clusterCount = 3;

        Cluster MainCluster = new Cluster(data);

        int pointdimensions = data[0].getDimension();
        int datalength = data.length;
        Point[] Means = new Point[clusterCount];
        double[][] PxBelongsToC = new double[datalength][clusterCount];
        List<Cluster> ClusterVector;
        //MainCluster.SSE = CONST.calSSE(MainCluster, CONST.EUCLIDEAN);
        double Deviation = Math.sqrt(MainCluster.sse() / data.length);

        Random random = new SecureRandom();

        for (int i = 0; i < clusterCount; i++) {
            Means[i] = new Point(data[random.nextInt(datalength)]);
        }


        for (int iter = 1; iter > 0; iter--) {
            // Expectation Step
            for (int k = 0; k < datalength; k++) {
                double SumOfPxBelongsToC = 0;
                for (int i = 0; i < clusterCount; i++) {
                    SumOfPxBelongsToC += data[k].normalDistribution(Means[i], Deviation);
                }

                for (int i = 0; i < clusterCount; i++) {
                    PxBelongsToC[k][i] = data[k].normalDistribution(Means[i], Deviation) / SumOfPxBelongsToC;
                }
            }

            // Maximization Step
            for (int i = 0; i < clusterCount; i++) {
                double[] SumOfMeanPx = new double[pointdimensions];
                double SumOfPx = 0;

                for (int k = 0; k < datalength; k++) {
                    for (int d = 0; d < pointdimensions; d++) {
                        SumOfMeanPx[d] += PxBelongsToC[k][i] * data[k].getSpecificCoord(d);
                    }

                    SumOfPx += PxBelongsToC[k][i];
                }

                for (int d = 0; d < pointdimensions; d++) {
                    Means[i].setSpecificCoord(d, SumOfMeanPx[d] / SumOfPx);
                }
            }
        }

        // create clusters
        ClusterVector = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            ClusterVector.add(new Cluster());
        }

        for (int k = 0; k < datalength; k++) {
            int pos = 0;
            double Min = Double.MAX_VALUE;
            double Max = 0;
            for (int i = 0; i < clusterCount; i++) {
                if (Min > Means[i].euclideanDistanceTo(data[k])) {
                    Min = Means[i].euclideanDistanceTo(data[k]);
                    //pos = i;
                }
                if (Max < PxBelongsToC[k][i]) {
                    Max = PxBelongsToC[k][i];
                    pos = i;
                }
            }
            ClusterVector.get(pos).add(data[k]);
        }


        return ClusterVector.toArray(new Cluster[0]);
    }

    @Override
    public Cluster assignToCluster(Point point) {
        return null;
    }

}