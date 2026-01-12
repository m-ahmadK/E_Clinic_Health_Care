import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

public class ConsultationForm extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // Components
    private JTextArea txtPatientInfo; 
    private JTextArea txtPrescription; 
    private JTextArea txtAdvice;       
    
    // Data Variables
    private int docID;
    private String patEmail;
    private String appTime;

    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Medical Blue
    private final Color BG_COLOR = new Color(245, 247, 250);     // Light Gray
    private final Color NOTE_COLOR = new Color(255, 252, 220);   // Pale Yellow for Notes

    public ConsultationForm(int docID, String pEmail, String time) {
        this.docID = docID;
        this.patEmail = pEmail;
        this.appTime = time;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 750);
        setTitle("Patient Consultation & Diagnosis");
        
        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setUndecorated(true);
        setLocationRelativeTo(null);

        // =========================================================================
        // 1. HEADER
        // =========================================================================
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(0, 0, 700, 70);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Consultation Room");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 300, 40);
        header.add(lblTitle);
        
        JLabel lblSub = new JLabel("Review symptoms and prescribe treatment");
        lblSub.setForeground(new Color(220, 220, 220));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setBounds(25, 45, 300, 20);
        header.add(lblSub);

        // =========================================================================
        // 2. PATIENT HISTORY (Read Only)
        // =========================================================================
        JLabel lblPInfo = new JLabel("Patient Clinical Notes:");
        lblPInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPInfo.setForeground(new Color(100, 100, 100));
        lblPInfo.setBounds(40, 90, 300, 20);
        contentPane.add(lblPInfo);

        txtPatientInfo = new JTextArea();
        txtPatientInfo.setEditable(false); 
        txtPatientInfo.setBackground(NOTE_COLOR); // Sticky note look
        txtPatientInfo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(240, 230, 140)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtPatientInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPatientInfo.setLineWrap(true);
        txtPatientInfo.setWrapStyleWord(true);
        
        JScrollPane scrollInfo = new JScrollPane(txtPatientInfo);
        scrollInfo.setBounds(40, 115, 600, 120);
        contentPane.add(scrollInfo);

        // =========================================================================
        // 3. DOCTOR'S DIAGNOSIS
        // =========================================================================
        
        // Prescription
        JLabel lblRx = new JLabel("Prescription (Rx):");
        lblRx.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRx.setForeground(PRIMARY_COLOR);
        lblRx.setBounds(40, 250, 200, 20);
        contentPane.add(lblRx);

        txtPrescription = new JTextArea();
        txtPrescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPrescription.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollRx = new JScrollPane(txtPrescription);
        scrollRx.setBounds(40, 275, 600, 100);
        scrollRx.setBorder(new LineBorder(Color.LIGHT_GRAY));
        contentPane.add(scrollRx);

        // Advice
        JLabel lblAdvice = new JLabel("Medical Advice / Instructions:");
        lblAdvice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdvice.setForeground(PRIMARY_COLOR);
        lblAdvice.setBounds(40, 390, 300, 20);
        contentPane.add(lblAdvice);

        txtAdvice = new JTextArea();
        txtAdvice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAdvice.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollAdv = new JScrollPane(txtAdvice);
        scrollAdv.setBounds(40, 415, 600, 100);
        scrollAdv.setBorder(new LineBorder(Color.LIGHT_GRAY));
        contentPane.add(scrollAdv);

        // =========================================================================
        // 4. ACTION BUTTONS
        // =========================================================================
        JButton btnSave = new JButton("Complete Consultation");
        btnSave.setBackground(new Color(46, 204, 113)); // Success Green
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setBounds(150, 540, 400, 50);
        btnSave.addActionListener(e -> savePrescription());
        contentPane.add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(275, 610, 150, 30);
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(192, 57, 43));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> {
            Left_Pat l = new Left_Pat(docID);
            l.setVisible(true);
            dispose();
        });
        contentPane.add(btnCancel);

        // Load data immediately
        loadPatientData();
    }

    private void loadPatientData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String query = "SELECT phone, symptoms, disease_description FROM appointment_record WHERE p_email=? AND a_time=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, patEmail);
            pst.setString(2, appTime);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) {
                String phone = rs.getString("phone");
                String symptoms = rs.getString("symptoms");
                String history = rs.getString("disease_description");

                StringBuilder sb = new StringBuilder();
                sb.append("Patient: ").append(patEmail).append(" | Phone: ").append(phone).append("\n\n");
                sb.append("REPORTED SYMPTOMS:\n").append(symptoms).append("\n\n");
                sb.append("MEDICAL HISTORY:\n").append(history);
                
                txtPatientInfo.setText(sb.toString());
            }
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void savePrescription() {
        if(txtPrescription.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please write a prescription before completing.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String updateQ = "UPDATE appointment_record SET prescription=?, doctor_advice=?, status='Checked' WHERE p_email=? AND a_time=?";
            PreparedStatement pst = con.prepareStatement(updateQ);
            pst.setString(1, txtPrescription.getText());
            pst.setString(2, txtAdvice.getText());
            pst.setString(3, patEmail);
            pst.setString(4, appTime);
            
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Consultation Completed Successfully!");
            Left_Pat l = new Left_Pat(docID);
            l.setVisible(true);
            dispose();
            
            con.close();
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}