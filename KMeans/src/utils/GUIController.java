package utils;
import structures.ClusteringAlgConf;


public interface GUIController {

    void generateUniformData(int dimensions, int points, int spread);
    void generateClusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread);
    void loadFileData(String file);

    void doClustering(int method, ClusteringAlgConf conf);

}
