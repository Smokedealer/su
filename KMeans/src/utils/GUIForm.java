package utils;

import dbscan.DBScanConf;
import kmeans.KMeansConf;
import structures.Cluster;
import structures.ClusteringAlgConf;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;


public class GUIForm extends JFrame {
    private JTextField fdFileField;
    private JButton fdOpenButton;
    private JPanel fileDataPanel;
    private JComboBox dataComboBox;
    private JPanel clusteredDataPanel;
    private JSpinner cdDimensionSpinner;
    private JSpinner cdPointsCountSpinner;
    private JSpinner cdPointsSpreadSpinner;
    private JSpinner cdClustersCountSpinner;
    private JSpinner cdClustersSpreadSpinner;
    private JPanel rootPanel;
    private JPanel uniformDataPanel;
    private JSpinner udPointsCountSpinner;
    private JSpinner udPointsSpreadSpinner;
    private JButton generateButton;
    private JPanel previewPanel;
    private JComboBox methodComboBox;
    private JButton processButton;
    private JSpinner udDimensionSpinner;
    private JSpinner kmeansClusterCountSpinner;
    private JPanel kmeansPanel;
    private JButton genprocessButton;
    private JSpinner kmeansAlgRepeatsSpinner;
    private JSpinner kmeansElbowTriggerSpinner;
    private JSpinner dbscanMinPointsSpinner;
    private JSpinner dbscanMaxDistance;
    private JPanel dbscanPanel;

    private ClusteringCanvas clusteringCanvas;
    private utils.GUIController GUIController;


    public GUIForm() {
        this.setupGUI();

        this.setTitle("Shluková analýza dat");
        this.add(this.rootPanel);
        this.setMinimumSize(this.rootPanel.getMinimumSize());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setupGUI() {

        // spinners
        this.cdDimensionSpinner.setModel(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        this.cdPointsCountSpinner.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
        this.cdPointsSpreadSpinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
        this.cdClustersCountSpinner.setModel(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        this.cdClustersSpreadSpinner.setModel(new SpinnerNumberModel(50, 1, Integer.MAX_VALUE, 1));

        this.udDimensionSpinner.setModel(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        this.udPointsCountSpinner.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
        this.udPointsSpreadSpinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));

        this.kmeansClusterCountSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        this.kmeansAlgRepeatsSpinner.setModel(new SpinnerNumberModel(20, 1, 1000, 1));
        this.kmeansElbowTriggerSpinner.setModel(new SpinnerNumberModel(0.15d, 0.01d, 1d, 0.05d));

        this.dbscanMinPointsSpinner.setModel(new SpinnerNumberModel(20, 1, Integer.MAX_VALUE, 1));
        this.dbscanMaxDistance.setModel(new SpinnerNumberModel(3d, 0.001d, (double) Integer.MAX_VALUE, 0.05d));

        // canvas
        this.clusteringCanvas = new ClusteringCanvas();
        this.previewPanel.add(this.clusteringCanvas);


        // data source panels
        this.uniformDataPanel.setVisible(false);
        this.fileDataPanel.setVisible(false);

        this.dataComboBox.addActionListener(e -> {
            int sel = this.dataComboBox.getSelectedIndex();

            this.clusteredDataPanel.setVisible(sel == 0);
            this.uniformDataPanel.setVisible(sel == 1);
            this.fileDataPanel.setVisible(sel == 2);
        });

        // method panels
        this.dbscanPanel.setVisible(false);

        this.methodComboBox.addActionListener(e -> {
            int sel = this.methodComboBox.getSelectedIndex();

            this.kmeansPanel.setVisible(sel == 0);
            this.dbscanPanel.setVisible(sel == 1);
        });


        // open file chooser
        final JFileChooser fc = new JFileChooser();

        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String path = file.getAbsolutePath().toLowerCase();
                return file.isDirectory() || path.endsWith(".txt") || path.endsWith(".png") || path.endsWith(".bmp");
            }

            @Override
            public String getDescription() {
                return "Images (*.txt;*.png;*.bmp)";
            }
        });


        this.fdOpenButton.addActionListener(e -> {
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            this.fdFileField.setText(fc.getSelectedFile().getAbsolutePath());
        });


        this.generateButton.addActionListener(e -> {
            utils.GUIController gdl = this.GUIController;

            switch (this.dataComboBox.getSelectedIndex()) {

                case 0:
                default:
                    gdl.generateClusteredData(
                            (int) this.cdDimensionSpinner.getValue(),
                            (int) this.cdPointsCountSpinner.getValue(),
                            (int) this.cdPointsSpreadSpinner.getValue(),
                            (int) this.cdClustersCountSpinner.getValue(),
                            (int) this.cdClustersSpreadSpinner.getValue());
                    break;

                case 1:
                    gdl.generateUniformData(
                            (int) this.udDimensionSpinner.getValue(),
                            (int) this.udPointsCountSpinner.getValue(),
                            (int) this.udPointsSpreadSpinner.getValue());
                    break;

                case 2:
                    gdl.loadFileData(this.fdFileField.getText());
                    break;

            }
        });

        processButton.addActionListener(e -> {
            int method = this.methodComboBox.getSelectedIndex();
            ClusteringAlgConf conf;

            switch(method) {

                default:
                case 0:
                    KMeansConf kMeansConf = new KMeansConf();
                    kMeansConf.setClusterCount((int) this.kmeansClusterCountSpinner.getValue());
                    kMeansConf.setAlgRepeats((int) this.kmeansAlgRepeatsSpinner.getValue());
                    kMeansConf.setElbowTriggerValue((double) this.kmeansElbowTriggerSpinner.getValue());
                    conf = kMeansConf;
                    break;

                case 1:
                    DBScanConf dbScanConf = new DBScanConf();
                    dbScanConf.setMinPoints((int) this.dbscanMinPointsSpinner.getValue());
                    dbScanConf.setMaxDistance((double) this.dbscanMaxDistance.getValue());
                    conf = dbScanConf;
                    break;

            }

            this.GUIController.doClustering(method, conf);
        });

        genprocessButton.addActionListener(e -> {
            this.generateButton.doClick();
            this.processButton.doClick();
        });
    }

    public void drawData(Cluster[] clusters){
        this.clusteringCanvas.drawData(clusters);
        this.repaint();
    }

    public void setGUIController(utils.GUIController GUIController) {
        this.GUIController = GUIController;
    }

}

