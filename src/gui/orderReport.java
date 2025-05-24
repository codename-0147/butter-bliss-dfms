package gui;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;

public class orderReport extends javax.swing.JDialog {

    private OutletSales os;

    HashMap<String, String> typeMap = new HashMap<>();

    public orderReport(java.awt.Frame parent, boolean modal, OutletSales os) {
        super(parent, modal);
        this.os = os;
        initComponents();
        generateReportID();
        loadOrders(null, null);
        loadType();

    }

    private void generateReportID() {
        String id = "BBOrder" + System.currentTimeMillis();
        jTextField1.setText(String.valueOf(id));

    }
 private void loadType() {

        try {

            Vector<String> vector = new Vector<>();
            vector.add("Select Type");

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `order_type`");

            while (resultSet.next()) {
                vector.add(resultSet.getString("type"));
                typeMap.put(resultSet.getString("type"), resultSet.getString("id"));
            }

            jComboBox2.setModel(new DefaultComboBoxModel<>(vector));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrders(String dateFilter, String typeFilter) {
        try {

            String query = "SELECT `order`.`id` AS order_id, `order`.`date` AS order_date, "
                    + "`w_product`.`id` AS w_product_id, `w_product`.`name` AS w_product_name, "
                    + "`order_items`.`qty` AS order_qty, `order_type`.`type` AS order_type, "
                    + "`order_status`.`name` AS status_name "
                    + "FROM `order` "
                    + "INNER JOIN `order_items` ON `order`.`id` = `order_items`.`order_id` "
                    + "INNER JOIN `w_product` ON `order_items`.`w_product_id` = `w_product`.`id` "
                    + "INNER JOIN `order_type` ON `order`.`order_type_id` = `order_type`.`id`"
                    + "INNER JOIN `order_status` ON `order`.`order_status_id` = `order_status`.`id`";

            boolean whereAdded = false;

            if (dateFilter != null && !dateFilter.isEmpty()) {
                query += " WHERE `order`.`date` = '" + dateFilter + "'";
                whereAdded = true;
            }

            if (typeFilter != null) {
                query += (whereAdded ? " AND " : " WHERE ") + "`order`.`order_type_id` = '" + typeFilter + "'";
            }

            ResultSet resultSet = MySQL.executeSearch(query);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("order_id"));
                vector.add(resultSet.getString("order_type"));
                vector.add(resultSet.getString("order_date"));
                vector.add(resultSet.getString("w_product_id"));
                vector.add(resultSet.getString("w_product_name"));
                vector.add(resultSet.getString("order_qty"));
                vector.add(resultSet.getString("status_name"));
                model.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jButton19 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton20 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(245, 239, 210));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/file (1).png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel2.setText("O R D E R  R E P O R T");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Report ID :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Date :");

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Order ID", "Type", "Date", "Product Id", "Product Name", "Quantity", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jButton3.setBackground(new java.awt.Color(255, 248, 244));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-print-28.png"))); // NOI18N
        jButton3.setText("Print");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jDateChooser1.setBackground(new java.awt.Color(255, 255, 255));
        jDateChooser1.setForeground(new java.awt.Color(255, 255, 255));
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setFont(new java.awt.Font("Poppins", 0, 11)); // NOI18N

        jButton19.setBackground(new java.awt.Color(245, 219, 200));
        jButton19.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton19.setText("Find");
        jButton19.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setText("Type");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton20.setBackground(new java.awt.Color(245, 219, 200));
        jButton20.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton20.setText("Find");
        jButton20.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(245, 219, 200));
        jButton13.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jButton13.setText("Reset");
        jButton13.setPreferredSize(new java.awt.Dimension(115, 40));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 820, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 21, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addGap(85, 85, 85)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
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

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = jTable1.getSelectedRow();
        jComboBox2.setSelectedItem(String.valueOf(jTable1.getValueAt(row, 1)));
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        JFileChooser dialog = new JFileChooser();
        dialog.setSelectedFile(new File("OrderReport" + ".pdf"));
        int dialogResult = dialog.showSaveDialog(null);

        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            String filePath = dialog.getSelectedFile().getPath();

            Document doc = new Document();

            try {
                PdfWriter.getInstance(doc, new FileOutputStream(filePath));
                doc.open();

                PdfPTable tb1 = new PdfPTable(7);

                float[] columnWidths = {3f, 3f, 3f, 3f, 3f, 3f, 3f};
                tb1.setWidths(columnWidths);

                Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);

                PdfPCell header;
                header = new PdfPCell(new Phrase("Order ID", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Type", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Date", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Product ID", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Product Name", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Quantity", headerFont));
                tb1.addCell(header);
                header = new PdfPCell(new Phrase("Status", headerFont));
                tb1.addCell(header);

                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    PdfPCell cell;

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 0).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 1).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 2).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 3).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 4).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 5).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                    cell = new PdfPCell(new Phrase(jTable1.getValueAt(i, 6).toString(), cellFont));
                    cell.setFixedHeight(20f);
                    tb1.addCell(cell);

                }

                doc.add(tb1);
                doc.close();
                JOptionPane.showMessageDialog(null, "PDF Generated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        try {
            java.util.Date selectedDate = jDateChooser1.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Please select a date!");
                return;
            }

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

            loadOrders(formattedDate, null);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed

        try {
            String selectedType = (String) jComboBox2.getSelectedItem();

            if (selectedType.equals("Select Type")) {
                JOptionPane.showMessageDialog(this, "Please select a valid type!");
                return;
            }

            String typeId = typeMap.get(selectedType);

            loadOrders(null, typeId);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        loadOrders(null, null);
        jDateChooser1.setDate(null);
        jComboBox2.setSelectedIndex(0);
    }//GEN-LAST:event_jButton13ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
