package utils;

import kmeans.*;
import kmeans.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Matěj Kareš on 02.12.2016.
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
}


class ClusteringCanvas extends JPanel{
    BufferedImage image;

    public ClusteringCanvas() {
        image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(1280,720));
    }

    public void drawData(Cluster[] clusters){
        if(clusters == null) return;

        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.BLACK);

        drawGuideLines(g2);


        double [] globalBounds = new double[4];

        double maxX = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;

        for(Cluster cluster : clusters){

            double[] localBounds = cluster.getBounds();
            if(localBounds == null) return;

            if(localBounds[0] > maxX){
                maxX = localBounds[0];
            }

            if(localBounds[1] < minX){
                minX = localBounds[1];
            }

            if(localBounds[2] > maxY){
                maxY = localBounds[2];
            }

            if(localBounds[3] < minY){
                minY = localBounds[3];
            }

            globalBounds[0] = maxX;
            globalBounds[1] = minX;
            globalBounds[2] = maxY;
            globalBounds[3] = minY;
        }

        System.out.println("Global max X: " + globalBounds[0]);
        System.out.println("Global min X: " + globalBounds[1]);
        System.out.println("Global max Y: " + globalBounds[2]);
        System.out.println("Global min Y: " + globalBounds[3]);

        double scaleX = getWidth() / (globalBounds[0] - globalBounds[1]);
        double scaleY = getHeight() / (globalBounds[2] - globalBounds[3]);

        int i = 0;

        for(Cluster cluster : clusters){

            for(Point point : cluster){
                int x = (int)((point.getCoordinates()[0] - globalBounds[1]) * scaleX);

                int y = getHeight() / 2;

                if(point.getDimension() > 1){
                    y = (int)((point.getCoordinates()[1] - globalBounds[3]) * scaleY);
                }

                g2.setColor(Color.getHSBColor(i*(1f/clusters.length), 1f, 0.9f));
                g2.fillRect(x-1, y-1, 3,3);

                g2.setColor(Color.getHSBColor(i*(1f/clusters.length), 1f, 0.6f));
                g2.drawRect(x-2, y-2, 4,4);
            }

            System.out.println("cluster: " + i);
            i++;
        }

        repaint();
    }

    private void drawGuideLines(Graphics2D g2) {
        g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        g2.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(image,0,0,null);
    }
}
