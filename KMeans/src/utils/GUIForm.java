package utils;

import structures.Cluster;

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

    private ClusteringCanvas clusteringCanvas;
    private GenerateDataListener generateDataListener;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored) { }
        new GUIForm();

    }


    public GUIForm() {
        this.setTitle("Shluková analýza dat");
        this.add(this.rootPanel);
        this.setMinimumSize(this.rootPanel.getMinimumSize());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.clusteringCanvas = new ClusteringCanvas();
        this.previewPanel.add(this.clusteringCanvas);

        this.cdDimensionSpinner.setValue(2);
        this.cdPointsCountSpinner.setValue(300);
        this.cdPointsSpreadSpinner.setValue(30);
        this.cdClustersCountSpinner.setValue(3);
        this.cdClustersSpreadSpinner.setValue(30);


        // data source
        this.uniformDataPanel.setVisible(false);
        this.fileDataPanel.setVisible(false);

        this.dataComboBox.addActionListener(e -> {
            int sel = this.dataComboBox.getSelectedIndex();

            this.clusteredDataPanel.setVisible(sel == 0);
            this.uniformDataPanel.setVisible(sel == 1);
            this.fileDataPanel.setVisible(sel == 2);
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
            GenerateDataListener gdl = this.generateDataListener;

            switch (this.dataComboBox.getSelectedIndex()) {

                default:
                    gdl.clusteredData(
                            (int) this.cdDimensionSpinner.getValue(),
                            (int) this.cdPointsCountSpinner.getValue(),
                            (int) this.cdPointsSpreadSpinner.getValue(),
                            (int) this.cdClustersCountSpinner.getValue(),
                            (int) this.cdClustersSpreadSpinner.getValue());
                    break;

            }
        });

        processButton.addActionListener(e -> {
            this.generateDataListener.process(this.methodComboBox.getSelectedIndex());


        });
    }

    public void drawData(Cluster[] clusters){
        this.clusteringCanvas.drawData(clusters);
        this.repaint();
    }

    public void setGenerateDataListener(GenerateDataListener generateDataListener) {
        this.generateDataListener = generateDataListener;
    }

}

