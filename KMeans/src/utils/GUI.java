package utils;

import structures.Cluster;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Matěj Kareš on 02.12.2016.
 * @deprecated Not used
 */
public class GUI extends JFrame {

    ClusteringCanvas mainPanel;

    public GUI() throws HeadlessException {
        mainPanel = new ClusteringCanvas();

        add(mainPanel);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public void drawData(Cluster[] clusters){
        mainPanel.drawData(clusters);
    }

    public ClusteringCanvas getMainPanel() {
        return mainPanel;
    }

}