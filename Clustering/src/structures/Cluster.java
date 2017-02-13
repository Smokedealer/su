package structures;
import java.util.Arrays;
import java.util.HashSet;


/**
 * Class representing a cluster - set of points with similar properties.
 */
public class Cluster extends HashSet<Point> {

    /** Counter for numbering. **/
    private static int clusterID = 0;

    /** ID of cluster. **/
    private int id;

    /** Centroid of cluster. **/
    private Point centroid;

    /** Cluster is "shadow" = not so relevant for data, for example cluster of outliers. **/
    private boolean shadowCluster = false; // shadow clusters = before clustering or outliers (points excluded from all other clusters)

    /**
     * Class representing a cluster - set of points with similar properties.
     */
    public Cluster(){
        super();
        this.id = clusterID++;
    }

    /**
     * Class representing a cluster - set of points with similar properties.
     * @param data Initial points for cluster.
     */
    public Cluster(Point[] data){
        this();
        this.addAll(Arrays.asList(data));
    }

    /**
     * Computes a geometrical middle (mean) of cluster.
     * @return Point.
     */
    public Point geometricalMiddle(){
        if(this.isEmpty()) return null;

        int dimension = this.iterator().next().getDimension();
        Point center = new Point(dimension);

        for(int i = 0; i < dimension; i++){
            double coordinateSum = 0;

            for(Point p : this){
                coordinateSum += p.getSpecificCoord(i);
            }

            coordinateSum /= this.size();

            center.setSpecificCoord(i, coordinateSum);
        }

        return center;
    }

    /**
     * Computers a geometric median of cluster.
     * @return Point.
     */
    public Point geometricMedian(){
        if(this.isEmpty()) return null;

        int dimension = this.iterator().next().getDimension();
        Point median = new Point(dimension);

        for(int i = 0; i < dimension; i++){
            double[] coord = new double[this.size()];
            int j = 0;

            for(Point p : this){
                coord[j++] = p.getSpecificCoord(i);
            }

            Arrays.sort(coord);

            median.setSpecificCoord(i, coord[j / 2]);
        }

        return median;
    }

    /**
     * Gets cluster's ID.
     * @return ID.
     */
    public int getId() {
        return this.id;
    }


    /**
     * Gets data bounding rectangle (ie. limited to 2D).
     * @return
     */
    public double[] getBounds(){
        double[] bounds = new double[4];
        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for(Point p : this){
            double[] coords = p.getCoordinates();

            if(coords[0] > maxX){
                maxX = coords[0];
            }
            if(coords[0] < minX){
                minX = coords[0];
            }

            if(p.getDimension() < 2) {
                maxY = 0;
                minY = 0;
                continue;
            }

            if(coords[1] > maxY){
                maxY = coords[1];
            }
            if(coords[1] < minY){
                minY = coords[1];
            }
        }

        bounds[0] = maxX;
        bounds[1] = minX;
        bounds[2] = maxY;
        bounds[3] = minY;

        return bounds;
    }

    /**
     * Computes an average distance from point to centroid.
     * @return Average distance from point to centroid
     */
    public double average(){
        double average = 0;
        int count = 0;

        for(Point p : this){
            average += Math.pow(this.centroid.euclideanDistanceTo(p), 2d);
            count++;
        }

        average /= count;

        return average;
    }

    /**
     * Computes a median distance from point to centroid.
     * @return Median distance from point to centroid
     */
    public double median(){
        double[] distances = new double[this.size()];
        int i = 0;

        for(Point p : this){
            distances[i++] = this.centroid.euclideanDistanceTo(p);
        }

        Arrays.sort(distances);

        return distances[i / 2];
    }

    /**
     * Sets new centroid for cluster.
     * @param centroid Point representing centroid.
     */
    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    /**
     * Gets centroid of cluster.
     * @return Point representing centroid.
     */
    public Point getCentroid() {
        return this.centroid;
    }

    /**
     * Checks whether is cluster marked as shadow.
     * @return Result.
     */
    public boolean isShadowCluster() {
        return this.shadowCluster;
    }

    /**
     * Sets/unsets a cluster "shadow" mark.
     * @param shadowCluster True if cluster should be marked as shadow.
     */
    public void setShadowCluster(boolean shadowCluster) {
        this.shadowCluster = shadowCluster;
    }


    @Override
    public String toString() {
        return "Cluster{" +
                "id=" + this.id +
                ", centroid=" + this.centroid +
                '}';
    }

}
