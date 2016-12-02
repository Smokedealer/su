package kmeans;


import java.util.Random;

/**
 * Created by kares on 30.11.2016.
 */
public class KMeans implements ICluster{

    int dimension;

    Point[] centroids;

    Point[] data;

    int clusterCount;

    Cluster[] clusters;

    int iterations = 10;



    @Override
    public void doClustering(Point[] data, int clusterCount) {
        //Wrong data
        if(data == null || data.length == 0) return;


        this.dimension = data[0].getDimension();
        this.data = data;
        this.clusterCount = clusterCount;

        centroids = new Point[clusterCount];
        clusters = new Cluster[clusterCount];
    }

    public void start(){
        if(data == null || data.length == 0) return;

        initCentroids();

        for(int i = 0; i < iterations; i++){
            assignCentroids();
            adjustCentroids();
        }

        for(Point centroid : centroids){
            System.out.println(centroid.toString());
        }
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

            System.out.println("Centroid " + centroids[i].id + " moved by " + old.eulerDistanceTo(fresh));

            centroids[i].moveTo(fresh);
        }
    }


    private int getClosestCentroid(Point point){
        double minValue = Double.MAX_VALUE;
        int closestCentroidIndex = 0;

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
