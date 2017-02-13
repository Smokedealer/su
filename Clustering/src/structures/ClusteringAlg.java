package structures;


/**
 * Clustering algorithm interface.
 */
public interface ClusteringAlg {

    /**
     * Runs clustering algorithm.
     * @param data Input "unsorted" data.
     * @param conf Algorithm configuration.
     * @return Data sorted into clusters.
     */
    Cluster[] doClustering(Point[] data, ClusteringAlgConf conf);

    // TODO: delete? Přiřazení jednoho bodu ke clusteru nejspíš nemá nic společného se samotnou metodou, jakou se clustery vytvořily, ne?
    /**
     * Finds cluster to which would be point assigned.
     * @param point Point.
     * @return Cluster.
     */
    Cluster assignToCluster(Point point);

}
