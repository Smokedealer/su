package kmeans;


import java.util.*;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans implements ICluster{

    public static final int DISTANCE_EUKLEID = 0;
    public static final int DISTANCE_MANHATTAN = 1;



    /** Method used for determining distances of points **/
    private int distanceMethod;

    /** Dimension the K-Means is dealing with **/
    private int dimension;

    /** Array of centroids **/
    private Point[] centroids;

    /** Data for clustering **/
    private Point[] data;

    /** Number of clusters to be done **/
    private int clusterCount;

    /** Array of clusters **/
    private Cluster[] clusters;

    /** If the convergence is lower than this amount, algorithm will stop **/
    private double convergenceThreshold;

    /** Biggest distance traveled by a centroid in one iteration **/
    private double lastBiggestAdjustment;

    /** Fail safe for ending the iterations **/
    private int maxIterations;


    private SortedMap<Double, Cluster[]> results;

    private Cluster[] previousResult;

    private ArrayList<Double> elbowValues;

    private boolean isElbowingEnabled = false;

    private double elbowDifference;

    private int maxElbowIterations = 15;

    private double elbowDifferenceTrigger = 0.15;






    @Override
    public Cluster[] doClustering(Point[] data, int clusterCount, int nCount) {

        //Wrong data
        if(data == null || data.length == 0 || nCount <= 0) return null;

        System.out.println("Starting K-Means.");

        this.dimension = data[0].getDimension();
        this.data = data;

        if(clusterCount<1){
            isElbowingEnabled = true;
            this.clusterCount = 1;
        }
        else this.clusterCount = clusterCount;

        this.lastBiggestAdjustment = Double.MIN_VALUE;
        this.convergenceThreshold = 0.01;
        this.maxIterations = 1000;
        this.results = new TreeMap<Double, Cluster[]>();

        if(isElbowingEnabled){

            elbowValues = new ArrayList<Double>();

            int iteration = 0;

            while(true){
                iteration++;

                System.out.println("\nStarting elbow iteration " + iteration + ".");

                if(iteration > maxElbowIterations){
                    System.out.println("Exceeded maximum iterations for elbow method.");
                    return null;
                }

                this.results = new TreeMap<Double, Cluster[]>();

                for (int i = 0; i < nCount; i++){
                    System.out.println("Restarting algorithm. Round " + i + ".");
                    start();
                }

                if(!doElbowing()) return previousResult;

                previousResult = results.get(results.firstKey());

                this.clusterCount++;
            }
        }else{

            for (int i = 0; i < nCount; i++){
                System.out.println("Restarting algorithm. Round " + i + ".");
                start();
            }

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
            System.out.println("Iteration " + iteration + ", Convergence: " + lastBiggestAdjustment);
        }while (!convergenceIsLow(++iteration));

        double jValue = jFunction();

        /*
        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }*/

        results.put(jValue, clusters);

    }

    /**
     * Heuristic method to find if more clusters mean marginally better results.
     *
     * @return true - if the result wasn't good enough to stop
     */
    private boolean doElbowing(){
        boolean result = true;

        elbowValues.add(results.firstKey());

        if(elbowValues.size() == 1){
            elbowDifference = 0;
            return true;
        }

        double last = elbowValues.get(elbowValues.size() - 1);
        double lastButOne= elbowValues.get(elbowValues.size() - 2);

        double difference = lastButOne - last;

        //System.out.println("Base difference: " + elbowDifference + ", Difference: " + difference );

        if(elbowDifference * elbowDifferenceTrigger > difference || (difference > elbowDifference && elbowDifference > 0.0)){
            result = false;
        }
        elbowDifference = difference;


        return result;
    }

    /**
     * Prevents centroid infinite adjusting based od the amount of convergence or number of iterations
     *
     * @param iteration
     * @return true - adjusting should stop
     */
    private boolean convergenceIsLow(int iteration){
        boolean result = false;

        if(lastBiggestAdjustment <= convergenceThreshold || iteration > maxIterations) result = true;

        return result;
    }


    /**
     * This method determines how good the solution was. Lower values mean better solution.
     *
     * @return double jValue
     */
    private double jFunction(){
        double average = 0;
        int count = 0;

        for (Cluster cluster : clusters){
            average += cluster.average();
            count++;
        }

        average /= count;

        //System.out.println("Function J: " + average);

        return average;
    }



    /**
     * Initializes centroids at locations of the few first data points.
     */
    private void initCentroids(){
        ArrayList<Integer> startingPoints = new ArrayList<Integer>(clusterCount);
        Random rng = new Random();

        //Assign each centroid to cluster
        for(int i = 0; i < clusterCount; i++){
            clusters[i] = new Cluster();

            int randomIndex = 0;

            //Find starting point that hasn't been used yet.
            do {
                randomIndex = rng.nextInt(data.length);
            }while (startingPoints.contains(randomIndex));

            startingPoints.add(randomIndex);

            centroids[i] = new Point(data[randomIndex]);
        }
    }


    private void assignCentroids(){
        //Clear all
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

    public void setConvergenceThreshold(double convergenceThreshold) {
        if(convergenceThreshold < 0) convergenceThreshold = 0;

        this.convergenceThreshold = convergenceThreshold;
    }

    public void setMaxIterations(int maxIterations) {
        if(maxIterations < 1) maxIterations = 1;
        this.maxIterations = maxIterations;
    }

    public void setMaxElbowIterations(int maxElbowIterations) {
        if(maxElbowIterations < 0) maxElbowIterations = 0;
        this.maxElbowIterations = maxElbowIterations;
    }

    public void setElbowDifferenceTrigger(double elbowDifferenceTrigger) {
        if(elbowDifferenceTrigger > 1) elbowDifferenceTrigger = 1;
        if(elbowDifferenceTrigger < 0) elbowDifferenceTrigger = 0;

        this.elbowDifferenceTrigger = elbowDifferenceTrigger;
    }
}
