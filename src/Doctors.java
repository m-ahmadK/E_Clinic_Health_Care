import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Doctors extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainContainer;

    // Database Credentials
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = new Color(245, 247, 250);

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Doctors frame = new Doctors("test@email.com");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Doctors(String pat_mail) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setTitle("Find a Doctor");
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        // Main Layout: Border Layout (Header Top, Scroll in Center)
        getContentPane().setLayout(new BorderLayout());

        // 1. HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setLayout(null);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Available Specialists");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(420, 15, 300, 30);
        headerPanel.add(lblTitle);

        // Back Button (Arrow)
        JLabel btnBack = new JLabel(" < Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBack.setBounds(10, 15, 80, 30);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PatProfile p = new PatProfile(pat_mail);
                p.setVisible(true);
                dispose();
            }
        });
        headerPanel.add(btnBack);

        // 2. SCROLLABLE CONTENT AREA
        mainContainer = new JPanel();
        mainContainer.setBackground(BG_COLOR);
        // Use WrapLayout or FlowLayout with width preference for grid effect
        mainContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        mainContainer.setPreferredSize(new Dimension(960, 1200)); // Make it tall enough to scroll

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setBorder(null);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // 3. LOAD DOCTORS
        loadDoctors(pat_mail);
    }

    private void loadDoctors(String pat_mail) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement s = con.createStatement();
            
            // IMPORTANT: Only show APPROVED doctors
            // If you haven't implemented approval yet, remove "WHERE status='APPROVED'"
            String query = "SELECT * FROM doc_signup WHERE status = 'APPROVED'"; 
            ResultSet rs = s.executeQuery(query);
            
            boolean found = false;

            while (rs.next()) {
                found = true;
                int docId = rs.getInt("ID");
                String name = rs.getString("Name");
                String spec = rs.getString("Specialization");
                String license = rs.getString("license_no");

                // Create a beautiful card for this doctor
                JPanel card = createDoctorCard(docId, name, spec, license, pat_mail);
                mainContainer.add(card);
            }

            if (!found) {
                JLabel noDocs = new JLabel("No Approved Doctors Available yet.");
                noDocs.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                noDocs.setForeground(Color.GRAY);
                mainContainer.add(noDocs);
            }

            rs.close(); s.close(); con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading doctors: " + ex.getMessage());
        }
    }

    /**
     * Creates a single "Card" panel for a doctor
     */
    private JPanel createDoctorCard(int id, String name, String spec, String license, String pat_mail) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(280, 320));
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        // Add a nice border
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 1. Avatar Image
        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        lblImg.setBounds(90, 15, 100, 100);
        
        // Try to load doctor icon
        ImageIcon icon = loadIcon("doctor-avatar");
        if(icon != null) {
            lblImg.setIcon(resizeIcon(icon, 100, 100));
        } else {
            // Fallback visuals if image missing
            lblImg.setText("IMG");
            lblImg.setBorder(new LineBorder(PRIMARY_COLOR)); 
            lblImg.setOpaque(true);
            lblImg.setBackground(new Color(240, 248, 255));
        }
        card.add(lblImg);

        // 2. Name
        JLabel lblName = new JLabel("Dr. " + name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(new Color(50, 50, 50));
        lblName.setHorizontalAlignment(SwingConstants.CENTER);
        lblName.setBounds(10, 125, 260, 25);
        card.add(lblName);

        // 3. Specialization
        if(spec == null || spec.isEmpty()) spec = "General Physician";
        JLabel lblSpec = new JLabel(spec);
        lblSpec.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSpec.setForeground(PRIMARY_COLOR); // Blue text
        lblSpec.setHorizontalAlignment(SwingConstants.CENTER);
        lblSpec.setBounds(10, 150, 260, 20);
        card.add(lblSpec);

        // 4. Verified Badge
        JLabel lblVerify = new JLabel("✔ Verified License: " + license);
        lblVerify.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVerify.setForeground(new Color(46, 204, 113)); // Green
        lblVerify.setHorizontalAlignment(SwingConstants.CENTER);
        lblVerify.setBounds(10, 175, 260, 20);
        card.add(lblVerify);

        // 5. Book Button
        JButton btnBook = new JButton("Book Appointment");
        btnBook.setBounds(40, 220, 200, 40);
        btnBook.setBackground(PRIMARY_COLOR);
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.setFocusPainted(false);
        btnBook.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBook.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open Appointment Page
                try {
                     Appointment pf = new Appointment(id, pat_mail);
                     pf.setVisible(true);
                     dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });
        card.add(btnBook);
        
        // 6. View Profile (Optional, inactive for now)
        JLabel lblProfile = new JLabel("View Full Profile");
        lblProfile.setHorizontalAlignment(SwingConstants.CENTER);
        lblProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblProfile.setForeground(Color.GRAY);
        lblProfile.setBounds(10, 270, 260, 20);
        card.add(lblProfile);

        return card;
    }

    // --- UTILITIES ---
    private ImageIcon loadIcon(String name) {
        String[] paths = {"/images/", "/"};
        String[] exts = {".png", ".jpg", ".jpeg"};
        for(String p : paths) {
            for(String e : exts) {
                URL url = getClass().getResource(p + name + e);
                if(url != null) return new ImageIcon(url);
            }
        }
        return null;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int w, int h) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}