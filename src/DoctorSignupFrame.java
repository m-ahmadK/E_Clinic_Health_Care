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

public class DoctorSignupFrame extends JFrame {

    private JPanel contentPane;
    private JTextField nameField;
    private JTextField specField; 
    private JTextField emailField;
    private JTextField cnicField;
    private JTextField licenseField;
    private JPasswordField passwordField;
    
    int xx, xy;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DoctorSignupFrame frame = new DoctorSignupFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public DoctorSignupFrame() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 600); 
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // --- LEFT SIDE PANEL ---
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(41, 128, 185));
        sidePanel.setBounds(0, 0, 300, 600);
        contentPane.add(sidePanel);
        sidePanel.setLayout(null);

        JLabel lblTitle = new JLabel("E-Health Care");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(0, 200, 300, 40);
        sidePanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Doctor Portal");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(220, 220, 220));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(0, 240, 300, 30);
        sidePanel.add(lblSubtitle);

        // --- RIGHT SIDE FORM ---
        
        // Window Drag Logic
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
                DoctorSignupFrame.this.setLocation(x - xx, y - xy);
            }
        });
        dragLabel.setBounds(0, 0, 800, 40);
        contentPane.add(dragLabel);

        // Close Button
        JLabel lblClose = new JLabel("X");
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FrontFrame f = new FrontFrame();
                f.setVisible(true);
                dispose(); 
            }
        });
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setForeground(new Color(41, 128, 185));
        lblClose.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblClose.setBounds(800, 0, 50, 40);
        contentPane.add(lblClose);

        // Header
        JLabel lblRegister = new JLabel("Doctor Registration");
        lblRegister.setForeground(new Color(105, 105, 105));
        lblRegister.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblRegister.setBounds(340, 30, 300, 40);
        contentPane.add(lblRegister);

        // 1. Full Name
        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblName.setBounds(340, 80, 100, 20);
        contentPane.add(lblName);

        nameField = new JTextField();
        nameField.setBounds(340, 105, 220, 30);
        contentPane.add(nameField);
        nameField.setColumns(10);
        
        // 2. Specialization
        JLabel lblSpec = new JLabel("Specialization");
        lblSpec.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSpec.setBounds(580, 80, 150, 20);
        contentPane.add(lblSpec);

        specField = new JTextField(); 
        specField.setBounds(580, 105, 220, 30);
        contentPane.add(specField);
        specField.setColumns(10);

        // 3. Email
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setBounds(340, 150, 100, 20);
        contentPane.add(lblEmail);

        emailField = new JTextField();
        emailField.setBounds(340, 175, 460, 30); 
        contentPane.add(emailField);
        emailField.setColumns(10);

        // 4. CNIC
        JLabel lblCnic = new JLabel("CNIC Number (13 Digits)");
        lblCnic.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCnic.setBounds(340, 220, 200, 20);
        contentPane.add(lblCnic);

        cnicField = new JTextField();
        cnicField.setBounds(340, 245, 220, 30);
        contentPane.add(cnicField);
        cnicField.setColumns(10);

        // 5. License
        JLabel lblLicense = new JLabel("Medical License #");
        lblLicense.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLicense.setBounds(580, 220, 200, 20);
        contentPane.add(lblLicense);

        licenseField = new JTextField();
        licenseField.setBounds(580, 245, 220, 30);
        contentPane.add(licenseField);
        licenseField.setColumns(10);

        // 6. Password
        JLabel lblPassword = new JLabel("Create Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setBounds(340, 290, 150, 20);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(340, 315, 220, 30);
        contentPane.add(passwordField);

        // Register Button
        JButton btnRegister = new JButton("Submit for Approval");
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerDoctor();
            }
        });
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setBackground(new Color(41, 128, 185));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRegister.setBounds(340, 380, 200, 40);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(null);
        contentPane.add(btnRegister);

        // Login Link
        JLabel lblLogin = new JLabel("Already have an account? Login here");
        lblLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PatientLoginFrame login = new PatientLoginFrame();
                login.LoginScreen("Doctor");
                login.setVisible(true);
                dispose();
            }
        });
        lblLogin.setHorizontalAlignment(SwingConstants.LEFT);
        lblLogin.setForeground(new Color(41, 128, 185));
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLogin.setBounds(340, 430, 250, 20);
        contentPane.add(lblLogin);
    }

    private void registerDoctor() {
        String name = nameField.getText();
        String spec = specField.getText(); 
        String email = emailField.getText();
        String cnic = cnicField.getText();
        String license = licenseField.getText();
        String password = new String(passwordField.getPassword());

        // --- VALIDATIONS START ---

        // 1. Empty Check
        if(name.isEmpty() || spec.isEmpty() || email.isEmpty() || cnic.isEmpty() || license.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Name Validation
        if(name.length() < 5) {
            JOptionPane.showMessageDialog(this, "Full Name must be at least 5 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Email Validation
        if(!email.contains("@") || !email.endsWith(".com")) {
            JOptionPane.showMessageDialog(this, "Invalid Email! Must contain '@' and end with '.com'.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. CNIC Validation (Must be 13 digits and Numeric)
        if(!cnic.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "CNIC must be exactly 13 digits (numbers only).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. License Validation
        if(license.length() < 5) {
            JOptionPane.showMessageDialog(this, "License Number seems too short.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 6. Specialization Check
        if(spec.length() < 3) {
             JOptionPane.showMessageDialog(this, "Please enter a valid specialization.", "Validation Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        // 7. Password Validation
        if(password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // --- VALIDATIONS END ---

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms_db", "hms_user", "hms_pass");

            // Check duplicate Email or License
            String checkQuery = "SELECT * FROM doc_signup WHERE Email=? OR license_no=?";
            PreparedStatement checkPst = con.prepareStatement(checkQuery);
            checkPst.setString(1, email);
            checkPst.setString(2, license);
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "A Doctor with this Email or License already exists!", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
            } else {
                // INSERT NEW DOCTOR
                String insertQuery = "INSERT INTO doc_signup (Name, Specialization, Email, Password, cnic, license_no, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(insertQuery);
                pst.setString(1, name);
                pst.setString(2, spec);
                pst.setString(3, email);
                pst.setString(4, password);
                pst.setString(5, cnic);
                pst.setString(6, license);
                pst.setString(7, "PENDING"); 

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!\n\nYour account is PENDING approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    PatientLoginFrame login = new PatientLoginFrame();
                    login.LoginScreen("Doctor");
                    login.setVisible(true);
                    dispose();
                }
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
}