 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import com.formdev.flatlaf.ui.FlatListCellBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Oshadha
 */
public class outlet extends javax.swing.JPanel {
    
  
    
    private Home home;
  private int totalemployeesCount;  
    private int totaldistrubutorsCount;    
    /**
     * Creates new form outlet
     */
    public outlet(Home home) {
        initComponents();
        this.home = home;
        loadBestOutlet();
        loadOutletSales();
        showPieChart();
        
         totalemployeesCount = calculateemployees();
        totaldistrubutorsCount = calculatedistrubutors();
        updateCountsDisplay(totalemployeesCount,totaldistrubutorsCount);
    }
     private int calculateemployees() {
        int count = 0;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM outlet_manager";
            rs = MySQL.executeSearch(query);

            if (rs.next()) {
                count = rs.getInt(1); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

   
     private int calculatedistrubutors() {
        int count = 0;
        ResultSet rs = null;

        try {
            String query = "SELECT COUNT(*) FROM outlet";
            rs = MySQL.executeSearch(query);

            if (rs.next()) {
                count = rs.getInt(1); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    private void updateCountsDisplay(int employeeCount,int distrubutorsCount) {
        jButton6.setText("<html><div style='text-align: center;'><span style='font-size:14px;'><b>Total Outlet Managers: </b></span>"
                      + "<span style='font-size:18px;'>" + employeeCount + "</span><br></br></div></html>");
        jButton5.setText("<html><div style='text-align: center;'><span style='font-size:14px;'><b>Total Outlets: </b></span>"
                      + "<span style='font-size:18px;'>" + distrubutorsCount + "</span><br></br></div></html>");

   }
    private void loadOutletSales() {
        try {
              ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `customer_invoice`"
                + "INNER JOIN `outlet` ON `customer_invoice`.`outlet_id`=`outlet`.`id`"
                + "INNER JOIN `outlet_payment_method` ON `customer_invoice`.`outlet_payment_method_id`=`outlet_payment_method`.`id`"
                + "INNER JOIN `customer` ON `customer_invoice`.`customer_id`=`customer`.`id`");
              
              DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
              
              while (resultSet.next()) {
                  Vector<String> vector = new Vector<>();
                  vector.add(resultSet.getString("id"));
                  vector.add(resultSet.getString("date"));
                  vector.add(resultSet.getString("amount"));
                  vector.add(resultSet.getString("discount"));
                  vector.add(resultSet.getString("customer.name"));
                  vector.add(resultSet.getString("outlet_payment_method.type"));
                  vector.add(resultSet.getString("outlet.name"));
                  model.addRow(vector);
                
            }
              
            
        } catch (Exception e) {
        }
      
    }
    
    private void loadBestOutlet(){
        ResultSet rs = null;
        StringBuilder bestoutletContent = new StringBuilder();
        
        try {
            String query = "SELECT * FROM `customer_invoice` "
                    + "INNER JOIN `outlet` ON `customer_invoice`.`outlet_id`=`outlet`.`id`"
                    + "WHERE `customer_invoice`.`amount` = (SELECT MAX(`amount`) FROM `customer_invoice`)";

            rs = MySQL.executeSearch(query);
            
            while (rs.next()) {

            String bestOutletName = rs.getString("outlet.name");
            String bestOutletDate = rs.getString("customer_invoice.date");
            
            bestoutletContent.append("<html><font face='Calibri' size='4'>")
                             .append("<div style='text-align:center;'><b><span style='color:brown;'>")
                             .append(bestOutletName).append("<br>")
                             .append(bestOutletDate).append("<span/></b></div>")
                             .append("<div style='text-align:center;'><span style='color:red;'></html>");
                
            }
            
            jLabel1.setText(bestoutletContent.toString());
      
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //bar chart
    
     public void showPieChart() {
     try {
        
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        
        String query = "SELECT o.name AS outlet_name, SUM(ci.amount) AS total_sales " +
                       "FROM customer_invoice ci " +
                       "INNER JOIN outlet o ON ci.outlet_id = o.id " +
                       "GROUP BY o.name";

        
        ResultSet rs = MySQL.executeSearch(query);

        
        while (rs.next()) {
            String outletName = rs.getString("outlet_name");
            double totalSales = rs.getDouble("total_sales");

            pieDataset.setValue(outletName, totalSales);
        }

        
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Outlet Sales Distribution", 
                pieDataset, 
                true, 
                true, 
                false
        );

        // Customize the pie chart
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setBackgroundPaint(Color.WHITE);

        // Example section coloring (optional)
        piePlot.setSectionPaint("Outlet A", new Color(221, 160, 221));
        piePlot.setSectionPaint("Outlet B", new Color(255, 255, 153));
        piePlot.setSectionPaint("Outlet C", new Color(144, 238, 144));

        // Create ChartPanel and add it to jPanel5
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(700, 400));
        chartPanel.setMouseWheelEnabled(true);

        jPanel2.removeAll();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(chartPanel, BorderLayout.CENTER);
        jPanel2.validate();

        rs.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton17 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 248, 239));
        setPreferredSize(new java.awt.Dimension(888, 650));

        jButton17.setBackground(new java.awt.Color(255, 246, 234));
        jButton17.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton17.setText("Outlet Management System");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jTable1.setBackground(new java.awt.Color(255, 255, 228));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Date", "Amount", "Discount", "Customer", "Payment Method", "Outlet Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        jLabel2.setText("Sales Table");

        jButton4.setBackground(new java.awt.Color(245, 219, 200));
        jButton4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton4.setText("Outlet Registration");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-close-24.png"))); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(245, 219, 200));
        jButton1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton1.setText("Product Registration");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(245, 219, 200));
        jButton2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton2.setText("Category Registration");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 242, 222));

        jButton5.setBackground(new java.awt.Color(231, 255, 255));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 51, 102));
        jButton5.setText("Total Outlets");

        jButton6.setBackground(new java.awt.Color(229, 229, 255));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(102, 0, 153));
        jButton6.setText("Outlet Managers");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 337, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/outlet).jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(932, 932, 932)
                        .addComponent(jLabel4))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                            .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        outletRegistration or = new outletRegistration(home, true);
        or.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        HomeOU.getObj().setVisible(true);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        this.home.removeOutlet();
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        productTable pt = new productTable(home, true);
        pt.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        categoryRegistration cr = new categoryRegistration(home, true);
        cr.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
