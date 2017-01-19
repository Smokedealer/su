import dbscan.DBScan;
import kmeans.KMeans;
import structures.Cluster;
import structures.ICluster;
import structures.Point;
import utils.DataGenerator;
import utils.GUI;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    private static long timeStart;
    private static long timeEnd;

    public static void main(String[] args) {

        Point[] data = loadDataFromFile("data.txt");

        if(data == null) {
            try {
                DataGenerator dg = new DataGenerator(2, 5000, 50);
                data = dg.generateClusteredData(40, 5);
                //data = dg.generateData();

                saveDataToFile(data, "data.txt");
            } catch (Exception e) {
                System.err.println("Invalid data for DataGenerator.");
            }
        }

        startTimer();
        tryKMeans(data);
        stopTimer();

        startTimer();
        tryDBScan(data);
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

    static void saveDataToFile(Point[] data, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Point[] loadDataFromFile(String file) {
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
