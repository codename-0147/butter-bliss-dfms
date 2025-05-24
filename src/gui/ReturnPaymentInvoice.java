/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.MySQL;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author User
 */
public class ReturnPaymentInvoice extends javax.swing.JFrame {

    /**
     * Creates new form ReturnPaymentInvoice
     */
    public ReturnPaymentInvoice() {
        initComponents();
        loadTransaction();
    }
private void loadTransaction() {
        try {
            ResultSet resultSet = MySQL.executeSearch(
                    "SELECT "
                    + "outlet.name AS outlet_name, "
                    + "return_invoice.date AS return_date, "
                    + "`transaction`.amount AS transaction_amount, "
                    + "`transaction`.date AS payment_date, "
                    + "payment_status.status AS payment_status, "
                    + "payment_method.type AS payment_type "
                    + "FROM `transaction` "
                    + "INNER JOIN `return_invoice` ON `transaction`.`return_invoice_id` = `return_invoice`.`id` "
                    + "INNER JOIN `outlet` ON `transaction`.`outlet_id` = `outlet`.`id` "
                    + "INNER JOIN `payment_status` ON `transaction`.`payment_status_id` = `payment_status`.`id` "
                    + "INNER JOIN `payment_method` ON `transaction`.`payment_method_id` = `payment_method`.`id` "
                    + "ORDER BY `transaction`.`id` ASC"
            );

            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("outlet_name"));       // Outlet name
                vector.add(resultSet.getString("return_date"));       // Return invoice date
                vector.add(resultSet.getString("transaction_amount")); // Amount
                vector.add(resultSet.getString("payment_date"));      // Transaction date
                vector.add(resultSet.getString("payment_status"));    // Payment status
                vector.add(resultSet.getString("payment_type"));      // Payment method type
                dtm.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField7 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Outlet ID", "Return Date", "Amount", "Payment Date", "Status", "Method"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-print-25.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        jLabel11.setText("Search");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 813, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        DefaultTableModel ob = (DefaultTableModel) jTable1.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(ob);
        jTable1.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(jTextField7.getText()));
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        try {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please Select a Row", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String finalDate = dateFormat.format(date);
            // Extract data from selected row
            String distributorID = jTable1.getValueAt(selectedRow, 0).toString();
            String paymentID = jTable1.getValueAt(selectedRow, 1).toString();
            // String paymentDate = jTable1.getValueAt(selectedRow, 3).toString(); // Or use new Date()

            // Load the compiled Jasper report
            InputStream path = getClass().getResourceAsStream("/reports/Distributorpayment.jasper");
            if (path == null) {
                throw new FileNotFoundException("Report file not found:/reports/Distributorpayment.jasper");
            }

            // Prepare parameters for the report
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("Parameter1", distributorID);
            parameters.put("Parameter2", finalDate);
            parameters.put("Parameter3", paymentID);

            // Use JRTableModelDataSource if your table model matches the report fields
            JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable1.getModel());

            // Fill and show report
            JasperPrint report = JasperFillManager.fillReport(path, parameters, dataSource);
            JasperViewer.viewReport(report, false);

