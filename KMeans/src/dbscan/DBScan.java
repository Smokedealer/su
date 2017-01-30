package dbscan;

import structures.Cluster;
import structures.ClusteringAlg;
import structures.ClusteringAlgConf;
import structures.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matěj Kareš on 17.01.2017.
 */
public class DBScan implements ClusteringAlg {

    /** Minimum points to count as a cluster */
    private int minPoints = 20;

    /** Maximum distance between two points still to be counted as neighbours */
    private double maxDistance = 3.0;

    /** Array of centroids **/
    private Point[] centroids;

    /** Data for clustering **/
    private Point[] data;

    /** Number of clusters to be done **/
    private int clusterCount;

    /** Array of clusters **/
    private List<Cluster> clusters;

    /** Fail safe for ending the iterations **/
    private int maxIterations;



    @Override
    public Cluster[] doClustering(Point[] data, ClusteringAlgConf conf) {
        //Wrong data
        if(data == null || data.length == 0 || !(conf instanceof DBScanConf)) return null;

        DBScanConf dbScanConf = (DBScanConf) conf;
        this.minPoints = dbScanConf.getMinPoints();
        this.maxDistance = dbScanConf.getMaxDistance();

        System.out.println("Starting DBScan.");

        this.data = data;
        //clusterCount is not used, DBScan determines cluster count by itself
        this.clusters = new ArrayList<>();

        this.start();

        this.addOutliersCluster();

        return this.clusters.toArray(new Cluster[0]);
    }


    private void start(){
        for(Point p : this.data){
            if(p.isVisited()) continue;

            p.setVisited(true);

            List<Point> neighbours = this.getNeighbours(p, this.maxDistance);

            if(neighbours.size() < this.minPoints){
                p.setOutlier(true);
            }
            else {
                Cluster cluster = new Cluster();
                this.clusters.add(cluster);
                this.expandCluster(p, neighbours, cluster);
            }
        }
    }

    private void expandCluster(Point point, List<Point> neighbours, Cluster cluster){
        cluster.add(point);

        // check all neighbours (if unvisited then also their neighbours), add to cluster
        for (int i = 0; i < neighbours.size(); i++) {
            Point other = neighbours.get(i);

            if (!other.isVisited()) {
                other.setVisited(true);

                List<Point> neighboursNeighbours = this.getNeighbours(other, this.maxDistance);

                if (neighboursNeighbours.size() >= this.minPoints) {
                    neighbours.addAll(neighboursNeighbours);
                }
            }

            if (!this.isInCluster(other)) {
                cluster.add(other);
            }
        }
    }

    private boolean isInCluster(Point p){
        //if(clusters.isEmpty()) return false; // unnecessary, clusters.add() is called before this

        for(Cluster c : this.clusters){
            if(c.contains(p)) return true;
        }

        return false;
    }

    private List<Point> getNeighbours(Point p, double maxDistance){
        List<Point> neighbours = new ArrayList<>(2048);

        for (Point other : this.data){
            double distance = p.euclideanDistanceTo(other);
            if(Double.compare(distance, maxDistance) < 1){
                neighbours.add(other);
            }
        }

        return neighbours;
    }


    /**
     * Creates a new "shadow" cluster just for outliers.
     */
    private void addOutliersCluster() {
        Cluster outliers = new Cluster();
        outliers.setShadowCluster(true);

        for(Point p : this.data){
            if(!p.isOutlier()) continue;

            outliers.add(p);
        }

        if(!outliers.isEmpty())
            this.clusters.add(outliers);
    }


    private Point getStartingPoint(){
        int index = 0;

        //Iterate through points and find first unvisited point
        for(; index < this.data.length; index++){
            Point point = this.data[index];

            if(!point.isVisited()){
                point.setVisited(true);
                return point;
            }
        }

        return null;
    }



    @Override
    public Cluster assignToCluster(Point point) {
        return null;
    }


    public int getMinPoints() {
        return this.minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public double getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMaxIterations() {
        return this.maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
