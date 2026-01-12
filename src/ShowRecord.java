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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ShowRecord extends JFrame {

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
                    ShowRecord frame = new ShowRecord(0, "");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ShowRecord(int doc_id, String pat_mail) {
        setTitle("Health Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        getContentPane().setLayout(null); // Set layout immediately

        JLabel lblDiseaseName = new JLabel("Disease Name:");
        lblDiseaseName.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblDiseaseName.setBounds(130, 125, 142, 32);
        getContentPane().add(lblDiseaseName);

        di_nameF = new JTextField();
        di_nameF.setEditable(false);
        di_nameF.setBounds(330, 122, 288, 32);
        getContentPane().add(di_nameF);
        di_nameF.setColumns(10);

        JLabel lblSymptoms = new JLabel("Symptoms:");
        lblSymptoms.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSymptoms.setBounds(130, 167, 142, 32);
        getContentPane().add(lblSymptoms);

        symptomsField = new JTextField();
        symptomsField.setForeground(new Color(0, 0, 0));
        symptomsField.setEditable(false); // Changed from setEnabled(false) to look better
        symptomsField.setBounds(330, 164, 288, 32);
        getContentPane().add(symptomsField);
        symptomsField.setColumns(10);

        JLabel lblMedicine = new JLabel("Medicine:");
        lblMedicine.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblMedicine.setBounds(130, 210, 142, 34);
        getContentPane().add(lblMedicine);

        medicineField = new JTextField();
        medicineField.setEditable(false);
        medicineField.setBounds(330, 211, 288, 37);
        getContentPane().add(medicineField);
        medicineField.setColumns(10);

        JLabel lblExtraDetail = new JLabel("Extra Detail:");
        lblExtraDetail.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblExtraDetail.setBounds(130, 296, 142, 104);
        getContentPane().add(lblExtraDetail);

        extraDetailArea = new JTextArea();
        extraDetailArea.setEditable(false);
        extraDetailArea.setBounds(330, 296, 294, 104);
        extraDetailArea.setRows(3);
        extraDetailArea.setColumns(20);
        getContentPane().add(extraDetailArea);

        // --- LOAD DATA ---
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String Query = "SELECT * FROM med_record WHERE D_ID = ? AND pat_email = ?";
            PreparedStatement ps = con.prepareStatement(Query);
            ps.setInt(1, doc_id);
            ps.setString(2, pat_mail);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String Disease = resultSet.getString("dis_name");
                String sym = resultSet.getString("symptoms");
                String med = resultSet.getString("medicine");
                String extras = resultSet.getString("extras");

                di_nameF.setText(Disease);
                symptomsField.setText(sym);
                medicineField.setText(med);
                extraDetailArea.setText(extras);
            }

            ps.close();
            con.close();

        } catch (Exception e2) {
            e2.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e2.getMessage());
        }

        JButton delBtn = new JButton("Delete");
        JButton testBtn = new JButton("Tests");
        JButton EditBtn = new JButton("Edit");

        // --- DELETE BUTTON ---
        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                        String query = "DELETE FROM med_record WHERE D_ID = ? AND pat_email = ?";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, doc_id);
                        ps.setString(2, pat_mail);

                        int r = ps.executeUpdate();

                        if (r > 0) {
                            JOptionPane.showMessageDialog(null, "Record Deleted Successfully");
                            // Dealt_Pat p = new Dealt_Pat(doc_id);
                            // p.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "No record found to delete");
                        }

                        ps.close();
                        con.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting: " + e2.getMessage());
                    }
                }
            }
        });
        delBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        delBtn.setBounds(330, 455, 116, 34);
        getContentPane().add(delBtn);

        // --- TESTS BUTTON ---
        testBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TestResults t = new TestResults(pat_mail, doc_id);
                t.setVisible(true);
                dispose();
            }
        });
        testBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        testBtn.setBounds(417, 505, 116, 34);
        getContentPane().add(testBtn);

        // --- EDIT BUTTON ---
        EditBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditBtn.setVisible(false);
                delBtn.setVisible(false);
                testBtn.setVisible(false);

                di_nameF.setEditable(true);
                symptomsField.setEditable(true);
                symptomsField.setEnabled(true); // Re-enable for editing
                medicineField.setEditable(true);
                extraDetailArea.setEditable(true);

                // Create Submit Button dynamically
                JButton submtBtn = new JButton("Submit");
                submtBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                            String updateQuery = "UPDATE med_record SET dis_name = ?, symptoms = ?, medicine = ?, extras = ? WHERE D_ID = ? AND pat_email = ?";
                            PreparedStatement updateStmt = con.prepareStatement(updateQuery);

                            updateStmt.setString(1, di_nameF.getText());
                            updateStmt.setString(2, symptomsField.getText());
                            updateStmt.setString(3, medicineField.getText());
                            updateStmt.setString(4, extraDetailArea.getText());
                            updateStmt.setInt(5, doc_id);
                            updateStmt.setString(6, pat_mail);

                            int r = updateStmt.executeUpdate();

                            if (r > 0) {
                                JOptionPane.showMessageDialog(null, "Record updated successfully");
                                // Reload current page to see changes (or go back to Dealt_Pat)
                                dispose();
                                new ShowRecord(doc_id, pat_mail).setVisible(true);
                                
                            } else {
                                JOptionPane.showMessageDialog(null, "No record found to update");
                            }

                            updateStmt.close();
                            con.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error updating: " + e2.getMessage());
                        }
                    }
                });
                submtBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
                submtBtn.setBounds(418, 455, 116, 34);
                getContentPane().add(submtBtn);
                getContentPane().repaint(); // Ensure new button draws
            }
        });
        EditBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        EditBtn.setBounds(505, 455, 116, 34);
        getContentPane().add(EditBtn);

        JButton backBtn = new JButton("<<");
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Ensure Dealt_Pat exists or this will fail
                // Dealt_Pat p = new Dealt_Pat(doc_id);
                // p.setVisible(true);
                dispose();
            }
        });
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        backBtn.setBounds(29, 30, 56, 32);
        getContentPane().add(backBtn);
    }
}