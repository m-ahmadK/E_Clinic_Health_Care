import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class AdminAppointments extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public AdminAppointments() {
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

        JLabel lblTitle = new JLabel("Master Appointment Log");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 300, 30);
        header.add(lblTitle);

        // --- BACK BUTTON (White/Black Style) ---
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
        String[] columns = {"Appt ID", "Doctor Name", "Patient Name", "Time", "Status"};
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

        // --- CANCEL BUTTON (Bottom Right Sequence) ---
        JButton btnCancel = new JButton("Cancel Selected Appt");
        btnCancel.setBounds(630, 570, 250, 45); // X=630
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(Color.DARK_GRAY);
        btnCancel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        btnCancel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(new Color(231, 76, 60)); // Red
                btnCancel.setForeground(Color.WHITE);
                btnCancel.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(Color.WHITE);
                btnCancel.setForeground(Color.DARK_GRAY);
                btnCancel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnCancel.addActionListener(e -> deleteAppointment());
        contentPane.add(btnCancel);

        // --- ADD BUTTON (Far Bottom Right) ---
        JButton btnAdd = new JButton("Book New Appt");
        btnAdd.setBounds(900, 570, 150, 45); // X=900 (Sequence: Cancel -> Add)
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setBackground(Color.WHITE);
        btnAdd.setForeground(Color.DARK_GRAY);
        btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Green Hover Effect
        btnAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(new Color(46, 204, 113)); // Green
                btnAdd.setForeground(Color.WHITE);
                btnAdd.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(Color.WHITE);
                btnAdd.setForeground(Color.DARK_GRAY);
                btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnAdd.addActionListener(e -> openAddAppointmentDialog());
        contentPane.add(btnAdd);

        loadAppointments();
    }

    private void loadAppointments() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Join tables to get actual names instead of just IDs
            String query = "SELECT ar.ID, ds.Name AS DocName, ps.name AS PatName, ar.a_time, ar.status " +
                           "FROM appointment_record ar " +
                           "JOIN doc_signup ds ON ar.D_ID = ds.ID " +
                           "JOIN pat_signup ps ON ar.p_email = ps.email";
            
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("DocName"),
                    rs.getString("PatName"),
                    rs.getString("a_time"),
                    rs.getString("status")
                });
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- ADD APPOINTMENT DIALOG ---
    private void openAddAppointmentDialog() {
        JTextField txtDocID = new JTextField();
        JTextField txtPatEmail = new JTextField();
        JTextField txtTime = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Doctor ID:")); panel.add(txtDocID);
        panel.add(new JLabel("Patient Email:")); panel.add(txtPatEmail);
        panel.add(new JLabel("Time (e.g., 10:00 AM):")); panel.add(txtTime);

        int result = JOptionPane.showConfirmDialog(null, panel, "Manual Booking",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String docID = txtDocID.getText();
            String email = txtPatEmail.getText();
            String time = txtTime.getText();

            if(docID.isEmpty() || email.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                
                String sql = "INSERT INTO appointment_record (D_ID, p_email, a_time, status) VALUES (?, ?, ?, 'Scheduled')";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(docID));
                pst.setString(2, email);
                pst.setString(3, time);
                
                pst.executeUpdate();
                con.close();
                
                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
                loadAppointments();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: Check Doctor ID or Patient Email validity.");
            }
        }
    }

    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel Appt ID: " + id + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pst = con.prepareStatement("DELETE FROM appointment_record WHERE ID = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
                con.close();
                
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Appointment cancelled.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}