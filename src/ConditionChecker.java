import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class ConditionChecker extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel symptomsContainer; // The main grid container
    private JTextArea resultArea;
    private List<JToggleButton> symptomButtons;

    // Database Credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private static final String DB_USER = "hms_user";
    private static final String DB_PASS = "hms_pass";

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color BG_COLOR = new Color(245, 247, 250);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ConditionChecker frame = new ConditionChecker("test@email.com");
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ConditionChecker(String patientEmail) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700);
        setUndecorated(true); 
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(BG_COLOR);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // ================= HEADER =================
        
        JPanel header = new JPanel();
        
        header.setBackground(PRIMARY_COLOR);
        header.setBounds(0, 0, 1100, 70);
        header.setLayout(null);
        contentPane.add(header);
        
        JLabel btnBack = new JLabel("< Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        //btnBack.setBounds(900, 25, 150, 20);
        btnBack.setBounds(30, 20, 300, 30);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new PatProfile(patientEmail).setVisible(true);
                dispose();
            }
        });
        header.add(btnBack);
        
        JLabel lblTitle = new JLabel("AI Symptom Checker");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        //lblTitle.setBounds(30, 20, 300, 30);
        lblTitle.setBounds(420, 15, 300, 30);
        
        header.add(lblTitle);

        

        // ================= LEFT: SYMPTOM COLUMNS =================
        JLabel lblInstr = new JLabel("Select symptoms by category:");
        lblInstr.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInstr.setForeground(Color.DARK_GRAY);
        lblInstr.setBounds(30, 90, 400, 30);
        contentPane.add(lblInstr);

        // This panel will hold the Columns (One column per Category)
        symptomsContainer = new JPanel();
        symptomsContainer.setBackground(BG_COLOR);
        // GridLayout(1, 0) means 1 row, ANY number of columns (Auto-calculated)
        symptomsContainer.setLayout(new GridLayout(1, 0, 20, 0)); 
        
        JScrollPane scrollPane = new JScrollPane(symptomsContainer);
        scrollPane.setBounds(30, 130, 620, 400);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPane.add(scrollPane);

        symptomButtons = new ArrayList<>();
        loadSymptomsFromDB(); // This now creates columns!

        // ================= RIGHT: DIAGNOSIS RESULT =================
        JPanel resultPanel = new JPanel();
        resultPanel.setBounds(680, 130, 380, 400);
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setLayout(null);
        resultPanel.setBorder(new LineBorder(new Color(230,230,230), 1));
        contentPane.add(resultPanel);

        JLabel lblResultTitle = new JLabel("Analysis Report");
        lblResultTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResultTitle.setForeground(PRIMARY_COLOR);
        lblResultTitle.setBounds(20, 20, 200, 30);
        resultPanel.add(lblResultTitle);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultArea.setForeground(Color.DARK_GRAY);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setText("Select symptoms on the left and click 'Analyze' to generate a potential diagnosis.");
        
        JScrollPane resScroll = new JScrollPane(resultArea);
        resScroll.setBounds(20, 60, 340, 320);
        resScroll.setBorder(null);
        resultPanel.add(resScroll);

        // ================= BOTTOM: ACTION BUTTONS =================
        JButton btnAnalyze = new JButton("ANALYZE SYMPTOMS");
        btnAnalyze.setBounds(30, 560, 300, 50);
        btnAnalyze.setBackground(new Color(46, 204, 113)); // Green
        btnAnalyze.setForeground(Color.WHITE);
        btnAnalyze.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAnalyze.setFocusPainted(false);
        btnAnalyze.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnalyze.addActionListener(e -> analyzeSymptoms());
        contentPane.add(btnAnalyze);
        
        JButton btnClear = new JButton("Clear Selection");
        btnClear.setBounds(350, 560, 150, 50);
        btnClear.setBackground(Color.WHITE);
        btnClear.setForeground(Color.GRAY);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClear.addActionListener(e -> clearSelection());
        contentPane.add(btnClear);
    }

    private void loadSymptomsFromDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // Order by category so we can group them
            String q = "SELECT name, category FROM Symptoms ORDER BY category, name";
            PreparedStatement pst = con.prepareStatement(q);
            ResultSet rs = pst.executeQuery();

            String currentCategory = "";
            JPanel currentColumnPanel = null;
            
            while(rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");

                // If the category changes, create a NEW COLUMN PANEL
                if(!category.equals(currentCategory)) {
                    currentCategory = category;
                    
                    // Create a vertical column for this category
                    currentColumnPanel = new JPanel();
                    currentColumnPanel.setLayout(new BoxLayout(currentColumnPanel, BoxLayout.Y_AXIS));
                    currentColumnPanel.setBackground(Color.WHITE);
                    currentColumnPanel.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(230,230,230), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    
                    // Add Category Header
                    JLabel catLbl = new JLabel(category.toUpperCase());
                    catLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    catLbl.setForeground(PRIMARY_COLOR);
                    catLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    currentColumnPanel.add(catLbl);
                    currentColumnPanel.add(Box.createVerticalStrut(10)); // Spacer
                    
                    // Add this column to the main container
                    symptomsContainer.add(currentColumnPanel);
                }

                // Add Button to the CURRENT Column
                JToggleButton btn = new JToggleButton(name);
                btn.setMaximumSize(new Dimension(140, 35)); // Fixed width
                btn.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
                btn.setBackground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                // Click Effect
                btn.addActionListener(e -> {
                    if(btn.isSelected()) {
                        btn.setBackground(ACCENT_COLOR);
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(Color.WHITE);
                        btn.setForeground(Color.BLACK);
                    }
                });
                
                currentColumnPanel.add(btn);
                currentColumnPanel.add(Box.createVerticalStrut(5)); // Spacer between buttons
                symptomButtons.add(btn);
            }
            con.close();
            
            // Refresh layout to show columns
            symptomsContainer.revalidate();
            symptomsContainer.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeSymptoms() {
        List<String> selected = new ArrayList<>();
        for(JToggleButton btn : symptomButtons) {
            if(btn.isSelected()) selected.add(btn.getText());
        }

        if(selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one symptom.");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("BASED ON SELECTED SYMPTOMS:\n");
        report.append(selected.toString().replace("[", "").replace("]", "")).append("\n\n");
        report.append("--------------------------------------------------\n");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            StringBuilder query = new StringBuilder(
                "SELECT c.name, c.advice, COUNT(s.symptom_id) as match_count, " +
                "(SELECT COUNT(*) FROM SymptomConditionMapping scm2 WHERE scm2.condition_id = c.condition_id) as total_symptoms " +
                "FROM Conditions c " +
                "JOIN SymptomConditionMapping scm ON c.condition_id = scm.condition_id " +
                "JOIN Symptoms s ON scm.symptom_id = s.symptom_id " +
                "WHERE s.name IN ("
            );

            for(int i=0; i<selected.size(); i++) {
                query.append(i==0 ? "?" : ", ?");
            }
            query.append(") GROUP BY c.condition_id ORDER BY match_count DESC LIMIT 3");

            PreparedStatement pst = con.prepareStatement(query.toString());
            for(int i=0; i<selected.size(); i++) {
                pst.setString(i+1, selected.get(i));
            }

            ResultSet rs = pst.executeQuery();
            boolean found = false;

            while(rs.next()) {
                found = true;
                String condition = rs.getString("name");
                String advice = rs.getString("advice");
                int matched = rs.getInt("match_count");
                int total = rs.getInt("total_symptoms");
                
                int probability = (int) (((double)matched / total) * 100);
                if(probability > 100) probability = 100;

                report.append("POSSIBILITY: ").append(condition.toUpperCase()).append("\n");
                report.append("Match Probability: ").append(probability).append("%\n");
                report.append("Advice: ").append(advice).append("\n\n");
            }

            if(!found) {
                report.append("No specific conditions matched.\nPlease consult a doctor for a checkup.");
            } else {
                report.append("--------------------------------------------------\n");
                report.append("DISCLAIMER: This is an AI estimation.\nIt does not replace professional medical advice.");
            }
            
            resultArea.setText(report.toString());
            resultArea.setCaretPosition(0);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during analysis.");
        }
    }
    
    private void clearSelection() {
        for(JToggleButton btn : symptomButtons) {
            btn.setSelected(false);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        }
        resultArea.setText("Select symptoms on the left and click 'Analyze' to generate a potential diagnosis.");
    }
}