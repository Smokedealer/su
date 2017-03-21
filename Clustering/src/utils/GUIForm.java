package utils;

import dbscan.DBScanConf;
import kmeans.KMeansConf;
import structures.Cluster;
import structures.ClusteringAlgConf;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
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
    private JPanel controlPanel;
    private JSplitPane splitPanel;

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

        try {
            this.splitPanel.setBorder(BorderFactory.createEmptyBorder());
            ((JPanel) this.splitPanel.getLeftComponent()).setBorder(BorderFactory.createEmptyBorder());
            ((BasicSplitPaneUI) this.splitPanel.getUI()).getDivider().setBorder(null);
        } catch (Exception ignored) {
            System.err.println("Can't remove unnecessary panel borders.");
        }

        this.splitPanel.setDividerLocation(0.5);

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

            switch (method) {

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

    public void drawData(Cluster[] clusters) {
        this.clusteringCanvas.setData(clusters);
        this.repaint();
    }

    public void setGUIController(utils.GUIController GUIController) {
        this.GUIController = GUIController;
    }

    public void setControlPanelVisible(boolean visible) {
        this.controlPanel.setVisible(visible);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }




    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(4, 4, 4, 4), 0, -1));
        rootPanel.setMinimumSize(new Dimension(700, 650));
        rootPanel.setPreferredSize(new Dimension(1000, 650));
        splitPanel = new JSplitPane();
        splitPanel.setDividerLocation(300);
        rootPanel.add(splitPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        controlPanel = new JPanel();
        controlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPanel.setLeftComponent(controlPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(4, 4, 4, 4), -1, -1));
        controlPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Zdroj dat"));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dataComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Generátor shlukovaných dat");
        defaultComboBoxModel1.addElement("Generátor uniformních dat");
        defaultComboBoxModel1.addElement("Načíst data ze souboru");
        dataComboBox.setModel(defaultComboBoxModel1);
        panel2.add(dataComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileDataPanel = new JPanel();
        fileDataPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(fileDataPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fdFileField = new JTextField();
        fdFileField.setText("");
        fileDataPanel.add(fdFileField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fdOpenButton = new JButton();
        fdOpenButton.setText("Otevřít");
        fileDataPanel.add(fdOpenButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Soubor:");
        fileDataPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clusteredDataPanel = new JPanel();
        clusteredDataPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(clusteredDataPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Počet dimenzí:");
        clusteredDataPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cdDimensionSpinner = new JSpinner();
        clusteredDataPanel.add(cdDimensionSpinner, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Počet bodů:");
        clusteredDataPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Rozptyl bodů:");
        clusteredDataPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Počet shluků:");
        clusteredDataPanel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Rozptyl shluků:");
        clusteredDataPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cdPointsCountSpinner = new JSpinner();
        clusteredDataPanel.add(cdPointsCountSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cdPointsSpreadSpinner = new JSpinner();
        clusteredDataPanel.add(cdPointsSpreadSpinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cdClustersCountSpinner = new JSpinner();
        clusteredDataPanel.add(cdClustersCountSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cdClustersSpreadSpinner = new JSpinner();
        clusteredDataPanel.add(cdClustersSpreadSpinner, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uniformDataPanel = new JPanel();
        uniformDataPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(uniformDataPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Počet dimenzí:");
        uniformDataPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        udDimensionSpinner = new JSpinner();
        uniformDataPanel.add(udDimensionSpinner, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Počet bodů:");
        uniformDataPanel.add(label8, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Rozptyl bodů:");
        uniformDataPanel.add(label9, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        udPointsCountSpinner = new JSpinner();
        uniformDataPanel.add(udPointsCountSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        udPointsSpreadSpinner = new JSpinner();
        uniformDataPanel.add(udPointsSpreadSpinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generateButton = new JButton();
        generateButton.setText("Vygenerovat");
        panel3.add(generateButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(1, 1, 1, 2), -1, -1));
        controlPanel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        genprocessButton = new JButton();
        genprocessButton.setText("Vygenerovat data + provést shlukování");
        panel4.add(genprocessButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(4, 4, 4, 4), -1, -1));
        controlPanel.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Metoda"));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        methodComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("K-Means");
        defaultComboBoxModel2.addElement("DBScan");
        defaultComboBoxModel2.addElement("K-Medians");
        methodComboBox.setModel(defaultComboBoxModel2);
        panel6.add(methodComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kmeansPanel = new JPanel();
        kmeansPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(kmeansPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Počet shluků:");
        label10.setToolTipText("0 = pokusit se automaticky nalézt počet shluků pomocí \"elbow\" metody");
        kmeansPanel.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kmeansClusterCountSpinner = new JSpinner();
        kmeansClusterCountSpinner.setToolTipText("0 = pokusit se automaticky nalézt počet shluků pomocí \"elbow\" metody");
        kmeansPanel.add(kmeansClusterCountSpinner, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Počet opakování:");
        label11.setToolTipText("Počet opakování, při kterých se volí náhodné počáteční body. Následně se vybere iterace s nejlepším výsledkem.");
        kmeansPanel.add(label11, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kmeansAlgRepeatsSpinner = new JSpinner();
        kmeansAlgRepeatsSpinner.setToolTipText("Počet opakování, při kterých se volí náhodné počáteční body. Následně se vybere iterace s nejlepším výsledkem.");
        kmeansPanel.add(kmeansAlgRepeatsSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Elbow, práh rozdílu:");
        label12.setToolTipText("Pokud je rozdíl ohodnocení dvou po sobě jdoucích iterací menší než prahová hodnota, je první z nich označen jako zlom (elbow) a použit pro výstup. ");
        kmeansPanel.add(label12, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kmeansElbowAbsThresholdSpinner = new JSpinner();
        kmeansElbowAbsThresholdSpinner.setToolTipText("Pokud je rozdíl ohodnocení dvou po sobě jdoucích iterací menší než prahová hodnota, je první z nich označen jako zlom (elbow) a použit pro výstup. ");
        kmeansPanel.add(kmeansElbowAbsThresholdSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Elbow, práh zpomalení:");
        label13.setToolTipText("Pokud je podíl ohodnocení dvou po sobě jdoucích iterací (neboli zpomalení) menší než prahová hodnota, je první z nich označen jako zlom (elbow) a použit pro výstup. ");
        kmeansPanel.add(label13, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kmeansElbowDroprateThresholdSpinner = new JSpinner();
        kmeansElbowDroprateThresholdSpinner.setToolTipText("Pokud je podíl ohodnocení dvou po sobě jdoucích iterací (neboli zpomalení) menší než prahová hodnota, je první z nich označen jako zlom (elbow) a použit pro výstup. ");
        kmeansPanel.add(kmeansElbowDroprateThresholdSpinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        processButton = new JButton();
        processButton.setText("Provést shlukování");
        panel7.add(processButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbscanPanel = new JPanel();
        dbscanPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        dbscanPanel.setEnabled(true);
        dbscanPanel.setVisible(true);
        panel5.add(dbscanPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Min. počet bodů ve shluku:");
        label14.setToolTipText("0 = pokusit se automaticky nalézt počet shluků pomocí \"elbow\" metody");
        dbscanPanel.add(label14, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbscanMinPointsSpinner = new JSpinner();
        dbscanMinPointsSpinner.setToolTipText("0 = pokusit se automaticky nalézt počet shluků pomocí \"elbow\" metody");
        dbscanPanel.add(dbscanMinPointsSpinner, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Max. vzdálenost bodů");
        label15.setToolTipText("Počet opakování, při kterých se volí náhodné počáteční body. Následně se vybere iterace s nejlepším výsledkem.");
        dbscanPanel.add(label15, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbscanMaxDistance = new JSpinner();
        dbscanMaxDistance.setToolTipText("Počet opakování, při kterých se volí náhodné počáteční body. Následně se vybere iterace s nejlepším výsledkem.");
        dbscanPanel.add(dbscanMaxDistance, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        controlPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(1, 1, 1, 2), -1, -1));
        controlPanel.add(panel8, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        exportgraphButton = new JButton();
        exportgraphButton.setText("Exportovat graf");
        panel8.add(exportgraphButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel8.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        exportdataButton = new JButton();
        exportdataButton.setText("Exportovat data");
        panel8.add(exportdataButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout(0, 0));
        previewPanel.setBackground(new Color(-1));
        splitPanel.setRightComponent(previewPanel);
        previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}

