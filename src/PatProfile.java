import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class PatProfile extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    
    // Profile Labels
    private JLabel pNameLbl;
    private JLabel pAgeLbl;
    private JLabel pEmailLbl;
    private JLabel lblAvatar; // Made this global so we can update it
    
    // Database Credentials
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
                PatProfile frame = new PatProfile("test@example.com");
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PatProfile(String mail) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700); 
        setTitle("Patient Dashboard - E-Health Care");
        
        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // 1. TOP NAV
        JPanel navPanel = new JPanel();
        navPanel.setBackground(PRIMARY_COLOR);
        navPanel.setBounds(0, 0, 1100, 60);
        navPanel.setLayout(null);
        contentPane.add(navPanel);

        JLabel lblBrand = new JLabel("E-Health Patient Portal");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setBounds(20, 15, 250, 30);
        navPanel.add(lblBrand);

        // --- WORKING SEARCH BAR ---
        JTextField txtSearch = new JTextField(" Search Doctors...");
        txtSearch.setBounds(300, 15, 300, 30);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Add Enter Key Listener
        txtSearch.addActionListener(e -> {
            String term = txtSearch.getText();
            // Open Doctors page (You can improve logic to filter later)
            Doctors d = new Doctors(mail); 
            d.setVisible(true);
            JOptionPane.showMessageDialog(d, "Showing results for: " + term); 
            dispose();
        });
        navPanel.add(txtSearch);

        createNavLink(navPanel, "Logout", 980, () -> {
            FrontFrame f = new FrontFrame();
            f.setVisible(true);
            dispose();
        });

        // 2. LEFT PROFILE CARD
        JPanel profileCard = createCard(20, 80, 280, 300);
        contentPane.add(profileCard);

        lblAvatar = new JLabel();
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setBounds(90, 20, 100, 100);
        // Default placeholder (will be overwritten if DB has image)
        lblAvatar.setText("<html><div style='background-color:#ddd; width:100px; height:100px; border-radius:50%; text-align:center;'>IMG</div></html>"); 
        profileCard.add(lblAvatar);

        pNameLbl = new JLabel("Loading...");
        pNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        pNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pNameLbl.setForeground(Color.DARK_GRAY);
        pNameLbl.setBounds(10, 130, 260, 30);
        profileCard.add(pNameLbl);

        pEmailLbl = new JLabel(mail);
        pEmailLbl.setHorizontalAlignment(SwingConstants.CENTER);
        pEmailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pEmailLbl.setForeground(Color.GRAY);
        pEmailLbl.setBounds(10, 160, 260, 20);
        profileCard.add(pEmailLbl);

        pAgeLbl = new JLabel("Age: --");
        pAgeLbl.setHorizontalAlignment(SwingConstants.CENTER);
        pAgeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pAgeLbl.setForeground(Color.GRAY);
        pAgeLbl.setBounds(10, 185, 260, 20);
        profileCard.add(pAgeLbl);
        
        // --- WORKING EDIT PROFILE BUTTON ---
        JButton btnEditProfile = new JButton("Edit Profile");
        btnEditProfile.setBackground(PRIMARY_COLOR);
        btnEditProfile.setForeground(Color.BLACK);
        btnEditProfile.setFocusPainted(false);
        btnEditProfile.setBounds(50, 230, 180, 35);
        btnEditProfile.addActionListener(e -> {
            // Open the new Edit Page
            new EditProfile(mail).setVisible(true);
            dispose();
        });
        profileCard.add(btnEditProfile);

        // 3. APPOINTMENTS
        JPanel tableCard = createCard(320, 80, 740, 300);
        contentPane.add(tableCard);

        JLabel lblAppTitle = new JLabel("Upcoming Appointments");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAppTitle.setForeground(PRIMARY_COLOR);
        lblAppTitle.setBounds(20, 15, 300, 30);
        tableCard.add(lblAppTitle);

        String[] columnHeaders = { "Doctor ID", "Appointment Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnHeaders, 0);
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 60, 700, 220);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane);

        loadAppointments(mail, model);

        // 4. ACTION DASHBOARD
        JLabel lblActions = new JLabel("Quick Actions");
        lblActions.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblActions.setForeground(Color.DARK_GRAY);
        lblActions.setBounds(20, 400, 200, 30);
        contentPane.add(lblActions);

        createActionButton("Find Doctors", "doctor-icon", 20, 440, e -> {
            Doctors d = new Doctors(mail);
            d.setVisible(true);
            dispose();
        });

        createActionButton("Quick Treatments", "treatment-icon", 280, 440, e -> {
            ShowDisease s = new ShowDisease(0, mail);
            s.setVisible(true);
            dispose();
        });

        createActionButton("History/Prescriptions", "test-tube", 540, 440, e -> {
            TestResults t = new TestResults(mail, 0);
            t.setVisible(true);
            dispose();
        });

        createActionButton("Symptom Checker", "pharmacy-icon", 800, 440, e -> {
            ConditionChecker c = new ConditionChecker(mail); 
            c.setVisible(true);
            dispose(); 
        });
        
        JButton btnAmbulance = new JButton("EMERGENCY AMBULANCE");
        btnAmbulance.setBounds(20, 580, 1040, 50);
        btnAmbulance.setBackground(new Color(231, 76, 60)); 
        btnAmbulance.setForeground(Color.DARK_GRAY);
        btnAmbulance.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAmbulance.setFocusPainted(false);
        btnAmbulance.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAmbulance.addActionListener(e -> JOptionPane.showMessageDialog(null, "🚑 Ambulance Dispatch Contacted!\nEmergency Team is on the way to your registered address.", "Emergency Alert", JOptionPane.WARNING_MESSAGE));
        contentPane.add(btnAmbulance);

        loadProfileData(mail);
    }

    // --- HELPER METHODS ---

    private JPanel createCard(int x, int y, int w, int h) {
        JPanel p = new JPanel();
        p.setBounds(x, y, w, h);
        p.setBackground(Color.WHITE);
        p.setLayout(null);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return p;
    }

    private void createActionButton(String title, String iconName, int x, int y, ActionListener action) {
        JButton btn = new JButton();
        btn.setBounds(x, y, 240, 120);
        btn.setBackground(Color.WHITE);
        btn.setLayout(null);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        JLabel lblIcon = new JLabel();
        ImageIcon ico = loadIcon(iconName);
        if(ico != null) lblIcon.setIcon(resizeIcon(ico, 50, 50));
        lblIcon.setBounds(20, 35, 50, 50);
        btn.add(lblIcon);
        
        JLabel lblText = new JLabel(title);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblText.setForeground(PRIMARY_COLOR);
        lblText.setBounds(90, 35, 140, 30);
        btn.add(lblText);
        
        JLabel lblSub = new JLabel("Click to view");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(90, 60, 100, 20);
        btn.add(lblSub);

        contentPane.add(btn);
    }

    private void createNavLink(JPanel panel, String text, int x, Runnable action) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBounds(x, 15, 100, 30);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) { lbl.setForeground(new Color(220, 220, 220)); }
            public void mouseExited(MouseEvent e) { lbl.setForeground(Color.WHITE); }
        });
        panel.add(lbl);
    }

    private void loadProfileData(String mail) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM pat_signup WHERE email = ?");
            pst.setString(1, mail);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pNameLbl.setText(rs.getString("name"));
                pAgeLbl.setText("Age: " + rs.getInt("age") + " Years");
                
                // --- LOAD IMAGE FROM DB ---
                byte[] imgData = rs.getBytes("profile_picture");
                if (imgData != null) {
                    ImageIcon ii = new ImageIcon(imgData);
                    // Resize to fit the 100x100 circle
                    Image img = ii.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    lblAvatar.setIcon(new ImageIcon(img));
                    lblAvatar.setText(""); // Remove the text "IMG"
                }
            }
            rs.close(); pst.close(); con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppointments(String mail, DefaultTableModel model) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM appointment_record WHERE p_email = ?");
            pst.setString(1, mail);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("D_ID"),
                    rs.getString("a_time"),
                    rs.getString("status")
                });
            }
            rs.close(); pst.close(); con.close();
        } catch (Exception e) {
            System.out.println("No appointments found.");
        }
    }

    private ImageIcon loadIcon(String name) {
        String[] exts = {".gif", ".png", ".jpg", ".jpeg"}; 
        String[] paths = {"/images/", "/"};
        for (String path : paths) {
            for (String ext : exts) {
                URL url = getClass().getResource(path + name + ext);
                if (url != null) return new ImageIcon(url);
            }
        }
        return null;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int w, int h) {
        if (icon.getDescription() != null && icon.getDescription().endsWith(".gif")) {
            return icon; 
        }
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}