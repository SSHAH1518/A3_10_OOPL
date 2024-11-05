/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oopl_project_1;

import java.util.Scanner;
import java.util.Vector;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;  
import java.awt.Dimension;
import javax.swing.JFrame;  
import javax.swing.SwingUtilities;  
import javax.swing.WindowConstants;  

import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;  
import org.jfree.data.xy.XYDataset;  
import org.jfree.data.xy.XYSeries;  
import org.jfree.data.xy.XYSeriesCollection;  
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


/**
 *
 * @author somin
 */

class tableAction{
    
    public DefaultTableModel d;
    public String[] c;
    
    public String getFileName(){
        String fileName = "Nothing";
        JFileChooser fileChooser = new JFileChooser(new File("."));
        int result = fileChooser.showOpenDialog(fileChooser);
        
        try{
            if(result == JFileChooser.APPROVE_OPTION){
                File selectedFile = fileChooser.getSelectedFile();
                fileName = selectedFile.getAbsolutePath();
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "File Not Selected");
        }
        
        return fileName;
    }
    
    public DefaultTableModel getDataTable(){
        DefaultTableModel data = new DefaultTableModel();
 
        try{
            File csv_data = new File(getFileName());
            Scanner reader = new Scanner(csv_data);
            int count = 0;
            while(reader.hasNextLine()){
                if(count == 0){
                    String line = reader.nextLine();
                    String columns[] = line.split(",");
                    this.c = columns;
                    for(String column : columns){
                        data.addColumn(column);
                    }
                    count++;
                }else{
                    String line = reader.nextLine();
                    String values[] = line.split(",");
                    Vector row = new Vector();
                    for(String value : values){
                        row.add(value);
                    }
                    data.addRow(row);
                }
            }
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "File Not Found");
        }
        
        this.d = data;
        return data;
    }
}

class graphAction{
    DefaultTableModel d;
    int colx;
    int coly;
    public double[][] data;
    graphAction(DefaultTableModel data, int x, int y){
        this.d = data;
        this.colx = x;
        this.coly = y;
        this.data = selectColumns();
    }
    
    public double[][] selectColumns(){
        int rowCount = d.getRowCount();
        double[][] data = new double[rowCount][2];
        
        for(int i = 0; i < rowCount; i++){
            data[i][0] = Double.parseDouble((String)this.d.getValueAt(i, this.colx));
            data[i][1] = Double.parseDouble((String)this.d.getValueAt(i, this.coly));
        }
        
        return data;
    }
    
    
    
}

