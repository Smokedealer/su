package dbscan;

import structures.Cluster;
import structures.ICluster;
import structures.Point;

import java.util.ArrayList;

/**
 * Created by Matěj Kareš on 17.01.2017.
 */
public class DBScan implements ICluster {

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
    private ArrayList<Cluster> clusters;

    /** Fail safe for ending the iterations **/
    private int maxIterations;



    @Override
    public Cluster[] doClustering(Point[] data, int clusterCount, int nCount) {

        //Wrong data
        if(data == null || data.length == 0) return null;

        System.out.println("Starting DBScan.");

        this.data = data;
        //clusterCount is not used, DBScan determines cluster count by itself
        clusters = new ArrayList<Cluster>();

        start();

        Cluster[] results = new Cluster[clusters.size()];
        results = clusters.toArray(results);

        return results;
    }


    private void start(){
        for(Point p : data){
            if(p.isVisited()) continue;

            p.setVisited(true);

            ArrayList<Point> neighbours = getNeighbours(p, maxDistance);

            if(neighbours.size() < minPoints){
                p.setOutlier(true);
            }
            else {
                Cluster cluster = new Cluster();
                clusters.add(cluster);
                expandCluster(p, neighbours, cluster);
            }

        }
    }

    private void expandCluster(Point point, ArrayList<Point> neighbours, Cluster cluster){
        cluster.add(point);

        boolean done = false;

        while (!done){
            for(Point other : neighbours){
                done=true;

                if (!other.isVisited()){
                    other.setVisited(true);

                    ArrayList<Point> neighboursNeighbours = getNeighbours(other, maxDistance);

                    if(neighboursNeighbours.size() >= minPoints){
                        neighbours.addAll(neighboursNeighbours);
                        done = false;
                        break;
                    }
                }

                if (!isInCluster(other)){
                    cluster.add(other);
                }
            }
        }
    }

    private boolean isInCluster(Point p){
        if(clusters.isEmpty())  return false;

        for(Cluster c : clusters){
            if(c.contains(p)) return true;
        }

        return false;
    }

    private ArrayList<Point> getNeighbours(Point p, double maxDistance){
        ArrayList<Point> neighbours = new ArrayList<Point>(10);

        for (Point other : data){
            double distance = p.eukleidDistanceTo(other);
            if(Double.compare(distance, maxDistance) < 1){
                neighbours.add(other);
            }
        }

        return neighbours;
    }


    private Point getStartingPoint(){
        int index = 0;

        //Iterate through points and find first unvisited point
        for(; index < data.length; index++){
            Point point = data[index];

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
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
