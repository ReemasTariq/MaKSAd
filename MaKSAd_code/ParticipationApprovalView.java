package maksadpro;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ParticipationApprovalView extends JFrame {

    private final int organizerId;
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel model;


    private static final Color BG_MAIN   = Color.decode("#263717");
    private static final Color BG_CARD   = Color.decode("#FFFADD");
    private static final Color BTN_COLOR = Color.decode("#74835A");
    private static final Color TEXT_DARK = Color.decode("#1E1E1E");


    private static final String[] STATUS_OPTIONS = {
            "APPROVED", "PENDING", "CANCELED"
    };

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ParticipationApprovalView(int organizerId) {
        this.organizerId = organizerId;

        setTitle("Volunteer Participation Approval");
        setSize(1200, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        buildTopBar();
        buildTable();
        loadParticipationFromDB();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_MAIN);
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);

        JLabel sLbl = new JLabel("Search:");
        sLbl.setForeground(Color.WHITE);
        searchField = new JTextField(20);

        left.add(sLbl);
        left.add(searchField);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);

        JButton saveBtn = makeRoundButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(150, 32));
        saveBtn.addActionListener(e -> saveChangesToDatabase());

        JButton backBtn = makeRoundButton("Back");
        backBtn.addActionListener(e -> dispose());

        right.add(saveBtn);
        right.add(backBtn);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
    }

   
    private void buildTable() {

        String[] cols = {
                "Volunteer ID",   
                "Volunteer Name", 
                "Interests",      
                "Skills",         
                "Event Name", 
                "Event Date",     
                "Check-in",      
                "Check-out",     
                "Hours",         
                "Role",          
                "Status"          
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
               
                return (col == 9 || col == 10);
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setGridColor(Color.GRAY);

        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setFont(new Font("Serif", Font.BOLD, 13));
        table.setBackground(Color.WHITE);

        JComboBox<String> statusCombo = new JComboBox<>(STATUS_OPTIONS);
        table.getColumnModel().getColumn(10).setCellEditor(new DefaultCellEditor(statusCombo));

       
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void filter() {
                sorter.setRowFilter(RowFilter.regexFilter(
                        "(?i)" + Pattern.quote(searchField.getText()),
                        1, 
                        4  
                ));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

   
    private void loadParticipationFromDB() {
        model.setRowCount(0);

        String sql = """
            SELECT 
                vp.volunteer_id,
                vp.volunteer_name,
                vp.event_name,
                vp.event_date,
                vp.check_in  AS resolved_check_in,
                vp.check_out AS resolved_check_out,
                vp.hours,
                vp.role,
                vp.status,
                mu.interests,
                mu.skills
            FROM volunteer_participations vp
            JOIN events e 
                ON  vp.event_name = e.name
                AND vp.event_date = e.event_date
            LEFT JOIN maksad_users mu
                ON vp.volunteer_id = mu.volunteer_id
            ORDER BY vp.event_date DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ci = rs.getTimestamp("resolved_check_in");
                Timestamp co = rs.getTimestamp("resolved_check_out");

                Object hoursDisplay = "—"; 

                model.addRow(new Object[] {
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteer_name"),
                        rs.getString("interests") == null ? "" : rs.getString("interests"),
                        rs.getString("skills") == null ? "" : rs.getString("skills"),
                        rs.getString("event_name"),
                        rs.getDate("event_date").toString(),
                        (ci == null ? "" : dtf.format(ci.toLocalDateTime())),
                        (co == null ? "" : dtf.format(co.toLocalDateTime())),
                        hoursDisplay,
                        rs.getString("role"),
                        rs.getString("status")  
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading participation data:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void saveChangesToDatabase() {

        String sql = """
            UPDATE volunteer_participations
            SET role = ?, status = ?
            WHERE volunteer_id = ? AND event_name = ? AND event_date = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < model.getRowCount(); i++) {

                Object volIdObj     = model.getValueAt(i, 0);
                Object eventNameObj = model.getValueAt(i, 4);
                Object eventDateObj = model.getValueAt(i, 5);
                Object roleObj      = model.getValueAt(i, 9);
                Object statusObj    = model.getValueAt(i, 10);

                if (volIdObj == null || eventNameObj == null || eventDateObj == null) {
                    showValidationError("Missing key data in row " + (i + 1) + ".");
                    return;
                }

                int volunteerId;
                try {
                    volunteerId = Integer.parseInt(volIdObj.toString());
                } catch (NumberFormatException ex) {
                    showValidationError("Invalid volunteer ID in row " + (i + 1) + ".");
                    return;
                }

                String eventName = eventNameObj.toString();
                String eventDate = eventDateObj.toString();
                String role      = roleObj   == null ? "" : roleObj.toString().trim();
                String status    = statusObj == null ? "" : statusObj.toString().trim();

                ps.setString(1, role);
                ps.setString(2, status);  
                ps.setInt(3, volunteerId);
                ps.setString(4, eventName);
                ps.setString(5, eventDate);

                ps.addBatch();
            }

            ps.executeBatch();

            JOptionPane.showMessageDialog(this,
                    "Changes saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadParticipationFromDB();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Saving error:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton makeRoundButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BTN_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Serif", Font.BOLD, 14));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT organizer_id FROM organizers ORDER BY organizer_id LIMIT 1"
                 );
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int organizerId = rs.getInt("organizer_id");
                    new ParticipationApprovalView(organizerId).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No organizers found in DB",
                            "Info",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
