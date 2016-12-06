import kmeans.Cluster;
import kmeans.ICluster;
import kmeans.KMeans;
import kmeans.Point;
import utils.DataGenerator;
import utils.GUI;

import java.util.Scanner;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    public static void main(String[] args) {

        Point[] data = null;

        try {
            DataGenerator dg = new DataGenerator(2, 30000, 50);
            data = dg.generateClusteredData(5);
            //data = dg.generateData();
        }catch (Exception e){
            System.err.println("Invalid data for DataGenerator.");
        }


        KMeans km = new KMeans();
        Cluster[] returned = km.doClustering(data, 0, 20);


        System.out.println("Final Centroid locations");

        for(Cluster cluster : returned){
            System.out.println(cluster.getCentroid().toString());
        }


        GUI gui = new GUI();
        gui.repaint();
        gui.drawData(returned);


        //scanInput(km);
    }

    static void scanInput(ICluster clustering){
        Scanner sc = new Scanner(System.in);

        while(true){
            String line = sc.nextLine();

            String[] values = line.split(" ");
            double[] dValues = new double[values.length];


            for(int i = 0; i < values.length; i++){
                dValues[i] = Double.valueOf(values[i]);
            }

            Point newPoint = new Point(dValues);

            clustering.assignToCluster(newPoint);
        }
    }

}
