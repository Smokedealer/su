package structures;

/**
 * Created by Matěj Kareš on 01.12.2016.
 */
public interface ClusteringAlg {

    Cluster[] doClustering(Point[] data, ClusteringAlgConf conf);

    // TODO: delete? Přiřazení jednoho bodu ke clusteru nejspíš nemá nic společného se samotnou metodou, jakou se clustery vytvořily, ne?
    Cluster assignToCluster(Point point);

}
