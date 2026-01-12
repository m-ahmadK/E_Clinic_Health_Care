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

public class ManagePatients extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public ManagePatients() {
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

        JLabel lblTitle = new JLabel("Manage Patients");
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
        // Patients usually don't have License numbers, so we show Name, Email, Age, Gender, Address
        String[] columns = {"Name", "Email", "Age", "Gender", "Address"};
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

        // --- DELETE BUTTON (Bottom Right Sequence) ---
        JButton btnDelete = new JButton("Delete Selected Patient");
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
        
        btnDelete.addActionListener(e -> deletePatient());
        contentPane.add(btnDelete);

        // --- ADD BUTTON (Far Bottom Right) ---
        JButton btnAdd = new JButton("Add New Patient");
        btnAdd.setBounds(900, 570, 150, 45); // X=900 (Sequence: Delete -> Add)
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setBackground(Color.WHITE); // Starts White per your request styling
        btnAdd.setForeground(Color.DARK_GRAY);
        btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Add Green Hover Effect
        btnAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(new Color(46, 204, 113)); // Green
                btnAdd.setForeground(Color.WHITE);
                btnAdd.setBorder(null);
            }
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(Color.WHITE);
                btnAdd.setForeground(Color.DARK_GRAY);
                btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
        });
        
        btnAdd.addActionListener(e -> openAddPatientDialog());
        contentPane.add(btnAdd);

        loadPatients();
    }

    private void loadPatients() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM pat_signup");
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("age"),
                    rs.getString("gender"),
                    rs.getString("address")
                });
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openAddPatientDialog() {
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtAge = new JTextField();
        JTextField txtGender = new JTextField();
        JTextField txtAddr = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Full Name:")); panel.add(txtName);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Password:")); panel.add(txtPass);
        panel.add(new JLabel("Age:")); panel.add(txtAge);
        panel.add(new JLabel("Gender:")); panel.add(txtGender);
        panel.add(new JLabel("Address:")); panel.add(txtAddr);

        int result = JOptionPane.showConfirmDialog(null, panel, "Register New Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = txtEmail.getText();
            String pass = new String(txtPass.getPassword());
            String name = txtName.getText();
            
            // --- VALIDATION ---
            if(name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
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
                
                String sql = "INSERT INTO pat_signup (name, email, password, age, gender, address) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, pass);
                pst.setInt(4, Integer.parseInt(txtAge.getText())); // Ensure age is a number
                pst.setString(5, txtGender.getText());
                pst.setString(6, txtAddr.getText());
                
                pst.executeUpdate();
                con.close();
                
                JOptionPane.showMessageDialog(this, "Patient Added Successfully!");
                loadPatients(); 
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding patient: " + e.getMessage());
            }
        }
    }

    private void deletePatient() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            return;
        }

        // We use Email to identify the patient (Column Index 1)
        String email = (String) model.getValueAt(row, 1);
        String name = (String) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove patient: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pst = con.prepareStatement("DELETE FROM pat_signup WHERE email = ?");
                pst.setString(1, email);
                pst.executeUpdate();
                con.close();
                
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Patient removed successfully.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}