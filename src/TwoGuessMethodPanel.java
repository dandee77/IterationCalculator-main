import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class TwoGuessMethodPanel extends JPanel {
    private JTextField functionField;
    private JTextField initialGuessField1;
    private JTextField initialGuessField2;
    private JTextField toleranceField;
    private JTextArea historyArea;
    private JTextField answerField;
    private JButton calculateButton;

    public TwoGuessMethodPanel(String methodDescription) {
        setBackground(new Color(250, 252, 255));
        setLayout(new MigLayout("fill, insets 32, gap 32", "[grow,fill][grow,fill]", "[grow,fill]"));

        // Left panel for inputs
        JPanel left = new JPanel(new MigLayout(
                "fillx, wrap 2, gapy 18", "[grow,fill][grow,fill]", ""
        ));
        left.setOpaque(false);

        // Function label
        JLabel funcLabel = new JLabel("Enter Function f(x)");
        funcLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        left.add(funcLabel, "span 2, wrap");

        // Function input in RoundedPanel
        functionField = new PlaceholderTextField("e.g., x^2 - 4x + 4");
        functionField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        functionField.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        functionField.setBackground(new Color(245, 247, 250));
        RoundedPanel functionPanel = new RoundedPanel(28, new Color(245, 247, 250));
        functionPanel.setLayout(new BorderLayout());
        functionPanel.add(functionField, BorderLayout.CENTER);
        left.add(functionPanel, "span 2, h 44!, growx, wrap");

        // Initial Guess labels
        JLabel guessLabel1 = new JLabel("Initial Guess");
        guessLabel1.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(guessLabel1);

        JLabel guessLabel2 = new JLabel("Initial Guess");
        guessLabel2.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(guessLabel2, "wrap");

        // Initial Guess fields in RoundedPanels
        initialGuessField1 = new PlaceholderTextField("e.g., x₀ = 1");
        initialGuessField1.setFont(new Font("SansSerif", Font.PLAIN, 16));
        initialGuessField1.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        initialGuessField1.setBackground(new Color(245, 247, 250));
        RoundedPanel guessPanel1 = new RoundedPanel(28, new Color(245, 247, 250));
        guessPanel1.setLayout(new BorderLayout());
        guessPanel1.add(initialGuessField1, BorderLayout.CENTER);
        left.add(guessPanel1, "h 44!, growx");

        initialGuessField2 = new PlaceholderTextField("e.g., x₁ = 2");
        initialGuessField2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        initialGuessField2.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        initialGuessField2.setBackground(new Color(245, 247, 250));
        RoundedPanel guessPanel2 = new RoundedPanel(28, new Color(245, 247, 250));
        guessPanel2.setLayout(new BorderLayout());
        guessPanel2.add(initialGuessField2, BorderLayout.CENTER);
        left.add(guessPanel2, "h 44!, growx, wrap");

        // Initial Guess hints
        JLabel guessHint1 = new JLabel("Starting point for iteration for x₀");
        guessHint1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guessHint1.setForeground(new Color(150, 150, 150));
        left.add(guessHint1);

        JLabel guessHint2 = new JLabel("Starting point for iteration for x₁");
        guessHint2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guessHint2.setForeground(new Color(150, 150, 150));
        left.add(guessHint2, "wrap");

        // Tolerance label
        JLabel tolLabel = new JLabel("Tolerance");
        tolLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(tolLabel, "span 2, wrap");

        // Tolerance field in RoundedPanel
        toleranceField = new PlaceholderTextField("e.g., 0.0001");
        toleranceField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        toleranceField.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        toleranceField.setBackground(new Color(245, 247, 250));
        RoundedPanel tolPanel = new RoundedPanel(28, new Color(245, 247, 250));
        tolPanel.setLayout(new BorderLayout());
        tolPanel.add(toleranceField, BorderLayout.CENTER);
        left.add(tolPanel, "span 2, h 44!, growx, wrap");

        // Tolerance hint
        JLabel tolHint = new JLabel("Desired accuracy of result");
        tolHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tolHint.setForeground(new Color(150, 150, 150));
        left.add(tolHint, "span 2, wrap");

        // Description Panel in RoundedPanel
        RoundedPanel descPanel = new RoundedPanel(28, new Color(245, 247, 250));
        descPanel.setLayout(new BorderLayout());
        JLabel descTitle = new JLabel("Method Description");
        descTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        descTitle.setForeground(Color.BLACK);
        descTitle.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 0));
        JTextArea desc = new JTextArea(methodDescription);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc.setForeground(Color.DARK_GRAY);
        desc.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        descPanel.add(descTitle, BorderLayout.NORTH);
        descPanel.add(desc, BorderLayout.CENTER);
        left.add(descPanel, "span 2, growx, h 80!, wrap");

        // Calculate button in RoundedPanel
        calculateButton = new JButton("Calculate");
        calculateButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        calculateButton.setBackground(new Color(51, 102, 255));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        RoundedPanel calcPanel = new RoundedPanel(28, new Color(51, 102, 255));
        calcPanel.setLayout(new BorderLayout());
        calcPanel.add(calculateButton, BorderLayout.CENTER);
        left.add(calcPanel, "span 2, growx, h 54!, gaptop 30, aligny bottom");

        // Right panel for history and answer
        JPanel right = new JPanel(new MigLayout(
                "fill, wrap 1, gapy 18", "[grow,fill]", ""
        ));
        right.setOpaque(false);

        RoundedPanel historyPanel = new RoundedPanel(28, new Color(245, 247, 250));
        historyPanel.setLayout(new BorderLayout());
        JLabel historyLabel = new JLabel("Calculation History");
        historyLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        historyLabel.setForeground(Color.BLACK);
        historyLabel.setBorder(BorderFactory.createEmptyBorder(10, 18, 0, 0));
        historyPanel.add(historyLabel, BorderLayout.NORTH);

        historyArea = new JTextArea("No calculations yet. Start by entering a function.");
        historyArea.setEditable(false);
        historyArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        historyArea.setBackground(new Color(245, 247, 250));
        historyArea.setForeground(new Color(150, 150, 150));
        historyArea.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        right.add(historyPanel, "grow, pushy, hmin 120");

        RoundedPanel answerPanel = new RoundedPanel(28, new Color(245, 247, 250));
        answerPanel.setLayout(new BorderLayout());
        JLabel answerLabel = new JLabel("Answer");
        answerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        answerLabel.setForeground(Color.BLACK);
        answerLabel.setBorder(BorderFactory.createEmptyBorder(10, 18, 0, 0));
        answerPanel.add(answerLabel, BorderLayout.NORTH);

        answerField = new JTextField();
        answerField.setEditable(false);
        answerField.setFont(new Font("SansSerif", Font.BOLD, 16));
        answerField.setBackground(new Color(245, 247, 250));
        answerField.setForeground(Color.DARK_GRAY);
        answerField.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        answerPanel.add(answerField, BorderLayout.CENTER);
        right.add(answerPanel, "growx, hmin 48");

        add(left, "grow, push, w 50%");
        add(right, "grow, push, w 50%");
    }
}