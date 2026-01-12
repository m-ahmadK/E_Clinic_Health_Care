import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class ShowDisease extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainContainer;
    
    private int xx, xy;

    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color BG_COLOR = new Color(245, 247, 250);      

    // Context Variables
    private int docID;
    private String userRole; // "Doctor", "Patient", or "Admin"
    private String patientEmail; 

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ShowDisease(0, "test@email.com").setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ShowDisease(int id, String roleOrEmail) {
        this.docID = id;
        
        // Determine Role
        if (roleOrEmail.equalsIgnoreCase("Doctor") || id > 0) {
            this.userRole = "Doctor";
            this.patientEmail = ""; 
        } else if (roleOrEmail.equalsIgnoreCase("Admin")) {
            this.userRole = "Admin";
            this.patientEmail = "";
        } else {
            this.userRole = "Patient";
            this.patientEmail = roleOrEmail; 
        }

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setTitle("Medical Knowledge Base");
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG_COLOR);
        getContentPane().setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(1000, 70));
        header.setLayout(null);
        getContentPane().add(header, BorderLayout.NORTH);

        // Drag Logic
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { xx = e.getX(); xy = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { 
                int x = e.getXOnScreen(); int y = e.getYOnScreen(); 
                ShowDisease.this.setLocation(x - xx, y - xy); 
            }
        });

        JLabel lblTitle = new JLabel("Medical Knowledge Base");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 0, 1000, 70); 
        header.add(lblTitle);

        // Back Button
        JLabel btnBack = new JLabel("< Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBounds(20, 20, 60, 30);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                goBack();
            }
        });
        header.add(btnBack);
        header.setComponentZOrder(btnBack, 0); 
        
        JLabel btnClose = new JLabel("X");
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnClose.setBounds(960, 20, 30, 30);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
        });
        header.add(btnClose);
        header.setComponentZOrder(btnClose, 0);

        // --- GRID CONTAINER ---
        mainContainer = new JPanel();
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        mainContainer.setPreferredSize(new Dimension(960, 1500)); 

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        loadConditionsFromAdminDB();
    }

    private void loadConditionsFromAdminDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement s = con.createStatement();
            
            // CORRECTED QUERY: Now reading from the Admin's 'Conditions' table
            String query = "SELECT * FROM Conditions ORDER BY name ASC";
            ResultSet rs = s.executeQuery(query);
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                mainContainer.add(createConditionCard(
                    rs.getString("name"), 
                    rs.getString("advice")
                ));
            }
            if(!found) {
                JLabel lblEmpty = new JLabel("No medical records found.");
                lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                mainContainer.add(lblEmpty);
            }
            con.close();
            mainContainer.revalidate();
            mainContainer.repaint();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private JPanel createConditionCard(String conditionName, String adviceText) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(280, 160));
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title
        JLabel lblTitle = new JLabel(conditionName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBounds(20, 20, 240, 25);
        card.add(lblTitle);

        // Subtitle
        JLabel lblSub = new JLabel("General Medical Advice");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(20, 45, 200, 20);
        card.add(lblSub);

        // Action Button
        JButton btnView = new JButton("Read Advice");
        btnView.setBounds(20, 100, 240, 35);
        btnView.setBackground(Color.WHITE);
        btnView.setForeground(PRIMARY_COLOR);
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnView.setFocusPainted(false);
        btnView.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        btnView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover Effect
        btnView.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnView.setBackground(PRIMARY_COLOR);
                btnView.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btnView.setBackground(Color.WHITE);
                btnView.setForeground(PRIMARY_COLOR);
            }
        });

        // Click Action: Show Popup with Advice
        btnView.addActionListener(e -> {
            JTextArea textArea = new JTextArea(adviceText);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textArea.setColumns(30);
            textArea.setRows(10);
            
            JScrollPane scroll = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scroll, conditionName + " - Advice", JOptionPane.INFORMATION_MESSAGE);
        });
        
        card.add(btnView);
        return card;
    }

    private void goBack() {
        if(userRole.equals("Doctor")) {
            new DocProfile(docID).setVisible(true);
        } else if(userRole.equals("Admin")) {
            new AdminPanel().setVisible(true);
        } else {
            // Patient
            new PatProfile(patientEmail).setVisible(true);
        }
        dispose();
    }
}