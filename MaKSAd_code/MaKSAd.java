package maksadpro;

public class MaKSAd {

    public static void main(String[] args) {

     
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {}

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWelcomeFrame().setVisible(true);
        });
    }
}
