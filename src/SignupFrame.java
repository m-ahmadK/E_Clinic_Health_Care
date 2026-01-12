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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class SignupFrame extends JFrame {

    private JPanel contentPane;
    private JTextField nameF;
    private JTextField ageF;
    private JTextField emailF;
    private JPasswordField passwordF;
    private JComboBox<String> genderBox; 
    
    int xx, xy;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SignupFrame frame = new SignupFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SignupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 1));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // --- LEFT PANEL ---
        JPanel panel = new JPanel();
        panel.setBackground(new Color(41, 128, 185));
        panel.setBounds(0, 0, 300, 550);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblTitle = new JLabel("E-Health Care");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(0, 150, 300, 40);
        panel.add(lblTitle);

        JLabel lblSub = new JLabel("Patient Registration");
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setForeground(new Color(220, 220, 220));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setBounds(0, 190, 300, 30);
        panel.add(lblSub);

        JLabel lblLogin = new JLabel("Already have an account?");
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogin.setForeground(Color.WHITE);
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLogin.setBounds(0, 430, 300, 20);
        panel.add(lblLogin);

        JButton loginBtn = new JButton("Login Here");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PatientLoginFrame f = new PatientLoginFrame();
                f.LoginScreen("Patient");
                f.setVisible(true);
                dispose();
            }
        });
        loginBtn.setForeground(new Color(41, 128, 185));
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBounds(75, 460, 150, 35);
        panel.add(loginBtn);


        // --- RIGHT PANEL ---
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
                SignupFrame.this.setLocation(x - xx, y - xy);  
            }
        });
        dragLabel.setBounds(0, 0, 800, 40);
        contentPane.add(dragLabel);

        JLabel lblClose = new JLabel("X");
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
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

        JLabel lblHeader = new JLabel("Create Account");
        lblHeader.setForeground(new Color(100, 100, 100));
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setBounds(340, 30, 200, 40);
        contentPane.add(lblHeader);

        // 1. Name (Username)
        JLabel lblName = new JLabel("Full Name / Username");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblName.setBounds(340, 80, 200, 20);
        contentPane.add(lblName);
        
        nameF = new JTextField();
        nameF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameF.setBounds(340, 105, 220, 30);
        contentPane.add(nameF);
        nameF.setColumns(10);

        // 2. Age
        JLabel lblAge = new JLabel("Age");
        lblAge.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAge.setBounds(580, 80, 100, 20);
        contentPane.add(lblAge);
        
        ageF = new JTextField();
        ageF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ageF.setBounds(580, 105, 220, 30);
        contentPane.add(ageF);
        ageF.setColumns(10);

        // 3. Gender
        JLabel lblGender = new JLabel("Gender");
        lblGender.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGender.setBounds(340, 145, 100, 20);
        contentPane.add(lblGender);

        genderBox = new JComboBox<String>();
        genderBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Select Gender", "Male", "Female", "Other"}));
        genderBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderBox.setBackground(Color.WHITE);
        genderBox.setBounds(340, 170, 220, 30);
        contentPane.add(genderBox);

        // 4. Email
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setBounds(340, 210, 150, 20);
        contentPane.add(lblEmail);
        
        emailF = new JTextField();
        emailF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailF.setBounds(340, 235, 460, 30); 
        contentPane.add(emailF);
        emailF.setColumns(10);

        // 5. Password
        JLabel lblPass = new JLabel("Create Password");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(340, 275, 150, 20);
        contentPane.add(lblPass);
        
        passwordF = new JPasswordField();
        passwordF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordF.setBounds(340, 300, 460, 30);
        contentPane.add(passwordF);

        // Register Button
        JButton SignupBtn = new JButton("Register Patient");
        SignupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                String name = nameF.getText().trim();
                String ageText = ageF.getText().trim();
                String email = emailF.getText().trim();
                String password = new String(passwordF.getPassword());
                String gender = (String) genderBox.getSelectedItem();

                // --- VALIDATIONS START ---

                // 1. Basic Empty Check
                if(name.isEmpty() || ageText.isEmpty() || email.isEmpty() || password.isEmpty() || gender.equals("Select Gender")) {
                    JOptionPane.showMessageDialog(SignupBtn, "Please fill in all fields and select a gender.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 2. Name Validation
                if(name.length() < 5) {
                    JOptionPane.showMessageDialog(SignupBtn, "Username/Name must be at least 5 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 3. Age Validation
                int age = 0;
                try {
                    age = Integer.parseInt(ageText);
                    if(ageText.length() < 2 || ageText.length() > 3) {
                         JOptionPane.showMessageDialog(SignupBtn, "Age must be 2 or 3 digits long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                         return;
                    }
                    if(age <= 14) {
                        JOptionPane.showMessageDialog(SignupBtn, "You must be older than 14 to register.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SignupBtn, "Age must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 4. Email Validation
                if(!email.contains("@") || !email.endsWith(".com")) {
                    JOptionPane.showMessageDialog(SignupBtn, "Invalid Email! Must contain '@' and end with '.com'.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 5. Password Validation
                if(password.length() < 8) {
                    JOptionPane.showMessageDialog(SignupBtn, "Password must be at least 8 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // --- VALIDATIONS END ---
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String url = "jdbc:mysql://localhost:3306/hms_db"; 
                    String user = "hms_user";
                    String pass = "hms_pass";
                    Connection con = DriverManager.getConnection(url, user, pass);

                    // --- CHECK 1: EMAIL DUPLICATION ---
                    String checkEmailQuery = "SELECT * FROM pat_signup WHERE email = ?";
                    PreparedStatement checkEmailStmt = con.prepareStatement(checkEmailQuery);
                    checkEmailStmt.setString(1, email);
                    ResultSet rsEmail = checkEmailStmt.executeQuery();

                    if (rsEmail.next()) {
                        JOptionPane.showMessageDialog(SignupBtn, "This Email is already in use.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                        rsEmail.close();
                        checkEmailStmt.close();
                        con.close();
                        return; // STOP EXECUTION
                    }

                    // --- CHECK 2: USERNAME (NAME) DUPLICATION ---
                    String checkNameQuery = "SELECT * FROM pat_signup WHERE name = ?";
                    PreparedStatement checkNameStmt = con.prepareStatement(checkNameQuery);
                    checkNameStmt.setString(1, name);
                    ResultSet rsName = checkNameStmt.executeQuery();

                    if (rsName.next()) {
                        JOptionPane.showMessageDialog(SignupBtn, "This Username is already in use.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                        rsName.close();
                        checkNameStmt.close();
                        con.close();
                        return; // STOP EXECUTION
                    }

                    // --- INSERT NEW USER ---
                    String insertQuery = "INSERT INTO pat_signup (email, password, name, age, gender) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertQuery);

                    insertStmt.setString(1, email);
                    insertStmt.setString(2, password);
                    insertStmt.setString(3, name);
                    insertStmt.setInt(4, age);
                    insertStmt.setString(5, gender);

                    int i = insertStmt.executeUpdate();
                    if(i > 0) {
                         JOptionPane.showMessageDialog(SignupBtn, "Registration Successful! Please Login.");
                         PatientLoginFrame f = new PatientLoginFrame();
                         f.LoginScreen("Patient");
                         f.setVisible(true);
                         dispose();
                    }
                    
                    // Close all resources
                    insertStmt.close();
                    rsEmail.close();
                    rsName.close();
                    checkEmailStmt.close();
                    checkNameStmt.close();
                    con.close();

                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(SignupBtn, "Database Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        SignupBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        SignupBtn.setForeground(Color.WHITE);
        SignupBtn.setBackground(new Color(41, 128, 185));
        SignupBtn.setFocusPainted(false);
        SignupBtn.setBorder(null);
        SignupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        SignupBtn.setBounds(340, 360, 200, 40); 
        contentPane.add(SignupBtn);
    }
}