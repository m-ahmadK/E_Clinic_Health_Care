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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ManageMedicalKB extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public ManageMedicalKB() {
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

        JLabel lblTitle = new JLabel("Manage Medical Knowledge Base");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 400, 30);
        header.add(lblTitle);

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
        // Showing the ID, Condition Name, and the Advice text
        String[] columns = {"ID", "Condition Name", "Medical Advice"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Adjust column width for "Advice" since it is long text
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(700);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 1000, 450);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(scrollPane);

        // --- DELETE BUTTON ---
        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setBounds(630, 570, 250, 45); 
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
        
        btnDelete.addActionListener(e -> deleteCondition());
        contentPane.add(btnDelete);

        // --- ADD BUTTON ---
        JButton btnAdd = new JButton("Add New Condition");
        btnAdd.setBounds(900, 570, 150, 45); 
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setBackground(Color.WHITE);
        btnAdd.setForeground(Color.DARK_GRAY);
        btnAdd.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
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
        
        btnAdd.addActionListener(e -> openAddDialog());
        contentPane.add(btnAdd);

        loadConditions();
    }

    private void loadConditions() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM Conditions");
            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("condition_id"),
                    rs.getString("name"),
                    rs.getString("advice")
                });
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openAddDialog() {
        JTextField txtName = new JTextField();
        JTextArea txtAdvice = new JTextArea(5, 20);
        txtAdvice.setLineWrap(true);
        txtAdvice.setWrapStyleWord(true);
        JScrollPane scrollAdvice = new JScrollPane(txtAdvice);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Condition Name (e.g., Flu):")); 
        panel.add(txtName);
        panel.add(new JLabel("Medical Advice / Treatment:")); 
        panel.add(scrollAdvice);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Medical Condition",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText();
            String advice = txtAdvice.getText();

            if(name.isEmpty() || advice.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String sql = "INSERT INTO Conditions (name, advice) VALUES (?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, advice);
                
                pst.executeUpdate();
                con.close();
                
                JOptionPane.showMessageDialog(this, "Condition Added Successfully!");
                loadConditions();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding condition: " + e.getMessage());
            }
        }
    }

    private void deleteCondition() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a condition to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement pst = con.prepareStatement("DELETE FROM Conditions WHERE condition_id = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
                con.close();
                
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Condition deleted successfully.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}