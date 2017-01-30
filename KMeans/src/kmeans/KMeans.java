package kmeans;


import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;

import java.util.*;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans implements ClusteringAlg {

    public static final int DISTANCE_EUKLEID = 0;
    public static final int DISTANCE_MANHATTAN = 1;


    /** Method used for determining distances of points **/
    private int distanceMethod;

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

    private double elbowDifferenceAbsThreshold;

    private double elbowDroprateThreshold = 0.8d;

    private int maxElbowIterations = 15;



    @Override
    public Cluster[] doClustering(Point[] data, ClusteringAlgConf conf) {

        //Wrong data
        if(data == null || data.length == 0 || !(conf instanceof KMeansConf)) return null;

        KMeansConf kMeansConf = (KMeansConf) conf;
        int clusterCount = kMeansConf.getClusterCount();
        int algRepeats = kMeansConf.getAlgRepeats();
        this.elbowDroprateThreshold = kMeansConf.getElbowDroprateThresholdValue();
        this.elbowDifferenceAbsThreshold = kMeansConf.getElbowDifferenceAbsThresholdValue();

        System.out.println("Starting K-Means.");


        if(clusterCount < 1){
            this.isElbowingEnabled = true;
            this.clusterCount = 1;
        }
        else {
            this.clusterCount = clusterCount;
        }

        this.data = data;
        this.lastBiggestAdjustment = Double.MIN_VALUE;
        this.convergenceThreshold = 0.01;
        this.maxIterations = 1000;
        this.results = new TreeMap<>();

        if(this.isElbowingEnabled){

            this.elbowValues = new ArrayList<>();

            int iteration = 0;

            while(true){
                iteration++;

                System.out.println("\nStarting elbow iteration " + iteration + ".");

                if(iteration > this.maxElbowIterations){
                    System.out.println("Exceeded maximum iterations for elbow method.");
                    return this.previousResult;
                }
                if(this.clusterCount > this.data.length){
                    System.out.println("Not enough data for more clusters.");
                    return this.previousResult;
                }

                this.results = new TreeMap<>();

                // repeat multiple times with different random points to get best results
                for (int i = 0; i < algRepeats; i++){
                    //System.out.println("Restarting algorithm. Round " + i + ".");
                    this.start(); // select n random points and do the whole clusters adjusting (in a loop until convergence is low)
                    if(this.clusterCount == 1) break;
                }

                if(!this.doElbowing()) return this.previousResult;

                this.previousResult = this.results.get(this.results.firstKey());
                this.clusterCount++;
            }

        } else {

            for (int i = 0; i < algRepeats; i++){
                //System.out.println("Restarting algorithm. Round " + i + ".");
                this.start();
            }

        }


        return this.results.get(this.results.firstKey());
    }


    private void start(){
        this.centroids = new Point[this.clusterCount];
        this.clusters = new Cluster[this.clusterCount];

        this.initCentroids();

        int iteration = 0;

        do {
            this.assignCentroids();
            this.adjustCentroids();
            //System.out.println("Iteration " + iteration + ", Convergence: " + lastBiggestAdjustment);
        } while (!this.convergenceIsLow(++iteration));

        double jValue = this.jFunction();

        /*
        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }*/

        //System.out.println("jValue: " + jValue);

        this.results.put(jValue, this.clusters);
    }

    /**
     * Heuristic method to find if more clusters mean marginally better results.
     *
     * @return true - if the result wasn't good enough to stop
     */
    private boolean doElbowing(){
        boolean result = true;

        this.elbowValues.add(this.results.firstKey());

        if(this.elbowValues.size() == 1){
            //this.elbowPrevDropRate = 0;
            return true;
        }

        double last = this.elbowValues.get(this.elbowValues.size() - 1);
        double lastButOne = this.elbowValues.get(this.elbowValues.size() - 2);
        double difference = lastButOne - last;
        double dropRate = lastButOne / last - 1;

        System.out.println("Last two difference: " + lastButOne + " - " + last + " = " + difference + ", drop rate: " + dropRate);

        // if current difference is larger than previous difference
        if(last > lastButOne) {
            result = false;
            System.out.println("Stop elbowing, difference got larger");
        }
        // check for differences using
        if(dropRate > 0 && dropRate < this.elbowDroprateThreshold){
            result = false;
            System.out.println("Stop elbowing, droprate below threshold");
        }
        // check for elbow using absolute difference value
        if(difference < this.elbowDifferenceAbsThreshold && this.elbowDifferenceAbsThreshold > 0){
            result = false;
            System.out.println("Stop elbowing, difference below threshold");
        }

        //this.elbowPrevDropRate = dropRate;

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

        if(this.lastBiggestAdjustment <= this.convergenceThreshold || iteration > this.maxIterations) result = true;

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

        for (Cluster cluster : this.clusters){
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
        ArrayList<Integer> startingPoints = new ArrayList<Integer>(this.clusterCount);
        Random rng = new Random();

        //Assign each centroid to cluster
        for(int i = 0; i < this.clusterCount; i++){
            this.clusters[i] = new Cluster();

            int randomIndex;

            //Find starting point that hasn't been used yet.
            do {
                randomIndex = rng.nextInt(this.data.length);
            } while (startingPoints.contains(randomIndex));

            startingPoints.add(randomIndex);

            this.centroids[i] = new Point(this.data[randomIndex]);
        }
    }


    private void assignCentroids(){
        //Clear all
        for(Cluster cluster : this.clusters){
            cluster.clear();
        }

        for(Point point : this.data){
            //Get the closest centroid
            int closestCentroidIndex = this.getClosestCentroid(point);

            Cluster cluster = this.clusters[closestCentroidIndex];

            //Add point to the cluster
            cluster.add(point);

            //Give the cluster reference to it's centroid
            cluster.setCentroid(this.centroids[closestCentroidIndex]);
        }
    }



    private void adjustCentroids(){
        double iterBiggestAdjustment = 0;
        this.lastBiggestAdjustment = 0;

        for(int i = 0; i < this.clusters.length; i++){

            Cluster cluster = this.clusters[i];

            Point old = new Point(this.centroids[i]);
            Point fresh = cluster.geometricalMiddle();

            if(cluster.size() == 0) continue;

            //System.out.println("Centroid " + centroids[i].id + " moved by " + getDistance(old, fresh));

            double change = this.getDistance(old, fresh);

            if(change > iterBiggestAdjustment) iterBiggestAdjustment = change;

            this.centroids[i].moveTo(fresh);
        }

        this.lastBiggestAdjustment = iterBiggestAdjustment;
    }


    private int getClosestCentroid(Point point){

        double minValue = Double.MAX_VALUE;
        int closestCentroidIndex = 0;

        for (int j = 0; j < this.centroids.length; j++) {
            Point centroid = this.centroids[j];

            double actDistance = this.getDistance(point, centroid);

            if (actDistance < minValue) {
                minValue = actDistance;
                closestCentroidIndex = j;
            }

        }

        return closestCentroidIndex;
    }


    public double getDistance(Point a, Point b){

        double distance;

        switch (this.distanceMethod){
            default:
            case DISTANCE_EUKLEID:
                distance = a.euclideanDistanceTo(b);
                break;

            case DISTANCE_MANHATTAN:
                distance = a.manhattanDistanceTo(b);
                break;
        }

        return distance;
    }


    public Cluster assignToCluster(Point point){
        if (point == null){
            System.err.println("Null Point cannot be assigned to a cluster.");
            return null;
        }

        int index = this.getClosestCentroid(point);
        this.clusters[index].add(point);

        System.out.println(point.toString() + " was assigned to cluster " + this.clusters[index].getId() + ".");

        return this.clusters[index];
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

}
