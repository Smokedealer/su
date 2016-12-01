package kmeans;

/**
 * Created by Matěj Kareš on 01.12.2016.
 */
public interface ICluster {

    void doClustering(Point[] data, int clusterCount);

}
