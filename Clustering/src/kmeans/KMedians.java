package kmeans;
import structures.Cluster;
import structures.Point;

/**
 * KMedians clustering algorithm (derived from KMeans).
 *
 * @author Vojtěch Kinkor
 */
public class KMedians extends KMeans {

    /**
     * Computes new centroid for cluster.
     * @param cluster Cluster.
     * @return Point-centroid.
     */
    @Override
    protected Point getNewCentroid(Cluster cluster) {
        return cluster.geometricMedian();
    }

    /**
     * Computes distance between two points using manhattan distance formula.
     * @param a First point.
     * @param b Second point.
     * @return Distance.
     */
    @Override
    public double getDistance(Point a, Point b){
        return a.manhattanDistanceTo(b);
    }

}
