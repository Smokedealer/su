import dbscan.DBScan;
import dbscan.DBScanConf;
import kmeans.KMeans;
import kmeans.KMeansConf;
import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;
import utils.DataGenerator;
import utils.GUIController;
import utils.GUIForm;
import utils.CsvDataProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    private static long timeStart;

    private GUIForm gui;

    public static void main(String[] args) {

        new App(); // controllable GUI
        //new App(null); // just for testing

    }

    /**
     * Just for testing! GUI won't work properly.
     */
    private App(Object debugmode) {
        Point[] data = deserializeDataFromFile("data.txt");

        if (data == null) {
            try {
                DataGenerator dg = new DataGenerator(2, 5000, 50);
                data = dg.generateClusteredData(40, 5).toArray(new Point[0]);
                //data = dg.generateData();

                serializeDataToFile(data, "data.txt");
            } catch (Exception e) {
                System.err.println("Invalid data for DataGenerator.");
            }
        }

        this.gui = new GUIForm();
        tryKMeans(data, new KMeansConf());

        this.gui = new GUIForm();
        tryDBScan(data, new DBScanConf());
    }


    private App() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            this.gui = new GUIForm();
            this.gui.setGUIController(new GUIController() {

                Cluster data = null;
                Cluster[] clusteredData = null;

                @Override
                public void generateUniformData(int dimensions, int points, int spread) {
                    try {
                        DataGenerator dg = new DataGenerator(dimensions, points, spread);
                        this.data = dg.generateData();
                        App.this.gui.drawData(new Cluster[]{this.data});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void generateClusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread) {
                    try {
                        DataGenerator dg = new DataGenerator(dimensions, points, spread);
                        this.data = dg.generateClusteredData(clustersSpread, clusters);
                        this.clusteredData = new Cluster[]{this.data};
                        App.this.gui.drawData(this.clusteredData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void loadFileData(String file) {
                    CsvDataProcessor csv = new CsvDataProcessor();

                    this.data = csv.readFile(file);

                    if(this.data == null || this.data.isEmpty()){
                        App.this.gui.showMessage("Soubor \"" + file + "\" se nepodařilo načíst.");
                    } else {
                        this.data.setShadowCluster(true);
                        this.clusteredData = new Cluster[]{this.data};
                        App.this.gui.drawData(this.clusteredData);
                    }
                }

                @Override
                public void doClustering(int method, ClusteringAlgConf conf) {
                    if (this.data == null) return;

                    ClusteringAlg alg;

                    switch (method) {
                        default:
                        case 0:
                            alg = new KMeans();
                            //alg = new em.EM();
                            break;
                        case 1:
                            alg = new DBScan();
                            break;
                    }

                    startTimer();
                    Cluster[] clusters = alg.doClustering(deepCopyOfPoints(this.data), conf);
                    stopTimer();
                    System.out.println();
                    System.out.println("Found " + clusters.length + " clusters with geometrical middles:");

                    for (Cluster cluster : clusters) {
                        if(cluster.isShadowCluster()) continue;
                        System.out.println(" - " + cluster.geometricalMiddle().toString());
                    }

                    this.clusteredData = clusters;
                    App.this.gui.drawData(this.clusteredData);
                }

                @Override
                public void exportPNG(File file, BufferedImage image) {
                    String path = file.getAbsolutePath();
                    if(!path.endsWith(".png")) path += ".png";

                    try {
                        ImageIO.write(image, "png", new File(path));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void exportCSV(File file) {
                    String path = file.getAbsolutePath();
                    if(!path.endsWith(".csv")) path += ".csv";

                    try {
                        CsvDataProcessor csv = new CsvDataProcessor();
                        csv.saveFile(this.clusteredData, new File(path));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

    }

    private void tryKMeans(Point[] data, ClusteringAlgConf conf) {
        startTimer();

        KMeans km = new KMeans();
        Cluster[] returned = km.doClustering(data, conf);

        stopTimer();

        System.out.println("Final Centroid locations");

        for (Cluster cluster : returned) {
            System.out.println(cluster.getCentroid().toString());
        }

        this.gui.drawData(returned);
    }


    private void tryDBScan(Point[] data, ClusteringAlgConf conf) {
        startTimer();

        DBScan dbscan = new DBScan();
        Cluster[] returned = dbscan.doClustering(data, conf);

        stopTimer();

        this.gui.drawData(returned);
    }

    private static void serializeDataToFile(Point[] data, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Point[] deserializeDataFromFile(String file) {
        Point[] points = null;

        FileInputStream fin;
        try {
            fin = new FileInputStream(file);
            ObjectInputStream objectinputstream = new ObjectInputStream(fin);
            points = (Point[]) objectinputstream.readObject();
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    @Deprecated
    private static void scanInput(ClusteringAlg clustering) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            String line = sc.nextLine();

            String[] values = line.split(" ");
            double[] dValues = new double[values.length];


            for (int i = 0; i < values.length; i++) {
                dValues[i] = Double.valueOf(values[i]);
            }

            Point newPoint = new Point(dValues);

            clustering.assignToCluster(newPoint);
        }
    }

    private static void startTimer() {
        timeStart = System.currentTimeMillis();
    }

    private static void stopTimer() {
        System.out.println("=== Algorithm took " + (System.currentTimeMillis() - timeStart) + " ms to complete. ===");
    }

    private static Point[] deepCopyOfPoints(Cluster cluster) {
        Point[] output = new Point[cluster.size()];
        int i = 0;

        for (Point point : cluster) {
            output[i++] = new Point(point);
        }

        return output;
    }

}
