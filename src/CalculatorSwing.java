package src;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class CalculatorSwing extends JFrame {
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private CardLayout mainCardLayout;
    private JPanel mainPanel;
    private final Map<String, JPanel> methodPanels = new HashMap<>();
    private final Color primaryColor = new Color(28, 34, 43);
    private final Color accentColor = new Color(115, 138, 247);
    private final Color backgroundColor = new Color(18, 23, 30);
    private final Color textColor = new Color(236, 239, 244);
    private final Color hoverColor = new Color(46, 54, 66);
    private final String[] methodNames = {
        "Newton-Raphson", "Secant", "Bisection", "Fixed-Point", "False Position", "Matrix Multiplication", "Cramer's Rule"
    };
    private final String[] methodDescriptions = {
        "Newton's method finds successively better approximations using the function's derivative.",
        "The Secant method uses a sequence of roots of secant lines to approximate a root of a function.",
        "The Bisection method repeatedly bisects an interval and selects a subinterval in which a root must lie.",
        "The Fixed-Point method iterates a function to find a point where f(x) = x.",
        "The False Position method is similar to bisection but uses a secant line to find the root.",
        "Multiply two matrices of compatible sizes.",
        "Solve a system of linear equations using Cramer's Rule."
    };
    private final boolean[] hasTwoGuesses = {
        false, true, true, false, true, false, false
    };
    
    // Custom window closing operation
    private boolean isDragging = false;
    private Point dragStart;
    
    public CalculatorSwing() {
        setTitle("NM x DSA Project");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        // Set shape with rounded corners
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        // Apply custom window behavior
        setupWindowBehavior();
        
        // Initialize UI
        initUI();
    }
    
    private void setupWindowBehavior() {
        // Mouse listener for dragging the window
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getY() <= 50) { // Restrict to top area for titlebar-like behavior
                    isDragging = true;
                    dragStart = e.getPoint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    Point currentLocation = getLocation();
                    setLocation(
                        currentLocation.x + e.getX() - dragStart.x,
                        currentLocation.y + e.getY() - dragStart.y
                    );
                }
            }
        });
    }

    private void initUI() {
        // Set up main content container
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        
        // Create title bar panel
        JPanel titleBar = createTitleBar();
        contentPanel.add(titleBar, BorderLayout.NORTH);
        
        // Create sidebar
        sidebarPanel = createSidebar();
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create main content area with card layout
        mainCardLayout = new CardLayout();
        mainPanel = new JPanel(mainCardLayout);
        mainPanel.setBackground(backgroundColor);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Add method panels to main panel
        for (int i = 0; i < methodNames.length; i++) {
            String methodName = methodNames[i];
            JPanel methodPanel;
            if (methodName.equals("Matrix Multiplication") || methodName.equals("Cramer's Rule")) {
                methodPanel = new MatrixCalculatorPanel(methodName);
            } else {
                methodPanel = new ModernCalculatorPanel(methodDescriptions[i], hasTwoGuesses[i]);
            }
            methodPanels.put(methodName, methodPanel);
            mainPanel.add(methodPanel, methodName);
        }
        
        // Show first method by default
        mainCardLayout.show(mainPanel, methodNames[0]);
        
        // Add content to frame
        setContentPane(contentPanel);
        
        // Make first sidebar item selected by finding and activating accent bar
        Component firstItem = sidebarPanel.getComponent(1); // Skip logo panel
        if (firstItem instanceof JPanel) {
            JPanel firstPanel = (JPanel) firstItem;
            firstPanel.setBackground(hoverColor);
            
            // Find label and accent bar
            JPanel accentBar = null;
            JLabel label = null;
            
            for (Component c : firstPanel.getComponents()) {
                if (c instanceof JLabel) {
                    label = (JLabel) c;
                } else if (c instanceof JPanel) {
                    accentBar = (JPanel) c;
                }
            }
            
            if (label != null) {
                label.setForeground(accentColor);
            }
            
            if (accentBar != null) {
                accentBar.setBackground(accentColor);
            }
        }
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(primaryColor);
        titleBar.setPreferredSize(new Dimension(getWidth(), 50));
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel title = new JLabel("NM x DSA Project");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(textColor);
        titleBar.add(title, BorderLayout.WEST);
        
        // Window controls panel
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        windowControls.setOpaque(false);
        
        // Minimize button
        JLabel minimizeBtn = new JLabel("−");
        minimizeBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        minimizeBtn.setForeground(textColor);
        minimizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minimizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setState(JFrame.ICONIFIED);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeBtn.setForeground(accentColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                minimizeBtn.setForeground(textColor);
            }
        });
        
        // Close button
        JLabel closeBtn = new JLabel("×");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeBtn.setForeground(textColor);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(new Color(255, 96, 92));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(textColor);
            }
        });
        
        windowControls.add(minimizeBtn);
        windowControls.add(closeBtn);
        
        titleBar.add(windowControls, BorderLayout.EAST);
        
        return titleBar;
    }

    private JPanel logoPanel;
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(primaryColor);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        // Logo/header panel
        logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(primaryColor);
        logoPanel.setPreferredSize(new Dimension(250, 100));
        logoPanel.setMaximumSize(new Dimension(250, 100));
        
        JLabel appLogo = new JLabel("Group 5");
        appLogo.setFont(new Font("Segoe UI", Font.BOLD, 40));
        appLogo.setForeground(accentColor);
        appLogo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel appName = new JLabel("Final Project");
        appName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appName.setForeground(textColor);
        appName.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.add(appName, BorderLayout.CENTER);
        
        logoPanel.add(appLogo, BorderLayout.CENTER);
        logoPanel.add(namePanel, BorderLayout.SOUTH);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        sidebar.add(logoPanel);
        
        // Create menu items
        for (String methodName : methodNames) {
            JPanel menuItem = createMenuItem(methodName);
            sidebar.add(menuItem);
        }
        
        // Add a spacer
        sidebar.add(Box.createVerticalGlue());
        
        // Add a footer panel (empty now that we removed version text)
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(primaryColor);
        footerPanel.setPreferredSize(new Dimension(250, 20));
        footerPanel.setMaximumSize(new Dimension(250, 20));
        
        sidebar.add(footerPanel);
        
        return sidebar;
    }
    
    private JPanel createMenuItem(String text) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(primaryColor);
        menuItem.setPreferredSize(new Dimension(250, 50));
        menuItem.setMaximumSize(new Dimension(250, 50));
        
        // Create left accent bar for selected items
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(5, 50));
        accentBar.setBackground(primaryColor);
        
        JLabel menuText = new JLabel(text);
        menuText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        menuText.setForeground(textColor);
        menuText.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        menuItem.add(accentBar, BorderLayout.WEST);
        menuItem.add(menuText, BorderLayout.CENTER);
        
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects and click behavior
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menuText.getForeground().equals(accentColor)) {
                    menuItem.setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!menuText.getForeground().equals(accentColor)) {
                    menuItem.setBackground(primaryColor);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reset all menu items
                for (Component comp : sidebarPanel.getComponents()) {
                    if (comp instanceof JPanel && comp != logoPanel) {
                        comp.setBackground(primaryColor);
                        for (Component c : ((JPanel) comp).getComponents()) {
                            if (c instanceof JLabel) {
                                ((JLabel) c).setForeground(textColor);
                            } else if (c instanceof JPanel) {
                                // Reset accent bar
                                c.setBackground(primaryColor);
                            }
                        }
                    }
                }
                
                // Highlight selected item
                menuItem.setBackground(hoverColor);
                menuText.setForeground(accentColor);
                accentBar.setBackground(accentColor);
                
                // Show selected panel
                mainCardLayout.show(mainPanel, text);
                
                // Update ModernCalculatorPanel with correct method type
                JPanel currentPanel = methodPanels.get(text);
                if (currentPanel instanceof ModernCalculatorPanel) {
                    ((ModernCalculatorPanel) currentPanel).updateMethodType(text);
                }
            }
        });
        
        return menuItem;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new CalculatorSwing().setVisible(true));
    }
}
