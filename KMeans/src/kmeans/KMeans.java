package kmeans;


import java.util.Random;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans {

    int dimension;

    Point[] centroids;
    Point[] data;
    int clusterCount;


    public KMeans(int dimension, Point[] data, int clusterCount) {
        this.dimension = dimension;
        this.data = data;
        this.clusterCount = clusterCount;
    }

    public KMeans() {
        int a = magnitude(new Point(-5, -5), new Point(0,0));
        System.out.println(a);
    }

    private void initCentroids(){
        Point rndPoint;

        Random rng = new Random();

        for(int i = 0; i < clusterCount; i++){

            rndPoint = data[rng.nextInt(data.length)];

            centroids[i] = new Point(rndPoint.getX(), rndPoint.getY());
        }

    }

    private void computeDistances(){
        for(int i = 0; i < centroids.length; i++){

        }
    }

    private void assignToCentroids(){

    }

    private void adjustCentroids(){

    }


    private int getClosestPoint(Point point){
        int minValue = 0;
        int closestPointIndex = 0;

        for(int i = 0; i < centroids.length; i++){
            Point centroid = centroids[i];

            int actDistance = magnitude(centroid, point);

            if(actDistance < minValue){
                minValue = actDistance;
                closestPointIndex = i;
            }
        }

        return closestPointIndex;
    }

    private int magnitude(Point a, Point b){
        int distance;

        int zeroedX = a.getX() - b.getX();
        int zeroedY = a.getY() - b.getY();

        Point zeroed = new Point(zeroedX, zeroedY);

        double magnitude = Math.sqrt(zeroed.getX() * zeroed.getX() + zeroed.getY() * zeroed.getY());

        distance = (int) magnitude;

        return distance;
    }

}
