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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class AdminLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUser;
    private JPasswordField txtPass;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new AdminLogin().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AdminLogin() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 550);
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new LineBorder(PRIMARY_COLOR, 2));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Header
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(2, 2, 446, 80);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Admin Portal");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 446, 40);
        header.add(lblTitle);
        
        JLabel lblClose = new JLabel("X");
        lblClose.setForeground(Color.WHITE);
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setBounds(415, 10, 30, 30);
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new FrontFrame().setVisible(true); 
                dispose();
            }
        });
        header.add(lblClose);

        // Form
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.GRAY);
        lblUser.setBounds(50, 130, 100, 20);
        contentPane.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(50, 155, 350, 40);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPane.add(txtUser);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(Color.GRAY);
        lblPass.setBounds(50, 220, 100, 20);
        contentPane.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(50, 245, 350, 40);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPane.add(txtPass);

        // --- STYLED LOGIN BUTTON ---
        JButton btnLogin = new JButton("SECURE LOGIN");
        btnLogin.setBounds(50, 330, 350, 50);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Default Style: White BG, Dark Gray Text
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setForeground(Color.DARK_GRAY);
        btnLogin.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Hover Effect
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(PRIMARY_COLOR); // Blue
                btnLogin.setForeground(Color.WHITE);   // White Text
                btnLogin.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(Color.WHITE);   // Back to White
                btnLogin.setForeground(Color.DARK_GRAY); // Back to Gray
                btnLogin.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnLogin.addActionListener(e -> checkLogin());
        contentPane.add(btnLogin);
    }

    private void checkLogin() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both Username and Password.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Query by Username only first
            PreparedStatement pst = con.prepareStatement("SELECT * FROM admin_login WHERE username=?");
            pst.setString(1, user);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Get the exact values from Database
                String dbUser = rs.getString("username");
                String dbPass = rs.getString("password");

                // --- STRICT CASE SENSITIVITY CHECK ---
                if (dbUser.equals(user) && dbPass.equals(pass)) {
                    JOptionPane.showMessageDialog(null, "Access Granted!");
                    new AdminPanel().setVisible(true); 
                    dispose();
                } else {
                    // Password matched in DB query (if SQL was case-insensitive) but failed Java check
                    // OR Password was wrong
                    JOptionPane.showMessageDialog(null, "Invalid Credentials (Check capitalization)", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Username", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
    }
}