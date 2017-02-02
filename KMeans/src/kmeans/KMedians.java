package kmeans;
import structures.Cluster;
import structures.Point;


public class KMedians extends KMeans {

    @Override
    protected Point getNewCentroid(Cluster cluster) {
        return cluster.geometricMedian();
    }

    @Override
    public double getDistance(Point a, Point b){
        return a.manhattanDistanceTo(b);
    }

}