class plotter{
    
    
    void plotNormal(JComboBox xaxis_combo, JComboBox yaxis_combo, JPanel graph, tableAction t){
        graphAction ga = new graphAction(t.d, xaxis_combo.getSelectedIndex(), yaxis_combo.getSelectedIndex());
        double[][] intdata = ga.selectColumns();

        XYSeriesCollection data = new XYSeriesCollection();  
        XYSeries series = new XYSeries("PLOT");

        for (int i = 0; i < intdata.length; i++) {
            series.add(intdata[i][0], intdata[i][1]);
        }

        data.addSeries(series);
        XYDataset dataset = data;

        String xtitle = xaxis_combo.getSelectedItem().toString();
        String ytitle = yaxis_combo.getSelectedItem().toString();

        // Create the chart
        JFreeChart chart = ChartFactory.createScatterPlot(xtitle + " vs " + ytitle, xtitle, ytitle, dataset, PlotOrientation.VERTICAL, false, true, false);

        // Create ChartPanel to hold the chart
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 400));

        // Remove old content from the 'graph' panel if necessary
        graph.removeAll();

        // Set proper layout to 'graph' panel
        graph.setLayout(new BorderLayout());
        graph.add(panel, BorderLayout.CENTER); // Add chart panel to 'graph'

        // Revalidate and repaint to refresh the 'graph' panel
        graph.revalidate();
        graph.repaint();
        
    }
    
    void plotRegression(JComboBox xaxis_combo, JComboBox yaxis_combo, JPanel graph, tableAction t){
        graphAction ga = new graphAction(t.d, xaxis_combo.getSelectedIndex(), yaxis_combo.getSelectedIndex());
        double[][] intdata = ga.selectColumns();
        XYSeriesCollection data = new XYSeriesCollection();
        XYSeries series = new XYSeries("PLOT");

        // Populate scatter plot series
        for (int i = 0; i < intdata.length; i++) {
            series.add(intdata[i][0], intdata[i][1]);
        }
        data.addSeries(series);

        // Calculate linear regression parameters
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = intdata.length;
        for (int i = 0; i < n; i++) {
            sumX += intdata[i][0];
            sumY += intdata[i][1];
            sumXY += intdata[i][0] * intdata[i][1];
            sumXX += intdata[i][0] * intdata[i][0];
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // Create regression line series using min and max x values
        XYSeries regressionLine = new XYSeries("Regression Line");
        double xMin = intdata[0][0];
        double xMax = intdata[0][0];
        for (int i = 1; i < n; i++) {
            if (intdata[i][0] < xMin) xMin = intdata[i][0];
            if (intdata[i][0] > xMax) xMax = intdata[i][0];
        }
        regressionLine.add(xMin, slope * xMin + intercept);
        regressionLine.add(xMax, slope * xMax + intercept);
        data.addSeries(regressionLine);

        // Create the dataset
        XYDataset dataset = data;

        // Chart labels
        String xtitle = xaxis_combo.getSelectedItem().toString();
        String ytitle = yaxis_combo.getSelectedItem().toString();

        // Create the scatter plot chart
        JFreeChart chart = ChartFactory.createScatterPlot(xtitle + " vs " + ytitle, xtitle, ytitle, dataset, PlotOrientation.VERTICAL, false, true, false);

        // Customize the chart to show regression line as a line and scatter plot as points
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false); // Scatter points only for main series
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true); // Line for regression series
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // Create ChartPanel to hold the chart
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 400));

        // Remove old content from the 'graph' panel if necessary
        graph.removeAll();

        // Set layout and add chart panel to 'graph'
        graph.setLayout(new BorderLayout());
        graph.add(panel, BorderLayout.CENTER);

        // Revalidate and repaint to refresh the 'graph' panel
        graph.revalidate();
        graph.repaint();
    }
    
    void plotLogistic(JComboBox xaxis_combo, JComboBox yaxis_combo, JPanel graph, tableAction t){
        graphAction ga = new graphAction(t.d, xaxis_combo.getSelectedIndex(), yaxis_combo.getSelectedIndex());
        double[][] intdata = ga.selectColumns();
        XYSeriesCollection data = new XYSeriesCollection();
        XYSeries series = new XYSeries("PLOT");

        // Populate scatter plot series
        for (int i = 0; i < intdata.length; i++) {
            series.add(intdata[i][0], intdata[i][1]);
        }
        data.addSeries(series);

        // Perform gradient descent to calculate beta0 and beta1
        double beta0 = 0.0;
        double beta1 = 0.0;
        double learningRate = 0.01;
        int iterations = 10000;

        for (int iter = 0; iter < iterations; iter++) {
            double gradientBeta0 = 0;
            double gradientBeta1 = 0;
            for (int i = 0; i < intdata.length; i++) {
                double x = intdata[i][0];
                double y = intdata[i][1];
                double predicted = 1 / (1 + Math.exp(-(beta0 + beta1 * x))); // Sigmoid function
                gradientBeta0 += (y - predicted);
                gradientBeta1 += (y - predicted) * x;
            }
            // Update beta values
            beta0 += learningRate * gradientBeta0;
            beta1 += learningRate * gradientBeta1;
        }

        // Create logistic regression curve series
        XYSeries logisticRegressionCurve = new XYSeries("Logistic Regression Curve");

        // Determine the x range for plotting
        double xMin = intdata[0][0];
        double xMax = intdata[0][0];
        for (int i = 1; i < intdata.length; i++) {
            if (intdata[i][0] < xMin) xMin = intdata[i][0];
            if (intdata[i][0] > xMax) xMax = intdata[i][0];
        }

        // Generate points for a smooth logistic regression curve
        for (double x = xMin; x <= xMax; x += (xMax - xMin) / 100.0) { // 100 points for smoothness
            double predictedY = 1 / (1 + Math.exp(-(beta0 + beta1 * x)));
            logisticRegressionCurve.add(x, predictedY);
        }
        data.addSeries(logisticRegressionCurve);

        // Create the dataset
        XYDataset dataset = data;

        // Chart labels
        String xtitle = xaxis_combo.getSelectedItem().toString();
        String ytitle = yaxis_combo.getSelectedItem().toString();

        // Create the scatter plot chart
        JFreeChart chart = ChartFactory.createScatterPlot(xtitle + " vs " + ytitle, xtitle, ytitle, dataset, PlotOrientation.VERTICAL, false, true, false);

        // Customize the chart to show logistic regression curve as a line and scatter plot as points
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false); // Scatter points only for main series
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true); // Line for logistic regression series
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // Create ChartPanel to hold the chart
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 400));

        // Remove old content from the 'graph' panel if necessary
        graph.removeAll();

        // Set layout and add chart panel to 'graph'
        graph.setLayout(new BorderLayout());
        graph.add(panel, BorderLayout.CENTER);

        // Revalidate and repaint to refresh the 'graph' panel
        graph.revalidate();
        graph.repaint();
    }
    
}

