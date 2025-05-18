import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.List;

public class ModernCalculatorPanel extends JPanel {
    private JTextField functionField;
    private JTextField initialGuessField1;
    private JTextField initialGuessField2;
    private JTextField toleranceField;
    private JTextArea historyArea;
    private JLabel answerValue;
    private JPanel stepPanel;
    private JTable iterationTable;
    private DefaultTableModel tableModel;
    private JButton calculateButton;
    private String methodType;
    private CardLayout resultsCardLayout;
    private JPanel resultsPanel;    
    private JPanel noResultsPanel;
    private JPanel hasResultsPanel;
    private boolean hasTwoGuesses;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JPanel progressPanel; // Added this field to store a reference to the progress panel
    
    // Visual elements
    private final Color backgroundColor = new Color(18, 23, 30);
    private final Color panelColor = new Color(35, 42, 52);
    private final Color accentColor = new Color(115, 138, 247);
    private final Color successColor = new Color(76, 209, 149);
    private final Color errorColor = new Color(251, 85, 85);
    private final Color textColor = new Color(236, 239, 244);
    private final Color textSecondaryColor = new Color(160, 170, 190);
    private final Color separatorColor = new Color(55, 65, 80);
    private final Color inputFieldColor = new Color(46, 54, 66);
    
    // Constants
    private static final int MAX_ITERATIONS = 100;
    private static final int BORDER_RADIUS = 12;
    
    // Animation elements
    private Timer animationTimer;
    private int animationStep = 0;
    
