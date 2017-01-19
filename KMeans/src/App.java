import dbscan.DBScan;
import kmeans.KMeans;
import structures.Cluster;
import structures.ICluster;
import structures.Point;
import utils.DataGenerator;
import utils.GUI;

import java.util.Scanner;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    private static long timeStart;
    private static long timeEnd;

    public static void main(String[] args) {

        Point[] data = null;

        try {
            DataGenerator dg = new DataGenerator(2, 800, 50);
            data = dg.generateClusteredData(5);
            //data = dg.generateData();
        } catch (Exception e) {
            System.err.println("Invalid data for DataGenerator.");
        }

        startTimer();
        tryDBScan(data);
        stopTimer();

        startTimer();
        tryKMeans(data);
        stopTimer();

        //scanInput(km);
    }

    static void tryKMeans(Point[] data) {
        KMeans km = new KMeans();
        Cluster[] returned = km.doClustering(data, 0, 20);

        System.out.println("Final Centroid locations");

        for (Cluster cluster : returned) {
            System.out.println(cluster.getCentroid().toString());
        }

        GUI gui = new GUI();
        gui.repaint();
        gui.drawData(returned);
    }


    static void tryDBScan(Point[] data) {
        DBScan dbscan = new DBScan();
        Cluster[] returned = dbscan.doClustering(data, 0, 0);

        GUI gui = new GUI();
        gui.repaint();
        gui.drawData(returned);
    }

    static void scanInput(ICluster clustering) {
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
        timeEnd = System.currentTimeMillis();
        System.out.println("Algorithm took " + (timeEnd - timeStart) + "ms to complete.");
    }


}
