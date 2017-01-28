package utils;

import structures.Cluster;
import structures.Point;

import java.util.Random;

/**
 * Created by Matěj Kareš on 02.12.2016.
 *
 * Generates array of random points upon specifying dimension, quantity and spread.
 * Data will be equally spread so this is not ideal for clustering, just to simplify the testing
 */
public class DataGenerator {

    private int dimension;
    private int pointCount;
    private double spread;

    public DataGenerator(int dimension, int pointCount, double spread) throws Exception {
        if(dimension <= 0 || pointCount < 0 || spread < 0) throw new Exception();

        this.dimension = dimension;
        this.pointCount = pointCount;
        this.spread = spread;
    }


    public Cluster generateData(){
        Cluster cluster = new Cluster();
        cluster.setShadowCluster(true);

        for(int i = 0; i < pointCount; i++){
            cluster.add(generateRandomPoint(this.spread));
        }

        return cluster;
    }

    public Point[] generateLineData(){
        Point[] data = new Point[pointCount];

        for(int i = 0; i < pointCount; i++){
            data[i] = generatePointOnLine();
        }

        return data;
    }

    public Cluster generateClusteredData(double centroidsSpread, int clustersCount){
        Cluster cluster = new Cluster();
        cluster.setShadowCluster(true);
        Random rng = new Random();

        Point center = null;
        int maxPointsPerCluster = pointCount / clustersCount;
        int nextClusterSize = 0;

        int i = 0;

        while(i < pointCount){
            if(clustersCount > 0 && nextClusterSize <= 0) {
                center = generateRandomPoint(centroidsSpread);
                nextClusterSize = rng.nextInt(maxPointsPerCluster / 2 + 1) + maxPointsPerCluster / 2;
                clustersCount--;
            }

            cluster.add(generateNearRandomPoint(center, this.spread));
            i++;
            nextClusterSize--;
        }

        return cluster;
    }


    private Point generateRandomPoint(double spread) {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int i = 0; i < dimension; i++){
            double value = rng.nextDouble() * spread * 2 - spread;
            generatedPoint.setSpecificCoord(i, value);
        }

        return generatedPoint;
    }

    private Point generateNearRandomPoint(Point center, double spread) {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int i = 0; i < dimension; i++){
            double value = center.getSpecificCoord(i) + (rng.nextGaussian() * spread - spread);
            generatedPoint.setSpecificCoord(i, value);
        }

        return generatedPoint;
    }

    private Point generatePointOnLine() {
        Point generatedPoint = new Point(dimension);
        Random rng = new Random();

        for(int j = 0; j < dimension; j++){
            generatedPoint.setSpecificCoord(j, 1.0);
        }

        return generatedPoint;
    }

}
