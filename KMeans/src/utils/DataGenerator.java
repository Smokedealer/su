package utils;

import kmeans.Point;
import java.util.Random;

/**
 * Created by Matěj Kareš on 02.12.2016.
 *
 * Generates array of random points upon specifying dimension, quantity and spread.
 * Data will be equally spread so this is not ideal for clustering, just to simplify the testing
 */
public class DataGenerator {

    Point[] data;
    int dimension;
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

        for(int i = 0; i < pointCount; i++){
            data[i] = generateRandomPoint(this.spread);
        }

        return data;
    }

    public Point[] generate1Data(){
        data = new Point[pointCount];

        for(int i = 0; i < pointCount; i++){
            data[i] = generate1Point();
        }

        return data;
    }

    public Point[] generateClusteredData(int clustersCount){
        data = new Point[pointCount];

        Random rng = new Random();

        Point center = null;
        int maxPointsPerCluster = pointCount / clustersCount;
        int clusterSize = 0;

        int i = 0;

        while(i < pointCount){

            if(clustersCount > 0 && clusterSize <= 0) {
                center = generateRandomPoint(this.spread);
                clusterSize = rng.nextInt(maxPointsPerCluster / 2 + 1) + maxPointsPerCluster / 2;
                clustersCount--;
                System.out.println("cluster " + clustersCount);
            }

            data[i] = generateNearRandomPoint(center, this.spread / 7);
            i++;
            clusterSize--;
        }

        return data;
    }


    private Point generateRandomPoint(double spread) {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int j = 0; j < dimension; j++){
            double value = rng.nextDouble() * spread * 2 - spread;
            generatedPoint.setSpecificCoord(j, value);
        }

        return generatedPoint;
    }

    private Point generateNearRandomPoint(Point center, double spread) {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int j = 0; j < dimension; j++){
            double value = center.getSpecificCoord(j) + (rng.nextGaussian() * spread - spread);
            generatedPoint.setSpecificCoord(j, value);
        }

        return generatedPoint;
    }
    private Point generate1Point() {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int j = 0; j < dimension; j++){
            generatedPoint.setSpecificCoord(j, 1.0);
        }

        return generatedPoint;
    }

}
