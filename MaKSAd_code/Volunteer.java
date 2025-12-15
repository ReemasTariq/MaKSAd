
package maksadpro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Volunteer extends JFrame { 

    private static final Color COLOR_BG_MAIN = Color.decode("#FFFADD"); 
    private static final Color COLOR_DARK = Color.decode("#263717"); 
    private static final Color COLOR_CARD = Color.decode("#74835A"); 
    private static final Color COLOR_CARD_BORDER = Color.decode("#5A5A3A");
    private static final Color COLOR_BTN = Color.decode("#263717"); 
    private static final Color COLOR_LOG_BG = Color.decode("#F2F2EE"); 
    private static final Color COLOR_INTEREST_BG = Color.decode("#263717"); 
    private static final Color COLOR_INTEREST_INNER = Color.decode("#F2F2EE");

    private static final double HOURS_GOAL = 200.0; 

    private final MaKSAdUserSystem.AuthenticatedUser user; 

    private JLabel lblTotalHours = new JLabel("0");
    private JLabel lblUpcoming = new JLabel("0");
    private JLabel lblJoined = new JLabel("0"); 
    private JLabel lblCertificates = new JLabel("0"); 

    private JProgressBar hoursProgress = new JProgressBar(0, 100); 

    private JTextArea log = new JTextArea(7, 40); 

    private DefaultListModel<String> interestModel = new DefaultListModel<>(); 
    private JTextField interestInput = new JTextField(); 

    public Volunteer(MaKSAdUserSystem.AuthenticatedUser user) {
        this.user = user;
        initFrame(); 
    }

    private void initFrame() { 
        setTitle("MaKSAd Volunteer Dashboard - " + user.getName()); 
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setSize(1200, 900); 
        setLocationRelativeTo(null); 

        JPanel root = new JPanel(new BorderLayout()); 
        root.setBackground(COLOR_BG_MAIN);
        setContentPane(root); 

        root.add(createHeader(), BorderLayout.NORTH); 
        root.add(createCenterContent(), BorderLayout.CENTER); 
        root.add(createBottomBar(), BorderLayout.SOUTH);

        refreshSummary(); 
        loadInterestsFromDB();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_DARK);
        header.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel title = new JLabel("Welcome, " + user.getName() + ". We are happy to have you on MaKSAd!"); 
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title, BorderLayout.CENTER);
        return header;
    }

    private JComponent createCenterContent() { 
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(18, 24, 24, 24)); 

        JPanel middleRow = new JPanel(new BorderLayout());
        middleRow.setOpaque(false);

        middleRow.add(createLeftColumn(), BorderLayout.WEST); 
        middleRow.add(createRightButtons(), BorderLayout.EAST); 

        container.add(middleRow, BorderLayout.CENTER);
        container.add(createActiveLogPanel(), BorderLayout.SOUTH); 

        return container;
    }

    private JComponent createLeftColumn() { 
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        
        statsPanel.add(createHoursCard());
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(createSimpleStatCard("Upcoming Events", lblUpcoming));
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(createSimpleStatCard("Joined Events", lblJoined));
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(createSimpleStatCard("Certificates", lblCertificates));

        statsPanel.add(Box.createVerticalStrut(16));

        left.add(statsPanel);
        left.add(Box.createVerticalStrut(25));
        left.add(createInterestsPanel()); 

        return left;
    }

    private JPanel createCardShell() { 
        JPanel p = new JPanel();
        p.setBackground(COLOR_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1), 
                new EmptyBorder(8, 12, 8, 12) 
        ));
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(220, 55)); 
        return p;
    }

    private JComponent createHoursCard() { 
        JPanel card = createCardShell();

        JLabel title = new JLabel("Total Volunteer Hours");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));

        lblTotalHours.setForeground(Color.WHITE);
        lblTotalHours.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotalHours.setHorizontalAlignment(SwingConstants.CENTER);

        hoursProgress.setStringPainted(true); 
        hoursProgress.setForeground(Color.decode("#3C5C2A"));
        hoursProgress.setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(lblTotalHours, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(hoursProgress, BorderLayout.SOUTH);

        return card;
    }

    private JComponent createSimpleStatCard(String titleText, JLabel valueLabel) { 
        JPanel card = createCardShell();

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JComponent createInterestsPanel() {
        JPanel outer = new JPanel(new BorderLayout(6, 6));
        outer.setBackground(COLOR_INTEREST_BG);
        outer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        outer.setPreferredSize(new Dimension(260, 150));

        JLabel title = new JLabel("Interests");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(4, 8, 0, 8));

        JList<String> list = new JList<>(interestModel);
        list.setSelectionBackground(Color.decode("#B0BE97"));
        list.setSelectionForeground(Color.BLACK);
        JScrollPane scroll = new JScrollPane(list);
        scroll.getViewport().setBackground(COLOR_INTEREST_INNER);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(COLOR_INTEREST_INNER);
        inner.setBorder(new EmptyBorder(4, 8, 4, 8));
        inner.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.setOpaque(false);

        JButton addBtn = createSmallButton("Add"); 
        addBtn.addActionListener(e -> { 
            String text = interestInput.getText().trim(); 
            if (text.isEmpty()) return; 

            if (interestModel.contains(text)) { 
                JOptionPane.showMessageDialog(Volunteer.this,
                        "This interest already exists.");
                return;
            }

            interestModel.addElement(text);

            if (updateInterestsInDB()) { 
                interestInput.setText(""); 
                log.append("Added interest: " + text + "\n"); 
            } else {
                interestModel.removeElement(text);
            }
        });

        bottom.add(interestInput, BorderLayout.CENTER);
        bottom.add(addBtn, BorderLayout.EAST);
        bottom.setBorder(new EmptyBorder(4, 8, 6, 8));

        outer.add(title, BorderLayout.NORTH);
        outer.add(inner, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);

        return outer;
    }

    private JComponent createRightButtons() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(20, 40, 0, 0));

        JButton requestBtn = createMainButton("Request to Join"); 
        JButton cancelBtn = createMainButton("Cancel Join"); 
        JButton viewPartBtn = createMainButton("View Participation"); 
        JButton viewCertBtn = createMainButton("View Certificate"); 

        requestBtn.addActionListener(e -> new VolunteerDialogs.EventDialog(this)); 
        cancelBtn.addActionListener(e -> new VolunteerDialogs.CancelJoinDialog(this)); 
        viewPartBtn.addActionListener(e -> new VolunteerDialogs.ParticipationTable(this)); 
        viewCertBtn.addActionListener(e -> new Certificate(user.getId(), user.getName())); 

        right.add(requestBtn);
        right.add(Box.createVerticalStrut(16));
        right.add(cancelBtn);
        right.add(Box.createVerticalStrut(16));
        right.add(viewPartBtn);
        right.add(Box.createVerticalStrut(16));
        right.add(viewCertBtn);

        return right;
    }

    private JComponent createActiveLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 60, 0, 60));

        JLabel title = new JLabel("Active Log");
        title.setForeground(COLOR_DARK);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(0, 8, 4, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_DARK);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        log.setEditable(false); 
        log.setBackground(COLOR_LOG_BG);
        log.setFont(new Font("Consolas", Font.PLAIN, 13));

        card.add(new JScrollPane(log), BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private JComponent createBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(4, 24, 10, 24));

        JButton logoutBtn = createSmallButton("Logout"); 
        logoutBtn.setPreferredSize(new Dimension(90, 32));

       
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        bottom.add(logoutBtn, BorderLayout.EAST);
        return bottom;
    }

    public JButton createMainButton(String text) { 
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) { 
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(210, 46));
        btn.setMaximumSize(new Dimension(210, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBorder(new EmptyBorder(6, 18, 6, 18));
        return btn;
    }

    public JButton createSmallButton(String text) { 
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) { 
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        return btn;
    }

    private void refreshSummary() { 
        double hours = 0.0;
        int joined = 0;
        int upcoming = 0;
        int certs = 0;

        String sqlHours = "SELECT total_hours FROM volunteers WHERE volunteer_id = ?"; 
        String sqlJoined = "SELECT COUNT(*) FROM volunteer_participations WHERE volunteer_id = ?";
        String sqlUpcoming = """
                SELECT COUNT(*)
                FROM volunteer_participations
                WHERE volunteer_id = ?
                  AND status = 'UNSET'
                  AND event_date >= CURDATE()
                """; // only future UNSET events
        String sqlCerts = "SELECT COUNT(*) FROM certificates WHERE volunteer_id = ?"; 

        try (Connection conn = DBConnection.getConnection()) { 

            PreparedStatement ps1 = conn.prepareStatement(sqlHours); 
            ps1.setInt(1, user.getId());
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) hours = rs1.getDouble(1);

            PreparedStatement ps2 = conn.prepareStatement(sqlJoined); 
            ps2.setInt(1, user.getId());
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) joined = rs2.getInt(1);

            PreparedStatement ps3 = conn.prepareStatement(sqlUpcoming);
            ps3.setInt(1, user.getId());
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) upcoming = rs3.getInt(1);

            PreparedStatement ps4 = conn.prepareStatement(sqlCerts); 
            ps4.setInt(1, user.getId());
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) certs = rs4.getInt(1);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading summary:\n" + ex.getMessage());
            log.append("Error loading summary: " + ex.getMessage() + "\n");
        }

        lblTotalHours.setText(String.format("%.0f", hours)); 
        lblJoined.setText(String.valueOf(joined));
        lblUpcoming.setText(String.valueOf(upcoming));
        lblCertificates.setText(String.valueOf(certs));

        int percent = 0; 
        if (HOURS_GOAL > 0) {
            percent = (int) Math.round((hours / HOURS_GOAL) * 100.0); 
        }
        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;

        hoursProgress.setValue(percent);
        hoursProgress.setString(percent + "%");

        if (percent < 30) {
            hoursProgress.setToolTipText("Nice start! Keep going to build your volunteering journey."); 
        } else if (percent < 70) {
            hoursProgress.setToolTipText("You’re making great progress - keep it up!"); 
        } else {
            hoursProgress.setToolTipText("Go ahead to reach your goal!!");
        }
    }

    // DB 
    private void loadInterestsFromDB() {
        interestModel.clear(); 

        String sql = "SELECT interests FROM maksad_users WHERE volunteer_id = ?";

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String interestsStr = rs.getString("interests"); 
                if (interestsStr != null && !interestsStr.trim().isEmpty()) { 
                    String cleaned = interestsStr.replace("[", "").replace("]", ""); 
                    String[] parts = cleaned.split(","); 

                    for (String raw : parts) {
                        String t = raw.trim(); 
                        if (t.startsWith("\"") || t.startsWith("'")) t = t.substring(1); 
                        if (t.endsWith("\"") || t.endsWith("'")) t = t.substring(0, t.length() - 1); 
                        if (!t.isEmpty()) interestModel.addElement(t); 
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading interests:\n" + ex.getMessage());
            log.append("Error loading interests: " + ex.getMessage() + "\n");
        }
    }

    private boolean updateInterestsInDB() {
        StringBuilder sb = new StringBuilder("["); 

        for (int i = 0; i < interestModel.size(); i++) {
            if (i > 0) sb.append(", "); 

            String v = interestModel.getElementAt(i); 
            v = v.replace("\"", "\\\""); 
            sb.append("\"").append(v).append("\""); 
        }

        sb.append("]"); 

        String sql = "UPDATE maksad_users SET interests = ? WHERE volunteer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sb.toString()); 
            ps.setInt(2, user.getId());
            ps.executeUpdate();

            return true; 

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving interests:\n" + ex.getMessage());
            log.append("Error saving interests: " + ex.getMessage() + "\n");
            return false; 
        }
    }

   
    public int getVolunteerId() {
        return user.getId();
    }

    public String getVolunteerName() {
        return user.getName();
    }

    public void appendLogLine(String msg) {
        log.append(msg + "\n");
    }

    public void refreshSummaryPublic() {
        refreshSummary();
    }

}