    public ModernCalculatorPanel(String methodDescription, boolean hasTwoGuesses) {
        this.hasTwoGuesses = hasTwoGuesses;
        
        // Identify method type
        if (methodDescription.contains("Newton's method")) {
            methodType = "Newton-Raphson";
        } else if (methodDescription.contains("Secant method")) {
            methodType = "Secant";
        } else if (methodDescription.contains("Bisection method")) {
            methodType = "Bisection";        } else if (methodDescription.contains("Fixed-Point method")) {
            methodType = "Fixed-Point";
        } else if (methodDescription.contains("False Position method")) {
            methodType = "False Position";
        } else {
            methodType = "Unknown";
        }
        
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setLayout(new BorderLayout(20, 20));
        
        // Create main containers
        JPanel leftPanel = createLeftPanel(methodDescription);
        JPanel rightPanel = createRightPanel();
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        
        // Set up animation timer
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationStep++;
                if (animationStep > 10) {
                    animationStep = 0;
                }
                progressBar.setValue(animationStep * 10);
                
                // Animate status text
                StringBuilder status = new StringBuilder("Calculating");
                for (int i = 0; i < animationStep % 4; i++) {
                    status.append(".");
                }
                statusLabel.setText(status.toString());
            }
        });
    }
    
    private JPanel createLeftPanel(String methodDescription) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(backgroundColor);
        leftPanel.setPreferredSize(new Dimension(320, 100));
        
        // Method title
        JLabel titleLabel = new JLabel(methodType + " Method");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        // Method description
        JTextArea descriptionArea = new JTextArea(methodDescription);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(textSecondaryColor);
        descriptionArea.setBackground(backgroundColor);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Input fields container
        JPanel fieldsPanel = createRoundedPanel(panelColor, BORDER_RADIUS);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
          // Function field
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.setOpaque(false);
        JLabel functionLabel = new JLabel(methodType.equals("Fixed-Point") ? "Function g(x)" : "Function f(x)");
        functionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        functionLabel.setForeground(textColor);
        functionPanel.add(functionLabel, BorderLayout.NORTH);
        
        functionField = createStyledTextField(getFunctionPlaceholder());
        functionPanel.add(functionField, BorderLayout.CENTER);
        
        // Hints for function
        JLabel functionHint = new JLabel(getFunctionHint());
        functionHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        functionHint.setForeground(textSecondaryColor);
        functionPanel.add(functionHint, BorderLayout.SOUTH);
        
        fieldsPanel.add(functionPanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Initial guesses panel (1 or 2 fields depending on method)
        JPanel guessesPanel = new JPanel(new GridLayout(hasTwoGuesses ? 2 : 1, 1, 0, 10));
        guessesPanel.setOpaque(false);
        
        // First guess
        JPanel guess1Panel = new JPanel(new BorderLayout());
        guess1Panel.setOpaque(false);
        JLabel guess1Label = new JLabel(getFirstGuessLabel());
        guess1Label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        guess1Label.setForeground(textColor);
        guess1Panel.add(guess1Label, BorderLayout.NORTH);
        
        initialGuessField1 = createStyledTextField(getFirstGuessPlaceholder());
        guess1Panel.add(initialGuessField1, BorderLayout.CENTER);
        
        JLabel guess1Hint = new JLabel(getFirstGuessHint());
        guess1Hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        guess1Hint.setForeground(textSecondaryColor);
        guess1Panel.add(guess1Hint, BorderLayout.SOUTH);
        
        guessesPanel.add(guess1Panel);
        
        // Second guess if applicable
        if (hasTwoGuesses) {
            JPanel guess2Panel = new JPanel(new BorderLayout());
            guess2Panel.setOpaque(false);
            JLabel guess2Label = new JLabel(getSecondGuessLabel());
            guess2Label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            guess2Label.setForeground(textColor);
            guess2Panel.add(guess2Label, BorderLayout.NORTH);
            
            initialGuessField2 = createStyledTextField(getSecondGuessPlaceholder());
            guess2Panel.add(initialGuessField2, BorderLayout.CENTER);
            
            JLabel guess2Hint = new JLabel(getSecondGuessHint());
            guess2Hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            guess2Hint.setForeground(textSecondaryColor);
            guess2Panel.add(guess2Hint, BorderLayout.SOUTH);
            
            guessesPanel.add(guess2Panel);
        }
        
        fieldsPanel.add(guessesPanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Tolerance field
        JPanel tolerancePanel = new JPanel(new BorderLayout());
        tolerancePanel.setOpaque(false);
        JLabel toleranceLabel = new JLabel("Tolerance");
        toleranceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toleranceLabel.setForeground(textColor);
        tolerancePanel.add(toleranceLabel, BorderLayout.NORTH);
        
        toleranceField = createStyledTextField("e.g., 0.0001");
        tolerancePanel.add(toleranceField, BorderLayout.CENTER);
        
        JLabel toleranceHint = new JLabel("Desired accuracy of the result");
        toleranceHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        toleranceHint.setForeground(textSecondaryColor);
        tolerancePanel.add(toleranceHint, BorderLayout.SOUTH);
        
        fieldsPanel.add(tolerancePanel);
        fieldsPanel.add(Box.createVerticalStrut(25));
          // Calculate button
        calculateButton = new JButton("Calculate Root") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(135, 155, 255));
                } else {
                    g2.setColor(accentColor);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                // Draw text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(this.getText(), g2).getBounds();
                int textX = (getWidth() - textRect.width) / 2;
                int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        calculateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        calculateButton.setFocusPainted(false);
        calculateButton.setBorderPainted(false);
        calculateButton.setContentAreaFilled(false);
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
          // Button hover effect - not needed anymore since we handle it in paintComponent
        // Just add the action listener
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCalculation();
            }
        });
        
        fieldsPanel.add(calculateButton);
        
        // Progress bar and status (initially hidden)
        progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setOpaque(false);
        progressPanel.setVisible(false);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(false);
        progressBar.setForeground(accentColor);
        progressBar.setBackground(inputFieldColor);
        progressBar.setBorderPainted(false);
        
        statusLabel = new JLabel("Calculating...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(textSecondaryColor);
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);
        
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(progressPanel);
        
        // Add components to left panel
        leftPanel.add(titleLabel);
        leftPanel.add(descriptionArea);
        leftPanel.add(fieldsPanel);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(backgroundColor);
        
        // Initialize card layout for showing/hiding results
        resultsCardLayout = new CardLayout();
        resultsPanel = new JPanel(resultsCardLayout);
        resultsPanel.setOpaque(false);
        
        // Panel to show when no calculation has been performed
        noResultsPanel = createNoResultsPanel();
        
        // Panel to show when we have results
        hasResultsPanel = new JPanel(new BorderLayout(0, 20));
        hasResultsPanel.setOpaque(false);
        
        // Result header (contains the answer value)
        JPanel resultHeader = createRoundedPanel(panelColor, BORDER_RADIUS);
        resultHeader.setLayout(new BorderLayout());
        resultHeader.setPreferredSize(new Dimension(100, 100));
        
        JPanel answerPanel = new JPanel(new BorderLayout(10, 0));
        answerPanel.setOpaque(false);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel answerLabel = new JLabel("Root Value");
        answerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        answerLabel.setForeground(textColor);
        
        answerValue = new JLabel("Waiting for calculation...");
        answerValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        answerValue.setForeground(accentColor);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(answerLabel);
        
        answerPanel.add(titlePanel, BorderLayout.NORTH);
        answerPanel.add(answerValue, BorderLayout.CENTER);
        
        resultHeader.add(answerPanel, BorderLayout.CENTER);
        
        // Results tabbed pane
        JTabbedPane resultsTabs = new JTabbedPane();
        resultsTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsTabs.setForeground(textColor);
        resultsTabs.setBackground(panelColor);
        
        // Steps panel - contains the iteration table
        stepPanel = createRoundedPanel(panelColor, BORDER_RADIUS);
        stepPanel.setLayout(new BorderLayout());
        stepPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create the iteration table
        String[] columnNames = getTableColumnNames();
        tableModel = new DefaultTableModel(columnNames, 0);
        iterationTable = new JTable(tableModel);
        
        // Style the table
        iterationTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        iterationTable.setForeground(textColor);
        iterationTable.setBackground(panelColor);
        iterationTable.setGridColor(separatorColor);
        iterationTable.setRowHeight(30);
        iterationTable.setShowGrid(true);
        iterationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        iterationTable.getTableHeader().setBackground(inputFieldColor);
        iterationTable.getTableHeader().setForeground(textColor);
        
        // Customize table renderer for better appearance
        for (int i = 0; i < iterationTable.getColumnCount(); i++) {
            iterationTable.getColumnModel().getColumn(i).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                  boolean isSelected, boolean hasFocus,
                                                                  int row, int column) {
                        Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                        
                        c.setBackground(row % 2 == 0 ? panelColor : new Color(40, 48, 58));
                        c.setForeground(textColor);
                        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                        setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.RIGHT);
                        
                        return c;
                    }
                }
            );
        }
        
        JScrollPane tableScrollPane = new JScrollPane(iterationTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.getViewport().setBackground(panelColor);
        
        stepPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Summary panel - contains text details of the calculation
        JPanel summaryPanel = createRoundedPanel(panelColor, BORDER_RADIUS);
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyArea.setForeground(textColor);
        historyArea.setBackground(panelColor);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(null);
        historyScroll.getViewport().setBackground(panelColor);
        
        summaryPanel.add(historyScroll, BorderLayout.CENTER);
        
        // Add tabs
        resultsTabs.addTab("Iteration Steps", null, stepPanel, "View all iteration steps");
        resultsTabs.addTab("Summary", null, summaryPanel, "View calculation summary");
        
        // Add components to results panel
        hasResultsPanel.add(resultHeader, BorderLayout.NORTH);
        hasResultsPanel.add(resultsTabs, BorderLayout.CENTER);
        
        // Add both states to the card layout
        resultsPanel.add(noResultsPanel, "noResults");
        resultsPanel.add(hasResultsPanel, "hasResults");
        
        // Show no results initially
        resultsCardLayout.show(resultsPanel, "noResults");
        
        rightPanel.add(resultsPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    // Method to create the no results panel with proper Unicode symbols
    private JPanel createNoResultsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Create some spacing
        panel.add(Box.createVerticalGlue());
        
        // Icon or placeholder - use Unicode checkmark that's better supported
        // JLabel iconLabel = new JLabel(");  // Unicode checkmark
        // iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        // iconLabel.setForeground(new Color(50, 60, 70));
        // iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Empty state message
        JLabel messageLabel = new JLabel("Enter parameters and click Calculate");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        messageLabel.setForeground(textSecondaryColor);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Example usage
        JLabel tipLabel = new JLabel(getSampleEquation());
        tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tipLabel.setForeground(textSecondaryColor);
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(tipLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                // Create a rounded background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputFieldColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
                
                // Let the original paint method handle text rendering
                super.paintComponent(g);
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setOpaque(false); // Make non-opaque so rounded background shows
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12)); // Slightly more horizontal padding
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        
        // Add placeholder text
        field.putClientProperty("placeholder", placeholder);
        
        // Custom rendering for placeholder
        field.putClientProperty("JTextField.placeholderText", placeholder);
        
        return field;
    }
    
    private JPanel createRoundedPanel(Color bgColor, int radius) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose();
            }
        };
    }
      private void startCalculation() {
        // Disable button and show progress
        calculateButton.setEnabled(false);
        // No need to set background as it's handled in the paintComponent method
        
        // Make progress panel visible - no need to find it by index anymore
        progressPanel.setVisible(true);
        
        // Start animation
        animationTimer.start();
        
        // Run calculation in separate thread
        SwingWorker<CalculatorBackend.Result, Void> worker = new SwingWorker<CalculatorBackend.Result, Void>() {
            private String function;
            private double guess1Value;
            private double guess2Value;
            private double toleranceValue;
            private Exception validationError;
            
            @Override
            protected CalculatorBackend.Result doInBackground() throws Exception {
                try {
                    // Get input values
                    function = functionField.getText().trim();
                    String initialGuess1 = initialGuessField1.getText().trim();
                    String initialGuess2 = hasTwoGuesses ? initialGuessField2.getText().trim() : "";
                    String tolerance = toleranceField.getText().trim();
                    
                    // Validate inputs
                    if (function.isEmpty()) {
                        validationError = new Exception("Please enter a function.");
                        return null;
                    }
                    
                    if (initialGuess1.isEmpty()) {
                        validationError = new Exception("Please enter " + 
                                                       (hasTwoGuesses ? getFirstGuessLabel() : "an initial guess."));
                        return null;
                    }
                    
                    if (hasTwoGuesses && initialGuess2.isEmpty()) {
                        validationError = new Exception("Please enter " + getSecondGuessLabel());
                        return null;
                    }
                    
                    if (tolerance.isEmpty()) {
                        validationError = new Exception("Please enter a tolerance value.");
                        return null;
                    }
                    
                    // Parse numeric inputs
                    try {
                        guess1Value = Double.parseDouble(initialGuess1);
                    } catch (NumberFormatException ex) {
                        validationError = new Exception("Invalid value for " + getFirstGuessLabel() + 
                                                      ". Please enter a valid number.");
                        return null;
                    }
                    
                    if (hasTwoGuesses) {
                        try {
                            guess2Value = Double.parseDouble(initialGuess2);
                        } catch (NumberFormatException ex) {
                            validationError = new Exception("Invalid value for " + getSecondGuessLabel() + 
                                                         ". Please enter a valid number.");
                            return null;
                        }
                    }
                    
                    try {
                        toleranceValue = Double.parseDouble(tolerance);
                        if (toleranceValue <= 0) {
                            validationError = new Exception("Tolerance must be a positive number.");
                            return null;
                        }
                    } catch (NumberFormatException ex) {
                        validationError = new Exception("Invalid tolerance. Please enter a valid number.");
                        return null;
                    }
                    
                    // Simulate longer calculation for demo purposes
                    Thread.sleep(1500);
                    
                    // Call appropriate calculation method
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
                            break;                        case "Fixed-Point":
                            result = CalculatorBackend.fixedPoint(function, guess1Value, toleranceValue, MAX_ITERATIONS);
                            break;
                        case "False Position":
                            result = CalculatorBackend.falsePosition(function, guess1Value, guess2Value, toleranceValue, MAX_ITERATIONS);
                            break;
                    }
                    
                    return result;
                    
                } catch (Exception e) {
                    validationError = e;
                    return null;
                }
            }
            
            @Override
            protected void done() {                // Stop animation
                animationTimer.stop();
                
                // Hide progress panel - use stored reference instead of finding by index
                progressPanel.setVisible(false);
                
                // Re-enable button
                calculateButton.setEnabled(true);
                // No need to set background as it's handled in the paintComponent method
                calculateButton.repaint(); // Force a repaint to show the enabled state
                
                try {
                    CalculatorBackend.Result result = get();
                    
                    if (validationError != null) {
                        JOptionPane.showMessageDialog(
                            ModernCalculatorPanel.this,
                            validationError.getMessage(),
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    
                    if (result != null) {
                        // Display results
                        updateResultsDisplay(result, function);
                        // Switch to results panel
                        resultsCardLayout.show(resultsPanel, "hasResults");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        ModernCalculatorPanel.this,
                        "Error in calculation: " + e.getMessage(),
                        "Calculation Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
      private void updateResultsDisplay(CalculatorBackend.Result result, String function) {
        // Create a format based on tolerance
        double tolerance = getToleranceValue();
        DecimalFormat df = createDecimalFormatFromTolerance(tolerance);
        
        // Update answer display
        if (result.hasConverged()) {
            answerValue.setText(df.format(result.getRoot()));
            answerValue.setForeground(successColor);
        } else {
            answerValue.setText("Failed to converge");
            answerValue.setForeground(errorColor);
        }
        
        // Clear and update table data
        tableModel.setRowCount(0);
        
        // Add steps to table
        List<CalculatorBackend.IterationStep> steps = result.getSteps();
        for (CalculatorBackend.IterationStep step : steps) {
            Object[] row = createTableRow(step, df);
            tableModel.addRow(row);
        }
        
        // Generate and display summary
        generateResultSummary(result, function, steps);
    }
      private void generateResultSummary(CalculatorBackend.Result result, String function, List<CalculatorBackend.IterationStep> steps) {
        // Create a format based on tolerance
        double tolerance = getToleranceValue();
        DecimalFormat df = createDecimalFormatFromTolerance(tolerance);
        
        StringBuilder summary = new StringBuilder();
        
        // Function
        summary.append("Function: ").append(function).append("\n\n");
        
        // Result status
        if (result.hasConverged()) {
            summary.append("Method successfully converged\n\n");
            summary.append("Root found: x = ").append(df.format(result.getRoot())).append("\n");
            summary.append("Iterations required: ").append(steps.size()).append("\n");
            
            // Get function value at the root (if available)
            if (!steps.isEmpty()) {
                if (methodType.equals("Fixed-Point")) {
                    // For fixed point, the function value is actually g(x)
                    double lastGx = steps.get(steps.size() - 1).getFx();
                    summary.append("g(x) value at root: ").append(df.format(lastGx)).append("\n");
                    summary.append("|g(x) - x| at root: ").append(df.format(Math.abs(lastGx - result.getRoot()))).append("\n");
                } else {
                    // For other methods, we can get f(x)
                    double lastFx = steps.get(steps.size() - 1).getFx();
                    summary.append("Function value at root: f(x) = ").append(df.format(lastFx)).append("\n");
                }
            }
            
            // Get final error
            double lastError = steps.isEmpty() ? 0 : steps.get(steps.size() - 1).getError();
            summary.append("Final error: ").append(df.format(lastError)).append("\n\n");
            
            // Method-specific details
            summary.append("Method Details:\n");
            switch (methodType) {
                case "Newton-Raphson":
                    summary.append("Newton-Raphson uses the formula: xₙ₊₁ = xₙ - f(xₙ)/f'(xₙ)\n");
                    summary.append("The derivative was approximated using the central difference method.\n");
                    break;
                case "Secant":
                    summary.append("Secant method uses the formula: xₙ₊₁ = xₙ - f(xₙ)·(xₙ - xₙ₋₁)/(f(xₙ) - f(xₙ₋₁))\n");
                    summary.append("This method approximates the derivative using two points.\n");
                    break;
                case "Bisection":
                    summary.append("Bisection method repeatedly divides the interval in half.\n");
                    summary.append("It's guaranteed to converge if f(a) and f(b) have opposite signs.\n");
                    break;
                case "Fixed-Point":
                    summary.append("Fixed-Point method uses the formula: xₙ₊₁ = g(xₙ)\n");
                    summary.append("The method converges when g'(x) < 1 in the neighborhood of the root.\n");
                    break;
                case "False Position":
                    summary.append("False Position uses linear interpolation to find better approximations.\n");
                    summary.append("It often converges faster than bisection while maintaining its reliability.\n");
                    break;
            }
        } else {
            summary.append("\u2717 Method failed to converge\n\n");
            
            if (steps.size() >= MAX_ITERATIONS) {
                summary.append("\u2717 Reached maximum number of iterations (").append(MAX_ITERATIONS).append(")\n");
            } else if (methodType.equals("Bisection") || methodType.equals("False Position")) {
                summary.append("\u2717 Function may not have a sign change in the given interval\n");
            } else if (methodType.equals("Newton-Raphson")) {
                // Check if derivative was too close to zero
                double lastFpx = steps.isEmpty() ? 0 : steps.get(steps.size() - 1).getFpx();
                if (Math.abs(lastFpx) < 1e-10) {
                    summary.append("\u2717 Derivative became too close to zero\n");
                }
            } else if (methodType.equals("Fixed-Point")) {
                summary.append("\u2717 Method may be diverging. Try a different function or initial guess.\n");
            }
            
            summary.append("\nSuggestions for Success:\n");
            summary.append("\u2022 Try a different initial guess or interval\n");
            summary.append("\u2022 Check if your function has a root in the expected region\n");
            summary.append("\u2022 Consider using a different numerical method\n");
            summary.append("\u2022 Increase the tolerance value slightly\n");
        }
        
        historyArea.setText(summary.toString());
    }      private Object[] createTableRow(CalculatorBackend.IterationStep step, DecimalFormat df) {
        switch (methodType) {
            case "Newton-Raphson":                return new Object[] {
                    step.getIteration(),
                    df.format(step.getX()),
                    df.format(step.getFx()),
                    df.format(step.getFpx()),
                    df.format(step.getError())
                };
            case "Secant":
                if (step.getIteration() == 0) {
                    // First row displays initial guesses - x_n-1 and x_n
                    // For initial row, we don't have x_n yet, so we'll use "N/A"
                    return new Object[] {
                        step.getIteration(),
                        df.format(step.getX()), // x_n-1
                        "N/A",                  // x_n (not available yet)
                        df.format(step.getFx()), // f(x_n-1)
                        "N/A",                  // f(x_n) (not available yet)
                        "N/A"                   // Error (not available yet)
                    };
                } else {
                    // For subsequent rows:
                    // - x stores x_n-1
                    // - fx stores f(x_n-1)
                    // - fpx stores f(x_n-2) from previous iteration
                    // - error stores |x_n - x_n-1|
                    double x_prev = step.getX();          // x_n-1
                    double x_current = x_prev + step.getError(); // Approximate x_n
                    double fx_prev = step.getFx();        // f(x_n-1)
                    double fx_prev_prev = step.getFpx();  // f(x_n-2)
                    
                    return new Object[] {
                        step.getIteration(),
                        df.format(x_prev),     // x_n-1
                        df.format(x_current),  // x_n (approximated)
                        df.format(fx_prev),    // f(x_n-1)
                        df.format(fx_prev_prev), // f(x_n-2)
                        df.format(step.getError())
                    };
                }              case "Bisection":
            case "False Position":
                // In the CalculatorBackend, for Bisection and False Position:
                // - x stores the midpoint c
                // - fx stores f(c)
                // - fpx stores f(b) for display purposes
                // - error stores |b-a| (the interval width)
                double c = step.getX();
                double fc = step.getFx();
                double error = step.getError();
                
                // Estimate a and b based on error and midpoint:
                // In bisection, error = b-a, c = (a+b)/2
                // So, a = c - error/2 and b = c + error/2
                double a = c - error/2; // Left endpoint
                double b = c + error/2; // Right endpoint
                
                return new Object[] {
                    step.getIteration(),
                    df.format(a),
                    df.format(b),
                    df.format(c),
                    df.format(fc), 
                    df.format(error)
                };
            case "Fixed-Point":
                return new Object[] {
                    step.getIteration(),
                    df.format(step.getX()),     // x_n
                    df.format(step.getFx()),    // g(x_n) = x_n+1
                    df.format(step.getError())  // |x_n+1 - x_n|
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
    
    private String[] getTableColumnNames() {
        switch (methodType) {            case "Newton-Raphson":
                return new String[]{"Iter.", "xₙ", "f(xₙ)", "f'(xₙ)", "Error"};
            case "Secant":
                return new String[]{"Iteration", "x_n-1", "x_n", "f(x_n-1)", "f(x_n)", "Error"};            case "Bisection":
                return new String[]{"Iteration", "a", "b", "c", "f(c)", "Error"};
            case "False Position":
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
                return "Function g(x) where x = g(x), e.g., sqrt(4+x)";
            default:
                return "e.g., x^2 - 4";
        }
    }
    
    private String getFunctionHint() {
        switch (methodType) {
            case "Fixed-Point":
                return "Enter a function g(x) such that f(x) = 0 can be rewritten as x = g(x)";
            default:
                return "Enter the function f(x) for which you want to find the root (f(x) = 0)";
        }
    }
    
    private String getFirstGuessLabel() {        switch (methodType) {
            case "Bisection":
            case "False Position":
                return "Left Endpoint (a)";
            case "Secant":
                return "First Guess (x₀)";
            default:
                return "Initial Guess";
        }
    }
    
    private String getSecondGuessLabel() {        switch (methodType) {
            case "Bisection":
            case "False Position":
                return "Right Endpoint (b)";
            case "Secant":
                return "Second Guess (x₁)";
            default:
                return "Initial Guess";
        }
    }
    
    private String getFirstGuessPlaceholder() {        switch (methodType) {
            case "Bisection":
            case "False Position":
                return "e.g., 1";
            case "Secant":
                return "e.g., 1";
            default:
                return "e.g., 1.0";
        }
    }
    
    private String getSecondGuessPlaceholder() {        switch (methodType) {
            case "Bisection":
            case "False Position":
                return "e.g., 3";
            case "Secant":
                return "e.g., 2";
            default:
                return "e.g., 2.0";
        }
    }
    
    private String getFirstGuessHint() {        switch (methodType) {
            case "Bisection":
            case "False Position":
                return "Left endpoint of interval containing the root";
            case "Secant":
                return "First point for the secant line approximation";
            default:
                return "Starting point for iteration";
        }
    }
    
    private String getSecondGuessHint() {        switch (methodType) {
            case "Bisection":
            case "False Position":
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
                return "Example: Try solving x^2 - 4 = 0 with initial guess 3";
            case "Secant":
                return "Example: Try solving x^2 - 4 = 0 with guesses 1 and 3";
            case "Bisection":
                return "Example: Try solving x^2 - 4 = 0 with interval [1, 3]";            case "Fixed-Point":
                return "Example: Try solving x = sqrt(4+x) with initial guess 1";
            case "False Position":
                return "Example: Try solving x^2 - 4 = 0 with interval [1, 3]";
            default:
                return "Example: Try solving x^2 - 4 = 0";
        }
    }
    
    /**
     * Creates a DecimalFormat instance with precision based on the tolerance value.
     * @param tolerance The tolerance for calculations
     * @return A DecimalFormat instance with appropriate precision
     */
    private DecimalFormat createDecimalFormatFromTolerance(double tolerance) {
        int digits = Math.max(6, (int) Math.ceil(-Math.log10(tolerance)) + 2);
        return new DecimalFormat("0." + "0".repeat(digits));
    }
    
    /**
     * Gets the current tolerance value from the tolerance field.
     * @return The tolerance value as a double, or a default value (0.0001) if invalid
     */
    private double getToleranceValue() {
        try {
            return Double.parseDouble(toleranceField.getText().trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0001; // Default tolerance
        }
    }
    
    /**
     * Updates the method type and refreshes any UI components that depend on it
     * @param methodType The new method type to set
     */
    public void updateMethodType(String methodType) {
        this.methodType = methodType;
        
        // Update UI elements that depend on method type
        if (functionField != null) {
            JLabel functionLabel = null;
            for (Component c : functionField.getParent().getComponents()) {
                if (c instanceof JLabel) {
                    functionLabel = (JLabel) c;
                    break;
                }
            }
            
            if (functionLabel != null) {
                functionLabel.setText(methodType.equals("Fixed-Point") ? "Function g(x)" : "Function f(x)");
            }
        }
        
        // Reset the table if it exists
        if (tableModel != null) {
            tableModel.setRowCount(0);
            
            // Update column headers based on method type
            if (methodType.equals("Bisection") || methodType.equals("False Position")) {
                String[] columnNames = {"Iter.", "a", "b", "c", "f(c)", "Error"};
                tableModel.setColumnIdentifiers(columnNames);            } else if (methodType.equals("Secant")) {
                String[] columnNames = {"Iter.", "x₀", "x₁", "x₂", "f(x₂)", "Error"};
                tableModel.setColumnIdentifiers(columnNames);
            } else if (methodType.equals("Newton-Raphson")) {
                String[] columnNames = {"Iter.", "xₙ", "f(xₙ)", "f'(xₙ)", "Error"};
                tableModel.setColumnIdentifiers(columnNames);
            } else if (methodType.equals("Fixed-Point")) {
                String[] columnNames = {"Iter.", "xₙ", "g(xₙ)", "Error"};
                tableModel.setColumnIdentifiers(columnNames);
            }
        }
        
        // Reset results
        if (resultsCardLayout != null) {
            resultsCardLayout.show(resultsPanel, "noResults");
        }
        
        // Update visibility of second guess field based on method
        if (initialGuessField2 != null) {
            boolean needsTwoGuesses = methodType.equals("Bisection") || 
                                     methodType.equals("Secant") || 
                                     methodType.equals("False Position");
            
            initialGuessField2.getParent().setVisible(needsTwoGuesses);
        }
    }
}