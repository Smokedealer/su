package structures;

import structures.Cluster;
import structures.Point;

/**
 * Created by Matěj Kareš on 01.12.2016.
 */
public interface ICluster {

    Cluster[] doClustering(Point[] data, int clusterCount, int nCount);

    Cluster assignToCluster(Point point);
}
