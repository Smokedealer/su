package utils;
import structures.ClusteringAlgConf;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Controller for GUI.
 */
public interface GUIController {

    /**
     * Generates uniform data.
     * @param dimensions Dimension of points (1D, 2D, 3D, 4D, ...).
     * @param points Count of points to be generated.
     * @param spread Spread of points.
     */
    void generateUniformData(int dimensions, int points, int spread);

    /**
     * Generates clustered data.
     * @param dimensions Dimension of points (1D, 2D, 3D, 4D, ...).
     * @param points Count of points to be generated.
     * @param spread Spread of points.
     * @param clusters Count of clusters.
     * @param clustersSpread Spread of clusters.
     */
    void generateClusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread);

    /**
     * Loads data from CSV file.
     * @param path Path to file.
     */
    void loadFileData(String path);

    /**
     * Process clustering.
     * @param method Desired method.
     * @param conf Configuration of method.
     */
    void doClustering(int method, ClusteringAlgConf conf);

    /**
     * Exports image into PNG file.
     * @param file Output file.
     * @param image Image.
     */
    void exportPNG(File file, BufferedImage image);

    /**
     * Eports data into CSV file.
     * @param file Output file.
     */
    void exportCSV(File file);

}
