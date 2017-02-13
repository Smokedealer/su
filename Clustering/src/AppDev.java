import dbscan.DBScan;
import dbscan.DBScanConf;
import kmeans.KMeans;
import kmeans.KMeansConf;
import structures.Cluster;
import structures.ClusteringAlgConf;
import structures.Point;
import utils.DataGenerator;
import utils.GUIForm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Matěj Kareš, Vojtěch Kinkor
 */
public class AppDev {

    private static long timeStart;

    private GUIForm gui;

    public static void main(String[] args) {
        new AppDev(); // dev mode
    }

    /**
     * Just for testing!
     */
    private AppDev() {
        Point[] data = deserializeDataFromFile("data.txt");

        if (data == null) {
            try {
                DataGenerator dg = new DataGenerator(2, 1000, 5);
                data = dg.generateClusteredData(50, 3).toArray(new Point[0]);
                //data = dg.generateData();

                serializeDataToFile(data, "data.txt");
            } catch (Exception e) {
                System.err.println("Invalid data for DataGenerator.");
            }
        }

        this.gui = new GUIForm();
        this.gui.setControlPanelVisible(false);
        this.tryKMeans(data, new KMeansConf());

        this.gui = new GUIForm();
        this.gui.setControlPanelVisible(false);
        this.tryDBScan(data, new DBScanConf());
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
        Point[] points;

        FileInputStream fin;
        try {
            fin = new FileInputStream(file);
            ObjectInputStream objectinputstream = new ObjectInputStream(fin);
            points = (Point[]) objectinputstream.readObject();
            fin.close();
        } catch (Exception e) {
            points = null;
            //e.printStackTrace();
        }

        return points;
    }


    private static void startTimer() {
        timeStart = System.currentTimeMillis();
    }

    private static void stopTimer() {
        System.out.println("=== Algorithm took " + (System.currentTimeMillis() - timeStart) + " ms to complete. ===");
    }


}
