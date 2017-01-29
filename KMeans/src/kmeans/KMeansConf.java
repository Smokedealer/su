package kmeans;
import structures.ClusteringAlgConf;


public class KMeansConf implements ClusteringAlgConf {

    private int clusterCount = 0;
    private int algRepeats = 20;
    private double elbowTriggerValue;

    public int getClusterCount() {
        return this.clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        if(algRepeats < 0) return;
        this.clusterCount = clusterCount;
    }

    public int getAlgRepeats() {
        return this.algRepeats;
    }

    public void setAlgRepeats(int algRepeats) {
        if(algRepeats <= 0) return;
        this.algRepeats = algRepeats;
    }

    public void setElbowTriggerValue(double elbowTriggerValue) {
        if(elbowTriggerValue <= 0) return;
        this.elbowTriggerValue = elbowTriggerValue;
    }

    public double getElbowTriggerValue() {
        return this.elbowTriggerValue;
    }
}
