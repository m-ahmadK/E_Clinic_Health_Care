import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class FrontFrame extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FrontFrame frame = new FrontFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public FrontFrame() {
        // 1. Basic Window Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Removes default title bar
        setBounds(100, 100, 900, 550);
        setLocationRelativeTo(null); // Centers on screen
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new LineBorder(new Color(41, 128, 185), 2)); // Blue Border
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 2. Title Section
        JLabel lblTitle = new JLabel("Hospital Management System");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(41, 128, 185)); // Medical Blue
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setBounds(0, 60, 900, 50);
        contentPane.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Select your role to continue");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.DARK_GRAY);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitle.setBounds(0, 110, 900, 30);
        contentPane.add(lblSubtitle);

        // 3. Close Button (X)
        JLabel lblClose = new JLabel("X");
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setForeground(Color.RED);
        lblClose.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblClose.setBounds(850, 10, 40, 40);
        contentPane.add(lblClose);

        // 4. Doctor Portal Button
        JButton btnDoctor = new JButton("Doctor Portal");
        btnDoctor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open Login in DOCTOR mode
                PatientLoginFrame pf = new PatientLoginFrame();
                pf.LoginScreen("Doctor");
                pf.setVisible(true);
                dispose();
            }
        });
        btnDoctor.setBackground(new Color(41, 128, 185)); // Blue
        btnDoctor.setForeground(Color.WHITE);
        btnDoctor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDoctor.setFocusPainted(false);
        btnDoctor.setBorder(null);
        btnDoctor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDoctor.setBounds(150, 420, 180, 50);
        contentPane.add(btnDoctor);

        // 5. Patient Portal Button
        JButton btnPatient = new JButton("Patient Portal");
        btnPatient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open Login in PATIENT mode
                PatientLoginFrame pf = new PatientLoginFrame();
                pf.LoginScreen("Patient");
                pf.setVisible(true);
                dispose();
            }
        });
        btnPatient.setBackground(new Color(46, 204, 113)); // Green
        btnPatient.setForeground(Color.WHITE);
        btnPatient.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPatient.setFocusPainted(false);
        btnPatient.setBorder(null);
        btnPatient.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPatient.setBounds(360, 420, 180, 50);
        contentPane.add(btnPatient);

        // 6. Admin Portal Button
        JButton btnAdmin = new JButton("Admin Portal");
        btnAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Fix: Just create the object and show it. No extra parameters needed.
                AdminLogin adl = new AdminLogin(); 
                adl.setVisible(true);
                dispose();
            }
        });
        btnAdmin.setBackground(new Color(52, 73, 94)); // Dark Grey
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdmin.setFocusPainted(false);
        btnAdmin.setBorder(null);
        btnAdmin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdmin.setBounds(570, 420, 180, 50);
        contentPane.add(btnAdmin);

        // 7. Background Image (Robust Loading)
        JLabel lblBg = new JLabel("");
        lblBg.setHorizontalAlignment(SwingConstants.CENTER);
        lblBg.setBounds(0, 0, 900, 550);
        
        // Try to load image safely
        URL imgUrl = getClass().getResource("/images/FrontFrame.jpg");
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Optional: Resize image to fit perfectly
            Image img = icon.getImage().getScaledInstance(900, 550, Image.SCALE_SMOOTH);
            lblBg.setIcon(new ImageIcon(img));
        } else {
            // Fallback text if image is missing
            lblBg.setText(""); 
        }
        
        // Add background LAST so it sits behind everything else
        contentPane.add(lblBg);
    }
}