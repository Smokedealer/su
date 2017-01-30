package structures;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Matěj Kareš on 30.11.2016.
 */
public class Point implements Serializable {
    private static int pointID = 0;

    private int id;

    /**
     * Dimension of the point. 1D, 2D, 3D, 4D, etc.
     */
    private int dimension;

    /**
     * Coordinates of this point in it's dimension.
     */
    private double[] coordinates;

    private boolean visited;

    private boolean outlier;

    public Point(int dimension, double[] coordinates) {
        this.dimension = dimension;
        this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
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
    public double euclideanDistanceTo(Point point) {
        double zeroedDim; // for each dimension we need their zeroed distance (point acts as base)
        double sumOfPartialDistances = 0;

        for (int i = 0; i < this.dimension; i++) {
            //Transpose to the base (zero)
            zeroedDim = this.coordinates[i] - point.coordinates[i];

            //Square distance
            sumOfPartialDistances += zeroedDim * zeroedDim;
        }

        //Distance is the square root of the sum of partial distances
        return Math.sqrt(sumOfPartialDistances);
    }


    /**
     * Determines the Manhattan distance between this and given point.
     * N-Dimensional algorithm.
     *
     * @param point - target point
     * @return double - distance between said points
     */
    public double manhattanDistanceTo(Point point) {
        //We need to sum all the distances through all dimensions.
        double sumOfPartialDistances = 0;

        for (int i = 0; i < this.dimension; i++) {
            sumOfPartialDistances += Math.abs(this.coordinates[i] - point.coordinates[i]);
        }

        return sumOfPartialDistances;
    }


    public double normalDistribution(Point point, double deviation) {
        double distance = this.euclideanDistanceTo(point);
        return Math.exp(-(distance * distance) / (2 * deviation * deviation));
    }


    public void moveTo(Point to) {
        this.coordinates = to.coordinates;
    }


    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public double getSpecificCoord(int dimension) {
        return this.coordinates[dimension];
    }

    public void setSpecificCoord(int dimension, double value) {
        this.coordinates[dimension] = value;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isOutlier() {
        return this.outlier;
    }

    public void setOutlier(boolean outlier) {
        this.outlier = outlier;
    }

    @Override
    public String toString() {
        return "Point " + this.id + " {" + Arrays.toString(this.coordinates) + '}';
    }

    public boolean equals(Point other) {
        for (int i = 0; i < this.dimension; i++) {
            if (this.coordinates[i] != other.coordinates[i]) {
                return false;
            }
        }

        return true;
    }

}
