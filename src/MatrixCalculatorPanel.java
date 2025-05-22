package src;

import javax.swing.*;
import java.awt.*;


public class MatrixCalculatorPanel extends JPanel {
    private final String mode;
    private JPanel matrixInputPanel;
    private JButton computeButton;
    private JSpinner rowsA, colsA, rowsB, colsB;
    private JTextField[][] matrixAFields;
    private JTextField[][] matrixBFields;
    private JTextField[][] augmentedFields;
    private int currentRowsA = 2, currentColsA = 2, currentRowsB = 2, currentColsB = 2;


    private final Color backgroundColor = new Color(18, 23, 30);
    private final Color panelColor = new Color(35, 42, 52);
    private final Color accentColor = new Color(115, 138, 247);
    private final Color successColor = new Color(76, 209, 149);
    private final Color errorColor = new Color(251, 85, 85);
    private final Color textColor = new Color(236, 239, 244);
    private final Color textSecondaryColor = new Color(160, 170, 190);

    private final Color inputFieldColor = new Color(46, 54, 66);

    public MatrixCalculatorPanel(String mode) {
        this.mode = mode;
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);



        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        JLabel title = new JLabel();
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(accentColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        if (mode.equals("Matrix Multiplication")) {
            title.setText("Matrix Multiplication");
        } else {
            title.setText("Cramer's Rule");
        }
        JLabel desc = new JLabel();
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(textSecondaryColor);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        if (mode.equals("Matrix Multiplication")) {
            desc.setText("Multiply two matrices of compatible sizes. Useful for linear algebra and data science.");
        } else {
            desc.setText("Solve a system of linear equations using Cramer's Rule (n equations, n variables).");
        }
        headerPanel.add(title);
        headerPanel.add(desc);
        add(headerPanel, BorderLayout.NORTH);


        matrixInputPanel = new RoundedPanel(18, panelColor);
        matrixInputPanel.setLayout(new BoxLayout(matrixInputPanel, BoxLayout.Y_AXIS));
        matrixInputPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        add(matrixInputPanel, BorderLayout.CENTER);
        computeButton = new JButton("Compute");
        computeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        computeButton.setBackground(accentColor);
        computeButton.setForeground(Color.WHITE);
        computeButton.setFocusPainted(false);
        computeButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        computeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonPanel.add(computeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        computeButton.addActionListener(e -> compute());

        updateMatrixInputs();
    }

    private void updateMatrixInputs() {
        matrixInputPanel.removeAll();
        matrixInputPanel.setBackground(panelColor);
        if (mode.equals("Matrix Multiplication")) {
            JPanel sizePanel = new JPanel();
            sizePanel.setOpaque(false);
            sizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sizeLabel = new JLabel("Set Matrix Sizes");
            sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            sizeLabel.setForeground(accentColor);
            sizePanel.add(sizeLabel);
            sizePanel.add(Box.createHorizontalStrut(20));
            sizePanel.add(new JLabel("A: "));
            rowsA = new JSpinner(new SpinnerNumberModel(currentRowsA, 1, 6, 1));
            colsA = new JSpinner(new SpinnerNumberModel(currentColsA, 1, 6, 1));
            styleSpinner(rowsA);
            styleSpinner(colsA);
            sizePanel.add(rowsA);
            sizePanel.add(new JLabel("x"));
            sizePanel.add(colsA);
            sizePanel.add(Box.createHorizontalStrut(20));
            sizePanel.add(new JLabel("B: "));
            rowsB = new JSpinner(new SpinnerNumberModel(currentRowsB, 1, 6, 1));
            colsB = new JSpinner(new SpinnerNumberModel(currentColsB, 1, 6, 1));
            styleSpinner(rowsB);
            styleSpinner(colsB);
            sizePanel.add(rowsB);            sizePanel.add(new JLabel("x"));
            sizePanel.add(colsB);
            JButton updateBtn = new JButton("Set Size");
            updateBtn.setBackground(accentColor);
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            updateBtn.setFocusPainted(false);
            updateBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            sizePanel.add(updateBtn);
            matrixInputPanel.add(sizePanel);
            JLabel instr = new JLabel("Enter two matrices. Only numbers are allowed. Example: A = [[1,2],[3,4]], B = [[5,6],[7,8]]");
            instr.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            instr.setForeground(textSecondaryColor);
            instr.setAlignmentX(Component.CENTER_ALIGNMENT);
            instr.setBorder(BorderFactory.createEmptyBorder(8, 10, 16, 10));
            matrixInputPanel.add(instr);

            updateBtn.addActionListener(e -> {
                currentRowsA = (int) rowsA.getValue();
                currentColsA = (int) colsA.getValue();
                currentRowsB = (int) rowsB.getValue();
                currentColsB = (int) colsB.getValue();
                updateMatrixInputs();
            });

            matrixAFields = new JTextField[currentRowsA][currentColsA];
            matrixBFields = new JTextField[currentRowsB][currentColsB];
            JPanel aPanel = new JPanel(new GridLayout(currentRowsA, currentColsA, 6, 6));
            aPanel.setOpaque(false);
            for (int i = 0; i < currentRowsA; i++)
                for (int j = 0; j < currentColsA; j++) {
                    matrixAFields[i][j] = createStyledField();
                    aPanel.add(matrixAFields[i][j]);
                }
            JPanel bPanel = new JPanel(new GridLayout(currentRowsB, currentColsB, 6, 6));
            bPanel.setOpaque(false);
            for (int i = 0; i < currentRowsB; i++)
                for (int j = 0; j < currentColsB; j++) {
                    matrixBFields[i][j] = createStyledField();
                    bPanel.add(matrixBFields[i][j]);
                }
            JPanel abPanel = new JPanel(new GridLayout(1, 2, 40, 0));
            abPanel.setOpaque(false);
            JPanel aWrap = new JPanel(new BorderLayout());
            aWrap.setOpaque(false);
            JLabel aLabel = new JLabel("Matrix A");
            aLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            aLabel.setForeground(textColor);
            aWrap.add(aLabel, BorderLayout.NORTH);
            aWrap.add(aPanel, BorderLayout.CENTER);
            abPanel.add(aWrap);
            JPanel bWrap = new JPanel(new BorderLayout());
            bWrap.setOpaque(false);
            JLabel bLabel = new JLabel("Matrix B");
            bLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            bLabel.setForeground(textColor);
            bWrap.add(bLabel, BorderLayout.NORTH);
            bWrap.add(bPanel, BorderLayout.CENTER);
            abPanel.add(bWrap);
            abPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            matrixInputPanel.add(Box.createVerticalStrut(20));
            matrixInputPanel.add(abPanel);
        } else {

            JPanel sizePanel = new JPanel();
            sizePanel.setOpaque(false);
            sizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sizeLabel = new JLabel("Set System Size");
            sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            sizeLabel.setForeground(accentColor);
            sizePanel.add(sizeLabel);
            sizePanel.add(Box.createHorizontalStrut(20));            sizePanel.add(new JLabel("n (n x n+1): "));
            rowsA = new JSpinner(new SpinnerNumberModel(currentRowsA, 2, 6, 1));
            styleSpinner(rowsA);
            sizePanel.add(rowsA);
            JButton updateBtn = new JButton("Set Size");
            updateBtn.setBackground(accentColor);
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            updateBtn.setFocusPainted(false);
            updateBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            sizePanel.add(updateBtn);
            matrixInputPanel.add(sizePanel);
            JLabel instr = new JLabel("Enter an augmented matrix (A|b). Only numbers are allowed. Example: [[2,1,3,9],[1,-1,2,8],[3,2,1,10]]");
            instr.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            instr.setForeground(textSecondaryColor);
            instr.setAlignmentX(Component.CENTER_ALIGNMENT);
            instr.setBorder(BorderFactory.createEmptyBorder(8, 10, 16, 10));
            matrixInputPanel.add(instr);

            updateBtn.addActionListener(e -> {
                currentRowsA = (int) rowsA.getValue();
                updateMatrixInputs();
            });
            int n = currentRowsA;
            augmentedFields = new JTextField[n][n+1];
            JPanel augPanel = new JPanel(new GridLayout(n, n+1, 6, 6));
            augPanel.setOpaque(false);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n+1; j++) {
                    augmentedFields[i][j] = createStyledField();
                    augPanel.add(augmentedFields[i][j]);
                }
            JLabel augLabel = new JLabel("Augmented Matrix (A|b)");
            augLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            augLabel.setForeground(textColor);
            augLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            matrixInputPanel.add(Box.createVerticalStrut(20));
            matrixInputPanel.add(augLabel);
            matrixInputPanel.add(augPanel);
        }
        matrixInputPanel.revalidate();
        matrixInputPanel.repaint();
    }

