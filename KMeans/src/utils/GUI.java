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
    VisualisationPanel mainPanel;



    public GUI() throws HeadlessException {
        mainPanel = new VisualisationPanel();

        add(mainPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public void drawData(Cluster[] clusters){
        mainPanel.drawData(clusters);
    }
}


class VisualisationPanel extends JPanel{
    BufferedImage image;

    public VisualisationPanel() {
        image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(1280,720));
    }

    public void drawData(Cluster[] clusters){
        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.BLACK);

        drawGuideLines(g2);



        for(Cluster cluster : clusters){

            double[] bounds = cluster.getBounds();

            //TODO: scaling is bugged

            double scaleX = getWidth() / (bounds[0] - bounds[1]);
            System.out.println(scaleX);
            double scaleY = getHeight() / (bounds[2] - bounds[3]);
            System.out.println(scaleY);

            for(Point point : cluster){
                int x = (int)((point.getCoordinates()[0]) * scaleX);
                System.out.println(x);
                int y = (int)((point.getCoordinates()[1]) * scaleY);
                System.out.println(y);

                g2.fillRect(x, y, 5,5);
            }
        }
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
