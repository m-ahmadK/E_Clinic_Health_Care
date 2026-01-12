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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.UIManager;

public class Dealt_Pat extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    
    // Details Panel Components
    private JTextArea txtDetails;

    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Medical Blue
    private final Color BG_COLOR = new Color(245, 247, 250);     // Light Gray
    private final Color ACCENT_GREEN = new Color(46, 204, 113);  // Success Green

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Set System Look & Feel for smoother fonts
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Dealt_Pat frame = new Dealt_Pat(1);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Dealt_Pat(int docID) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setTitle("Treatment History & Records");

        JPanel contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // =========================================================================
        // 1. HEADER
        // =========================================================================
        JPanel header = new JPanel();
        header.setBackground(ACCENT_GREEN); // Green signifies "Completed/History"
        header.setPreferredSize(new Dimension(1000, 60));
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

        JLabel lblTitle = new JLabel("Patient Treatment Records");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(380, 15, 400, 30);
        header.add(lblTitle);

        // =========================================================================
        // 2. MAIN CONTENT
        // =========================================================================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        centerPanel.setBackground(BG_COLOR);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- LEFT COLUMN: PATIENT LIST ---
        JLabel lblList = new JLabel("Select a Patient:");
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblList.setForeground(Color.GRAY);
        lblList.setBounds(20, 15, 200, 20);
        centerPanel.add(lblList);

        String[] columns = {"Patient Email", "Date Treated"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(232, 242, 254)); // Light Blue selection
        table.setSelectionForeground(Color.BLACK);
        
        // Custom Header
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 40, 350, 520);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane);

        // --- RIGHT COLUMN: MEDICAL REPORT ---
        JLabel lblDetails = new JLabel("Full Medical Report:");
        lblDetails.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetails.setForeground(Color.GRAY);
        lblDetails.setBounds(390, 15, 200, 20);
        centerPanel.add(lblDetails);

        txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtDetails.setForeground(Color.DARK_GRAY);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setText("\n  Select a patient from the list to load their medical record.");
        
        // Add padding inside the text area
        txtDetails.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane detailScroll = new JScrollPane(txtDetails);
        detailScroll.setBounds(390, 40, 570, 520);
        detailScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(detailScroll);

        // --- SELECTION LOGIC ---
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    String email = table.getValueAt(row, 0).toString();
                    String time = table.getValueAt(row, 1).toString();
                    loadFullDetails(docID, email, time);
                }
            }
        });

        // Load Data
        loadHistoryList(docID);
    }

    private void loadHistoryList(int docID) {
        model.setRowCount(0); 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // Fetch appointments where status is NOT 'Booked' (meaning Checked/Done)
            String query = "SELECT p_email, a_time FROM appointment_record WHERE D_ID = ? AND status <> 'Booked'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, docID);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("p_email"),
                    rs.getString("a_time")
                });
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFullDetails(int docID, String email, String time) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String query = "SELECT * FROM appointment_record WHERE D_ID=? AND p_email=? AND a_time=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, docID);
            pst.setString(2, email);
            pst.setString(3, time);
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) {
                String phone = rs.getString("phone");
                String symptoms = rs.getString("symptoms");
                String desc = rs.getString("disease_description");
                String rx = rs.getString("prescription");
                String advice = rs.getString("doctor_advice");

                StringBuilder sb = new StringBuilder();
                sb.append("MEDICAL RECORD SUMMARY\n");
                sb.append("=====================================\n\n");
                
                sb.append("PATIENT DETAILS\n");
                sb.append("----------------------------\n");
                sb.append("Email:      ").append(email).append("\n");
                sb.append("Phone:      ").append(phone == null ? "N/A" : phone).append("\n");
                sb.append("Visit Date: ").append(time).append("\n\n");
                
                sb.append("CLINICAL NOTES\n");
                sb.append("----------------------------\n");
                sb.append("Symptoms Reported:\n");
                sb.append("  ").append(symptoms).append("\n\n");
                sb.append("History / Description:\n");
                sb.append("  ").append(desc).append("\n\n");
                
                sb.append("TREATMENT PLAN\n");
                sb.append("----------------------------\n");
                sb.append("Prescription (Rx):\n");
                sb.append("  ").append(rx).append("\n\n");
                sb.append("Doctor's Advice:\n");
                sb.append("  ").append(advice).append("\n");
                
                txtDetails.setText(sb.toString());
                txtDetails.setCaretPosition(0); // Auto-scroll to top
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            txtDetails.setText("Error loading record details.");
        }
    }
}