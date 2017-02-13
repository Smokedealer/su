package dbscan;
import structures.ClusteringAlgConf;

/**
 * Configuration-"property bag" for DBScan algorithm.
 */
public class DBScanConf implements ClusteringAlgConf {
    private double maxDistance = 3d;
    private int minPoints = 20;

    /**
     * Gets maximal distance parameter.
     */
    public double getMaxDistance() {
        return this.maxDistance;
    }

    /**
     * Sets maximal distance parameter.
     */
    public void setMaxDistance(double maxDistance) {
        if(maxDistance < 0) return;
        this.maxDistance = maxDistance;
    }

    /**
     * Gets minimal points parameter (there must have at least x close points for creating a cluster).
     */
    public int getMinPoints() {
        return this.minPoints;
    }


    /**
     * Sets minimal points parameter (there must have at least x close points for creating a cluster).
     */
    public void setMinPoints(int minPoints) {
        if(minPoints <= 0) return;
        this.minPoints = minPoints;
    }

}
