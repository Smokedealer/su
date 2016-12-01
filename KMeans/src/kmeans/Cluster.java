package kmeans;

import java.util.ArrayList;

/**
 * Created by Matěj Kareš on 01.12.2016.
 */
public class Cluster extends ArrayList<Point> {
    private static int clusterID = 0;

    private int id;

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
            }

            coordinateSum /= size();

            center.setSpecificCoord(i, coordinateSum);
        }

        return center;
    }

    public int getId() {
        return id;
    }
}
