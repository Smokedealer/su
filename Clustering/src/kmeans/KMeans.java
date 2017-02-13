package kmeans;
import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;

import java.util.*;


/**
 * KMeans clustering algorithm.
 *
 * @author Matěj Kareš, Vojtěch Kinkor
 */
public class KMeans implements ClusteringAlg {

    public static final int DISTANCE_EUKLEID = 0;
    public static final int DISTANCE_MANHATTAN = 1;


    /** Method used for determining distances of points **/
    protected int distanceMethod;

    /** Array of centroids **/
    protected Point[] centroids;

    /** Data for clustering **/
    protected Point[] data;

    /** Number of clusters to be done **/
    protected int clusterCount;

    /** Array of clusters **/
    protected Cluster[] clusters;

    /** If the convergence is lower than this amount, algorithm will stop **/
    protected double convergenceThreshold;

    /** Biggest distance traveled by a centroid in one iteration **/
    protected double lastBiggestAdjustment;

    /** Fail safe for ending the iterations **/
    protected int maxIterations;


    /** Map with results, keys are result "prices" (lower=better), values are found clusters. **/
    protected SortedMap<Double, Cluster[]> results;

    /** Previous (last but one) results, used for elbowing. **/
    protected Cluster[] previousResult;

    /** List prices for different cluster counts, used for elbowing. **/
    protected List<Double> elbowValues;

    /** Controls whether elbow method should be used. **/
    protected boolean isElbowingEnabled = false;

    /** Elbow parameter, stop alg. when difference between two last iteration is below threshold. **/
    protected double elbowDifferenceAbsThreshold;

    /** Elbow parameter, stop alg. when droprate between two last iteration is below threshold. **/
    protected double elbowDroprateThreshold = 0.8d;

    /** Elbow parameter, top limit for iterations (also means stop alg. when there are more than x clusters). **/
    protected int maxElbowIterations = 15;


    /**
     * Runs clustering algorithm.
     * @param data Input "unsorted" data.
     * @param conf Algorithm configuration.
     * @return Data sorted into clusters.
     */
    @Override
    public Cluster[] doClustering(Point[] data, ClusteringAlgConf conf) {

        if(conf == null) conf = new KMeansConf();

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
                    return this.results.get(this.results.firstKey());
                }
                if(this.clusterCount > this.data.length){
                    System.out.println("Not enough data for more clusters.");
                    return this.results.get(this.results.firstKey());
                }

                this.results = new TreeMap<>();

                // repeat multiple times with different random points to get best results
                for (int i = 0; i < algRepeats; i++){
                    //System.out.println("Restarting algorithm. Round " + i + ".");
                    this.start(); // select n random points and do the whole clusters adjusting (in a loop until convergence is low)
                    if(this.clusterCount == 1) break;
                }

                // check if we should continue with more clusters or there is already sufficient elbow
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

    /**
     * KMeans alg entry point.
     */
    protected void start(){
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
     * Heuristic method to find if more clusters leads to significantly better results.
     * @return True if the result wasn't good enough to stop (ie. if it's better to continue with "elbowing").
     */
    protected boolean doElbowing(){
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
    protected boolean convergenceIsLow(int iteration){
        boolean result = false;

        if(this.lastBiggestAdjustment <= this.convergenceThreshold || iteration > this.maxIterations) result = true;

        return result;
    }


    /**
     * This method determines how good the solution was. Lower values mean better solution.
     *
     * @return double jValue
     */
    protected double jFunction(){
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
    protected void initCentroids(){
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


    /**
     * Every point gets assigned to closest centroid
     */
    protected void assignCentroids(){
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

    /**
     * Every centroid gets adjusted to the geometrical middle of it's cluster
     */
    protected void adjustCentroids(){
        double iterBiggestAdjustment = 0;
        this.lastBiggestAdjustment = 0;

        for(int i = 0; i < this.clusters.length; i++){
            Cluster cluster = this.clusters[i];
            if(cluster.size() == 0) continue;

            Point old = this.centroids[i];
            Point fresh = this.getNewCentroid(cluster);


            //System.out.println("Centroid " + centroids[i].id + " moved by " + getDistance(old, fresh));

            double change = this.getDistance(old, fresh);

            if(change > iterBiggestAdjustment) iterBiggestAdjustment = change;

            this.centroids[i].moveTo(fresh);
        }

        this.lastBiggestAdjustment = iterBiggestAdjustment;
    }


    protected Point getNewCentroid(Cluster cluster) {
        return cluster.geometricalMiddle();
    }

    /**
     * For given point the method returns closest Centroid
     *
     * @param point
     * @return int index of the closest centroid to given point
     */
    protected int getClosestCentroid(Point point){

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


    /**
     * Returns distance between point a and point b. The distance method has to be
     * set in an advance via {@link #setDistanceMethod(int)} (int) setDistanceMethod}
     *
     * @param a
     * @param b
     * @return double distance between a and b
     */
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


    /**
     * Assign point to a cluster
     *
     * @param point to be assigned
     * @return Cluster cluster that the point had been assigned to
     */
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
