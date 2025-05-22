package src;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class MethodCalculatorPanel extends JPanel {
    private JTextField functionField;
    private JTextField initialGuessField1;
    private JTextField initialGuessField2;
    private JTextField toleranceField;
    private JTextArea historyArea;
    private JTextField answerField;
    private JButton calculateButton;
    private String methodType;
    private JTable iterationTable;
    private DefaultTableModel tableModel;
    private JPanel iterationPanel;
    private JPanel resultPanel;
    private CardLayout cardLayout;
    private boolean hasTwoGuesses;
    
    // Maximum iterations constant to prevent infinite loops
    private static final int MAX_ITERATIONS = 100;

    public MethodCalculatorPanel(String methodDescription, boolean hasTwoGuesses) {
        this.hasTwoGuesses = hasTwoGuesses;
        Color outlineColor = new Color(200, 210, 230); // soft blue/gray outline
        int radius = 40; // rounder corners

        setBackground(new Color(250, 252, 255));
        setLayout(new MigLayout("fill, insets 32, gap 32", "[grow,fill][grow,fill]", "[grow,fill]"));

        // Identify which method this panel represents based on the description
        if (methodDescription.contains("Newton's method")) {
            methodType = "Newton-Raphson";
        } else if (methodDescription.contains("Secant method")) {
            methodType = "Secant";
        } else if (methodDescription.contains("Bisection method")) {
            methodType = "Bisection";
        } else if (methodDescription.contains("Fixed-Point method")) {
            methodType = "Fixed-Point";
        } else if (methodDescription.contains("False Position method")) {
            methodType = "False-Position";
        } else {
            methodType = "Unknown";
        }

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
        functionField = new PlaceholderTextField(getFunctionPlaceholder());
        functionField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        functionField.setBorder(null); // Remove outline
        functionField.setBackground(new Color(245, 247, 250));
        RoundedPanel functionPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        functionPanel.setLayout(new BorderLayout());
        functionPanel.add(functionField, BorderLayout.CENTER);
        left.add(functionPanel, "span 2, h 48!, growx, wrap");

        // Initial Guess labels
        JLabel guessLabel1 = new JLabel(hasTwoGuesses ? getFirstGuessLabel() : "Initial Guess");
        guessLabel1.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(guessLabel1);

        JLabel guessLabel2 = new JLabel(getSecondGuessLabel());
        guessLabel2.setFont(new Font("SansSerif", Font.BOLD, 14));
        if (hasTwoGuesses) {
            left.add(guessLabel2, "wrap");
        } else {
            left.add(new JLabel(), "wrap");
        }

        // Initial Guess fields in RoundedPanels
        initialGuessField1 = new PlaceholderTextField(getFirstGuessPlaceholder());
        initialGuessField1.setFont(new Font("SansSerif", Font.PLAIN, 16));
        initialGuessField1.setBorder(null); // Remove outline
        initialGuessField1.setBackground(new Color(245, 247, 250));
        RoundedPanel guessPanel1 = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        guessPanel1.setLayout(new BorderLayout());
        guessPanel1.add(initialGuessField1, BorderLayout.CENTER);
        left.add(guessPanel1, "h 48!, growx");

        if (hasTwoGuesses) {
            initialGuessField2 = new PlaceholderTextField(getSecondGuessPlaceholder());
            initialGuessField2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            initialGuessField2.setBorder(null); // Remove outline
            initialGuessField2.setBackground(new Color(245, 247, 250));
            RoundedPanel guessPanel2 = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
            guessPanel2.setLayout(new BorderLayout());
            guessPanel2.add(initialGuessField2, BorderLayout.CENTER);
            left.add(guessPanel2, "h 48!, growx, wrap");
        } else {
            left.add(new JLabel(), "wrap");
        }

        // Initial Guess hints
        JLabel guessHint1 = new JLabel(getFirstGuessHint());
        guessHint1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guessHint1.setForeground(new Color(150, 150, 150));
        left.add(guessHint1);

        if (hasTwoGuesses) {
            JLabel guessHint2 = new JLabel(getSecondGuessHint());
            guessHint2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            guessHint2.setForeground(new Color(150, 150, 150));
            left.add(guessHint2, "wrap");
        } else {
            left.add(new JLabel(), "wrap");
        }

        // Tolerance label
        JLabel tolLabel = new JLabel("Tolerance");
        tolLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(tolLabel, "span 2, wrap");

        // Tolerance field in RoundedPanel
        toleranceField = new PlaceholderTextField("e.g., 0.0001");
        toleranceField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        toleranceField.setBorder(null); // Remove outline
        toleranceField.setBackground(new Color(245, 247, 250));
        RoundedPanel tolPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        tolPanel.setLayout(new BorderLayout());
        tolPanel.add(toleranceField, BorderLayout.CENTER);
        left.add(tolPanel, "span 2, h 48!, growx, wrap");

        // Tolerance hint
        JLabel tolHint = new JLabel("Desired accuracy of result");
        tolHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tolHint.setForeground(new Color(150, 150, 150));
        left.add(tolHint, "span 2, wrap");

        // Description Panel in RoundedPanel
        RoundedPanel descPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
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
        calculateButton.setBorder(null); // Remove outline
        RoundedPanel calcPanel = new RoundedPanel(radius, new Color(51, 102, 255), outlineColor);
        calcPanel.setLayout(new BorderLayout());
        calcPanel.add(calculateButton, BorderLayout.CENTER);
        left.add(calcPanel, "span 2, growx, h 54!, gaptop 30, aligny bottom");

        // Right panel with CardLayout for switching between welcome screen and results
        JPanel right = new JPanel();
        cardLayout = new CardLayout();
        right.setLayout(cardLayout);
        right.setOpaque(false);

        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        RoundedPanel welcomeContent = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        welcomeContent.setLayout(new BorderLayout());
        
        JLabel welcomeTitle = new JLabel("Welcome to " + methodType + " Method");
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeTitle.setForeground(new Color(51, 102, 255));
        welcomeTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        welcomeContent.add(welcomeTitle, BorderLayout.NORTH);
        
        JTextArea welcomeText = new JTextArea(
            "Enter your function and parameters on the left panel, then click Calculate to find the root.\n\n" +
            "This calculator will show you step-by-step how the " + methodType + " method converges to the solution."
        );
        welcomeText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setEditable(false);
        welcomeText.setOpaque(false);
        welcomeText.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        welcomeContent.add(welcomeText, BorderLayout.CENTER);
        
        // Add a sample equation specific to the method
        String sampleEquation = getSampleEquation();
        
        JLabel exampleLabel = new JLabel(sampleEquation);
        exampleLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        exampleLabel.setForeground(new Color(100, 100, 100));
        exampleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        welcomeContent.add(exampleLabel, BorderLayout.SOUTH);
        
        welcomePanel.add(welcomeContent, BorderLayout.CENTER);
        
        // Result panel with table view and details
        resultPanel = new JPanel(new MigLayout("fill, wrap 1, gapy 18", "[grow,fill]", ""));
        resultPanel.setOpaque(false);
        
        // Iteration table panel
        iterationPanel = new JPanel(new BorderLayout());
        iterationPanel.setOpaque(false);
        RoundedPanel tableRoundedPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        tableRoundedPanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = new JLabel("Iteration Steps");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tableRoundedPanel.add(tableTitle, BorderLayout.NORTH);
        
        // Create table for iterations with method-specific columns
        String[] columnNames = getTableColumnNames();
        tableModel = new DefaultTableModel(columnNames, 0);
        iterationTable = new JTable(tableModel);
        iterationTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iterationTable.setRowHeight(30);
        iterationTable.setShowGrid(true);
        iterationTable.setGridColor(new Color(230, 230, 230));
        iterationTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        iterationTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        JScrollPane tableScrollPane = new JScrollPane(iterationTable);
        tableScrollPane.setBorder(null);
        tableRoundedPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        iterationPanel.add(tableRoundedPanel);
        
        // Result details panel
        JPanel detailsPanel = new JPanel(new MigLayout("fillx, wrap 1", "[grow,fill]", ""));
        detailsPanel.setOpaque(false);
        
        // Answer panel
        RoundedPanel answerRoundedPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        answerRoundedPanel.setLayout(new BorderLayout());
        
        JPanel answerHeader = new JPanel(new BorderLayout());
        answerHeader.setOpaque(false);
        
        JLabel answerLabel = new JLabel("Root Found");
        answerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        answerLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        
        JLabel methodLabel = new JLabel(methodType + " Method");
        methodLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        methodLabel.setForeground(new Color(100, 100, 100));
        methodLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        methodLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        answerHeader.add(answerLabel, BorderLayout.WEST);
        answerHeader.add(methodLabel, BorderLayout.EAST);
        
        answerRoundedPanel.add(answerHeader, BorderLayout.NORTH);
        
        JPanel answerContent = new JPanel(new BorderLayout());
        answerContent.setOpaque(false);
        answerContent.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));
        
        answerField = new JTextField();
        answerField.setEditable(false);
        answerField.setFont(new Font("SansSerif", Font.BOLD, 24));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setForeground(new Color(51, 102, 255));
        answerField.setBackground(new Color(245, 247, 250));
        answerField.setBorder(null);
        
        answerContent.add(answerField, BorderLayout.CENTER);
        answerRoundedPanel.add(answerContent, BorderLayout.CENTER);
        
        detailsPanel.add(answerRoundedPanel, "growx, h 100!");
        
        // Function summary panel
        RoundedPanel summaryPanel = new RoundedPanel(radius, new Color(245, 247, 250), outlineColor);
        summaryPanel.setLayout(new BorderLayout());
        
        JLabel summaryTitle = new JLabel("Calculation Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        summaryTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        summaryPanel.add(summaryTitle, BorderLayout.NORTH);
        
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        historyArea.setBackground(new Color(245, 247, 250));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(null);
        historyScroll.setOpaque(false);
        historyScroll.getViewport().setOpaque(false);
        
        summaryPanel.add(historyScroll, BorderLayout.CENTER);
        
        detailsPanel.add(summaryPanel, "growx, h 150!");
        
        resultPanel.add(iterationPanel, "grow, h 60%");
        resultPanel.add(detailsPanel, "grow, h 40%");
        
        // Add both panels to the card layout
        right.add(welcomePanel, "welcome");
        right.add(resultPanel, "results");
        
        // Initially show the welcome panel
        cardLayout.show(right, "welcome");
        
        add(left, "grow, push, w 40%");
        add(right, "grow, push, w 60%");

        // Add action listener to calculate button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRoot();
            }
        });
    }

    private String[] getTableColumnNames() {
        switch (methodType) {
            case "Newton-Raphson":
                return new String[]{"Iteration", "x_n", "f(x_n)", "f'(x_n)", "Error"};
            case "Secant":
                return new String[]{"Iteration", "x_n-1", "x_n", "f(x_n-1)", "f(x_n)", "Error"};
            case "Bisection":
            case "False-Position":
                return new String[]{"Iteration", "a", "b", "c", "f(c)", "Error"};
            case "Fixed-Point":
                return new String[]{"Iteration", "x_n", "g(x_n)", "Error"};
            default:
                return new String[]{"Iteration", "x_n", "f(x_n)", "Error"};
        }
    }
    
    private String getFunctionPlaceholder() {
        switch (methodType) {
            case "Fixed-Point":
                return "e.g., sqrt(4+x) (function g(x) where x = g(x))";
            default:
                return "e.g., x^2 - 4";
        }
    }
    
    private String getFirstGuessLabel() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "Left Endpoint (a)";
            case "Secant":
                return "First Guess (x₀)";
            default:
                return "Initial Guess";
        }
    }
    
    private String getSecondGuessLabel() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "Right Endpoint (b)";
            case "Secant":
                return "Second Guess (x₁)";
            default:
                return "Initial Guess";
        }
    }
    
    private String getFirstGuessPlaceholder() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "e.g., a = 1";
            case "Secant":
                return "e.g., x₀ = 1";
            default:
                return "e.g., 1.0";
        }
    }
    
    private String getSecondGuessPlaceholder() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "e.g., b = 3";
            case "Secant":
                return "e.g., x₁ = 2";
            default:
                return "e.g., 2.0";
        }
    }
    
    private String getFirstGuessHint() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "Left endpoint of interval containing the root";
            case "Secant":
                return "First point for the secant line approximation";
            default:
                return "Starting point for iteration";
        }
    }
    
    private String getSecondGuessHint() {
        switch (methodType) {
            case "Bisection":
            case "False-Position":
                return "Right endpoint of interval containing the root";
            case "Secant":
                return "Second point for the secant line approximation";
            default:
                return "Starting point for iteration";
        }
    }
    
    private String getSampleEquation() {
        switch (methodType) {
            case "Newton-Raphson":
                return "Try with: x^2 - 4 with initial guess 3";
            case "Secant":
                return "Try with: x^2 - 4 with initial guesses 1 and 3";
            case "Bisection":
                return "Try with: x^2 - 4 with interval [1, 3]";
            case "Fixed-Point":
                return "Try with: sqrt(4+x) with initial guess 1";
            case "False-Position":
                return "Try with: x^2 - 4 with interval [1, 3]";
            default:
                return "Try with: x^2 - 4";
        }
    }

    private void calculateRoot() {
        try {
            String function = functionField.getText().trim();
            String tolerance = toleranceField.getText().trim();
            String initialGuess1 = initialGuessField1.getText().trim();
            String initialGuess2 = hasTwoGuesses ? initialGuessField2.getText().trim() : "";
            
            // Validate inputs
            if (function.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a function.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (initialGuess1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter " + (hasTwoGuesses ? getFirstGuessLabel() : "an initial guess."), 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (hasTwoGuesses && initialGuess2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter " + getSecondGuessLabel(), "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (tolerance.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a tolerance value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse inputs
            double guess1Value;
            double guess2Value = 0;
            double toleranceValue;
            
            try {
                guess1Value = Double.parseDouble(initialGuess1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid value for " + getFirstGuessLabel() + 
                    ". Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (hasTwoGuesses) {
                try {
                    guess2Value = Double.parseDouble(initialGuess2);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid value for " + getSecondGuessLabel() + 
                        ". Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                toleranceValue = Double.parseDouble(tolerance);
                if (toleranceValue <= 0) {
                    JOptionPane.showMessageDialog(this, "Tolerance must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid tolerance. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Call the appropriate method based on the method type
            CalculatorBackend.Result result = null;
            
            switch (methodType) {
                case "Newton-Raphson":
                    result = CalculatorBackend.newtonRaphson(function, guess1Value, toleranceValue, MAX_ITERATIONS);
                    break;
                case "Secant":
                    result = CalculatorBackend.secant(function, guess1Value, guess2Value, toleranceValue, MAX_ITERATIONS);
                    break;
                case "Bisection":
                    result = CalculatorBackend.bisection(function, guess1Value, guess2Value, toleranceValue, MAX_ITERATIONS);
                    break;
                case "Fixed-Point":
                    result = CalculatorBackend.fixedPoint(function, guess1Value, toleranceValue, MAX_ITERATIONS);
                    break;
                case "False-Position":
                    result = CalculatorBackend.falsePosition(function, guess1Value, guess2Value, toleranceValue, MAX_ITERATIONS);
                    break;
            }
            
            // Display results
            if (result != null) {
                updateResultDisplay(result, function);
                // Switch to the results panel
                cardLayout.show((Container)resultPanel.getParent(), "results");
            } else {
                historyArea.setText("Method not implemented yet: " + methodType);
                answerField.setText("N/A");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error in calculation: " + ex.getMessage(), 
                "Calculation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateResultDisplay(CalculatorBackend.Result result, String function) {
        // Update the answer field
        DecimalFormat df = new DecimalFormat("0.00000000");
        
        if (result.hasConverged()) {
            answerField.setText(df.format(result.getRoot()));
            answerField.setForeground(new Color(51, 102, 255)); // Reset to blue for success
        } else {
            answerField.setText("Failed to converge");
            answerField.setForeground(new Color(220, 50, 50)); // Red color for failure
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Populate the table with iteration steps based on method type
        List<CalculatorBackend.IterationStep> steps = result.getSteps();
        for (CalculatorBackend.IterationStep step : steps) {
            Object[] row = createTableRow(step, df);
            tableModel.addRow(row);
        }
        
        // Update the summary area
        StringBuilder summary = new StringBuilder();
        summary.append("Function: ").append(function).append("\n\n");
        
        if (result.hasConverged()) {
            summary.append("✓ Root found: x = ").append(df.format(result.getRoot())).append("\n");
            summary.append("✓ Iterations required: ").append(steps.size()).append("\n");
            
            // Get function value at the root (if available)
            if (!steps.isEmpty()) {
                if (methodType.equals("Fixed-Point")) {
                    // For fixed point, the function value is actually g(x)
                    double lastGx = steps.get(steps.size() - 1).getFx();
                    summary.append("✓ g(x) value at root: ").append(df.format(lastGx)).append("\n");
                    summary.append("✓ |g(x) - x| at root: ").append(df.format(Math.abs(lastGx - result.getRoot()))).append("\n");
                } else {
                    // For other methods, we can get f(x)
                    double lastFx = steps.get(steps.size() - 1).getFx();
                    summary.append("✓ Function value at root: f(x) = ").append(df.format(lastFx)).append("\n");
                }
            }
            
            // Get final error
            double lastError = steps.isEmpty() ? 0 : steps.get(steps.size() - 1).getError();
            summary.append("✓ Final error: ").append(df.format(lastError));
        } else {
            summary.append("✗ Method failed to converge\n");
            
            if (steps.size() >= MAX_ITERATIONS) {
                summary.append("✗ Reached maximum number of iterations (100)\n");
            } else if (methodType.equals("Bisection") || methodType.equals("False-Position")) {
                summary.append("✗ Function may not have a sign change in the given interval\n");
            } else if (methodType.equals("Newton-Raphson")) {
                // Check if derivative was too close to zero
                double lastFpx = steps.isEmpty() ? 0 : steps.get(steps.size() - 1).getFpx();
                if (Math.abs(lastFpx) < 1e-10) {
                    summary.append("✗ Derivative became too close to zero\n");
                }
            } else if (methodType.equals("Fixed-Point")) {
                summary.append("✗ Method may be diverging. Try a different function or initial guess.\n");
            }
            
            summary.append("\nTry a different initial guess, interval, or function.");
        }
        
        historyArea.setText(summary.toString());
    }
    
    private Object[] createTableRow(CalculatorBackend.IterationStep step, DecimalFormat df) {
        switch (methodType) {
            case "Newton-Raphson":
                return new Object[] {
                    step.getIteration(),
                    df.format(step.getX()),
                    df.format(step.getFx()),
                    df.format(step.getFpx()),
                    df.format(step.getError())
                };
            case "Secant":
                if (step.getIteration() == 0) {
                    // First row just displays initial guesses
                    return new Object[] {
                        step.getIteration(),
                        df.format(step.getX()),
                        "N/A",
                        df.format(step.getFx()),
                        "N/A",
                        "N/A"
                    };
                } else {
                    return new Object[] {
                        step.getIteration(),
                        df.format(step.getX()),
                        "Next",  // This would be calculated in the next step
                        df.format(step.getFx()),
                        "Next",  // This would be calculated in the next step
                        df.format(step.getError())
                    };
                }
            case "Bisection":
            case "False-Position":
                return new Object[] {
                    step.getIteration(),
                    "a",  // Would need to track interval bounds
                    "b",  // Would need to track interval bounds
                    df.format(step.getX()),
                    df.format(step.getFx()),
                    df.format(step.getError())
                };
            case "Fixed-Point":
                return new Object[] {
                    step.getIteration(),
                    df.format(step.getX()),
                    df.format(step.getFx()),  // This is g(x) in fixed-point
                    df.format(step.getError())
                };
            default:
                return new Object[] {
                    step.getIteration(),
                    df.format(step.getX()),
                    df.format(step.getFx()),
                    df.format(step.getError())
                };
        }
    }
}