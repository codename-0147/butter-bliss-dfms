package gui;

import static com.mysql.cj.protocol.x.CompressionMode.MESSAGE;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JasperViewer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class OutletSales extends javax.swing.JPanel {

    private HomeOU home;
    private HashMap<String, String> outletMap = new HashMap<>();

    public OutletSales(HomeOU home) {
        initComponents();
        this.home = home;
        loadOutlet();
        loadPeriods();
        showSalesByProductPieChart(1);
        showDailySalesLineChart(1);
        showTop5ProductsBarChart();
    }

    private void loadOutlet() {
        try {
            Vector<String> vector = new Vector<>();
            vector.add("Select Outlet");

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `outlet`");

            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                outletMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }

            jComboBox3.setModel(new DefaultComboBoxModel<>(vector));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPeriods() {
        Vector<String> periods = new Vector<>();
        periods.add("Daily");
        periods.add("Weekly");
        periods.add("Monthly");

        jComboBox4.setModel(new DefaultComboBoxModel<>(periods));
    }

    private void generateTable() {
        try {
            String outletName = (String) jComboBox3.getSelectedItem();
            String period = (String) jComboBox4.getSelectedItem();

            if (outletName == null || outletName.equals("Select Outlet")) {
                JOptionPane.showMessageDialog(this, "Please select an outlet.");
                return;
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate;

            if (period.equals("Weekly")) {
                startDate = endDate.minusDays(6);
            } else if (period.equals("Monthly")) {
                startDate = endDate.minusDays(29);
            } else {
                startDate = endDate;
            }

            String query = "SELECT customer_invoice.date, product.name, "
                    + "customer_invoice_items.qty, (customer_invoice_items.qty * stock.price) AS amount "
                    + "FROM customer_invoice "
                    + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                    + "INNER JOIN stock ON customer_invoice_items.stock_id = stock.id "
                    + "INNER JOIN product ON stock.product_id = product.id "
                    + "INNER JOIN outlet ON customer_invoice.outlet_id = outlet.id "
                    + "WHERE outlet.name = '" + outletName + "'";

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating table: " + e.getMessage());
        }
    }

    private void showSalesByProductPieChart(int outletId) {
        try {
            String query = "SELECT product.name, SUM(customer_invoice_items.qty * stock.price) AS total_sales "
                    + "FROM customer_invoice "
                    + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                    + "INNER JOIN stock ON customer_invoice_items.stock_id = stock.id "
                    + "INNER JOIN product ON stock.product_id = product.id "
                    + "WHERE customer_invoice.outlet_id = '" + outletId + "' "
                    + "GROUP BY product.id";

            ResultSet rs = MySQL.executeSearch(query);
            DefaultPieDataset dataset = new DefaultPieDataset();

            while (rs.next()) {
                dataset.setValue(rs.getString("name"), rs.getDouble("total_sales"));
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "Sales by Product (Outlet " + outletId + ")", dataset, true, true, false
            );

            chart.setBackgroundPaint(Color.WHITE);

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(new Color(245, 221, 204));
            plot.setOutlineVisible(false);

            ChartPanel panel = new ChartPanel(chart);
            panel.setPreferredSize(new Dimension(jPanel1.getWidth(), jPanel1.getHeight()));
            jPanel1.removeAll();
            jPanel1.setLayout(new BorderLayout());
            jPanel1.add(panel, BorderLayout.CENTER);
            jPanel1.revalidate();
            jPanel1.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDailySalesLineChart(int outletId) {
        try {
            String query = "SELECT DATE(customer_invoice.date) AS sale_date, "
                    + "SUM(customer_invoice_items.qty * stock.price) AS total_sales "
                    + "FROM customer_invoice "
                    + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                    + "INNER JOIN stock ON customer_invoice_items.stock_id = stock.id "
                    + "WHERE customer_invoice.outlet_id = '" + outletId + "' "
                    + "GROUP BY sale_date ORDER BY sale_date ASC";

            ResultSet rs = MySQL.executeSearch(query);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs.next()) {
                String date = rs.getString("sale_date");
                double sales = rs.getDouble("total_sales");
                dataset.addValue(sales, "Sales", date);
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    "Daily Sales", "Date", "Total Sales",
                    dataset, PlotOrientation.VERTICAL, true, true, false
            );

            chart.setBackgroundPaint(Color.WHITE);

            CategoryPlot plot = chart.getCategoryPlot(); 
            plot.setBackgroundPaint(new Color(245, 221, 204)); 
            plot.setRangeGridlinePaint(Color.GRAY);
            plot.setDomainGridlinePaint(Color.GRAY);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(jPanel1.getWidth(), jPanel1.getHeight()));

            jPanel2.removeAll();
            jPanel2.setLayout(new BorderLayout());
            jPanel2.add(chartPanel, BorderLayout.CENTER);
            jPanel2.revalidate();
            jPanel2.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTop5ProductsBarChart() {
        try {
            String query = "SELECT product.name, SUM(customer_invoice_items.qty * stock.price) AS total_sales "
                    + "FROM customer_invoice "
                    + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                    + "INNER JOIN stock ON customer_invoice_items.stock_id = stock.id "
                    + "INNER JOIN product ON stock.product_id = product.id "
                    + "GROUP BY product.id "
                    + "ORDER BY total_sales DESC "
                    + "LIMIT 5";

            ResultSet rs = MySQL.executeSearch(query);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs.next()) {
                String product = rs.getString("name");
                double sales = rs.getDouble("total_sales");
                dataset.addValue(sales, "Sales", product);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Top 5 Products by Sales",
                    "Product",
                    "Total Sales",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(new Color(245, 221, 204));
            plot.setDomainGridlinePaint(Color.GRAY);
            plot.setRangeGridlinePaint(Color.GRAY);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(jPanel1.getWidth(), jPanel1.getHeight()));

            jPanel3.removeAll();
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(chartPanel, BorderLayout.CENTER);
            jPanel3.revalidate();
            jPanel3.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDayChooser1 = new com.toedter.calendar.JDayChooser();
        jDayChooser2 = new com.toedter.calendar.JDayChooser();
        jLabel20 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jButton13 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(930, 655));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(65, 36, 8));
        jLabel20.setText("Sales Overview");
        add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 48, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-close-24.png"))); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(894, 6, -1, -1));

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Outlet");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 99, -1, -1));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(245, 219, 200)));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });
        add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 96, 130, 27));

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setText("Type");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 99, -1, -1));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(245, 219, 200)));
        add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(339, 96, 130, 27));

        jButton13.setBackground(new java.awt.Color(245, 219, 200));
        jButton13.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton13.setText("View");
        jButton13.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 91, 129, -1));

        jButton15.setBackground(new java.awt.Color(245, 219, 200));
        jButton15.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton15.setText("Print");
        jButton15.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(616, 91, 129, -1));

        jButton16.setBackground(new java.awt.Color(245, 219, 200));
        jButton16.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton16.setText("Export");
        jButton16.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(757, 91, 129, -1));

        jButton3.setBackground(new java.awt.Color(245, 219, 200));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Inventory");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 570, 104, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(65, 36, 8));
        jLabel22.setText("Report Overview");
        add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 160, -1));

        jButton2.setBackground(new java.awt.Color(245, 219, 200));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Orders");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 570, 101, -1));

        jButton6.setBackground(new java.awt.Color(245, 219, 200));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setText("Sales");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 570, 110, -1));

        jPanel1.setBackground(new java.awt.Color(245, 221, 204));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 270, 390));

        jPanel2.setBackground(new java.awt.Color(245, 221, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, 310, 390));

        jPanel3.setBackground(new java.awt.Color(245, 221, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 140, 320, 390));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        this.home.removeoutletSales();
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jDateChooser1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDateChooser1MouseClicked

    }//GEN-LAST:event_jDateChooser1MouseClicked

    private void jDateChooser2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDateChooser2MouseClicked


    }//GEN-LAST:event_jDateChooser2MouseClicked

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        try {
            String sort = String.valueOf(jComboBox4.getSelectedItem());
            String date = "";
            ResultSet resultSet = null;
            InputStream filePath = null;

            if (sort.equals("Daily")) {
                date = new SimpleDateFormat("yyyy-MMMM-dd").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE DATE(customer_invoice.date) = CURDATE();");
                filePath = this.getClass().getResourceAsStream("/reports/sales_daily.jasper");

            } else if (sort.equals("Weekly")) {
                date = new SimpleDateFormat("yyyy-MMMM-W").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE WEEK(customer_invoice.date) = WEEK(CURDATE());");
                filePath = this.getClass().getResourceAsStream("/reports/sales_weekly.jasper");

            } else if (sort.equals("Monthly")) {
                date = new SimpleDateFormat("yyyy-MMMM").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE MONTH(customer_invoice.date) = MONTH(CURDATE());");
                filePath = this.getClass().getResourceAsStream("/reports/sales_monthly.jasper");
            }

            if (resultSet.next()) {
                String totalSales = resultSet.getString("COUNT(customer_invoice.id)");
                String totalRevenue = resultSet.getString("SUM(customer_invoice.amount)");
                String totalUnitsSold = resultSet.getString("SUM(customer_invoice_items.qty)");
                String totalDiscounts = resultSet.getString("SUM(customer_invoice.discount)");

                InputStream path = filePath;
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Parameter5", date);
                parameters.put("Parameter1", totalSales);
                parameters.put("Parameter2", totalRevenue);
                parameters.put("Parameter3", totalUnitsSold);
                parameters.put("Parameter4", totalDiscounts);

                JasperPrint report = JasperFillManager.fillReport(path, parameters, MySQL.connection);
                JasperViewer.viewReport(report, false);

            } else {
                JOptionPane.showMessageDialog(this, "No data available for report.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing report: " + e.getMessage());
        }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        try {
            String sort = String.valueOf(jComboBox4.getSelectedItem());
            String date = "";
            ResultSet resultSet = null;
            InputStream path = null;

            if (sort.equals("Daily")) {
                date = new SimpleDateFormat("yyyy-MMMM-dd").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE DATE(customer_invoice.date) = CURDATE();");
                path = this.getClass().getResourceAsStream("/reports/sales_daily.jasper");

            } else if (sort.equals("Weekly")) {
                date = new SimpleDateFormat("yyyy-MMMM-W").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE WEEK(customer_invoice.date) = WEEK(CURDATE());");
                path = this.getClass().getResourceAsStream("/reports/sales_weekly.jasper");

            } else if (sort.equals("Monthly")) {
                date = new SimpleDateFormat("yyyy-MMMM").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE MONTH(customer_invoice.date) = MONTH(CURDATE());");
                path = this.getClass().getResourceAsStream("/reports/sales_monthly.jasper");
            }

            if (resultSet.next()) {
                String totalSales = resultSet.getString("COUNT(customer_invoice.id)");
                String totalRevenue = resultSet.getString("SUM(customer_invoice.amount)");
                String totalUnitsSold = resultSet.getString("SUM(customer_invoice_items.qty)");
                String totalDiscounts = resultSet.getString("SUM(customer_invoice.discount)");

                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Parameter5", date);
                parameters.put("Parameter1", totalSales);
                parameters.put("Parameter2", totalRevenue);
                parameters.put("Parameter3", totalUnitsSold);
                parameters.put("Parameter4", totalDiscounts);

                JasperPrint report = JasperFillManager.fillReport(path, parameters, MySQL.connection);
                JasperPrintManager.printReport(report, false);

            } else {
                JOptionPane.showMessageDialog(this, "Error printing report", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        try {
            String sort = String.valueOf(jComboBox4.getSelectedItem());
            String date = "";
            ResultSet resultSet = null;
            InputStream filePath = null;
            String salesReportsTypeFolder = "";
            String reportFilePath = "";

            if (sort.equals("Daily")) {
                date = new SimpleDateFormat("yyyy-MMMM-dd").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE DATE(customer_invoice.date) = CURDATE();");
                filePath = this.getClass().getResourceAsStream("/reports/sales_daily.jasper");

            } else if (sort.equals("Weekly")) {
                date = new SimpleDateFormat("yyyy-MMMM-W").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE WEEK(customer_invoice.date) = WEEK(CURDATE());");
                filePath = this.getClass().getResourceAsStream("/reports/sales_weekly.jasper");

            } else if (sort.equals("Monthly")) {
                date = new SimpleDateFormat("yyyy-MMMM").format(new Date());
                resultSet = MySQL.executeSearch("SELECT COUNT(customer_invoice.id), SUM(customer_invoice.amount), "
                        + "SUM(customer_invoice_items.qty), SUM(customer_invoice.discount) FROM customer_invoice "
                        + "INNER JOIN customer_invoice_items ON customer_invoice.id = customer_invoice_items.customer_invoice_id "
                        + "WHERE MONTH(customer_invoice.date) = MONTH(CURDATE());");
                filePath = this.getClass().getResourceAsStream("/reports/sales_monthly.jasper");
            }

            if (resultSet.next()) {
                String totalSales = resultSet.getString("COUNT(customer_invoice.id)");
                String totalRevenue = resultSet.getString("SUM(customer_invoice.amount)");
                String totalUnitsSold = resultSet.getString("SUM(customer_invoice_items.qty)");
                String totalDiscounts = resultSet.getString("SUM(customer_invoice.discount)");

                InputStream path = filePath;
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Parameter5", date);
                parameters.put("Parameter1", totalSales);
                parameters.put("Parameter2", totalRevenue);
                parameters.put("Parameter3", totalUnitsSold);
                parameters.put("Parameter4", totalDiscounts);

                String appDir = new File("").getAbsolutePath(); // Get the application's directory
                String reportsFolder = appDir + File.separator + "ExportedReports"; // Main folder path
// Create the main "ExportedReports" folder if it doesn't exist
                File mainDirectory = new File(reportsFolder);
                if (!mainDirectory.exists()) {
                    mainDirectory.mkdirs();
                }

// Create subfolder for "Sales Reports" if it doesn't exist
                String salesReportsFolder = reportsFolder + File.separator + "Sales Reports";
                File salesDirectory = new File(salesReportsFolder);
                if (!salesDirectory.exists()) {
                    salesDirectory.mkdirs();
                }
                // Create subfolder for "Monthly Sales Reports" if it doesn't exist
                if (sort.equals("Daily")) {
                    salesReportsTypeFolder = salesReportsFolder + File.separator + "Daily Sales Reports";
                } else if (sort.equals("Weekly")) {
                    salesReportsTypeFolder = salesReportsFolder + File.separator + "Weekly Sales Reports";
                } else if (sort.equals("Monthly")) {
                    salesReportsTypeFolder = salesReportsFolder + File.separator + "Monthly Sales Reports";
                }
                String subSalesReportsFolder = salesReportsTypeFolder;
                File subSalesDirectory = new File(subSalesReportsFolder);
                if (!subSalesDirectory.exists()) {
                    subSalesDirectory.mkdirs();
                }

// Path to export the PDF file (inside "Monthly Sales Reports" subfolder)
                if (sort.equals("Daily")) {
                    reportFilePath = subSalesReportsFolder + File.separator + "Daily_Sales_report " + date + ".pdf";
                } else if (sort.equals("Weekly")) {
                    reportFilePath = subSalesReportsFolder + File.separator + "Weekly_Sales_report " + date + ".pdf";
                } else if (sort.equals("Monthly")) {
                    reportFilePath = subSalesReportsFolder + File.separator + "Monthly_Sales_report " + date + ".pdf";
                }

                String outputPath = reportFilePath;

                JasperPrint report = JasperFillManager.fillReport(path, parameters, MySQL.connection);
                JasperExportManager.exportReportToPdfFile(report, outputPath);
                JOptionPane.showMessageDialog(null, "PDF exported to: " + outputPath);
            } else {
                JOptionPane.showMessageDialog(this, "Error exporting report", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked

        InventoryReport ir = new InventoryReport(home, true, this);
        ir.setVisible(true);
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        orderReport or = new orderReport(home, true, this);
        or.setVisible(true);
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
        salesReport sr = new salesReport(home, true, this);
        sr.setVisible(true);
    }//GEN-LAST:event_jButton6MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private com.toedter.calendar.JDayChooser jDayChooser1;
    private com.toedter.calendar.JDayChooser jDayChooser2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
