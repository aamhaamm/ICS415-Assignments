import javax.swing.*;
import java.awt.*;

public class Main {
    private static final String MENU   = "MENU";
    private static final String BASIC  = "BASIC";
    private static final String BONUS  = "BONUS";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Frame
            JFrame frame = new JFrame("Poly Bézier Curve Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 640);
            frame.setLocationRelativeTo(null);

            // Card container
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);

            // 1) MENU panel
            JPanel menu = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JButton btnBasic = new JButton("Project 4 (Basic)");
            JButton btnBonus = new JButton("Bonus (Poly Bézier)");
            menu.add(btnBasic, gbc);
            gbc.gridy = 1;
            menu.add(btnBonus, gbc);

            // 2) BASIC panel
            JPanel basicPanel = new JPanel(new BorderLayout());
            basicPanel.add(new BezierSketch(), BorderLayout.CENTER);
            JButton back1 = new JButton("Back");
            basicPanel.add(back1, BorderLayout.SOUTH);

            // 3) BONUS panel
            JPanel bonusPanel = new JPanel(new BorderLayout());
            bonusPanel.add(new PolyBezierEditor(), BorderLayout.CENTER);
            JButton back2 = new JButton("Back");
            bonusPanel.add(back2, BorderLayout.SOUTH);

            // assemble cards
            cards.add(menu, MENU);
            cards.add(basicPanel, BASIC);
            cards.add(bonusPanel, BONUS);
            frame.setContentPane(cards);

            // wiring buttons
            btnBasic .addActionListener(e -> cardLayout.show(cards, BASIC));
            btnBonus .addActionListener(e -> cardLayout.show(cards, BONUS));
            back1    .addActionListener(e -> cardLayout.show(cards, MENU));
            back2    .addActionListener(e -> cardLayout.show(cards, MENU));

            frame.setVisible(true);
        });
    }
}
