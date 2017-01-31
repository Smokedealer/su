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

        for(int i = 0; i < this.pointCount; i++){
            cluster.add(this.generateRandomPoint(this.spread));
        }

        return cluster;
    }

    public Cluster generateClusteredData(double centroidsSpread, int clustersCount){
        Cluster cluster = new Cluster();
        cluster.setShadowCluster(true);
        Random rng = new Random();

        Point center = null;
        int maxPointsPerCluster = this.pointCount / clustersCount;
        int nextClusterSize = 0;

        int i = 0;

        while(i < this.pointCount){
            if(clustersCount > 0 && nextClusterSize <= 0) {
                center = this.generateRandomPoint(centroidsSpread);
                nextClusterSize = rng.nextInt(maxPointsPerCluster / 2 + 1) + maxPointsPerCluster / 2;
                clustersCount--;
            }

            cluster.add(this.generateNearRandomPoint(center, this.spread));
            i++;
            nextClusterSize--;
        }

        return cluster;
    }


    private Point generateRandomPoint(double spread) {
        Point generatedPoint = new Point(this.dimension);
        Random rng = new Random();

        for(int i = 0; i < this.dimension; i++){
            double value = rng.nextDouble() * spread * 2 - spread;
            generatedPoint.setSpecificCoord(i, value);
        }

        return generatedPoint;
    }

    private Point generateNearRandomPoint(Point center, double spread) {
        Point generatedPoint = new Point(this.dimension);
        Random rng = new Random();

        for(int i = 0; i < this.dimension; i++){
            double value = center.getSpecificCoord(i) + (rng.nextGaussian() * spread - spread);
            generatedPoint.setSpecificCoord(i, value);
        }

        return generatedPoint;
    }


}
