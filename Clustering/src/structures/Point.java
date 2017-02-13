package structures;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a "point" - object with position in euclidean space.
 */
public class Point implements Serializable {

    /** Counter for numbering. **/
    private static int pointID = 0;

    /** ID of point **/
    private int id;

    /** Dimension of the point. 1D, 2D, 3D, 4D, etc. **/
    private int dimension;

    /** Coordinates of this point in it's dimension. **/
    private double[] coordinates;

    /** Mark for visited point. **/
    private boolean visited;

    /** Mark for outlier. **/
    private boolean outlier;

    /**
     * Represents a "point" - object with position in euclidean space.
     * @param dimension Dimension of point.
     * @param coordinates Coordinates of point.
     */
    public Point(int dimension, double[] coordinates) {
        this.dimension = dimension;
        this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
        this.id = pointID++;
    }

    /**
     * Represents a "point" - object with position in euclidean space.
     * @param dimension Dimension of point.
     */
    public Point(int dimension) {
        this(dimension, new double[dimension]);
    }

    /**
     * Represents a "point" - object with position in euclidean space.
     * @param coordinates Coordinates of point.
     */
    public Point(double[] coordinates) {
        this(coordinates.length, coordinates);
    }

    /**
     * Represents a "point" - object with position in euclidean space.
     * Creates a copy of specified point.
     * @param point Point to be copied.
     */
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

    /**
     * Move point to coordinates of another point.
     * @param to Point which determines destination position.
     */
    public void moveTo(Point to) {
        this.coordinates = to.coordinates;
    }

    /**
     * Gets dimension of point.
     * @return Dimension.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Gets coordinates of point.
     * @return Coordinates.
     */
    public double[] getCoordinates() {
        return this.coordinates;
    }

    /**
     * Sets new coordinates of point.
     * @param coordinates Coordinates.
     */
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Gets single coordination of point.
     * @param dimension Which dimension/coordinate should be worked with.
     * @return Value.
     */
    public double getSpecificCoord(int dimension) {
        return this.coordinates[dimension];
    }

    /**
     * Sets single coordination of point.
     * @param dimension Which dimension/coordinate should be worked with.
     * @param value New value.
     */
    public void setSpecificCoord(int dimension, double value) {
        this.coordinates[dimension] = value;
    }

    /**
     * Checks whether is point visited.
     * @return Result.
     */
    public boolean isVisited() {
        return this.visited;
    }

    /**
     * Sets point as visited.
     * @param visited True = visited, otherwise mark as unvisited.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Checks whether is point an outlier.
     * @return Result.
     */
    public boolean isOutlier() {
        return this.outlier;
    }

    /**
     * Sets point as an outlier.
     * @param outlier True = is outlier.
     */
    public void setOutlier(boolean outlier) {
        this.outlier = outlier;
    }

    @Override
    public String toString() {
        return "Point " + this.id + " {" + Arrays.toString(this.coordinates) + '}';
    }

    /**
     * Compares two points based on coordinates.
     * @param other Other point.
     * @return True = equals.
     */
    public boolean equals(Point other) {
        if(this.dimension != other.dimension)
            return false;

        for (int i = 0; i < this.dimension; i++) {
            if (this.coordinates[i] != other.coordinates[i]) {
                return false;
            }
        }

        return true;
    }

}
