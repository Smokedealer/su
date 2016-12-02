import kmeans.KMeans;
import kmeans.Point;
import utils.DataGenerator;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    public static void main(String[] args) {

        DataGenerator dg = new DataGenerator(100, 20000, 1000);

        Point[] data = dg.generateData();

        for(Point p : data){
            System.out.println(p.toString());
        }


        KMeans km = new KMeans();
        km.doClustering(data, 50);
        km.start();

    }

}
