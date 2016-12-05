package kmeans;


import java.util.*;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans implements ICluster{

    /** Method used for determining distances of points **/
    int distanceMethod;

    /** Dimension the K-Means is dealing with **/
    int dimension;

    /** Array of centroids **/
    Point[] centroids;

    /** Data for clustering **/
    Point[] data;

    /** Number of clusters to be done **/
    int clusterCount;

    /** Array of clusters **/
    Cluster[] clusters;

    /** If the convergence is lower than this amount, algorithm will stop **/
    double alpha;

    /** Biggest distance traveled by a centroid in one iteration **/
    double lastBiggestAdjustment;

    /** Failsafe for ending the iterations **/
    int maxInterations;

    SortedMap<Double, Cluster[]> results;


    public static final int DISTANCE_EUKLEID = 0;
    public static final int DISTANCE_MANHATTAN = 1;



    @Override
    public Cluster[] doClustering(Point[] data, int clusterCount, int nCount) {

        //Wrong data
        if(data == null || data.length == 0 || nCount <= 0) return null;

        this.dimension = data[0].getDimension();
        this.data = data;
        this.clusterCount = clusterCount;
        this.lastBiggestAdjustment = Double.MIN_VALUE;
        this.alpha = 0;
        this.maxInterations = 10000000;
        this.results = new TreeMap<Double, Cluster[]>();


        for (int i = 0; i < nCount; i++){
            start();
        }

        return results.get(results.firstKey());
    }


    private void start(){
        centroids = new Point[clusterCount];
        clusters = new Cluster[clusterCount];

        initCentroids();

        int iteration = 0;

        do{
            assignCentroids();
            adjustCentroids();
            //System.out.println("Iteration " + iteration + ", Convergence: " + lastBiggestAdjustment);
        }while (!convergenceIsLow(++iteration));

        double jValue = jFunction();

        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }

        results.put(jValue, clusters);

    }

    private boolean convergenceIsLow(int iteration){
        boolean result = false;

        if(lastBiggestAdjustment <= alpha || iteration > maxInterations) result = true;

        return result;
    }



    private double jFunction(){
        double average = 0;
        int count = 0;

        for (Cluster cluster : clusters){
            average += cluster.average();
            count++;
        }

        average /= count;

        System.out.println("Function J: " + average);

        return average;
    }



    /**
     * Initializes centroids at locations of the few first data points.
     */
    private void initCentroids(){
        ArrayList<Integer> startingPoints = new ArrayList<Integer>(clusterCount);

        Random rng = new Random();

        //Assign each centroid to
        for(int i = 0; i < clusterCount; i++){
            clusters[i] = new Cluster();

            int randomIndex = 0;

            do {
                randomIndex = rng.nextInt(data.length);
            }while (startingPoints.contains(randomIndex));

            startingPoints.add(randomIndex);

            centroids[i] = new Point(data[randomIndex]);
        }
    }


    private void assignCentroids(){

        for(Cluster cluster : clusters){
            cluster.clear();
        }

        for(Point point : data){
            //Get the closest centroid
            int closestCentroidIndex = getClosestCentroid(point);

            //Add point to the cluster
            clusters[closestCentroidIndex].add(point);

            //Give the cluster reference to it's centroid
            clusters[closestCentroidIndex].centroid = centroids[closestCentroidIndex];
        }
    }



    private void adjustCentroids(){
        double iterBiggestAdjustment = 0;
        lastBiggestAdjustment = 0;

        for(int i = 0; i < clusters.length; i++){

            Cluster cluster = clusters[i];

            Point old = new Point(centroids[i]);
            Point fresh = cluster.geometricalMiddle();

            if(cluster.size() == 0) continue;

            //System.out.println("Centroid " + centroids[i].id + " moved by " + getDistance(old, fresh));

            double change = getDistance(old, fresh);

            if(change > iterBiggestAdjustment) iterBiggestAdjustment = change;

            centroids[i].moveTo(fresh);
        }

        lastBiggestAdjustment = iterBiggestAdjustment;
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


    public double getDistance(Point a, Point b){

        double distance = 0;

        switch (distanceMethod){
            case DISTANCE_EUKLEID:
                distance = a.eukleidDistanceTo(b);
                break;

            case DISTANCE_MANHATTAN:
                distance = a.manhattanDistanceTo(b);
                break;


            default:
                distance = a.eukleidDistanceTo(b);
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

    public void setAlpha(double alpha) {
        if(alpha < 0) alpha = 0;

        this.alpha = alpha;
    }

    public void setMaxInterations(int maxInterations) {
        if(maxInterations < 1) maxInterations = 1;
        this.maxInterations = maxInterations;
    }
}
