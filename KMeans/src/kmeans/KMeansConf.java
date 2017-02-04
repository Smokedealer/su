package kmeans;
import structures.ClusteringAlgConf;

/**
 * Configuration-"property bag" for KMeans algorithm (and derived variations).
 */
public class KMeansConf implements ClusteringAlgConf {

    private int clusterCount = 0;
    private int algRepeats = 20;
    private double elbowDroprateThreshold = 0.8d;
    private double elbowDifferenceAbsThresholdValue = 10d;

    /**
     * Gets cluster count parameter ("how many clusters we want").
     */
    public int getClusterCount() {
        return this.clusterCount;
    }

    /**
     * Sets cluster count parameter ("how many clusters we want").
     */
    public void setClusterCount(int clusterCount) {
        if (clusterCount < 0) return;
        this.clusterCount = clusterCount;
    }

    /**
     * Gets algorithm repeats parameter ("how many times repeat it to find best clustering").
     */
    public int getAlgRepeats() {
        return this.algRepeats;
    }

    /**
     * Sets algorithm repeats parameter ("how many times repeat it to find best clustering").
     */
    public void setAlgRepeats(int algRepeats) {
        if (algRepeats <= 0) return;
        this.algRepeats = algRepeats;
    }

    /**
     * Gets elbow droprate threshold parameter (stop clustering when the droprate is below).
     */
    public void setElbowDroprateThreshold(double elbowDroprateThreshold) {
        if (elbowDroprateThreshold <= 0) return;
        this.elbowDroprateThreshold = elbowDroprateThreshold;
    }
    /**
     * Sets elbow droprate threshold parameter (stop clustering when the droprate is below).
     */
    public double getElbowDroprateThresholdValue() {
        return this.elbowDroprateThreshold;
    }

    /**
     * Gets elbow difference threshold parameter (stop clustering when the difference of last two iterations is lower).
     */
    public double getElbowDifferenceAbsThresholdValue() {
        return this.elbowDifferenceAbsThresholdValue;
    }

    /**
     * Sets elbow difference threshold parameter (stop clustering when the difference of last two iterations is lower).
     */
    public void setElbowDifferenceAbsThresholdValue(double elbowDifferenceAbsThresholdValue) {
        this.elbowDifferenceAbsThresholdValue = elbowDifferenceAbsThresholdValue;
    }

}
