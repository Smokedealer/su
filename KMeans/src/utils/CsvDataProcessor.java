package utils;

import structures.Cluster;
import structures.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Matěj Kareš on 30.01.2017.
 */
public class CsvDataProcessor {

    private static final String CSV_SEPARATOR = ";";

    public Cluster readFile(String filename){
        File inputFile = new File(filename);

        Cluster dataArrayList = new Cluster();

        try {
            Scanner scanner = new Scanner(inputFile);
            int dimension = 0;

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                String[] coords = line.split(CSV_SEPARATOR);
                Point point = new Point(coords.length);

                if(coords.length == 0 || !isDouble(coords[0])){
                    continue;
                }

                // find data dimension
                if(dimension == 0) {
                    for (int i = 1; i < coords.length; i++) {
                        if(coords[i].trim().isEmpty() || !isDouble(coords[i])) {
                            dimension = i;
                            break;
                        }
                    }
                }

                // try to parse all points coordinates from line
                boolean coordsOk = (coords.length >= dimension);

                if(coordsOk) {
                    for (int i = 0; i < dimension; i++) {
                        if (coords[i].trim().isEmpty() || !isDouble(coords[i])) {
                            coordsOk = false;
                            break;
                        }

                        Double coord = Double.parseDouble(coords[i]);
                        point.setSpecificCoord(i, coord);
                    }
                }

                if(coordsOk) {
                    dataArrayList.add(point);
                }
                else {
                    System.err.println("Invalid line in input file: " + line);
                }
            }

            scanner.close();

            return dataArrayList;

        } catch (FileNotFoundException e) {
            System.out.println("File \"" + filename + "\" not found.");
            return null;
        } catch (NumberFormatException e){
            System.out.println("Invalid data format.");
            return null;
        }
    }

    public void saveFile(Cluster[] clusters, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);

        int clusterId = 1;

        for (Cluster cluster : clusters) {
            for (Point point : cluster) {
                StringBuilder sb = new StringBuilder();

                for (double c : point.getCoordinates()) {
                    sb.append(c);
                    sb.append(CSV_SEPARATOR);
                }

                sb.append("");
                sb.append(CSV_SEPARATOR);
                sb.append("Cluster ");
                sb.append(clusterId);

                sb.append("\n");
                pw.write(sb.toString());
            }

            clusterId++;
        }

        pw.close();
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
