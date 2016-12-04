package kmeans;


import java.util.Random;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans implements ICluster{

    int distanceMethod;

    int dimension;

    Point[] centroids;

    Point[] data;

    int clusterCount;

    Cluster[] clusters;

    int iterations = 10;


    public static final int DISTANCE_EULER = 0;
    public static final int DISTANCE_MANHATTAN = 1;
    public static final int DISTANCE_COSIN = 2;



    @Override
    public Cluster[] doClustering(Point[] data, int clusterCount) {

        //Wrong data
        if(data == null || data.length == 0) return null;


        this.dimension = data[0].getDimension();
        this.data = data;
        this.clusterCount = clusterCount;
        this.distanceMethod = 0;

        centroids = new Point[clusterCount];
        clusters = new Cluster[clusterCount];

        initCentroids();

        for(int i = 0; i < iterations; i++){
            assignCentroids();
            adjustCentroids();
        }

        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }

        return clusters;
    }



    /**
     * Initializes centroids at locations of the few first data points.
     */
    private void initCentroids(){

        Point rndPoint;

        Random rng = new Random();

        //Assign each centroid to
        for(int i = 0; i < clusterCount; i++){
            clusters[i] = new Cluster();
            centroids[i] = new Point(data[i]);
        }
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

            Point old = new Point(centroids[i]);
            Point fresh = cluster.geometricalMiddle();

            System.out.println("Centroid " + centroids[i].id + " moved by " + getDistance(old, fresh));

            centroids[i].moveTo(fresh);
        }
    }


    private int getClosestCentroid(Point point){

        double minValue = Double.MAX_VALUE;
        int closestCentroidIndex = 0;

        for (int j = 0; j < centroids.length; j++) {
            Point centroid = centroids[j];

            double actDistance = getDistance(point, centroid);

            if (actDistance < minValue) {
                minValue = actDistance;
                closestCentroidIndex = j;
            }

        }

        return closestCentroidIndex;
    }


    private double getDistance(Point a, Point b){

        double distance = 0;

        switch (distanceMethod){
            case DISTANCE_EULER:
                distance = a.eulerDistanceTo(b);
                break;

            case DISTANCE_MANHATTAN:
                distance = a.manhattanDistanceTo(b);
                break;

            case DISTANCE_COSIN:
                distance = a.cosineDistanceTo(b);
                break;

            default:
                distance = a.eulerDistanceTo(b);
                break;
        }

        return distance;
    }


    public Cluster assignToCluster(Point point){
        if (point == null){
            System.err.println("Null Point cannot be assigned to a cluster.");
            return null;
        }

        int index = getClosestCentroid(point);
        clusters[index].add(point);

        System.out.println(point.toString() + " was assigned to cluster " + clusters[index].getId() + ".");

        return clusters[index];
    }


    public void setDistanceMethod(int method){
        this.distanceMethod = method;
    }


}
