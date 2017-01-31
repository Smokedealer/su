package utils;

import structures.Cluster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ClusteringCanvas extends JPanel {
    private BufferedImage temp;
    private Cluster[] clusters;

    public ClusteringCanvas() {
        //image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        //setPreferredSize(new Dimension(1280, 720));
    }

    public void setData(Cluster[] clusters) {
        this.clusters = clusters;
        this.temp = null;
    }

    public BufferedImage renderImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.setPreferredSize(new Dimension(width, height));

        Graphics2D g2 = image.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, image.getWidth(), image.getHeight());

        if (this.clusters == null) return image;

        double[] globalBounds = new double[4];

        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Cluster cluster : this.clusters) {
            double[] localBounds = cluster.getBounds();
            if (localBounds == null) return image;

            if (localBounds[0] > maxX) {
                maxX = localBounds[0];
            }
            if (localBounds[1] < minX) {
                minX = localBounds[1];
            }

            if (localBounds[2] > maxY) {
                maxY = localBounds[2];
            }
            if (localBounds[3] < minY) {
                minY = localBounds[3];
            }

            globalBounds[0] = maxX + 1;
            globalBounds[1] = minX - 1;
            globalBounds[2] = maxY + 1;
            globalBounds[3] = minY - 1;
        }


        double scaleX = image.getWidth() / (globalBounds[0] - globalBounds[1]);
        double scaleY = image.getHeight() / (globalBounds[2] - globalBounds[3]);

        this.drawGuideLines(image, g2, globalBounds, scaleX, scaleY);
        this.drawClusters(image, g2, this.clusters, globalBounds, scaleX, scaleY);

        return image;
    }

    private void drawGuideLines(BufferedImage image, Graphics2D g2, double[] globalBounds, double scaleX, double scaleY) {
//        g2.setColor(Color.LIGHT_GRAY);
        g2.setColor(Color.BLACK);

        int x = (int) ((0 - globalBounds[1]) * scaleX);
        int y = image.getHeight() - (int) ((0 - globalBounds[3]) * scaleY);

        g2.drawLine(0, y, image.getWidth(), y);
        g2.drawLine(x, 0, x, image.getHeight());

        g2.drawString("0", x + 3, y - 3);

        int fractionCount = 5;
        DecimalFormat df = new DecimalFormat("#0.00");

        for (int i = 0; i < fractionCount; i++){
            int xFraction = image.getWidth() / fractionCount;
            int yFraction = image.getHeight() / fractionCount;

            double xValue = -((i*((globalBounds[1] - globalBounds[0])/fractionCount))-globalBounds[1]);
            double yValue = -((i*((globalBounds[3] - globalBounds[2])/fractionCount))-globalBounds[3]);


            g2.drawLine(i * xFraction, y - 3, i * xFraction, y + 3);
            g2.drawString(df.format(xValue), i * xFraction - 3, y + 16);


            g2.drawLine(x - 3, i * yFraction, x + 3, i * yFraction);
            g2.drawString(df.format(yValue), x + 16, yFraction * (fractionCount - i));
        }
    }

    private void drawClusters(BufferedImage image, Graphics2D g2, Cluster[] clusters, double[] globalBounds, double scaleX, double scaleY) {
        int i = 0;

        for (Cluster cluster : clusters) {

            Color colorFill, colorDraw;

            if (cluster.isShadowCluster()) {
                colorFill = Color.GRAY;
                colorDraw = null;
            } else {
                colorFill = Color.getHSBColor(i * (1f / clusters.length), 1f, 0.88f);
                colorDraw = Color.getHSBColor(i * (1f / clusters.length), 1f, 0.6f);
                i++;
            }

            for (structures.Point point : cluster) {
                int x = (int) ((point.getCoordinates()[0] - globalBounds[1]) * scaleX);
                int y;

                if (point.getDimension() > 1) {
                    y = image.getHeight() - (int) ((point.getCoordinates()[1] - globalBounds[3]) * scaleY);
                } else {
                    y = image.getHeight() / 2;
                }

                g2.setColor(colorFill);
                g2.fillRect(x - 1, y - 1, 3, 3);


                if (colorDraw == null)
                    continue;

                /*
                // Underpainting (resource heavy)
                Color color = Color.getHSBColor(i * (1f / clusters.length), 1f, 0.85f);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 5));
                g2.fillOval(x-15, y-15 , 30, 30);
                */

                // outline
                g2.setColor(colorDraw);
                g2.drawRect(x - 2, y - 2, 4, 4);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (this.temp == null || this.temp.getWidth() != this.getWidth() || this.temp.getHeight() != this.getHeight()) {
            this.temp = this.renderImage(this.getWidth(), this.getHeight());
        }

        g2.drawImage(this.temp, 0, 0, null);
    }
}
