package utils;
import structures.ClusteringAlgConf;

import java.awt.image.BufferedImage;
import java.io.File;


public interface GUIController {

    void generateUniformData(int dimensions, int points, int spread);
    void generateClusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread);
    void loadFileData(String file);

    void doClustering(int method, ClusteringAlgConf conf);

    void exportPNG(File file, BufferedImage image);

    void exportCSV(File file);
}