            // Optional: export to PDF
            String appDir = new File("").getAbsolutePath();
            String reportFolder = appDir + File.separator + "ExportedReports" + File.separator + "Distributor Payment Invoice";
            File reportDir = new File(reportFolder);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            String outputPath = reportFolder + File.separator + "DistributorPaymentInvoice_" + paymentID + ".pdf";
            JasperExportManager.exportReportToPdfFile(report, outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        //        try {
            //    int selectedRow = jTable1.getSelectedRow();
            //    if (selectedRow == -1) {
                //        JOptionPane.showMessageDialog(this, "Please Select a Row", "Error", JOptionPane.WARNING_MESSAGE);
                //        return;
                //    }
            //
            //    String distributorID = jTable1.getValueAt(selectedRow, 0).toString();
            //    String paymentID = jTable1.getValueAt(selectedRow, 1).toString();
            //    String salary = jTable1.getValueAt(selectedRow, 2).toString();
            //    String paymentDate = jTable1.getValueAt(selectedRow, 3).toString();
            //    String description = jTable1.getValueAt(selectedRow, 4).toString();
            //    String paymentStatus = jTable1.getValueAt(selectedRow, 5).toString();
            //    String paymentMethod = jTable1.getValueAt(selectedRow, 6).toString();
            //    JFileChooser dialog = new JFileChooser();
            //    dialog.setSelectedFile(new File("DistributorPaymentSlip.pdf"));
            //    int dialogResult = dialog.showSaveDialog(null);
            //
            //    if (dialogResult == JFileChooser.APPROVE_OPTION) {
                //        String filePath = dialog.getSelectedFile().getPath();
                //        Document myDocument = new Document(PageSize.A4, 50, 50, 50, 50);
                //
                //        try {
                    //            PdfWriter.getInstance(myDocument, new FileOutputStream(filePath));
                    //            myDocument.open();
                    //
                    //            // Title
                    //            Paragraph title = new Paragraph("Distributor Payment Slip",
                        //                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
                    //            title.setAlignment(Element.ALIGN_CENTER);
                    //            myDocument.add(title);
                    //
                    //            Paragraph date = new Paragraph("Generated on: " + new Date().toString(),
                        //                    FontFactory.getFont(FontFactory.HELVETICA, 10));
                    //            date.setAlignment(Element.ALIGN_RIGHT);
                    //            myDocument.add(date);
                    //
                    //            myDocument.add(Chunk.NEWLINE);
                    //            myDocument.add(new LineSeparator());
                    //            myDocument.add(Chunk.NEWLINE);
                    //
                    //            // Distributor Details
                    //            myDocument.add(new Paragraph("Distributor Details",
                        //                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                //            myDocument.add(Chunk.NEWLINE);
                //
                //            PdfPTable distTable = new PdfPTable(2);
                //            distTable.setWidthPercentage(100);
                //            distTable.setSpacingBefore(5f);
                //            distTable.setSpacingAfter(10f);
                //
                //            distTable.addCell("Distributor ID:");
                //            distTable.addCell(distributorID);
                //            distTable.addCell("Payment ID:");
                //            distTable.addCell(paymentID);
                //            distTable.addCell("Description:");
                //            distTable.addCell(description);
                //
                //            myDocument.add(distTable);
                //
                //            myDocument.add(new LineSeparator());
                //            myDocument.add(Chunk.NEWLINE);
                //
                //            // Payment Details
                //            myDocument.add(new Paragraph("Payment Details",
                    //                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            //            myDocument.add(Chunk.NEWLINE);
            //
            //            PdfPTable payTable = new PdfPTable(2);
            //            payTable.setWidthPercentage(100);
            //            payTable.setSpacingBefore(5f);
            //            payTable.setSpacingAfter(10f);
            //
            //            payTable.addCell("Salary:");
            //            payTable.addCell(salary);
            //            payTable.addCell("Payment Date:");
            //            payTable.addCell(paymentDate);
            //            payTable.addCell("Payment Status:");
            //            payTable.addCell(paymentStatus);
            //            payTable.addCell("Payment Method:");
            //            payTable.addCell(paymentMethod);
            //
            //            myDocument.add(payTable);
            //
            //            myDocument.add(new LineSeparator());
            //            myDocument.add(Chunk.NEWLINE);
            //
            //            Paragraph thankYou = new Paragraph("Thank you for your cooperation!",
                //                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12));
            //            thankYou.setAlignment(Element.ALIGN_CENTER);
            //            myDocument.add(thankYou);
            //
            //            myDocument.close();
            //            JOptionPane.showMessageDialog(null, "Report was successfully generated");
            //
            //        } catch (Exception e) {
            //            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            //      }
        //    }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(ReturnPaymentInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReturnPaymentInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReturnPaymentInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReturnPaymentInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReturnPaymentInvoice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
