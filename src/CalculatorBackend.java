import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.ArrayList;
import java.util.List;

public class CalculatorBackend {
    /**
     * Calculates the root of an equation using the Newton-Raphson method.
     * 
     * @param function   The function f(x) for which to find the root
     * @param initialGuess The initial starting point for the iteration
     * @param tolerance  The desired accuracy of the result
     * @param maxIterations Maximum number of iterations to prevent infinite loops
     * @return A Result object containing the solution and calculation history
     */
    public static Result newtonRaphson(String function, double initialGuess, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        
        try {
            // Parse the function using exp4j
            Expression f = new ExpressionBuilder(function)
                    .variable("x")
                    .build();
            
            // Calculate the derivative using central difference formula
            double h = 1e-8; // Small value for differentiation
            
            double x0 = initialGuess;
            double x1;
            int iteration = 0;
            double error = Double.MAX_VALUE;
            
            history.append("Newton-Raphson Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with initial guess x₀ = %.6f\n\n", x0));
            history.append("Iteration | x_n | f(x_n) | f'(x_n) | Error\n");
            history.append("---------|-----|--------|---------|------\n");
            
            while (error > tolerance && iteration < maxIterations) {
                // Evaluate function at current point
                double fx = f.setVariable("x", x0).evaluate();
                
                // Compute derivative using central difference formula
                double fpx = (f.setVariable("x", x0 + h).evaluate() - 
                             f.setVariable("x", x0 - h).evaluate()) / (2 * h);
                
                // Check if derivative is too close to zero
                if (Math.abs(fpx) < 1e-10) {
                    history.append("\nDerivative is too close to zero. Method failed to converge.\n");
                    return new Result(x0, history.toString(), steps, false);
                }
                
                // Newton-Raphson formula: x₁ = x₀ - f(x₀)/f'(x₀)
                x1 = x0 - fx / fpx;
                error = Math.abs(x1 - x0);
                
                // Record iteration details
                String iterInfo = String.format("%9d | %.6f | %.6f | %.6f | %.6f\n", 
                                              iteration+1, x0, fx, fpx, error);
                history.append(iterInfo);
                
                steps.add(new IterationStep(iteration+1, x0, fx, fpx, error));
                
                // Update for next iteration
                x0 = x1;
                iteration++;
            }
            
            if (iteration >= maxIterations) {
                history.append("\nReached maximum iterations. Method may not have converged.\n");
                return new Result(x0, history.toString(), steps, false);
            }
            
            history.append("\nRoot found: x = " + String.format("%.10f", x0));
            history.append("\nFunction value at root: f(x) = " + 
                          String.format("%.10f", f.setVariable("x", x0).evaluate()));
            history.append("\nIterations required: " + iteration);
            
            return new Result(x0, history.toString(), steps, true);
            
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
    /**
     * Calculates the root of an equation using the Secant method.
     * 
     * @param function The function f(x) for which to find the root
     * @param x0 The first initial guess
     * @param x1 The second initial guess
     * @param tolerance The desired accuracy of the result
     * @param maxIterations Maximum number of iterations to prevent infinite loops
     * @return A Result object containing the solution and calculation history
     */
    public static Result secant(String function, double x0, double x1, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        
        try {
            // Parse the function using exp4j
            Expression f = new ExpressionBuilder(function)
                    .variable("x")
                    .build();
            
            double x2;
            int iteration = 0;
            double error = Double.MAX_VALUE;
            
            // Evaluate function at initial points
            double fx0 = f.setVariable("x", x0).evaluate();
            double fx1 = f.setVariable("x", x1).evaluate();
            
            history.append("Secant Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with initial guesses x₀ = %.6f and x₁ = %.6f\n\n", x0, x1));
            history.append("Iteration | x_n-1 | x_n | f(x_n-1) | f(x_n) | Error\n");
            history.append("----------|-------|-----|----------|--------|------\n");
            
            // Add initial step
            String iterInfo = String.format("%10d | %.6f | %.6f | %.6f | %.6f | %s\n", 
                                         0, x0, x1, fx0, fx1, "N/A");
            history.append(iterInfo);
            
            // Store initial points (with dummy derivative and error since they are not used in secant method)
            steps.add(new IterationStep(0, x0, fx0, Double.NaN, Double.NaN));
            
            while (error > tolerance && iteration < maxIterations) {
                // Check to prevent division by zero
                if (Math.abs(fx1 - fx0) < 1e-10) {
                    history.append("\nDivision by zero encountered. Method failed to converge.\n");
                    return new Result(x1, history.toString(), steps, false);
                }
                
                // Secant formula: x2 = x1 - f(x1) * (x1 - x0) / (f(x1) - f(x0))
                x2 = x1 - fx1 * (x1 - x0) / (fx1 - fx0);
                error = Math.abs(x2 - x1);
                
                // Evaluate function at new point
                double fx2 = f.setVariable("x", x2).evaluate();
                
                // Record iteration details
                iterInfo = String.format("%10d | %.6f | %.6f | %.6f | %.6f | %.6f\n", 
                                       iteration+1, x1, x2, fx1, fx2, error);
                history.append(iterInfo);
                
                // In secant method, we use fpx field to store fx0 for display purposes
                steps.add(new IterationStep(iteration+1, x1, fx1, fx0, error));
                
                // Update for next iteration
                x0 = x1;
                x1 = x2;
                fx0 = fx1;
                fx1 = fx2;
                
                iteration++;
            }
            
            if (iteration >= maxIterations) {
                history.append("\nReached maximum iterations. Method may not have converged.\n");
                return new Result(x1, history.toString(), steps, false);
            }
            
            history.append("\nRoot found: x = " + String.format("%.10f", x1));
            history.append("\nFunction value at root: f(x) = " + 
                          String.format("%.10f", f.setVariable("x", x1).evaluate()));
            history.append("\nIterations required: " + iteration);
            
            return new Result(x1, history.toString(), steps, true);
            
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
    /**
     * Calculates the root of an equation using the Bisection method.
     * 
     * @param function The function f(x) for which to find the root
     * @param a The left endpoint of the interval
     * @param b The right endpoint of the interval
     * @param tolerance The desired accuracy of the result
     * @param maxIterations Maximum number of iterations to prevent infinite loops
     * @return A Result object containing the solution and calculation history
     */
    public static Result bisection(String function, double a, double b, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        
        try {
            // Parse the function using exp4j
            Expression f = new ExpressionBuilder(function)
                    .variable("x")
                    .build();
            
            double fa = f.setVariable("x", a).evaluate();
            double fb = f.setVariable("x", b).evaluate();
            
            // Check if there's a sign change in the interval
            if (fa * fb >= 0) {
                history.append("Error: Function must have opposite signs at interval endpoints.\n");
                history.append(String.format("f(%.6f) = %.6f and f(%.6f) = %.6f have the same sign.", a, fa, b, fb));
                return new Result(Double.NaN, history.toString(), steps, false);
            }
            
            double c = 0;  // Initialize c to avoid "may not have been initialized" error
            double fc = 0;
            double error = b - a;
            int iteration = 0;
            
            history.append("Bisection Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with interval [%.6f, %.6f]\n\n", a, b));
            history.append("Iteration | a | b | c | f(a) | f(b) | f(c) | Error\n");
            history.append("----------|---|---|---|------|------|------|------\n");
            
            while (error > tolerance && iteration < maxIterations) {
                // Calculate midpoint
                c = (a + b) / 2;
                fc = f.setVariable("x", c).evaluate();
                
                // Record iteration details
                String iterInfo = String.format("%10d | %.6f | %.6f | %.6f | %.6f | %.6f | %.6f | %.6f\n", 
                                              iteration+1, a, b, c, fa, fb, fc, error);
                history.append(iterInfo);
                
                // Store iteration info (using fpx field to store f(b) for display purposes)
                steps.add(new IterationStep(iteration+1, c, fc, fb, error));
                
                // Check if we found the root exactly
                if (Math.abs(fc) < 1e-10) {
                    history.append("\nExact root found at x = " + String.format("%.10f", c));
                    return new Result(c, history.toString(), steps, true);
                }
                
                // Update interval
                if (fa * fc < 0) {
                    b = c;
                    fb = fc;
                } else {
                    a = c;
                    fa = fc;
                }
                
                // Update error
                error = b - a;
                iteration++;
            }
            
            // Final approximation is the midpoint of the final interval
            c = (a + b) / 2;
            
            if (iteration >= maxIterations) {
                history.append("\nReached maximum iterations. Method may not have converged.\n");
                return new Result(c, history.toString(), steps, false);
            }
            
            history.append("\nRoot found: x = " + String.format("%.10f", c));
            history.append("\nFunction value at root: f(x) = " + 
                          String.format("%.10f", f.setVariable("x", c).evaluate()));
            history.append("\nIterations required: " + iteration);
            
            return new Result(c, history.toString(), steps, true);
            
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
    /**
     * Calculates the root of an equation using the Fixed-Point Iteration method.
     * 
     * @param function The function g(x) in the form x = g(x)
     * @param initialGuess The initial guess
     * @param tolerance The desired accuracy of the result
     * @param maxIterations Maximum number of iterations to prevent infinite loops
     * @return A Result object containing the solution and calculation history
     */
    public static Result fixedPoint(String function, double initialGuess, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        
        try {
            // Parse the function using exp4j
            Expression g = new ExpressionBuilder(function)
                    .variable("x")
                    .build();
            
            double x0 = initialGuess;
            double x1;
            double error = Double.MAX_VALUE;
            int iteration = 0;
            
            history.append("Fixed-Point Iteration Method for finding root of: x = " + function + "\n");
            history.append(String.format("Starting with initial guess x₀ = %.6f\n\n", x0));
            history.append("Iteration | x_n | g(x_n) | Error\n");
            history.append("----------|-----|--------|------\n");
            
            while (error > tolerance && iteration < maxIterations) {
                // Evaluate function
                x1 = g.setVariable("x", x0).evaluate();
                error = Math.abs(x1 - x0);
                
                // Record iteration details
                String iterInfo = String.format("%10d | %.6f | %.6f | %.6f\n", 
                                              iteration+1, x0, x1, error);
                history.append(iterInfo);
                
                // In fixed-point, we'll use fx to store g(x) and fpx can remain NaN since it's not used
                steps.add(new IterationStep(iteration+1, x0, x1, Double.NaN, error));
                
                // Update for next iteration
                x0 = x1;
                iteration++;
                
                // Check if the values are growing too large (diverging)
                if (Math.abs(x0) > 1e10) {
                    history.append("\nMethod is diverging. Try a different function or initial guess.\n");
                    return new Result(x0, history.toString(), steps, false);
                }
            }
            
            if (iteration >= maxIterations) {
                history.append("\nReached maximum iterations. Method may not have converged.\n");
                return new Result(x0, history.toString(), steps, false);
            }
            
            // To verify this is actually a fixed point, compute g(x) - x
            // Let's create an expression for f(x) = g(x) - x
            Expression f = new ExpressionBuilder(function + "-x")
                    .variable("x")
                    .build();
            
            double fValue = f.setVariable("x", x0).evaluate();
            
            history.append("\nFixed point found: x = " + String.format("%.10f", x0));
            history.append("\nVerification: g(x) - x = " + String.format("%.10f", fValue));
            history.append("\nIterations required: " + iteration);
            
            return new Result(x0, history.toString(), steps, true);
            
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
    /**
     * Calculates the root of an equation using the False Position (Regula Falsi) method.
     * 
     * @param function The function f(x) for which to find the root
     * @param a The left endpoint of the interval
     * @param b The right endpoint of the interval
     * @param tolerance The desired accuracy of the result
     * @param maxIterations Maximum number of iterations to prevent infinite loops
     * @return A Result object containing the solution and calculation history
     */
    public static Result falsePosition(String function, double a, double b, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        
        try {
            // Parse the function using exp4j
            Expression f = new ExpressionBuilder(function)
                    .variable("x")
                    .build();
            
            double fa = f.setVariable("x", a).evaluate();
            double fb = f.setVariable("x", b).evaluate();
            
            // Check if there's a sign change in the interval
            if (fa * fb >= 0) {
                history.append("Error: Function must have opposite signs at interval endpoints.\n");
                history.append(String.format("f(%.6f) = %.6f and f(%.6f) = %.6f have the same sign.", a, fa, b, fb));
                return new Result(Double.NaN, history.toString(), steps, false);
            }
            
            double c = a;  // Initialize c to avoid "may not have been initialized" error
            double fc = 0;
            double error = Double.MAX_VALUE;
            int iteration = 0;
            
            history.append("False Position Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with interval [%.6f, %.6f]\n\n", a, b));
            history.append("Iteration | a | b | c | f(a) | f(b) | f(c) | Error\n");
            history.append("----------|---|---|---|------|------|------|------\n");
            
            // Previous c value to calculate error
            double prevC = a;
            
            while (error > tolerance && iteration < maxIterations) {
                // Calculate c using the False Position formula
                c = (a * fb - b * fa) / (fb - fa);
                fc = f.setVariable("x", c).evaluate();
                
                // Calculate error
                error = Math.abs(c - prevC);
                if (iteration == 0) {
                    error = Math.abs(b - a); // For first iteration
                }
                
                // Record iteration details
                String iterInfo = String.format("%10d | %.6f | %.6f | %.6f | %.6f | %.6f | %.6f | %.6f\n", 
                                              iteration+1, a, b, c, fa, fb, fc, error);
                history.append(iterInfo);
                
                // Store iteration info (using fpx field to store f(b) for display purposes)
                steps.add(new IterationStep(iteration+1, c, fc, fb, error));
                
                // Check if we found the root exactly
                if (Math.abs(fc) < 1e-10) {
                    history.append("\nExact root found at x = " + String.format("%.10f", c));
                    return new Result(c, history.toString(), steps, true);
                }
                
                // Update interval
                if (fa * fc < 0) {
                    b = c;
                    fb = fc;
                } else {
                    a = c;
                    fa = fc;
                }
                
                // Save current c for error calculation in next iteration
                prevC = c;
                iteration++;
            }
            
            if (iteration >= maxIterations) {
                history.append("\nReached maximum iterations. Method may not have converged.\n");
                return new Result(c, history.toString(), steps, false);
            }
            
            history.append("\nRoot found: x = " + String.format("%.10f", c));
            history.append("\nFunction value at root: f(x) = " + 
                          String.format("%.10f", f.setVariable("x", c).evaluate()));
            history.append("\nIterations required: " + iteration);
            
            return new Result(c, history.toString(), steps, true);
            
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
    /**
     * Result class to store calculation results and history
     */
    public static class Result {
        private final double root;
        private final String history;
        private final List<IterationStep> steps;
        private final boolean converged;
        
        public Result(double root, String history, List<IterationStep> steps, boolean converged) {
            this.root = root;
            this.history = history;
            this.steps = steps;
            this.converged = converged;
        }
        
        public double getRoot() {
            return root;
        }
        
        public String getHistory() {
            return history;
        }
        
        public List<IterationStep> getSteps() {
            return steps;
        }
        
        public boolean hasConverged() {
            return converged;
        }
    }
    
    /**
     * Class to store details of each iteration step
     */
    public static class IterationStep {
        private final int iteration;
        private final double x;
        private final double fx;
        private final double fpx;
        private final double error;
        
        public IterationStep(int iteration, double x, double fx, double fpx, double error) {
            this.iteration = iteration;
            this.x = x;
            this.fx = fx;
            this.fpx = fpx;
            this.error = error;
        }
        
        public int getIteration() {
            return iteration;
        }
        
        public double getX() {
            return x;
        }
        
        public double getFx() {
            return fx;
        }
        
        public double getFpx() {
            return fpx;
        }
        
        public double getError() {
            return error;
        }
    }
}