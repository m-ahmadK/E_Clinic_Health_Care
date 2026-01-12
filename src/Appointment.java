import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Appointment extends JFrame {

    private JPanel contentPane;
    private JTextField txtPhone;
    private JTextArea txtSymptoms;
    private JTextArea txtDescription;
    
    // Date & Time Components
    private JComboBox<String> dayBox, monthBox, yearBox;
    private JComboBox<String> timeBox;

    private int docID;
    private String patEmail;

    // Database
    private final String DB_URL = "jdbc:mysql://localhost:3306/hms_db";
    private final String DB_USER = "hms_user";
    private final String DB_PASS = "hms_pass";

    public Appointment(int docID, String patEmail) {
        this.docID = docID;
        this.patEmail = patEmail;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 750); 
        setTitle("Book Appointment");
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setBounds(0, 0, 600, 60);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitle = new JLabel("Book Appointment");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(20, 15, 300, 30);
        header.add(lblTitle);
        
        // --- FORM FIELDS ---

        // 1. Phone
        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPhone.setBounds(40, 80, 150, 20);
        contentPane.add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(40, 105, 500, 30);
        txtPhone.setToolTipText("Format: 03xxxxxxxxx (11 digits) or +923xxxxxxxxx (13 digits)");
        contentPane.add(txtPhone);

        // 2. Date Selection (Day / Month / Year)
        JLabel lblDate = new JLabel("Select Date:");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDate.setBounds(40, 145, 150, 20);
        contentPane.add(lblDate);

        String[] days = new String[31];
        for(int i=0; i<31; i++) days[i] = String.format("%02d", i+1);
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        String[] years = new String[2]; 
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        years[0] = String.valueOf(currentYear);
        years[1] = String.valueOf(currentYear + 1);

        dayBox = new JComboBox<>(days);
        dayBox.setBounds(40, 170, 60, 30);
        contentPane.add(dayBox);

        monthBox = new JComboBox<>(months);
        monthBox.setBounds(110, 170, 80, 30);
        contentPane.add(monthBox);

        yearBox = new JComboBox<>(years);
        yearBox.setBounds(200, 170, 80, 30);
        contentPane.add(yearBox);

        ActionListener dateChangeListener = e -> updateAvailableTimeSlots();
        dayBox.addActionListener(dateChangeListener);
        monthBox.addActionListener(dateChangeListener);
        yearBox.addActionListener(dateChangeListener);

        // 3. Time Selection
        JLabel lblTime = new JLabel("Available Time Slots:");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTime.setBounds(300, 145, 200, 20);
        contentPane.add(lblTime);

        timeBox = new JComboBox<>();
        timeBox.setBounds(300, 170, 240, 30);
        contentPane.add(timeBox);

        // 4. Symptoms
        JLabel lblSymp = new JLabel("Current Symptoms:");
        lblSymp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSymp.setBounds(40, 220, 200, 20);
        contentPane.add(lblSymp);

        txtSymptoms = new JTextArea();
        txtSymptoms.setLineWrap(true);
        txtSymptoms.setWrapStyleWord(true);
        JScrollPane scrollSymp = new JScrollPane(txtSymptoms);
        scrollSymp.setBounds(40, 245, 500, 60);
        contentPane.add(scrollSymp);

        // 5. Description
        JLabel lblDesc = new JLabel("Disease Description / History:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDesc.setBounds(40, 320, 250, 20);
        contentPane.add(lblDesc);

        txtDescription = new JTextArea();
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setBounds(40, 345, 500, 80);
        contentPane.add(scrollDesc);

        // Buttons
        JButton btnBook = new JButton("Confirm Booking");
        btnBook.setBackground(new Color(46, 204, 113));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBook.setBounds(150, 460, 300, 45);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBook.addActionListener(e -> bookAppointment());
        contentPane.add(btnBook);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(225, 520, 150, 30);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> {
            Doctors d = new Doctors(patEmail);
            d.setVisible(true);
            dispose();
        });
        contentPane.add(btnCancel);

        updateAvailableTimeSlots();
    }

    // --- CORE LOGIC ---
    private void updateAvailableTimeSlots() {
        String selectedDate = getSelectedDateString();
        List<String> bookedTimes = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String sql = "SELECT a_time FROM appointment_record WHERE D_ID = ? AND a_date = ? AND status != 'Cancelled'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, docID);
            pst.setString(2, selectedDate);
            
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                bookedTimes.add(rs.getString("a_time"));
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }

        timeBox.removeAllItems();
        
        // 9 AM to 11 AM
        for (int h = 9; h <= 11; h++) checkAndAddSlot(String.format("%02d:00 AM", h), bookedTimes);
        // 12 PM
        checkAndAddSlot("12:00 PM", bookedTimes);
        // 1 PM to 11 PM
        for (int h = 1; h <= 11; h++) checkAndAddSlot(String.format("%02d:00 PM", h), bookedTimes);
    }

    private void checkAndAddSlot(String slot, List<String> bookedTimes) {
        if (!bookedTimes.contains(slot)) {
            timeBox.addItem(slot);
        }
    }

    private String getSelectedDateString() {
        return dayBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + yearBox.getSelectedItem();
    }

    // --- VALIDATION AND BOOKING ---
    private void bookAppointment() {
        String phone = txtPhone.getText().trim();
        String symptoms = txtSymptoms.getText().trim();
        String desc = txtDescription.getText().trim();
        String time = (String) timeBox.getSelectedItem();
        
        // 1. Basic Empty Checks
        if(phone.isEmpty() || symptoms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone Number and Symptoms are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if(time == null) {
            JOptionPane.showMessageDialog(this, "No time slots available for this date. Please choose another date.", "Date Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Phone Number Validation
        boolean isValidPhone = false;
        
        // Check for +923 format (Must be exactly 13 chars and all digits after +)
        if (phone.startsWith("+923")) {
            if (phone.length() == 13 && phone.substring(1).matches("\\d+")) {
                isValidPhone = true;
            }
        } 
        // Check for 03 format (Must be exactly 11 chars and all digits)
        else if (phone.startsWith("03")) {
            if (phone.length() == 11 && phone.matches("\\d+")) {
                isValidPhone = true;
            }
        }

        if (!isValidPhone) {
            JOptionPane.showMessageDialog(this, "Invalid Phone Number!\n\nAllowed formats:\n1. 03xxxxxxxxx (11 digits)\n2. +923xxxxxxxxx (13 digits)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Date Validation (Check for past dates & invalid dates like Feb 30)
        int day = Integer.parseInt((String) dayBox.getSelectedItem());
        int year = Integer.parseInt((String) yearBox.getSelectedItem());
        int monthIndex = monthBox.getSelectedIndex(); // 0 for Jan, 1 for Feb

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setLenient(false); // Strict date checking (Prevents Feb 30)
        selectedDate.set(year, monthIndex, day, 0, 0, 0); // Set time to midnight for fair comparison

        try {
            selectedDate.getTime(); // Triggers check for invalid dates (like Feb 30)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Date Selected! (e.g., Feb 30 does not exist)", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (selectedDate.before(today)) {
            JOptionPane.showMessageDialog(this, "You cannot book an appointment in the past!", "Date Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Database Insertion
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String query = "INSERT INTO appointment_record (p_email, D_ID, a_date, a_time, phone, symptoms, disease_description, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Booked')";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, patEmail);
            pst.setInt(2, docID);
            pst.setString(3, getSelectedDateString()); 
            pst.setString(4, time);
            pst.setString(5, phone);
            pst.setString(6, symptoms);
            pst.setString(7, desc);
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!\nDate: " + getSelectedDateString() + "\nTime: " + time);
            
            PatProfile p = new PatProfile(patEmail);
            p.setVisible(true);
            dispose();
            
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}