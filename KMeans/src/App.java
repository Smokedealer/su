import kmeans.KMeans;
import kmeans.Point;

/**
 * Created by kares on 30.11.2016.
 */
public class App {

    public static void main(String[] args) {


        double[] pdata1 = {-2, -2};
        double[] pdata2 = {-3, -2};
        double[] pdata3 = {-2, -3};
        double[] pdata4 = {-3, -3};

        double[] pdata5 = {2, 2};
        double[] pdata6 = {3, 2};
        double[] pdata7 = {2, 3};
        double[] pdata8 = {3, 3};


        Point point1 = new Point(pdata1);
        Point point2 = new Point(pdata2);
        Point point3 = new Point(pdata3);
        Point point4 = new Point(pdata4);
        Point point5 = new Point(pdata5);
        Point point6 = new Point(pdata6);
        Point point7 = new Point(pdata7);
        Point point8 = new Point(pdata8);

        Point[] data = {point1, point2, point3, point4, point5, point6, point7, point8};

        KMeans km = new KMeans(2,data,2);
        km.go();

    }

}