public class main_page extends javax.swing.JFrame {

    /**
     * Creates new form main_page
     */
    public main_page() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xaxis_combo = new javax.swing.JComboBox<>();
        yaxis_combo = new javax.swing.JComboBox<>();
        table_panel = new javax.swing.JScrollPane();
        jButton2 = new javax.swing.JButton();
        graph = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        regression_combo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Choose CSV");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("X Axis:");

        jLabel2.setText("Y Axis:");

        xaxis_combo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                xaxis_comboItemStateChanged(evt);
            }
        });
        xaxis_combo.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                xaxis_comboMouseDragged(evt);
            }
        });
        xaxis_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xaxis_comboActionPerformed(evt);
            }
        });

        jButton2.setText("Plot");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        graph.setPreferredSize(new java.awt.Dimension(501, 450));

        javax.swing.GroupLayout graphLayout = new javax.swing.GroupLayout(graph);
        graph.setLayout(graphLayout);
        graphLayout.setHorizontalGroup(
            graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 501, Short.MAX_VALUE)
        );
        graphLayout.setVerticalGroup(
            graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );

        jLabel3.setText("Regression: ");

        regression_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Linear Regression", "Logistic Regression" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(table_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton1)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(xaxis_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(yaxis_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(29, 29, 29)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(regression_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)))
                .addGap(18, 18, 18)
                .addComponent(graph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(table_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(xaxis_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(regression_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(yaxis_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(28, 28, 28)
                        .addComponent(jButton2)))
                .addContainerGap(151, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void xaxis_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xaxis_comboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xaxis_comboActionPerformed

    public tableAction t = new tableAction();
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        xaxis_combo.removeAllItems();
        yaxis_combo.removeAllItems();
        
        
        try{
            DefaultTableModel data = t.getDataTable();
            JTable table = new JTable();
            table.setModel(data);
            table_panel.getViewport().add(table);
            String columns[] = t.c;

            for(String column:columns){
                xaxis_combo.addItem(column);
                yaxis_combo.addItem(column);
            }

            table_panel.revalidate();
            table_panel.repaint();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in rendering table");
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void xaxis_comboMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xaxis_comboMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_xaxis_comboMouseDragged

    private void xaxis_comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_xaxis_comboItemStateChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_xaxis_comboItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:graphAction ga = new graphAction(t.d, xaxis_combo.getSelectedIndex(), yaxis_combo.getSelectedIndex());
        plotter plot = new plotter();
        String type = regression_combo.getSelectedItem().toString();
        if(type.equals("None")){
            plot.plotNormal(xaxis_combo, yaxis_combo, graph, t);
        }else if (type.equals("Linear Regression")){
            plot.plotRegression(xaxis_combo, yaxis_combo, graph, t);
        }else if(type.equals("Logistic Regression")){
            plot.plotLogistic(xaxis_combo, yaxis_combo, graph, t);
        }
      
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main_page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main_page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main_page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main_page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main_page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel graph;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox<String> regression_combo;
    private javax.swing.JScrollPane table_panel;
    private javax.swing.JComboBox<String> xaxis_combo;
    private javax.swing.JComboBox<String> yaxis_combo;
    // End of variables declaration//GEN-END:variables
}
