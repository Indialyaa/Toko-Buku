/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TokoBuku;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ACER
 */
public class Toko_Buku extends javax.swing.JFrame {

    String Tanggal;
    private DefaultTableModel model;
    
    public void totalBiaya(){
        int jumlahBaris = jTable1.getRowCount();
        int totalBiaya = 0;
        int jumlahBuku, hargaBuku;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahBuku = Integer.parseInt(jTable1.getValueAt(i, 3).toString());
            hargaBuku = Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            totalBiaya = totalBiaya + (jumlahBuku * hargaBuku);
        }
        txTotalBayar.setText(String.valueOf(totalBiaya));
        txTampil.setText("Rp. "+ totalBiaya +",00");
    }
    
    public void close(){
        int jumlahBaris = jTable1.getRowCount();
        for (int i = 0; i < jumlahBaris; i++) {
            String id = jTable1.getValueAt(i, 1).toString();
            int stok = Integer.parseInt(jTable1.getValueAt(i, 3).toString());
            try {
            Connection c = koneksi.getKoneksi();
            // ambil stok
            Statement s = c.createStatement();
            String sql = "SELECT * FROM buku WHERE ID_buku='"+id+"'";
            ResultSet r = s.executeQuery(sql);
            while(r.next()){
                stok += r.getInt("Stok");
            }
            r.close();
            String sqlUpdate = "UPDATE `buku` SET `Stok`='"+String.valueOf(stok)+"' WHERE ID_buku='"+id+"'";
            PreparedStatement u = c.prepareStatement(sqlUpdate);
            u.executeUpdate();
            u.close();
        } catch (SQLException ex) {
            Logger.getLogger(Toko_Buku.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    private void autonumber(){
        try {
            Connection c = koneksi.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM tokobuku ORDER BY NoFaktur DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("NoFaktur").substring(2);
                String TR = "" +(Integer.parseInt(NoFaktur)+1);
                String Nol = "";
                
                if(TR.length()==1)
                {Nol = "000";}
                else if(TR.length()==2)
                {Nol = "00";}
                else if(TR.length()==3)
                {Nol = "0";}
                else if(TR.length()==4)
                {Nol = "";}
                txNoTransaksi.setText("TR" + Nol + TR);
            } else {
                txNoTransaksi.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }
    
    public void loadData(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{
            txNoTransaksi.getText(),
            txIDBuku.getText(),
            txJudulBuku.getText(),
            txJumlah.getText(),
            txHarga.getText(),
            txTotalBayar.getText()
        });
    }
    
    public void kosong(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        while (model.getRowCount()>0) {
            model.removeRow(0);
        }
    }
    
    public void utama(){
        txNoTransaksi.setText("");
        txIDBuku.setText("");
        txJudulBuku.setText("");
        txHarga.setText("");
        txJumlah.setText("");
        autonumber();
    }
    
    public void clear(){
        txIDCustomer.setText("");
        txNamaCustomer.setText("");
        txTotalBayar.setText("0");
        txBayar.setText("0");
        txKembalian.setText("0");
        txTampil.setText("0");
    }
    
    public void clear2(){
        txIDBuku.setText("");
        txJudulBuku.setText("");
        txHarga.setText("");
        txJumlah.setText("");
    }
    
    public void tambahTransaksi(){
        int jumlah, harga, total, stok = 0;
        
        try {
            Connection c = koneksi.getKoneksi();
            // ambil stok
            Statement s = c.createStatement();
            String sql = "SELECT * FROM buku WHERE ID_buku='"+txIDBuku.getText()+"'";
            ResultSet r = s.executeQuery(sql);
            while(r.next()){
                stok = r.getInt("Stok");
            }
            r.close();
            jumlah = Integer.valueOf(txJumlah.getText());
            if(stok == 0){
                JOptionPane.showMessageDialog(null, "stok dah abis ges", "Invalid", JOptionPane.ERROR_MESSAGE);
                return;
            }else if(jumlah > stok){
                JOptionPane.showMessageDialog(null, "stok tidak mencukupi", "Invalid", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                String sqlUpdate = "UPDATE `buku` SET `Stok`='"+String.valueOf(stok-jumlah)+"' WHERE ID_buku='"+txIDBuku.getText()+"'";
                PreparedStatement u = c.prepareStatement(sqlUpdate);
                u.executeUpdate();
                u.close();
            }
            
            //update stok

            harga = Integer.valueOf(txHarga.getText());
            total = jumlah * harga;
            txTotalBayar.setText(String.valueOf(total));

            loadData();
            totalBiaya();
            clear2();
            txIDBuku.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(Toko_Buku.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public Toko_Buku() {
        initComponents();
        
        txNoTransaksi.setEnabled(false);
        //Create Table
        model = new DefaultTableModel();
        
        jTable1.setModel(model);
        
        model.addColumn("No Transaksi");
        model.addColumn("ID Buku");
        model.addColumn("Judul Buku");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Total");
        
        utama();
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        
        txTanggal.setText(s.format(date));
        txTotalBayar.setText("0");
        txBayar.setText("0");
        txKembalian.setText("0");
        txIDCustomer.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txIDBuku = new javax.swing.JTextField();
        txJudulBuku = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnCari = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txTampil = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        txTotalBayar = new javax.swing.JTextField();
        txBayar = new javax.swing.JTextField();
        txKembalian = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txNamaCustomer = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txIDCustomer = new javax.swing.JTextField();
        txNoTransaksi = new javax.swing.JTextField();
        txHarga = new javax.swing.JTextField();
        txJumlah = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txTanggal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(880, 675));
        setPreferredSize(new java.awt.Dimension(880, 675));
        getContentPane().setLayout(null);
        getContentPane().add(txIDBuku);
        txIDBuku.setBounds(16, 230, 94, 22);
        getContentPane().add(txJudulBuku);
        txJudulBuku.setBounds(200, 230, 162, 22);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(16, 264, 740, 200);

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        getContentPane().add(btnCari);
        btnCari.setBounds(116, 230, 66, 22);

        jPanel3.setBackground(new java.awt.Color(0, 102, 153));
        jPanel3.setMinimumSize(new java.awt.Dimension(880, 675));
        jPanel3.setPreferredSize(new java.awt.Dimension(880, 675));
        jPanel3.setLayout(null);

        txTampil.setFont(new java.awt.Font("Tahoma", 2, 16)); // NOI18N
        txTampil.setText("Rp. 0");
        jPanel3.add(txTampil);
        txTampil.setBounds(20, 490, 230, 50);

        btnSimpan.setBackground(new java.awt.Color(0, 204, 51));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel3.add(btnSimpan);
        btnSimpan.setBounds(680, 590, 83, 22);

        jPanel1.setBackground(new java.awt.Color(0, 102, 204));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setText("TOKO BUKU RIFSA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel1);
        jPanel1.setBounds(342, 32, 170, 34);

        btnTambah.setBackground(new java.awt.Color(51, 153, 255));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        jPanel3.add(btnTambah);
        btnTambah.setBounds(770, 270, 80, 50);

        btnHapus.setBackground(new java.awt.Color(255, 51, 51));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel3.add(btnHapus);
        btnHapus.setBounds(770, 330, 80, 50);

        txTotalBayar.setEnabled(false);
        jPanel3.add(txTotalBayar);
        txTotalBayar.setBounds(580, 490, 180, 22);

        txBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txBayarActionPerformed(evt);
            }
        });
        jPanel3.add(txBayar);
        txBayar.setBounds(580, 520, 180, 22);

        txKembalian.setEnabled(false);
        jPanel3.add(txKembalian);
        txKembalian.setBounds(580, 550, 180, 22);

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Kembalian");
        jPanel3.add(jLabel12);
        jLabel12.setBounds(490, 550, 60, 16);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total Bayar");
        jPanel3.add(jLabel10);
        jLabel10.setBounds(490, 490, 70, 16);

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Bayar");
        jPanel3.add(jLabel11);
        jLabel11.setBounds(490, 520, 40, 16);

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);
        jButton1.setBounds(770, 590, 80, 22);
        jPanel3.add(txNamaCustomer);
        txNamaCustomer.setBounds(137, 163, 160, 30);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Nama Customer");
        jPanel3.add(jLabel4);
        jLabel4.setBounds(18, 170, 101, 16);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ID Customer");
        jPanel3.add(jLabel3);
        jLabel3.setBounds(18, 131, 90, 20);

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("No Transaksi");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(18, 90, 90, 16);
        jPanel3.add(txIDCustomer);
        txIDCustomer.setBounds(137, 121, 160, 30);

        txNoTransaksi.setEnabled(false);
        txNoTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txNoTransaksiActionPerformed(evt);
            }
        });
        jPanel3.add(txNoTransaksi);
        txNoTransaksi.setBounds(137, 83, 160, 30);
        jPanel3.add(txHarga);
        txHarga.setBounds(400, 230, 160, 22);

        txJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txJumlahActionPerformed(evt);
            }
        });
        jPanel3.add(txJumlah);
        txJumlah.setBounds(590, 230, 160, 22);

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Jumlah");
        jPanel3.add(jLabel9);
        jLabel9.setBounds(590, 210, 90, 16);

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Harga");
        jPanel3.add(jLabel8);
        jLabel8.setBounds(400, 210, 70, 16);

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Judul Buku");
        jPanel3.add(jLabel7);
        jLabel7.setBounds(200, 210, 100, 16);

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("ID Buku");
        jPanel3.add(jLabel6);
        jLabel6.setBounds(20, 210, 50, 16);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Tanggal");
        jPanel3.add(jLabel5);
        jLabel5.setBounds(520, 90, 50, 16);

        txTanggal.setEnabled(false);
        jPanel3.add(txTanggal);
        txTanggal.setBounds(600, 90, 160, 22);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 870, 650);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txJumlahActionPerformed
        // TODO add your handling code here:
        tambahTransaksi();
    }//GEN-LAST:event_txJumlahActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        
        tambahTransaksi();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int row = jTable1.getSelectedRow();
        int stok = Integer.parseInt(jTable1.getValueAt(row, 3).toString());
        String id = jTable1.getValueAt(row, 1).toString();
        try {
            Connection c = koneksi.getKoneksi();
            // ambil stok
            Statement s = c.createStatement();
            String sql = "SELECT * FROM buku WHERE ID_buku='"+id+"'";
            ResultSet r = s.executeQuery(sql);
            while(r.next()){
                stok += r.getInt("Stok");
            }
            r.close();
            String sqlUpdate = "UPDATE `buku` SET `Stok`='"+String.valueOf(stok)+"' WHERE ID_buku='"+id+"'";
            PreparedStatement u = c.prepareStatement(sqlUpdate);
            u.executeUpdate();
            u.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Toko_Buku.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        model.removeRow(row);
        totalBiaya();
        txBayar.setText("0");
        txKembalian.setText("0");
    }//GEN-LAST:event_btnHapusActionPerformed

    private void txBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txBayarActionPerformed
        // TODO add your handling code here:
        int total, bayar, kembalian;
        
        total = Integer.valueOf(txTotalBayar.getText());
        bayar = Integer.valueOf(txBayar.getText());
        
        if (total > bayar) {
            JOptionPane.showMessageDialog(null, "Uang tidak cukup untuk melakukan pembayaran");
        } else {
            kembalian = bayar - total;
            txKembalian.setText(String.valueOf(kembalian));
        }
    }//GEN-LAST:event_txBayarActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        String noTransaksi = txNoTransaksi.getText();
        String tanggal = txTanggal.getText();
        String idCustomer = txIDCustomer.getText();
        String total = txTotalBayar.getText();
        
        try {
            Connection c = koneksi.getKoneksi();
            String sql = "INSERT INTO tokobuku VALUES (?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, noTransaksi);
            p.setString(2, tanggal);
            p.setString(3, idCustomer);
            p.setString(4, total);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("simpan penjualan error");
        }
        
        try {
            Connection c = koneksi.getKoneksi();
            int baris = jTable1.getRowCount();
            
            for (int i = 0; i < baris; i++) {
                String sql = "INSERT INTO tokobuku_rinci(NoFaktur, ID_Buku, Judul_Buku, Jumlah, Harga, Total) VALUES('"
                        + jTable1.getValueAt(i, 0) +"','"+ jTable1.getValueAt(i, 1) +"','"+ jTable1.getValueAt(i, 2) 
                        +"','"+ jTable1.getValueAt(i, 3) +"','"+ jTable1.getValueAt(i, 4) +"','"+ jTable1.getValueAt(i, 5) 
                        +"')";
                PreparedStatement p = c.prepareStatement(sql);
                p.executeUpdate();
                p.close();
            }
        } catch (Exception e) {
            System.out.println("simpan penjualanrinci error");
        }
        clear();
        utama();
        autonumber();
        kosong();
        txTampil.setText("Rp. 0");
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        List_Buku a = new List_Buku();
        a.setVisible(true);
    }//GEN-LAST:event_btnCariActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        Menu_Utama a = new Menu_Utama();
        a.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txNoTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txNoTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txNoTransaksiActionPerformed

    /**
     * @param args the command line arguments
     */
    private static Object lock = new Object();
    private static Toko_Buku frame = new Toko_Buku();
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
            java.util.logging.Logger.getLogger(Toko_Buku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Toko_Buku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Toko_Buku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Toko_Buku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Toko_Buku().setVisible(true);
//            }
//        });
         frame.setSize(300, 300);
         frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
         frame.setVisible(true);

        Thread t = new Thread() {
            public void run() {
                synchronized(lock) {
                    while (frame.isVisible())
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        frame.close();
                }
            }
        };
        t.start();

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                synchronized (lock) {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txBayar;
    public static javax.swing.JTextField txHarga;
    public static javax.swing.JTextField txIDBuku;
    private javax.swing.JTextField txIDCustomer;
    public static javax.swing.JTextField txJudulBuku;
    public static javax.swing.JTextField txJumlah;
    private javax.swing.JTextField txKembalian;
    private javax.swing.JTextField txNamaCustomer;
    private javax.swing.JTextField txNoTransaksi;
    private javax.swing.JTextField txTampil;
    private javax.swing.JTextField txTanggal;
    private javax.swing.JTextField txTotalBayar;
    // End of variables declaration//GEN-END:variables
}
