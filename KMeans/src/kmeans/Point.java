package kmeans;

import java.util.Arrays;

/**
 * Created by Matěj Kareš on 30.11.2016.
 */
public class Point {
    static int pointID = 0;

    int id;

    /** Dimension of the point. 1D, 2D, 3D, 4D, etc. */
    private int dimension;

    /** Coordinates of this point in it's dimension. */
    private double[] coordinates;


    public Point(int dimension, double[] coordinates) {
        this.dimension = dimension;
        this.coordinates = coordinates;
        this.id = pointID++;
    }

    public Point(int dimension) {
        this(dimension, new double[dimension]);
    }

    public Point(double[] coordinates) {
        this(coordinates.length, coordinates);
    }

    public Point(Point point) {
        this(point.coordinates);
    }


    /**
     * Determines the Euler distance between this and given point.
     * N-Dimensional algorithm.
     *
     * @param point - target point
     * @return double - distance between said points
     */
    public double eukleidDistanceTo(Point point){
        Point zeroed = new Point(dimension, new double[dimension]);
        double distance = 0;

        //For each dimension we need their distance
        double sumOfPartialDistances = 0;

        for(int i = 0; i < dimension; i++){
            //Transpose to the base (zero)
            zeroed.coordinates[i] = coordinates[i] - point.coordinates[i];

            //Square distance
            sumOfPartialDistances += zeroed.coordinates[i] * zeroed.coordinates[i];
        }

        //Distance is the square root of the sum of partial distances
        distance = Math.sqrt(sumOfPartialDistances);

        return distance;
    }

    /**
     * Determines the Manhattan distance between this and given point.
     * N-Dimensional algorithm.
     *
     * @param point - target point
     * @return double - distance between said points
     */
    public double manhattanDistanceTo(Point point){
        Point zeroed = new Point(dimension, null);

        //We need to sum all the distances through all dimensions.
        double sumOfPartialDistances = 0;

        for(int i = 0; i < dimension; i++){
            sumOfPartialDistances +=  Math.abs(this.coordinates[i] - point.coordinates[i]);
        }

        return sumOfPartialDistances;
    }



    public void moveTo(Point to){
        this.coordinates = to.coordinates;
    }


    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public void setSpecificCoord(int dimension, double value){
        coordinates[dimension] = value;
    }

    @Override
    public String toString() {
        return "Point " + id + " {" + Arrays.toString(coordinates) + '}';
    }

    public boolean equals(Point other) {
        boolean match = true;

        for(int i = 0; i < dimension; i++){
            if(coordinates[i] != other.coordinates[i]){
                return false;
            }
        }

        return match;
    }
}
