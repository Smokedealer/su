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

    Cluster[] clusters;


    public KMeans(int dimension, Point[] data, int clusterCount) {
        this.dimension = dimension;
        this.data = data;
        this.clusterCount = clusterCount;
        centroids = new Point[clusterCount];
        clusters = new Cluster[clusterCount];
    }

    public KMeans() {

    }

    public void go(){
        initCentroids();

        for(int i = 0; i < 5; i++){
            assignCentroids();
            adjustCentroids();
        }

        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }
    }

    private void initCentroids(){
        Point rndPoint;

        Random rng = new Random();

        for(int i = 0; i < clusterCount; i++){
            clusters[i] = new Cluster();

            Point centroidPotentialPoint;

            do {
                rndPoint = data[rng.nextInt(data.length)];
                centroidPotentialPoint = new Point(rndPoint.getCoordinates());

                centroids[i] = centroidPotentialPoint;
            }while (isStartingPointFree(centroidPotentialPoint));
        }

    }

    private boolean isStartingPointFree(Point point){
        boolean free = true;

        for(Point centroid : centroids){
            if(centroid.equals(point)){
                free = false;
                break;
            }
        }

        return free;
    }

    private void assignCentroids(){

        for(Cluster cluster : clusters){
            cluster.clear();
        }

        for(Point point : data){
            int closestCentroidIndex = getClosestCentroid(point);
            clusters[closestCentroidIndex].add(point);
        }


    }



    private void adjustCentroids(){
        for(int i = 0; i < clusters.length; i++){
            Cluster cluster = clusters[i];

            centroids[i] = (cluster.geometricalMiddle());
        }
    }


    private int getClosestCentroid(Point point){
        double minValue = Double.MAX_VALUE;
        int closestCentroidIndex = 0;
        int centroidIndex = 0;

        for (int j = 0; j < centroids.length; j++) {
            Point centroid = centroids[j];

            double actDistance = point.eulerDistanceTo(centroid);

            if (actDistance < minValue) {
                minValue = actDistance;
                closestCentroidIndex = j;
            }

        }

        return closestCentroidIndex;
    }

}
