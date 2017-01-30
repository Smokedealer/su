package kmeans;
import structures.ClusteringAlgConf;


public class KMeansConf implements ClusteringAlgConf {

    private int clusterCount = 0;
    private int algRepeats = 20;
    private double elbowDroprateThreshold = 0.15d;
    private double elbowDifferenceAbsThresholdValue = 0d;

    public int getClusterCount() {
        return this.clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        if (clusterCount < 0) return;
        this.clusterCount = clusterCount;
    }

    public int getAlgRepeats() {
        return this.algRepeats;
    }

    public void setAlgRepeats(int algRepeats) {
        if (algRepeats <= 0) return;
        this.algRepeats = algRepeats;
    }

    public void setElbowDroprateThreshold(double elbowDroprateThreshold) {
        if (elbowDroprateThreshold <= 0) return;
        this.elbowDroprateThreshold = elbowDroprateThreshold;
    }

    public double getElbowDroprateThresholdValue() {
        return this.elbowDroprateThreshold;
    }

    public double getElbowDifferenceAbsThresholdValue() {
        return this.elbowDifferenceAbsThresholdValue;
    }

    public void setElbowDifferenceAbsThresholdValue(double elbowDifferenceAbsThresholdValue) {
        this.elbowDifferenceAbsThresholdValue = elbowDifferenceAbsThresholdValue;
    }

}
