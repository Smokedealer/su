package utils;

import structures.Cluster;
import structures.Point;

import java.util.Random;

/**
 * Generator of clusters with random points upon specifying dimension, quantity and spread.
 *
 * @author Vojtěch Kinkor, Matěj Kareš
 */
public class DataGenerator {

    private int dimension;
    private int pointCount;
    private double spread;

    /**
     * Generator of clusters with random points upon specifying dimension, quantity and spread.
     * @param dimension Dimension of data (1D, 2D, 3D, 4D, ...). Must be positive.
     * @param pointCount Number of points to be generated. Must be nonnegative.
     * @param spread Spread for points. Must be nonnegative.
     * @throws Exception If any of the parameters is invalid.
     */
    public DataGenerator(int dimension, int pointCount, double spread) throws Exception {
        if(dimension <= 0 || pointCount < 0 || spread < 0) throw new Exception();

        this.dimension = dimension;
        this.pointCount = pointCount;
        this.spread = spread;
    }

    /**
     * Generates clusters with random points upon specifying dimension, quantity and spread.
     * Data will be equally spread so this is the worst case scenario for clustering.
     *
     * @return Cluster with generated points.
     */
    public Cluster generateData(){
        Cluster cluster = new Cluster();
        cluster.setShadowCluster(true);

        for(int i = 0; i < this.pointCount; i++){
            cluster.add(this.generateRandomPoint(this.spread));
        }

        return cluster;
    }


    /**
     * Generates clusters with random points upon specifying dimension, quantity and spread.
     * Data will be generated in "hyperball" clusters with specified spread.
     * This can generate the best case scenario for clustering.
     *
     * @param centroidsSpread Spread of clusters.
     * @param clustersCount Count of clusters to be generated.
     * @return Cluster with generated points.
     */
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

    /**
     * Generates single random point with some random spread from zero.
     * @param spread Spread limit.
     * @return Point.
     */
    private Point generateRandomPoint(double spread) {
        Point generatedPoint = new Point(this.dimension);
        Random rng = new Random();

        for(int i = 0; i < this.dimension; i++){
            double value = rng.nextDouble() * spread * 2 - spread;
            generatedPoint.setSpecificCoord(i, value);
        }

        return generatedPoint;
    }

    /**
     * Generates single random point with some random spread from specified point.
     * @param center Origin point.
     * @param spread Spread limit.
     * @return Point.
     */
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
