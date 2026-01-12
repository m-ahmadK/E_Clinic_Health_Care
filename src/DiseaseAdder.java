import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class DiseaseAdder extends JFrame {
    
    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    public DiseaseAdder(int docID) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500);
        setTitle("Add New Disease Entry");
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(null);
        setContentPane(p);
        
        // Header
        JPanel h = new JPanel();
        h.setBackground(new Color(41, 128, 185));
        h.setBounds(0,0,500,50);
        p.add(h);
        JLabel l = new JLabel("Add to Library");
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.add(l);
        
        // Fields
        JLabel l1 = new JLabel("Disease Name:");
        l1.setBounds(30, 80, 200, 20);
        p.add(l1);
        JTextField tName = new JTextField();
        tName.setBounds(30, 105, 420, 30);
        p.add(tName);
        
        JLabel l2 = new JLabel("Standard Cure/Treatment:");
        l2.setBounds(30, 150, 200, 20);
        p.add(l2);
        JTextArea tCure = new JTextArea();
        tCure.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        tCure.setBounds(30, 175, 420, 150);
        p.add(tCure);
        
        // Save
        JButton btnSave = new JButton("Save to Library");
        btnSave.setBounds(150, 350, 200, 40);
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String q = "INSERT INTO disease_record (dis_name, cure, D_ID) VALUES (?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(q);
                pst.setString(1, tName.getText());
                pst.setString(2, tCure.getText());
                pst.setInt(3, docID);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Saved!");
                new ShowDisease(docID, "Doctor").setVisible(true);
                dispose();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        p.add(btnSave);
        
        // Cancel
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(200, 400, 100, 30);
        btnCancel.addActionListener(e -> {
            new ShowDisease(docID, "Doctor").setVisible(true);
            dispose();
        });
        p.add(btnCancel);
    }
}