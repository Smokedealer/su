import dbscan.DBScan;
import kmeans.KMeans;
import kmeans.KMedians;
import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;
import utils.CsvDataProcessor;
import utils.DataGenerator;
import utils.GUIController;
import utils.GUIForm;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * App for demonstation of clustering algorithms.
 *
 * @author Matěj Kareš, Vojtěch Kinkor
 */
public class App {

    private static long timeStart;
    private GUIForm gui;

    /**
     * Main entry.
     */
    public static void main(String[] args) {
        new App(); // controllable GUI
    }

    /**
     * App for demonstation of clustering algorithms.
     * Opens GUI.
     */
    private App() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(this::setupGUI);
    }


    /**
     * Shows GUI of application.
     */
    private void setupGUI() {
        this.gui = new GUIForm();
        this.gui.setGUIController(new GUIController() {

            Cluster data = null;
            Cluster[] clusteredData = null;

            /**
             * Generates uniform data.
             * @param dimensions Dimension of points (1D, 2D, 3D, 4D, ...).
             * @param points Count of points to be generated.
             * @param spread Spread of points.
             */
            @Override
            public void generateUniformData(int dimensions, int points, int spread) {
                try {
                    DataGenerator dg = new DataGenerator(dimensions, points, spread);
                    this.data = dg.generateData();
                    App.this.gui.drawData(new Cluster[]{this.data});
                } catch (Exception e) {
                    e.printStackTrace();
                    App.this.gui.showMessage("Při generování dat nastala chyba.");
                }
            }

            /**
             * Generates clustered data.
             * @param dimensions Dimension of points (1D, 2D, 3D, 4D, ...).
             * @param points Count of points to be generated.
             * @param spread Spread of points.
             * @param clusters Count of clusters.
             * @param clustersSpread Spread of clusters.
             */
            @Override
            public void generateClusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread) {
                try {
                    DataGenerator dg = new DataGenerator(dimensions, points, spread);
                    this.data = dg.generateClusteredData(clustersSpread, clusters);
                    this.clusteredData = new Cluster[]{this.data};
                    App.this.gui.drawData(this.clusteredData);
                } catch (Exception e) {
                    e.printStackTrace();
                    App.this.gui.showMessage("Při generování dat nastala chyba.");
                }
            }

            /**
             * Loads data from CSV file.
             * @param path Path to file.
             */
            @Override
            public void loadFileData(String path) {
                CsvDataProcessor csv = new CsvDataProcessor();

                this.data = csv.readFile(new File(path));

                if (this.data == null || this.data.isEmpty()) {
                    App.this.gui.showMessage("Soubor \"" + path + "\" se nepodařilo načíst.");
                } else {
                    this.data.setShadowCluster(true);
                    this.clusteredData = new Cluster[]{this.data};
                    App.this.gui.drawData(this.clusteredData);
                }
            }

            /**
             * Process clustering.
             * @param method Desired method.
             * @param conf Configuration of method.
             */
            @Override
            public void doClustering(int method, ClusteringAlgConf conf) {
                if (this.data == null) return;

                ClusteringAlg alg;

                switch (method) {
                    default:
                    case 0:
                        alg = new KMeans();
                        break;
                    case 1:
                        alg = new DBScan();
                        break;
                    case 2:
                        alg = new KMedians();
                        break;
                }

                startTimer();
                Cluster[] clusters = alg.doClustering(deepCopyOfPoints(this.data), conf);
                stopTimer();
                System.out.println();

                // count clusters excl. shadows
                long foundClusters = Arrays.stream(clusters).filter(f -> !f.isShadowCluster()).count();
                System.out.println("Found " + foundClusters + " clusters with geometrical middles:");

                for (Cluster cluster : clusters) {
                    if (cluster.isShadowCluster()) continue;
                    System.out.println(" - " + cluster.geometricalMiddle().toString());
                }

                this.clusteredData = clusters;
                App.this.gui.drawData(this.clusteredData);
            }

            /**
             * Exports image into PNG file.
             * @param file Output file.
             * @param image Image.
             */
            @Override
            public void exportPNG(File file, BufferedImage image) {
                String path = file.getAbsolutePath();
                if (!path.endsWith(".png")) path += ".png";

                try {
                    ImageIO.write(image, "png", new File(path));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    App.this.gui.showMessage("Při exportu nastala chyba.");
                }
            }

            /**
             * Eports data into CSV file.
             * @param file Output file.
             */
            @Override
            public void exportCSV(File file) {
                String path = file.getAbsolutePath();
                if (!path.endsWith(".csv")) path += ".csv";

                try {
                    CsvDataProcessor csv = new CsvDataProcessor();
                    csv.saveFile(this.clusteredData, new File(path));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    App.this.gui.showMessage("Při exportu nastala chyba.");
                }
            }
        });
    }



    /**
     * Stars timer (stopwatch).
     */
    private static void startTimer() {
        timeStart = System.currentTimeMillis();
    }

    /**
     * Stops timer (stopwatch) and prints time.
     */
    private static void stopTimer() {
        System.out.println("=== Algorithm took " + (System.currentTimeMillis() - timeStart) + " ms to complete. ===");
    }

    /**
     * Makes a deep copy of points in cluster.
     * @param cluster Input cluster.
     * @return Array of points.
     */
    private static Point[] deepCopyOfPoints(Cluster cluster) {
        Point[] output = new Point[cluster.size()];
        int i = 0;

        for (Point point : cluster) {
            output[i++] = new Point(point);
        }

        return output;
    }

}
