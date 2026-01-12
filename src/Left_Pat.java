import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
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

public class Left_Pat extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;

    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = new Color(245, 247, 250);
    
    private int currentDocID;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Left_Pat frame = new Left_Pat(1); 
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Left_Pat(int docID) {
        this.currentDocID = docID;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setTitle("Pending Queue - Doctor Portal");

        JPanel contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // --- 1. HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(900, 60));
        header.setLayout(null);
        contentPane.add(header, BorderLayout.NORTH);

        JLabel btnBack = new JLabel("< Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBounds(20, 15, 200, 30);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DocProfile d = new DocProfile(docID);
                d.setVisible(true);
                dispose();
            }
        });
        header.add(btnBack);

        JLabel lblTitle = new JLabel("Pending Appointments");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(350, 15, 400, 30);
        header.add(lblTitle);

        // --- 2. TABLE AREA ---
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setLayout(null);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        String[] columns = {"Patient Email", "Date", "Time", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 20, 840, 400);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane);

        // --- 3. REJECT BUTTON (Admin Style) ---
        JButton btnReject = new JButton("Reject Appointment");
        btnReject.setBounds(20, 440, 200, 45);
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
        
        btnReject.addActionListener(e -> rejectAppointment());
        centerPanel.add(btnReject);

        // --- 4. DIAGNOSE BUTTON (Admin Style) ---
        JButton btnDiagnose = new JButton("Diagnose & Prescribe");
        btnDiagnose.setBounds(600, 440, 260, 45);
        btnDiagnose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDiagnose.setFocusPainted(false);
        btnDiagnose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnDiagnose.setBackground(Color.WHITE);
        btnDiagnose.setForeground(Color.DARK_GRAY);
        btnDiagnose.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Green Hover Effect
        btnDiagnose.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnDiagnose.setBackground(new Color(46, 204, 113)); // Green
                btnDiagnose.setForeground(Color.WHITE);
                btnDiagnose.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnDiagnose.setBackground(Color.WHITE);
                btnDiagnose.setForeground(Color.DARK_GRAY);
                btnDiagnose.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnDiagnose.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a patient to treat.");
            } else {
                String pEmail = (String) table.getValueAt(row, 0);
                String aTime = (String) table.getValueAt(row, 2); 
                
                ConsultationForm form = new ConsultationForm(docID, pEmail, aTime);
                form.setVisible(true);
                dispose(); 
            }
        });
        centerPanel.add(btnDiagnose);

        loadPending(docID);
    }

    private void loadPending(int docID) {
        model.setRowCount(0); 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "SELECT * FROM appointment_record WHERE D_ID = ? AND status = 'Booked'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, docID);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("p_email"),
                    rs.getString("a_date"), 
                    rs.getString("a_time"),
                    rs.getString("status")
                });
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rejectAppointment() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to reject.");
            return;
        }

        String pEmail = (String) model.getValueAt(row, 0);
        String date = (String) model.getValueAt(row, 1);
        String time = (String) model.getValueAt(row, 2);

        String reason = JOptionPane.showInputDialog(this, 
                "Enter reason for rejecting this appointment:", 
                "Reject Appointment", 
                JOptionPane.WARNING_MESSAGE);

        if(reason != null && !reason.trim().isEmpty()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                
                String sql = "UPDATE appointment_record SET status='Cancelled', rejection_reason=? " +
                             "WHERE D_ID=? AND p_email=? AND a_date=? AND a_time=?";
                
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, reason);
                pst.setInt(2, currentDocID);
                pst.setString(3, pEmail);
                pst.setString(4, date);
                pst.setString(5, time);
                
                int result = pst.executeUpdate();
                con.close();

                if(result > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment Cancelled & Patient Notified.");
                    model.removeRow(row); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating database.");
            }
        } else {
            if(reason != null) { 
                JOptionPane.showMessageDialog(this, "Rejection reason is required!");
            }
        }
    }
}