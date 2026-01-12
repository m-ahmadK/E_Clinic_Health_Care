import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EditProfile extends JFrame {

    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtAge;
    private JTextField txtAddress;
    private JLabel lblImagePreview;
    private File selectedFile;

    private String userEmail;
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    public EditProfile(String email) {
        this.userEmail = email;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 600);
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setBounds(0, 0, 500, 60);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Edit Profile");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(20, 15, 200, 30);
        header.add(lblTitle);

        JLabel lblClose = new JLabel("X");
        lblClose.setForeground(Color.WHITE);
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setBounds(460, 15, 20, 30);
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new PatProfile(userEmail).setVisible(true);
                dispose();
            }
        });
        header.add(lblClose);

        // --- IMAGE SECTION ---
        lblImagePreview = new JLabel("No Image");
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImagePreview.setBounds(175, 80, 150, 150);
        contentPane.add(lblImagePreview);

        JButton btnUpload = new JButton("Upload Photo");
        btnUpload.setBounds(175, 240, 150, 30);
        btnUpload.setBackground(new Color(245, 247, 250));
        btnUpload.setFocusPainted(false);
        btnUpload.addActionListener(e -> chooseImage());
        contentPane.add(btnUpload);

        // --- FIELDS ---
        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(50, 290, 100, 25);
        contentPane.add(lblName);
        txtName = new JTextField();
        txtName.setBounds(50, 315, 400, 35);
        contentPane.add(txtName);

        JLabel lblAge = new JLabel("Age:");
        lblAge.setBounds(50, 360, 100, 25);
        contentPane.add(lblAge);
        txtAge = new JTextField();
        txtAge.setBounds(50, 385, 400, 35);
        contentPane.add(txtAge);
        
        JLabel lblAddr = new JLabel("Address:");
        lblAddr.setBounds(50, 430, 100, 25);
        contentPane.add(lblAddr);
        txtAddress = new JTextField();
        txtAddress.setBounds(50, 455, 400, 35);
        contentPane.add(txtAddress);

        // --- SAVE BUTTON ---
        JButton btnSave = new JButton("SAVE CHANGES");
        btnSave.setBounds(50, 520, 400, 45);
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSave.addActionListener(e -> saveProfile());
        contentPane.add(btnSave);

        loadCurrentData();
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            ImageIcon ii = new ImageIcon(selectedFile.getAbsolutePath());
            // Resize for preview
            Image img = ii.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
            lblImagePreview.setText("");
        }
    }

    private void loadCurrentData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM pat_signup WHERE email = ?");
            pst.setString(1, userEmail);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtAge.setText(String.valueOf(rs.getInt("age")));
                txtAddress.setText(rs.getString("address"));
                
                // Load existing image if available
                byte[] imgData = rs.getBytes("profile_picture");
                if(imgData != null) {
                    ImageIcon ii = new ImageIcon(imgData);
                    Image img = ii.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    lblImagePreview.setIcon(new ImageIcon(img));
                    lblImagePreview.setText("");
                }
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveProfile() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Query depends on whether an image was uploaded
            String sql;
            if(selectedFile != null) {
                sql = "UPDATE pat_signup SET name=?, age=?, address=?, profile_picture=? WHERE email=?";
            } else {
                sql = "UPDATE pat_signup SET name=?, age=?, address=? WHERE email=?";
            }

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtName.getText());
            pst.setInt(2, Integer.parseInt(txtAge.getText()));
            pst.setString(3, txtAddress.getText());

            if(selectedFile != null) {
                FileInputStream fis = new FileInputStream(selectedFile);
                pst.setBinaryStream(4, fis, (int) selectedFile.length());
                pst.setString(5, userEmail);
            } else {
                pst.setString(4, userEmail);
            }

            pst.executeUpdate();
            con.close();
            JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            new PatProfile(userEmail).setVisible(true);
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }
}