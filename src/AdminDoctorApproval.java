import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class AdminDoctorApproval extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public AdminDoctorApproval() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700);
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(0, 0, 1100, 60);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Pending Doctor Approvals");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 400, 30);
        header.add(lblTitle);

        // --- BACK BUTTON (High Visibility Style) ---
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(980, 15, 80, 30);
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new LineBorder(Color.BLACK, 1));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            new AdminPanel().setVisible(true);
            dispose();
        });
        header.add(btnBack);

        // --- TABLE ---
        String[] columns = {"ID", "Name", "Specialization", "License No", "CNIC", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 1000, 450);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(scrollPane);

        // --- REJECT BUTTON (Bottom Right Sequence - X=630) ---
        JButton btnReject = new JButton("Reject / Delete");
        btnReject.setBounds(630, 570, 250, 45);
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReject.setFocusPainted(false);
        btnReject.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnReject.setBackground(Color.WHITE);
        btnReject.setForeground(Color.DARK_GRAY);
        btnReject.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Red Hover Effect
        btnReject.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnReject.setBackground(new Color(231, 76, 60)); // Red
                btnReject.setForeground(Color.WHITE);
                btnReject.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnReject.setBackground(Color.WHITE);
                btnReject.setForeground(Color.DARK_GRAY);
                btnReject.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnReject.addActionListener(e -> rejectDoctor());
        contentPane.add(btnReject);

        // --- APPROVE BUTTON (Far Bottom Right - X=900) ---
        JButton btnApprove = new JButton("Approve");
        btnApprove.setBounds(900, 570, 150, 45);
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApprove.setFocusPainted(false);
        btnApprove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnApprove.setBackground(Color.WHITE);
        btnApprove.setForeground(Color.DARK_GRAY);
        btnApprove.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Green Hover Effect
        btnApprove.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnApprove.setBackground(new Color(46, 204, 113)); // Green
                btnApprove.setForeground(Color.WHITE);
                btnApprove.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnApprove.setBackground(Color.WHITE);
                btnApprove.setForeground(Color.DARK_GRAY);
                btnApprove.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });

        btnApprove.addActionListener(e -> approveDoctor());
        contentPane.add(btnApprove);

        loadPendingDoctors();
    }

    private void loadPendingDoctors() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // ONLY SELECT PENDING DOCTORS
            PreparedStatement pst = con.prepareStatement("SELECT * FROM doc_signup WHERE status = 'PENDING'");
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getString("Specialization"),
                    rs.getString("license_no"),
                    rs.getString("cnic"),
                    rs.getString("status")
                });
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void approveDoctor() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to approve.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // UPDATE STATUS TO APPROVED
            PreparedStatement pst = con.prepareStatement("UPDATE doc_signup SET status = 'APPROVED' WHERE ID = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
            con.close();

            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Doctor " + name + " has been APPROVED!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void rejectDoctor() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to reject.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Rejecting will DELETE this account permanently.\nAre you sure?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                // DELETE THE RECORD
                PreparedStatement pst = con.prepareStatement("DELETE FROM doc_signup WHERE ID = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
                con.close();

                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Doctor " + name + " request rejected and removed.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}