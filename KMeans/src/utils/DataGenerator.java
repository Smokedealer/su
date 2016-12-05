package utils;

import kmeans.Point;

import javax.swing.text.html.HTMLDocument;
import java.util.Random;

/**
 * Created by Matěj Kareš on 02.12.2016.
 *
 * Generates array of random points upon specifying dimension, quantity and spread.
 * Data will be equally spread so this is not ideal for clustering, just to simplify the testing
 */
public class DataGenerator {

    int dimension;

    Point[] data;

    int pointCount;

    double spread;

    public DataGenerator(int dimension, int pointCount, double spread) throws Exception {
        if(dimension <= 0 ||pointCount <= 0 || spread <= 0) throw new Exception();

        this.dimension = dimension;
        this.pointCount = pointCount;
        this.spread = spread;
    }


    public Point[] generateData(){
        data = new Point[pointCount];

        Random rng = new Random();

        for(int i = 0; i < pointCount; i++){
            Point generatedPoint;
            generatedPoint = new Point(dimension);

            for(int j = 0; j < dimension; j++){
                double value = rng.nextDouble() * spread * 2 - spread;
                generatedPoint.setSpecificCoord(j, value);
            }

            data[i] = generatedPoint;
        }

        return data;
    }
}
