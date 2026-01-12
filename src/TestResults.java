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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class TestResults extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private String patEmail;
    
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public TestResults(String email, int dummy) {
        this.patEmail = email;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600); // Slightly wider
        setTitle("My Medical Records");
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        setContentPane(contentPane);
        
        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(1000, 60));
        header.setLayout(null);
        contentPane.add(header, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("My Medical History");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 300, 30);
        header.add(lblTitle);

        // Back Button in Header
        JLabel btnBack = new JLabel("Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setHorizontalAlignment(SwingConstants.CENTER);
        btnBack.setBounds(900, 15, 80, 30);
        btnBack.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PatProfile p = new PatProfile(patEmail);
                p.setVisible(true);
                dispose();
            }
        });
        header.add(btnBack);

        // --- TABLE ---
        // Added Date and consolidated "Remarks"
        String[] cols = {"Date", "Time", "Status", "My Symptoms", "Doctor's Remarks / Reason"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        
        // Styling Table
        table.setRowHeight(60); // Taller rows for text wrap effect visually
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 230, 255));
        
        // Column Width Adjustment
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(70);  // Time
        table.getColumnModel().getColumn(2).setPreferredWidth(70);  // Status
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Symptoms
        table.getColumnModel().getColumn(4).setPreferredWidth(400); // Remarks (Widest)

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Updated Query: Fetch Checked OR Cancelled, include Date and Rejection Reason
            String query = "SELECT a_date, a_time, status, symptoms, prescription, doctor_advice, rejection_reason " +
                           "FROM appointment_record " +
                           "WHERE p_email=? AND (status='Checked' OR status='Cancelled') " +
                           "ORDER BY a_date DESC"; // Most recent first
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, patEmail);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                String status = rs.getString("status");
                String remarks = "";

                // Logic to display relevant info based on status
                if(status.equalsIgnoreCase("Cancelled")) {
                    remarks = "[REJECTED] Reason: " + rs.getString("rejection_reason");
                } else {
                    // It is Checked
                    String rx = rs.getString("prescription");
                    String advice = rs.getString("doctor_advice");
                    remarks = "Rx: " + (rx != null ? rx : "N/A") + " | Advice: " + (advice != null ? advice : "N/A");
                }

                model.addRow(new Object[]{
                    rs.getString("a_date"),
                    rs.getString("a_time"),
                    status,
                    rs.getString("symptoms"),
                    remarks
                });
            }
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}