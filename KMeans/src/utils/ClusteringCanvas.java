package utils;

import structures.Cluster;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClusteringCanvas extends JPanel {
    BufferedImage image;

    public ClusteringCanvas() {
        image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(1280,720));
    }

    public void drawData(Cluster[] clusters){
        if(clusters == null) return;

        Graphics2D g2 = image.createGraphics();
        g2.setBackground(new Color(255,255,255,0));
        g2.clearRect(0, 0, image.getWidth(), image.getHeight());

        double [] globalBounds = new double[4];

        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
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


        double scaleX = image.getWidth() / (globalBounds[0] - globalBounds[1]);
        double scaleY = image.getHeight() / (globalBounds[2] - globalBounds[3]);

        drawGuideLines(g2, globalBounds, scaleX, scaleY);
        drawClusters(g2, clusters, globalBounds, scaleX, scaleY);

        try {
            ImageIO.write(image, "png", new File("render.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawGuideLines(Graphics2D g2, double[] globalBounds, double scaleX, double scaleY) {

        g2.setColor(Color.LIGHT_GRAY);

        int x = (int)((0 - globalBounds[1]) * scaleX);
        int y = image.getHeight() - (int)((0 - globalBounds[3]) * scaleY);

        g2.drawLine(0, y, image.getWidth(), y);
        g2.drawLine(x, 0, x, image.getHeight());
    }

    private void drawClusters(Graphics2D g2, Cluster[] clusters, double[] globalBounds, double scaleX, double scaleY){
        int i = 0;

        for(Cluster cluster : clusters){

            //System.out.println("Drawing " + cluster.toString());

            for(structures.Point point : cluster){
                int x = (int)((point.getCoordinates()[0] - globalBounds[1]) * scaleX);
                int y;

                if(point.getDimension() > 1){
                    y = image.getHeight() - (int)((point.getCoordinates()[1] - globalBounds[3]) * scaleY);
                }
                else {
                    y = image.getHeight() / 2;
                }

                if(cluster.isShadowCluster())
                    g2.setColor(Color.GRAY);
                else
                    g2.setColor(Color.getHSBColor(i*(1f/clusters.length), 1f, 0.88f));

                g2.fillRect(x-1, y-1, 3,3);


                if(cluster.isShadowCluster())
                    continue;


                /*
                // Underpainting (resource heavy)
                Color color = Color.getHSBColor(i * (1f / clusters.length), 1f, 0.85f);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 5));
                g2.fillOval(x-15, y-15 , 30, 30);
                */

                // outline
                g2.setColor(Color.getHSBColor(i*(1f/clusters.length), 1f, 0.6f));

                g2.drawRect(x-2, y-2, 4,4);
            }

            i++;
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(image, 1, 1, this.getWidth() - 2, this.getHeight() - 2, null);
    }
}
