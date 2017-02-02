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
    private JSpinner kmeansElbowAbsThresholdSpinner;
    private JSpinner dbscanMinPointsSpinner;
    private JSpinner dbscanMaxDistance;
    private JPanel dbscanPanel;
    private JButton exportdataButton;
    private JButton exportgraphButton;
    private JSpinner kmeansElbowDroprateThresholdSpinner;

    private ClusteringCanvas clusteringCanvas;
    private utils.GUIController GUIController;


    public GUIForm() {
        this.setupGUI();

        this.setTitle("Shluková analýza dat");
        this.add(this.rootPanel);
        this.setMinimumSize(this.rootPanel.getMinimumSize());
        this.setPreferredSize(this.rootPanel.getPreferredSize());
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
        this.kmeansElbowAbsThresholdSpinner.setModel(new SpinnerNumberModel(10d, 0d, Integer.MAX_VALUE, 0.1d));
        ((JSpinner.NumberEditor) this.kmeansElbowAbsThresholdSpinner.getEditor()).getFormat().setMinimumFractionDigits(1);
        this.kmeansElbowDroprateThresholdSpinner.setModel(new SpinnerNumberModel(0.8d, 0, Integer.MAX_VALUE, 0.1d));
        ((JSpinner.NumberEditor) this.kmeansElbowDroprateThresholdSpinner.getEditor()).getFormat().setMinimumFractionDigits(3);


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

            this.kmeansPanel.setVisible(sel == 0 || sel == 2);
            this.dbscanPanel.setVisible(sel == 1);
        });


        // open file chooser
        JFileChooser openCsvFileChooser = new JFileChooser();
        openCsvFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String path = file.getAbsolutePath().toLowerCase();
                return file.isDirectory() || path.endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV files (*.csv)";
            }
        });

        this.fdOpenButton.addActionListener(e -> {
            if (openCsvFileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            this.fdFileField.setText(openCsvFileChooser.getSelectedFile().getAbsolutePath());
        });


        // generate data button
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

        // process data / "do clustering" button
        this.processButton.addActionListener(e -> {
            int method = this.methodComboBox.getSelectedIndex();
            ClusteringAlgConf conf = null;

            switch(method) {

                case 0:
                case 2:
                    KMeansConf kMeansConf = new KMeansConf();
                    kMeansConf.setClusterCount((int) this.kmeansClusterCountSpinner.getValue());
                    kMeansConf.setAlgRepeats((int) this.kmeansAlgRepeatsSpinner.getValue());
                    kMeansConf.setElbowDifferenceAbsThresholdValue((double) this.kmeansElbowAbsThresholdSpinner.getValue());
                    kMeansConf.setElbowDroprateThreshold((double) this.kmeansElbowDroprateThresholdSpinner.getValue());
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

        // generate + process data button
        this.genprocessButton.addActionListener(e -> {
            this.generateButton.doClick();
            this.processButton.doClick();
        });


        // export output graph button
        JFileChooser savePngFileChooser = new JFileChooser();
        savePngFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        savePngFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String path = file.getAbsolutePath().toLowerCase();
                return file.isDirectory() || path.endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "PNG image (*.png)";
            }
        });

        this.exportgraphButton.addActionListener(e -> {
            if (savePngFileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            this.GUIController.exportPNG(savePngFileChooser.getSelectedFile(), this.clusteringCanvas.renderImage(1600, 1600));
        });


        // export CSV button
        JFileChooser saveCsvFileChooser = new JFileChooser();
        saveCsvFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveCsvFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String path = file.getAbsolutePath().toLowerCase();
                return file.isDirectory() || path.endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV file (*.csv)";
            }
        });

        this.exportdataButton.addActionListener(e -> {
            if (saveCsvFileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            this.GUIController.exportCSV(saveCsvFileChooser.getSelectedFile());
        });


        this.kmeansClusterCountSpinner.addChangeListener(e -> {
            boolean elbowing = ((int) this.kmeansClusterCountSpinner.getValue() == 0);

            this.kmeansElbowAbsThresholdSpinner.setEnabled(elbowing);
            this.kmeansElbowDroprateThresholdSpinner.setEnabled(elbowing);
        });
    }

    public void drawData(Cluster[] clusters){
        this.clusteringCanvas.setData(clusters);
        this.repaint();
    }

    public void setGUIController(utils.GUIController GUIController) {
        this.GUIController = GUIController;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}

