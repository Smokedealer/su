package kmeans;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Matěj Kareš on 01.12.2016.
 */
public class Cluster extends ArrayList<Point> {
    private static int clusterID = 0;

    private int id;

    Point centroid;

    public Cluster(){
        super();
        id = clusterID++;
    }

    public Point geometricalMiddle(){
        if(isEmpty()) return null;

        int dimension = get(0).getDimension();

        Point center = new Point(dimension);

        for(int i = 0; i < dimension; i++){
            double coordinateSum = 0;

            for(Point p : this){
                coordinateSum += p.getCoordinates()[i];
            }

            coordinateSum /= size();

            center.setSpecificCoord(i, coordinateSum);
        }

        return center;
    }

    public int getId() {
        return id;
    }


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


    public double average(){
        double average = 0;
        int count = 0;

        for(Point p : this){
            average += centroid.eukleidDistanceTo(p);
            count++;
        }

        average /= count;

        return average;
    }
}
