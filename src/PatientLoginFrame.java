import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import javax.swing.border.LineBorder;

public class PatientLoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField emailF;
    private JPasswordField PpasswordF;
    
    // Buttons
    private JButton SignupBtn; // The big LOGIN button
    private JButton loginBtn;  // The "Create Account" button
    
    // Drag variables
    int xx, xy;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PatientLoginFrame frame = new PatientLoginFrame();
                    
                    // Initialize with a default role for testing
                    frame.LoginScreen("Patient"); 
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public PatientLoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 500); // Same size as Doctor Signup approx
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 1));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // --- LEFT PANEL (Medical Blue) ---
        JPanel panel = new JPanel();
        panel.setBackground(new Color(41, 128, 185)); // The same Blue as Doctor Signup
        panel.setBounds(0, 0, 350, 500);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(0, 150, 350, 40);
        panel.add(lblWelcome);

        JLabel lblSub = new JLabel("Login to access your portal");
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(220, 220, 220));
        lblSub.setBounds(0, 190, 350, 20);
        panel.add(lblSub);
        
        // "New Here?" Section
        JLabel lblNew = new JLabel("Don't have an account?");
        lblNew.setHorizontalAlignment(SwingConstants.CENTER);
        lblNew.setForeground(Color.WHITE);
        lblNew.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNew.setBounds(0, 380, 350, 20);
        panel.add(lblNew);

        // This is the "SignUp" button (redirects to registration)
        loginBtn = new JButton("Create Account");
        loginBtn.setForeground(new Color(41, 128, 185)); // Blue text
        loginBtn.setBackground(Color.WHITE);             // White button
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBounds(85, 410, 180, 35);
        panel.add(loginBtn);

        // --- RIGHT PANEL (White) ---
        
        // Window Drag Logic (Invisible Label at top)
        JLabel dragLabel = new JLabel("");
        dragLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xx = e.getX();
                xy = e.getY();
            }
        });
        dragLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                int x = arg0.getXOnScreen();
                int y = arg0.getYOnScreen();
                PatientLoginFrame.this.setLocation(x - xx, y - xy);  
            }
        });
        dragLabel.setBounds(0, 0, 800, 40);
        contentPane.add(dragLabel);

        // Close Button
        JLabel lblClose = new JLabel("X");
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                // Return to FrontFrame
                FrontFrame f = new FrontFrame();
                f.setVisible(true);
                dispose();
            }
        });
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setForeground(new Color(41, 128, 185));
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.setBounds(800, 0, 50, 40);
        contentPane.add(lblClose);

        // Login Title
        JLabel lblLoginTitle = new JLabel("Secure Login");
        lblLoginTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoginTitle.setForeground(Color.DARK_GRAY);
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLoginTitle.setBounds(350, 60, 500, 40);
        contentPane.add(lblLoginTitle);

        // 1. Email Field (Replaces Username)
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setForeground(Color.DARK_GRAY);
        lblEmail.setBounds(420, 130, 150, 20);
        contentPane.add(lblEmail);
        
        emailF = new JTextField();
        emailF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailF.setBounds(420, 155, 350, 35);
        contentPane.add(emailF);
        emailF.setColumns(10);

        // 2. Password Field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(Color.DARK_GRAY);
        lblPassword.setBounds(420, 210, 150, 20);
        contentPane.add(lblPassword);
        
        PpasswordF = new JPasswordField();
        PpasswordF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        PpasswordF.setBounds(420, 235, 350, 35);
        contentPane.add(PpasswordF);

        // 3. Login Button (The Submit Button)
        SignupBtn = new JButton("Login");
        SignupBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        SignupBtn.setForeground(Color.WHITE);
        SignupBtn.setBackground(new Color(41, 128, 185)); // Medical Blue
        SignupBtn.setBounds(420, 300, 350, 40);
        SignupBtn.setFocusPainted(false);
        SignupBtn.setBorder(null);
        SignupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(SignupBtn);
    }

    /**
     * LOGIC: Handles Login & Navigation
     */
    public void LoginScreen(String role) {
        
        // 1. DYNAMIC NAVIGATION (The "Create Account" button on left panel)
        // Clear old listeners
        for(ActionListener al : loginBtn.getActionListeners()) {
            loginBtn.removeActionListener(al);
        }
        
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (role.equals("Doctor")) {
                    DoctorSignupFrame f = new DoctorSignupFrame();
                    f.setVisible(true);
                } else {
                    SignupFrame f = new SignupFrame(); // Old Patient Signup
                    f.setVisible(true);
                }
                dispose();
            }
        });

        // 2. LOGIN SUBMIT LOGIC
        // Clear old listeners
        for(ActionListener al : SignupBtn.getActionListeners()) {
            SignupBtn.removeActionListener(al);
        }

        SignupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailF.getText();
                String password = new String(PpasswordF.getPassword());

                if(email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both Email and Password.");
                    return;
                }

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hms_db", "hms_user", "hms_pass"
                    );

                    String query;
                    PreparedStatement pst;

                    if(role.equals("Doctor")){
                        // Check Doctor Table
                        query = "SELECT * FROM doc_signup WHERE Email=?";
                        pst = con.prepareStatement(query);
                        pst.setString(1, email);
                        
                        ResultSet resultSet = pst.executeQuery();

                        if (resultSet.next()) {
                            // --- STRICT CASE SENSITIVITY CHECK ---
                            String dbEmail = resultSet.getString("Email");
                            String dbPass = resultSet.getString("Password");
                            String status = resultSet.getString("status");

                            // We verify if the Database values EXACTLY match the Input values
                            if(dbEmail.equals(email) && dbPass.equals(password)) {
                                if ("APPROVED".equalsIgnoreCase(status)) {
                                    JOptionPane.showMessageDialog(null, "Login successful!");
                                    int ID = resultSet.getInt("ID");
                                    DocProfile d = new DocProfile(ID);
                                    d.setVisible(true);
                                    dispose();
                                } else {
                                    JOptionPane.showMessageDialog(null, 
                                        "Access Denied.\nYour Account Status: " + status + "\nPlease wait for Admin approval.", 
                                        "Security Check", JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid Email or Password! (Check capitalization)");
                            }
                        } else {
                            // If no email found at all
                            JOptionPane.showMessageDialog(null, "Invalid Email or Password!");
                        }
                        resultSet.close();
                    }
                    else {
                        // Check Patient Table
                        query = "SELECT * FROM pat_signup WHERE email=?";
                        pst = con.prepareStatement(query);
                        pst.setString(1, email);
                        
                        ResultSet resultSet = pst.executeQuery();

                        if (resultSet.next()) {
                            // --- STRICT CASE SENSITIVITY CHECK ---
                            String dbEmail = resultSet.getString("email");
                            String dbPass = resultSet.getString("password");

                            // We verify if the Database values EXACTLY match the Input values
                            if(dbEmail.equals(email) && dbPass.equals(password)) {
                                JOptionPane.showMessageDialog(null, "Login successful!");
                                PatProfile p = new PatProfile(email);
                                p.setVisible(true);
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid Email or Password! (Check capitalization)");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "User not found!");
                        }
                        resultSet.close();
                    }

                    pst.close();
                    con.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
                }
            }   
        });
    }
}