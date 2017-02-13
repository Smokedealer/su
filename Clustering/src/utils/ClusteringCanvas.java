package utils;

import structures.Cluster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;


/**
 * Canvas/panel for clusters visualization.
 *
 * @author Vojtěch Kinkor
 */
public class ClusteringCanvas extends JPanel {
    /** Rendered image. **/
    private BufferedImage temp;

    /** Currently shown data. **/
    private Cluster[] clusters;

    /**
     * Canvas/panel for clusters visualization.
     */
    public ClusteringCanvas() {
    }

    /**
     * Sets new data.
     * @param clusters New clusters.
     */
    public void setData(Cluster[] clusters) {
        this.clusters = clusters;
        this.temp = null;
    }

    /**
     * Render data into an image.
     * @param width Desired width.
     * @param height Desired height.
     * @return Image with clusters visualization.
     */
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

    /**
     * Draw guidelines into the image.
     * @param image Dest. image.
     * @param g2 Dest. Graphics2D object.
     * @param globalBounds Data bounds.
     * @param scaleX Width scale (image width/data cover width ratio)
     * @param scaleY Height scale (image height/data cover height ratio)
     */
    private void drawGuideLines(BufferedImage image, Graphics2D g2, double[] globalBounds, double scaleX, double scaleY) {
        g2.setColor(Color.LIGHT_GRAY);
        //g2.setColor(Color.BLACK);

        int x = (int) ((0 - globalBounds[1]) * scaleX);
        int y = image.getHeight() - (int) ((0 - globalBounds[3]) * scaleY);

        g2.drawLine(0, y, image.getWidth(), y);
        g2.drawLine(x, 0, x, image.getHeight());

        // labels + ticks
        g2.drawString("0", x + 5, y + 15);

        int fractionCount = 10;

        // count nearest value for rounding (0.5, 5, 50, ...)
        double yNearest = 0.5 * Math.pow(10, Math.floor(Math.log10((globalBounds[2] - globalBounds[3])*0.5)));
        double xNearest = 0.5 * Math.pow(10, Math.floor(Math.log10((globalBounds[0] - globalBounds[1])*0.5)));

        double yValFraction = Math.ceil((globalBounds[2] - globalBounds[3]) / fractionCount / yNearest) * yNearest;
        double xValFraction = Math.ceil((globalBounds[0] - globalBounds[1]) / fractionCount / xNearest) * xNearest;

        DecimalFormat df = new DecimalFormat(yNearest + xNearest <= 1 ? "#0.0" : "#0");

        // x ticks
        if (xValFraction > 0) {
            double xValue = 0;
            while (true) {
                xValue += xValFraction; // positive values
                int xPos = (int) ((xValue - globalBounds[1]) * scaleX);

                if (xPos > image.getWidth()) break;

                String str = df.format(xValue);
                g2.drawLine(xPos, y - 3, xPos, y + 3);
                g2.drawString(str, xPos - str.length() * 3, y + 18);
            }
            xValue = 0;
            while (true) {
                xValue -= Math.abs(xValFraction); // negative values
                int xPos = (int) ((xValue - globalBounds[1]) * scaleX);

                if (xPos < 0) break;

                String str = df.format(xValue);
                g2.drawLine(xPos, y - 3, xPos, y + 3);
                g2.drawString(str, xPos - str.length() * 3, y + 18);
            }
        }


        // y ticks
        if (yValFraction > 0) {
            double yValue = 0;
            while (true) {
                yValue += yValFraction; // positive values
                int yPos = image.getHeight() - (int) ((yValue - globalBounds[3]) * scaleY);

                if (yPos < 0) break;

                String str = df.format(yValue);
                g2.drawLine(x - 3, yPos, x + 3, yPos);
                g2.drawString(str, x + 8, yPos + 4);
            }
            yValue = 0;
            while (true) {
                yValue -= Math.abs(yValFraction); // negative values
                int yPos = image.getHeight() - (int) ((yValue - globalBounds[3]) * scaleY);

                if (yPos > image.getHeight()) break;

                String str = df.format(yValue);
                g2.drawLine(x - 3, yPos, x + 3, yPos);
                g2.drawString(str, x + 8, yPos + 4);
            }
        }
    }

    /**
     * Draw clusters (data) into the image.
     * @param image Dest. image.
     * @param g2 Dest. Graphics2D object.
     * @param clusters Data to be drawn.
     * @param globalBounds Data bounds.
     * @param scaleX Width scale (image width/data cover width ratio)
     * @param scaleY Height scale (image height/data cover height ratio)
     */
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

    /**
     * Repaint of panel.
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (this.temp == null || this.temp.getWidth() != this.getWidth() || this.temp.getHeight() != this.getHeight()) {
            this.temp = this.renderImage(this.getWidth(), this.getHeight());
        }

        g2.drawImage(this.temp, 0, 0, null);
    }
}
