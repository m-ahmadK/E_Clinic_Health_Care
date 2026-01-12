import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class DealingRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField di_nameF;
    private JTextField symptomsField;
    private JTextField medicineField;
    private JTextArea extraDetailArea;

    // Database Credentials
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DealingRecord frame = new DealingRecord(0, "");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public DealingRecord(int doc_id, String pat_mail) {
        setTitle("Health Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        getContentPane().setLayout(null); // Explicit layout

        JLabel lblDiseaseName = new JLabel("Disease Name:");
        lblDiseaseName.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblDiseaseName.setBounds(130, 125, 142, 32);
        getContentPane().add(lblDiseaseName);

        di_nameF = new JTextField();
        di_nameF.setBounds(330, 122, 288, 32);
        getContentPane().add(di_nameF);
        di_nameF.setColumns(10);

        JLabel lblSymptoms = new JLabel("Symptoms:");
        lblSymptoms.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSymptoms.setBounds(130, 167, 142, 32);
        getContentPane().add(lblSymptoms);

        symptomsField = new JTextField();
        symptomsField.setBounds(330, 164, 288, 32);
        getContentPane().add(symptomsField);
        symptomsField.setColumns(10);

        JLabel lblMedicine = new JLabel("Medicine:");
        lblMedicine.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblMedicine.setBounds(130, 210, 142, 34);
        getContentPane().add(lblMedicine);

        medicineField = new JTextField();
        medicineField.setBounds(330, 211, 288, 37);
        getContentPane().add(medicineField);
        medicineField.setColumns(10);

        JLabel lblExtraDetail = new JLabel("Extra Detail:");
        lblExtraDetail.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblExtraDetail.setBounds(130, 296, 142, 104);
        getContentPane().add(lblExtraDetail);

        extraDetailArea = new JTextArea();
        extraDetailArea.setBounds(330, 296, 294, 104);
        extraDetailArea.setRows(3);
        extraDetailArea.setColumns(20);
        getContentPane().add(extraDetailArea);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    // --- 1. INSERT MEDICAL RECORD ---
                    Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                    // Note: 'id' is Auto Increment, so we don't insert it.
                    // Columns: D_ID, pat_email, dis_name, symptoms, medicine, extras
                    String insertQuery = "INSERT INTO med_record (D_ID, pat_email, dis_name, symptoms, medicine, extras) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertQuery);

                    insertStmt.setInt(1, doc_id);
                    insertStmt.setString(2, pat_mail);
                    insertStmt.setString(3, di_nameF.getText());
                    insertStmt.setString(4, symptomsField.getText());
                    insertStmt.setString(5, medicineField.getText());
                    insertStmt.setString(6, extraDetailArea.getText());

                    int i = insertStmt.executeUpdate();

                    insertStmt.close();
                    con.close();

                    if (i > 0) {
                        JOptionPane.showMessageDialog(null, "Record added successfully");
                        
                        // --- 2. UPDATE APPOINTMENT STATUS ---
                        Connection con2 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        
                        // Mark appointment as 'Dealt'
                        String updateQuery = "UPDATE appointment_record SET status = ? WHERE p_email = ? AND D_ID = ?";
                        PreparedStatement updateStmt = con2.prepareStatement(updateQuery);

                        updateStmt.setString(1, "Dealt");
                        updateStmt.setString(2, pat_mail);
                        updateStmt.setInt(3, doc_id);
                        
                        updateStmt.executeUpdate();
                        
                        updateStmt.close();
                        con2.close();
                        
                        // Navigate back
                        Left_Pat l = new Left_Pat(doc_id);
                        l.setVisible(true);
                        dispose();
                    }

                } catch (Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e2.getMessage());
                }
            }
        });
        btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnSubmit.setBounds(405, 447, 135, 34);
        getContentPane().add(btnSubmit);

        JButton backBtn = new JButton("<<");
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Left_Pat p = new Left_Pat(doc_id);
                p.setVisible(true);
                dispose();
            }
        });
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        backBtn.setBounds(29, 30, 56, 32);
        getContentPane().add(backBtn);
    }
}