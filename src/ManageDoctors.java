import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ManageDoctors extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public ManageDoctors() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700);
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(0, 0, 1100, 60);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Manage Doctors");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 300, 30);
        header.add(lblTitle);

        // --- BACK BUTTON ---
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(980, 15, 80, 30);
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new LineBorder(Color.BLACK, 1));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            new AdminPanel().setVisible(true);
            dispose();
        });
        header.add(btnBack);

        // --- TABLE ---
        String[] columns = {"ID", "Name", "Specialization", "Email", "License No"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 1000, 450);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(scrollPane);

        // --- DELETE BUTTON (Moved to Bottom Right Sequence) ---
        JButton btnDelete = new JButton("Delete Selected Doctor");
        btnDelete.setBounds(630, 570, 250, 45); // X=630
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnDelete.setBackground(Color.WHITE);
        btnDelete.setForeground(Color.DARK_GRAY);
        btnDelete.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        btnDelete.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnDelete.setBackground(new Color(231, 76, 60)); // Red
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnDelete.setBackground(Color.WHITE);
                btnDelete.setForeground(Color.DARK_GRAY);
                btnDelete.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnDelete.addActionListener(e -> deleteDoctor());
        contentPane.add(btnDelete);

        // --- ADD BUTTON (Moved to Far Bottom Right) ---
        JButton btnAdd = new JButton("Add New Doctor");
        btnAdd.setBounds(900, 570, 150, 45); // X=900 (Sequence: Delete -> Add)
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setBackground(Color.WHITE);
        btnAdd.setForeground(Color.DARK_GRAY);
        btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        btnAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(Color.GREEN); // 
                btnAdd.setForeground(Color.WHITE);
                btnAdd.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(Color.WHITE);
                btnAdd.setForeground(Color.DARK_GRAY);
                btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        btnAdd.addActionListener(e -> openAddDoctorDialog());
        contentPane.add(btnAdd);

        loadDoctors();
    }

    private void loadDoctors() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM doc_signup");
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getString("Specialization"),
                    rs.getString("Email"),
                    rs.getString("license_no")
                });
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openAddDoctorDialog() {
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtSpec = new JTextField();
        JTextField txtLic = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Full Name:")); panel.add(txtName);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Password:")); panel.add(txtPass);
        panel.add(new JLabel("Specialization:")); panel.add(txtSpec);
        panel.add(new JLabel("License No:")); panel.add(txtLic);

        int result = JOptionPane.showConfirmDialog(null, panel, "Register New Doctor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());
            String name = txtName.getText();
            
            if(name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            if(!email.contains("@") || !email.endsWith(".com")) {
                JOptionPane.showMessageDialog(this, "Invalid Email! Must contain '@' and end with '.com'.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(pass.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                
                String sql = "INSERT INTO doc_signup (Name, Email, Password, Specialization, license_no) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, pass);
                pst.setString(4, txtSpec.getText());
                pst.setString(5, txtLic.getText());
                
                pst.executeUpdate();
                con.close();
                
                JOptionPane.showMessageDialog(this, "Doctor Added Successfully!");
                loadDoctors(); 
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding doctor: " + e.getMessage());
            }
        }
    }

    private void deleteDoctor() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove Dr. " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pst = con.prepareStatement("DELETE FROM doc_signup WHERE ID = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
                con.close();
                
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Doctor removed successfully.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}