package utils;

import structures.Cluster;
import structures.Point;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Matěj Kareš on 30.01.2017.
 */
public class InputDataParser {

    File inputFile;

    public Cluster readFile(String filename){
        inputFile = new File(filename);

        Cluster dataArrayList = new Cluster();

        try {
            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                String[] coords = line.split(";");
                Point point = new Point(coords.length);

                if(coords == null || coords.length < 1){
                    continue;
                }

                for(int i = 0; i < coords.length; i++){


                    if(coords[i].isEmpty()) coords[i] = "0";

                    Double coord = Double.parseDouble(coords[i]);

                    point.setSpecificCoord(i, coord);
                }

                dataArrayList.add(point);
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

}
