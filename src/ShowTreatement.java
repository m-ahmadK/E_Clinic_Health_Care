import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class ShowTreatement extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    private JTextField txtName;
    private JTextArea txtTreatment, txtMedicine, txtPrevention;
    private JLabel lblAuthor;
    
    private JButton btnEditSave, btnDelete;
    private boolean isEditing = false;
    
    // Context
    private int docID;
    private String userRole; // "Doctor" or Patient Email
    private int disID;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = new Color(245, 247, 250);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ShowTreatement(1, "Doctor", 1).setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    /**
     * @param docID - Doctor ID (0 if patient)
     * @param roleOrEmail - "Doctor" or Patient Email
     */
    public ShowTreatement(int docID, String roleOrEmail, int disID) {
        this.docID = docID;
        this.userRole = roleOrEmail; // This will hold "Doctor" OR "patient@email.com"
        this.disID = disID;

        setUndecorated(true); // Match style
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 750);
        setTitle("Disease Details");
        setUndecorated(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(0, 0, 900, 70);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Treatment Standard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(70, 20, 300, 30);
        header.add(lblTitle);

        JLabel btnBack = new JLabel("< Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBounds(15, 20, 60, 30);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Pass the role back correctly so ShowDisease knows who we are
                ShowDisease s = new ShowDisease(docID, userRole);
                s.setVisible(true);
                dispose();
            }
        });
        header.add(btnBack);

        // --- FORM ---
        JLabel lblName = new JLabel("Disease Name:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setBounds(50, 100, 150, 20);
        contentPane.add(lblName);

        txtName = new JTextField();
        txtName.setEditable(false);
        txtName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtName.setBounds(50, 125, 400, 35);
        contentPane.add(txtName);
        
        lblAuthor = new JLabel("Authored By: ...");
        lblAuthor.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblAuthor.setForeground(Color.GRAY);
        lblAuthor.setBounds(470, 135, 300, 20);
        contentPane.add(lblAuthor);

        JLabel lblTreat = new JLabel("Standard Treatment / Cure:");
        lblTreat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTreat.setBounds(50, 180, 250, 20);
        contentPane.add(lblTreat);

        txtTreatment = createArea();
        JScrollPane s1 = new JScrollPane(txtTreatment);
        s1.setBounds(50, 205, 800, 100);
        contentPane.add(s1);

        JLabel lblMed = new JLabel("Recommended Medicines:");
        lblMed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMed.setBounds(50, 320, 250, 20);
        contentPane.add(lblMed);

        txtMedicine = createArea();
        JScrollPane s2 = new JScrollPane(txtMedicine);
        s2.setBounds(50, 345, 800, 60);
        contentPane.add(s2);

        JLabel lblPrev = new JLabel("Prevention & Extra Details:");
        lblPrev.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrev.setBounds(50, 420, 250, 20);
        contentPane.add(lblPrev);

        txtPrevention = createArea();
        JScrollPane s3 = new JScrollPane(txtPrevention);
        s3.setBounds(50, 445, 800, 100);
        contentPane.add(s3);

        // --- SECURITY CHECK FOR BUTTONS ---
        // Only show if userRole explicitly equals "Doctor"
        // Patients will have their email string here, so this will be FALSE for them.
        if("Doctor".equalsIgnoreCase(userRole)) {
            
            btnEditSave = new JButton("Edit Record");
            btnEditSave.setBounds(530, 600, 150, 40);
            btnEditSave.setBackground(PRIMARY_COLOR);
            btnEditSave.setForeground(Color.WHITE);
            btnEditSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnEditSave.setFocusPainted(false);
            btnEditSave.addActionListener(e -> toggleEditMode());
            contentPane.add(btnEditSave);

            btnDelete = new JButton("Delete Record");
            btnDelete.setBounds(700, 600, 150, 40);
            btnDelete.setBackground(new Color(231, 76, 60)); 
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnDelete.setFocusPainted(false);
            btnDelete.addActionListener(e -> deleteRecord());
            contentPane.add(btnDelete);
        }

        loadData();
    }

    private JTextArea createArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return area;
    }

    private void loadData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String query = "SELECT doc_signup.Name, disease_record.* FROM disease_record "
                         + "JOIN doc_signup ON doc_signup.ID = disease_record.D_ID WHERE disease_record.ID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, disID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtName.setText(rs.getString("dis_name"));
                lblAuthor.setText("Authored By: Dr. " + rs.getString("Name"));
                txtTreatment.setText(rs.getString("cure"));
                try { txtMedicine.setText(rs.getString("medicine")); } catch (Exception e) {}
                try { txtPrevention.setText(rs.getString("prevention")); } catch (Exception e) {}
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleEditMode() {
        if (!isEditing) {
            isEditing = true;
            txtName.setEditable(true);
            txtTreatment.setEditable(true);
            txtMedicine.setEditable(true);
            txtPrevention.setEditable(true);
            
            btnEditSave.setText("Save Changes");
            btnEditSave.setBackground(new Color(46, 204, 113)); 
            btnDelete.setEnabled(false); 
        } else {
            saveChanges();
        }
    }

    private void saveChanges() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "UPDATE disease_record SET dis_name=?, cure=?, medicine=?, prevention=? WHERE ID=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, txtName.getText());
            ps.setString(2, txtTreatment.getText());
            ps.setString(3, txtMedicine.getText());
            ps.setString(4, txtPrevention.getText());
            ps.setInt(5, disID);

            int i = ps.executeUpdate();
            if (i > 0) {
                JOptionPane.showMessageDialog(null, "Record Updated!");
                isEditing = false;
                txtName.setEditable(false);
                txtTreatment.setEditable(false);
                txtMedicine.setEditable(false);
                txtPrevention.setEditable(false);
                btnEditSave.setText("Edit Record");
                btnEditSave.setBackground(PRIMARY_COLOR);
                btnDelete.setEnabled(true);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteRecord() {
        int opt = JOptionPane.showConfirmDialog(null, "Delete this record?", "Warning", JOptionPane.YES_NO_OPTION);
        if(opt == JOptionPane.YES_OPTION) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "DELETE FROM disease_record WHERE ID=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, disID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Deleted.");
                
                // Return to list with correct role
                ShowDisease s = new ShowDisease(docID, userRole);
                s.setVisible(true);
                dispose();
                con.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}