    private void compute() {
        try {
            if (mode.equals("Matrix Multiplication")) {
                double[][] a = new double[currentRowsA][currentColsA];
                double[][] b = new double[currentRowsB][currentColsB];
                for (int i = 0; i < currentRowsA; i++)
                    for (int j = 0; j < currentColsA; j++)
                        a[i][j] = Double.parseDouble(matrixAFields[i][j].getText());
                for (int i = 0; i < currentRowsB; i++)
                    for (int j = 0; j < currentColsB; j++)
                        b[i][j] = Double.parseDouble(matrixBFields[i][j].getText());
                if (currentColsA != currentRowsB) {
                    showResultDialog("Matrix A's columns must match Matrix B's rows.", false);
                    return;
                }
                double[][] result = CalculatorBackend.multiplyMatrices(a, b);
                showResultDialog(matrixToString(result), true);
            } else {
                int n = currentRowsA;
                double[][] aug = new double[n][n+1];
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n+1; j++)
                        aug[i][j] = Double.parseDouble(augmentedFields[i][j].getText());
                double[] sol = CalculatorBackend.solveCramer(aug);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < sol.length; i++)
                    sb.append("x" + (i+1) + " = " + sol[i] + "\n");
                showResultDialog(sb.toString(), true);
            }
        } catch (Exception ex) {
            showResultDialog("Error: " + ex.getMessage(), false);
        }
    }
    private void showResultDialog(String result, boolean success) {

        RoundedPanel panel = new RoundedPanel(22, panelColor);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));        JLabel label = new JLabel(success ? "Result" : "Error", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(success ? successColor : errorColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(label, BorderLayout.NORTH);        JTextArea area = new JTextArea(result);
        area.setFont(new Font("Consolas", Font.PLAIN, 18));
        area.setForeground(success ? textColor : errorColor);
        area.setBackground(panelColor);
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(panelColor);
        scroll.getViewport().setBackground(panelColor);
        scroll.setPreferredSize(new Dimension(420, 120));
        panel.add(scroll, BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(success ? accentColor : errorColor);        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);


        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setUndecorated(true);
        dialog.setModal(true);
        dialog.setContentPane(panel);
        

        closeButton.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(success ? successColor : errorColor, 3, true));
        

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private JTextField createStyledField() {
        JTextField field = new JTextField(3);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(inputFieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createLineBorder(accentColor, 1, true));
        field.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        return field;
    }

    private String matrixToString(double[][] m) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : m) {
            for (double v : row) sb.append(String.format("%10.4f ", v));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setBorder(BorderFactory.createLineBorder(accentColor, 1, true));
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
            spinnerEditor.getTextField().setBackground(inputFieldColor);
            spinnerEditor.getTextField().setForeground(textColor);
            spinnerEditor.getTextField().setCaretColor(textColor);
            spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
            spinnerEditor.getTextField().setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        }
    }
}
