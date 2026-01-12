import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class DocProfile extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // Labels
    private JLabel lblDocName;
    private JLabel lblPendingCount;
    private JLabel lblTreatedCount;

    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color BG_COLOR = new Color(245, 247, 250);     

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new DocProfile(1).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public DocProfile(int docID) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 650);
        setTitle("Doctor Dashboard - E-Health Care");

        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // --- HEADER ---
        JPanel navPanel = new JPanel();
        navPanel.setBackground(PRIMARY_COLOR);
        navPanel.setBounds(0, 0, 1000, 60);
        navPanel.setLayout(null);
        contentPane.add(navPanel);

        JLabel lblBrand = new JLabel("Doctor Portal");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setBounds(20, 15, 200, 30);
        navPanel.add(lblBrand);

        JLabel lblLogout = new JLabel("Logout");
        lblLogout.setForeground(Color.WHITE);
        lblLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogout.setBounds(900, 15, 80, 30);
        lblLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLogout.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                FrontFrame f = new FrontFrame();
                f.setVisible(true);
                dispose();
            }
        });
        navPanel.add(lblLogout);

        // --- WELCOME ---
        lblDocName = new JLabel("Welcome, Doctor");
        lblDocName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDocName.setForeground(Color.DARK_GRAY);
        lblDocName.setBounds(30, 80, 600, 40);
        contentPane.add(lblDocName);

        JLabel lblSub = new JLabel("Here is your daily overview.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(30, 120, 300, 20);
        contentPane.add(lblSub);

        // --- STAT CARDS ---
        // 1. Pending
        JPanel cardPending = createStatCard(30, 160, 450, 180, new Color(231, 76, 60)); 
        contentPane.add(cardPending);
        
        JLabel lblPendingTitle = new JLabel("Pending Appointments");
        lblPendingTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPendingTitle.setForeground(Color.DARK_GRAY);
        lblPendingTitle.setBounds(20, 20, 300, 25);
        cardPending.add(lblPendingTitle);

        lblPendingCount = new JLabel("0");
        lblPendingCount.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblPendingCount.setForeground(new Color(231, 76, 60));
        lblPendingCount.setBounds(20, 60, 100, 50);
        cardPending.add(lblPendingCount);

        JLabel lblClickView1 = new JLabel("Click to View Queue >");
        lblClickView1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblClickView1.setForeground(Color.GRAY);
        lblClickView1.setBounds(20, 130, 200, 20);
        cardPending.add(lblClickView1);

        cardPending.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Left_Pat l = new Left_Pat(docID);
                l.setVisible(true);
                dispose();
            }
        });

        // 2. Treated
        JPanel cardTreated = createStatCard(510, 160, 450, 180, new Color(46, 204, 113)); 
        contentPane.add(cardTreated);
        
        JLabel lblTreatedTitle = new JLabel("Patients Treated");
        lblTreatedTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTreatedTitle.setForeground(Color.DARK_GRAY);
        lblTreatedTitle.setBounds(20, 20, 300, 25);
        cardTreated.add(lblTreatedTitle);

        lblTreatedCount = new JLabel("0");
        lblTreatedCount.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblTreatedCount.setForeground(new Color(46, 204, 113));
        lblTreatedCount.setBounds(20, 60, 100, 50);
        cardTreated.add(lblTreatedCount);

        JLabel lblClickView2 = new JLabel("Click to View History >");
        lblClickView2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblClickView2.setForeground(Color.GRAY);
        lblClickView2.setBounds(20, 130, 200, 20);
        cardTreated.add(lblClickView2);

        cardTreated.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Dealt_Pat d = new Dealt_Pat(docID);
                d.setVisible(true);
                dispose();
            }
        });

        // --- MEDICAL TOOLS SECTION ---
        JLabel lblActions = new JLabel("Medical Tools");
        lblActions.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblActions.setForeground(Color.DARK_GRAY);
        lblActions.setBounds(30, 370, 200, 30);
        contentPane.add(lblActions);

        // ** THE FIX: ONLY ONE BUTTON HERE **
        JPanel btnLibrary = createActionCard(30, 420, "Medical Knowledge Base", "View & Add Disease Standards", "/images/search-icon.png");
        btnLibrary.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Opens the Viewer (ShowDisease). The 'Add' button is INSIDE this viewer.
                ShowDisease s = new ShowDisease(docID, "Doctor"); 
                s.setVisible(true);
                dispose();
            }
        });
        contentPane.add(btnLibrary);

        // Load Data
        loadDoctorName(docID);
        loadStats(docID);
    }

    private JPanel createStatCard(int x, int y, int w, int h, Color accent) {
        JPanel p = new JPanel();
        p.setBounds(x, y, w, h);
        p.setBackground(Color.WHITE);
        p.setLayout(null);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createMatteBorder(0, 5, 0, 0, accent)
        ));
        return p;
    }

    private JPanel createActionCard(int x, int y, String title, String sub, String iconPath) {
        JPanel p = new JPanel();
        p.setBounds(x, y, 320, 100); // Made slightly wider
        p.setBackground(Color.WHITE);
        p.setLayout(null);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBounds(20, 20, 280, 20);
        p.add(lblTitle);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(20, 45, 280, 20);
        p.add(lblSub);
        return p;
    }

    private void loadDoctorName(int docID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT Name FROM doc_signup WHERE ID = ?");
            pst.setInt(1, docID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                lblDocName.setText("Welcome, Dr. " + rs.getString("Name"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStats(int docID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Pending
            String q1 = "SELECT COUNT(*) FROM appointment_record WHERE status = 'Booked' AND D_ID = ?";
            PreparedStatement pst1 = con.prepareStatement(q1);
            pst1.setInt(1, docID);
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) lblPendingCount.setText(rs1.getString(1));

            // Treated
            String q2 = "SELECT COUNT(*) FROM appointment_record WHERE status <> 'Booked' AND D_ID = ?";
            PreparedStatement pst2 = con.prepareStatement(q2);
            pst2.setInt(1, docID);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) lblTreatedCount.setText(rs2.getString(1));

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}