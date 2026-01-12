import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DiseaseLibrary extends JFrame {

    public DiseaseLibrary(int docID) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        setTitle("Hospital Medical Knowledge Base");
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setBounds(0,0,600,60);
        contentPane.add(header);
        
        JLabel title = new JLabel("Medical Encyclopedia Manager");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        
        JLabel lblInfo = new JLabel("<html>This feature allows you to add new Diseases and Standard Treatments<br>to the hospital's central database for reference.</html>");
        lblInfo.setBounds(50, 80, 500, 40);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPane.add(lblInfo);
        
        // Placeholders for fields...
        // Disease Name Field
        // Standard Cure Field
        // Submit Button
        
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setBounds(200, 300, 200, 40);
        btnBack.addActionListener(e -> {
            DocProfile d = new DocProfile(docID);
            d.setVisible(true);
            dispose();
        });
        contentPane.add(btnBack);
    }
}