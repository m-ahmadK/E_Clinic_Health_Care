import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
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
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class AdminPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // Stat Labels
    private JLabel lblDocCount;
    private JLabel lblPatCount;
    private JLabel lblAppCount;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color BG_COLOR = new Color(245, 247, 250);     

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new AdminPanel().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AdminPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700);
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 0, 250, 700);
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(null);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        contentPane.add(sidebar);

        JLabel lblTitle = new JLabel("Admin Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 30, 250, 40);
        sidebar.add(lblTitle);

        // --- NAVIGATION BUTTONS ---
        createSidebarButton(sidebar, "Dashboard", 120);
        createSidebarButton(sidebar, "Manage Doctors", 180);
        
        // **NEW BUTTON: DOCTOR APPROVALS**
        createSidebarButton(sidebar, "Doctor Approvals", 240);
        
        createSidebarButton(sidebar, "Manage Patients", 300);
        createSidebarButton(sidebar, "Appointments", 360);
        createSidebarButton(sidebar, "Manage Medical Knowledge Base", 420);

        // --- STYLED LOGOUT BUTTON ---
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(25, 630, 200, 40);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(Color.DARK_GRAY);
        btnLogout.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(231, 76, 60)); // Red on Hover
                btnLogout.setForeground(Color.WHITE);
                btnLogout.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(Color.WHITE);
                btnLogout.setForeground(Color.DARK_GRAY);
                btnLogout.setBorder(new LineBorder(new Color(230, 230, 230), 1));
            }
        });
        
        btnLogout.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });
        sidebar.add(btnLogout);

        // --- MAIN CONTENT ---
        JLabel lblHeader = new JLabel("Dashboard Overview");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.DARK_GRAY);
        lblHeader.setBounds(280, 30, 300, 30);
        contentPane.add(lblHeader);

        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(280, 90, 780, 180);
        statsPanel.setBackground(BG_COLOR);
        statsPanel.setLayout(new GridLayout(1, 3, 20, 0)); 
        contentPane.add(statsPanel);

        lblDocCount = new JLabel("0");
        statsPanel.add(createStatCard("Total Doctors", lblDocCount, new Color(46, 204, 113)));

        lblPatCount = new JLabel("0");
        statsPanel.add(createStatCard("Total Patients", lblPatCount, new Color(52, 152, 219)));

        lblAppCount = new JLabel("0");
        statsPanel.add(createStatCard("Total Appointments", lblAppCount, new Color(155, 89, 182)));
        
        loadDashboardStats();
    }

    private void createSidebarButton(JPanel panel, String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(25, y, 200, 45);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.DARK_GRAY);
                btn.setBorder(new LineBorder(new Color(230, 230, 230), 1));
            }
        });
        
        btn.addActionListener(e -> {
             if(text.equals("Dashboard")) loadDashboardStats();
             else if(text.equals("Manage Doctors")) { new ManageDoctors().setVisible(true); dispose(); }
             
             // **OPENS APPROVAL PAGE**
             else if(text.equals("Doctor Approvals")) { new AdminDoctorApproval().setVisible(true); dispose(); }
             
             else if(text.equals("Manage Patients")) { new ManagePatients().setVisible(true); dispose(); }
             else if(text.equals("Appointments")) { new AdminAppointments().setVisible(true); dispose(); }
             else if(text.equals("Manage Medical Knowledge Base")) { 
                 new ManageMedicalKB().setVisible(true); 
                 dispose(); 
             }
        });
        panel.add(btn);
    }

    private JPanel createStatCard(String title, JLabel countLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBounds(20, 20, 200, 20);
        card.add(lblTitle);

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(Color.DARK_GRAY);
        countLabel.setBounds(20, 50, 100, 40);
        card.add(countLabel);

        return card;
    }

    private void loadDashboardStats() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            PreparedStatement st1 = con.prepareStatement("SELECT COUNT(*) FROM doc_signup");
            ResultSet rs1 = st1.executeQuery();
            if(rs1.next()) lblDocCount.setText(rs1.getString(1));
            
            PreparedStatement st2 = con.prepareStatement("SELECT COUNT(*) FROM pat_signup");
            ResultSet rs2 = st2.executeQuery();
            if(rs2.next()) lblPatCount.setText(rs2.getString(1));
            
            PreparedStatement st3 = con.prepareStatement("SELECT COUNT(*) FROM appointment_record");
            ResultSet rs3 = st3.executeQuery();
            if(rs3.next()) lblAppCount.setText(rs3.getString(1));

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}