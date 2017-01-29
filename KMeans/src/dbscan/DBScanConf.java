package dbscan;
import structures.ClusteringAlgConf;


public class DBScanConf implements ClusteringAlgConf {
    private double maxDistance = 3d;
    private int minPoints = 20;

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        if(maxDistance <= 0) return;
        this.maxDistance = maxDistance;
    }

    public int getMinPoints() {
        return this.minPoints;
    }

    public void setMinPoints(int minPoints) {
        if(minPoints <= 0) return;
        this.minPoints = minPoints;
    }
}
