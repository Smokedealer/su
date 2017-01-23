import dbscan.DBScan;
import kmeans.KMeans;
import structures.Cluster;
import structures.ICluster;
import structures.Point;
import utils.DataGenerator;
import utils.GUIForm;
import utils.GenerateDataListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    private static long timeStart;
    private static long timeEnd;

    private GUIForm gui;

    public static void main(String[] args) {

        new App(); // controllable GUI
        //new App(null); // just for testing

    }

    /**
     * Just for testing! GUI won't work properly.
     */
    private App(Object debugmode) {
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

        this.gui = new GUIForm();
        tryKMeans(data);

        this.gui = new GUIForm();
        tryDBScan(data);
    }


    public App() {
        this.gui = new GUIForm();
        this.gui.setGenerateDataListener(new GenerateDataListener() {

            Point[] data = null;

            @Override
            public void uniformData(int dimensions, int points, int spread) {
                try {
                    DataGenerator dg = new DataGenerator(dimensions, points, spread);
                    data = dg.generateData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void clusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread) {
                try {
                    DataGenerator dg = new DataGenerator(dimensions, points, spread);
                    data = dg.generateClusteredData(clustersSpread, clusters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fileData(String file) {
            }

            @Override
            public void process(int method) {
                if(this.data == null) return;

                switch(method) {
                    case 0: tryKMeans(deepCopyOfPoints(this.data)); break;
                    case 1: tryDBScan(deepCopyOfPoints(this.data)); break;
                }
            }
        });
    }

    private void tryKMeans(Point[] data) {
        startTimer();

        KMeans km = new KMeans();
        Cluster[] returned = km.doClustering(data, 0, 20);

        stopTimer();

        System.out.println("Final Centroid locations");

        for (Cluster cluster : returned) {
            System.out.println(cluster.getCentroid().toString());
        }

        gui.drawData(returned);
    }


    private void tryDBScan(Point[] data) {
        startTimer();

        DBScan dbscan = new DBScan();
        Cluster[] returned = dbscan.doClustering(data, 0, 0);

        stopTimer();

        gui.drawData(returned);
    }

    private static void saveDataToFile(Point[] data, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Point[] loadDataFromFile(String file) {
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

    private static void scanInput(ICluster clustering) {
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

    private static Point[] deepCopyOfPoints(Point[] input) {
        Point[] output = new Point[input.length];
        for (int i = 0, length = output.length; i < length; i++) {
            output[i] = new Point(input[i]);
        }
        return output;
    }

}
