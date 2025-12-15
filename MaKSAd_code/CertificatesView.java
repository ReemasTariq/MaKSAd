package maksadpro;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CertificatesView extends JFrame {

    private static final Color COLOR_BG_MAIN = Color.decode("#FFFADD");
    private static final Color COLOR_DARK    = Color.decode("#263717");
    private static final Color COLOR_CARD    = Color.decode("#74835A");

    private static final String ICON_PATH =
            "/maksadpro/MaKSAdPH/MaKSAdCertificate_logo.png";
    private static final String SIDE_IMAGE_PATH =
            "/maksadpro/MaKSAdPH/MaKSAdP.png";

    public CertificatesView(String certId,
                            String volunteerId,
                            String volunteerName,
                            String eventName,
                            String issueDate,
                            double hours) {

        setTitle("Certificate " + certId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG_MAIN);

        String prettyIssue = issueDate;
        try {
            LocalDate d = LocalDate.parse(issueDate);
            prettyIssue = d.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        } catch (Exception ignored) {}


    
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(COLOR_DARK);
        top.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel title = new JLabel("Congrats " + volunteerName + "!");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);


      
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        add(center, BorderLayout.CENTER);


      
        JLabel sideImage = new JLabel();
        try {
            ImageIcon sideIcon = new ImageIcon(
                    getClass().getResource(SIDE_IMAGE_PATH)
            );

            int patternHeight = 420;
            int patternWidth  = 230; 

            Image scaledSide = sideIcon.getImage()
                    .getScaledInstance(patternWidth, patternHeight, Image.SCALE_SMOOTH);

            sideImage.setIcon(new ImageIcon(scaledSide));
            sideImage.setPreferredSize(new Dimension(patternWidth + 10, patternHeight));

        } catch (Exception ignored) {}

        JPanel sideWrapper = new JPanel(new BorderLayout());
        sideWrapper.setOpaque(false);
        sideWrapper.add(sideImage, BorderLayout.CENTER);
        add(sideWrapper, BorderLayout.EAST);


    
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setPreferredSize(new Dimension(330, 900));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 60), 2),
                new EmptyBorder(24, 40, 24, 40)
        ));


      
        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(ICON_PATH));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.NORTH);


     
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("Certificate of Completion");
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setFont(new Font("Serif", Font.BOLD, 26));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        JLabel line0 = new JLabel("This is to certify that");
        line0.setForeground(Color.WHITE);
        line0.setFont(new Font("SansSerif", Font.PLAIN, 14));
        line0.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(volunteerName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line2 = new JLabel("has successfully completed " + (int) hours + " volunteer hours");
        line2.setForeground(Color.WHITE);
        line2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line3 = new JLabel("in the " + eventName + " event.");
        line3.setForeground(Color.WHITE);
        line3.setFont(new Font("SansSerif", Font.BOLD, 14));
        line3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line4 = new JLabel("Issued on: " + prettyIssue);
        line4.setForeground(Color.WHITE);
        line4.setFont(new Font("SansSerif", Font.ITALIC, 12));
        line4.setAlignmentX(Component.CENTER_ALIGNMENT);
        line4.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel footer = new JLabel("MaKSAd Platform – Volunteer Program");
        footer.setForeground(Color.WHITE);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 11));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(mainTitle);
        textPanel.add(Box.createVerticalStrut(18));
        textPanel.add(line0);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(line2);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(line3);
        textPanel.add(Box.createVerticalStrut(14));
        textPanel.add(line4);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(footer);

        card.add(textPanel, BorderLayout.CENTER);
        center.add(card, gbc);


     
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 0, 24, 160));

        JButton closeBtn = createMainButton("Close Certificate");
        closeBtn.setPreferredSize(new Dimension(150, 36));
        closeBtn.addActionListener(e -> dispose());

        bottom.add(closeBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }


    private JButton createMainButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(6, 18, 6, 18));
        return btn;
    }
}
