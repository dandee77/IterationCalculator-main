package src;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
// import java.util.Arrays;


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
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            ArrayList<Double> iterates = m.newtonRaphson(expr, initialGuess, new ArrayList<>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("Newton-Raphson Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with initial guess x₀ = " + formatStr + "\n\n", initialGuess));
            history.append("Iteration | x_n | f(x_n) | Error\n");
            history.append("---------|-----|--------|------\n");
            for (int i = 0; i < iterates.size() - 1; i++) {
                double x0 = iterates.get(i);
                double x1 = iterates.get(i+1);
                double fx = expr.setVariable("x", x0).evaluate();
                double error = Math.abs(x1 - x0);
                String iterInfo = String.format("%9d | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, x0, fx, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, x0, fx, Double.NaN, error));
            }
            double root = iterates.get(iterates.size()-1);
            double fxRoot = expr.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nRoot found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nFunction value at root: f(x) = " + String.format(highPrecisionFormat, fxRoot));
            history.append("\nIterations required: " + (iterates.size()-1));
            return new Result(root, history.toString(), steps, true);
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
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            ArrayList<Double> iterates = m.secant(expr, x0, x1, new ArrayList<>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("Secant Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with initial guesses x₀ = " + formatStr + " and x₁ = " + formatStr + "\n\n", x0, x1));
            history.append("Iteration | x_n-1 | x_n | f(x_n-1) | f(x_n) | Error\n");
            history.append("----------|-------|-----|----------|--------|------\n");
            for (int i = 0; i < iterates.size() - 2; i++) {
                double prev = iterates.get(i);
                double curr = iterates.get(i+1);
                double next = iterates.get(i+2);
                double fxPrev = expr.setVariable("x", prev).evaluate();
                double fxCurr = expr.setVariable("x", curr).evaluate();
                double error = Math.abs(next - curr);
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, prev, curr, fxPrev, fxCurr, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, curr, fxCurr, fxPrev, error));
            }
            double root = iterates.get(iterates.size()-1);
            double fxRoot = expr.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nRoot found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nFunction value at root: f(x) = " + String.format(highPrecisionFormat, fxRoot));
            history.append("\nIterations required: " + (iterates.size()-2));
            return new Result(root, history.toString(), steps, true);
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
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            ArrayList<Pair<Double, Double>> iterates = m.bisection(expr, a, b, new ArrayList<Pair<Double, Double>>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("Bisection Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with interval [" + formatStr + ", " + formatStr + "]\n\n", a, b));
            history.append("Iteration | a | b | c | f(a) | f(b) | f(c) | Error\n");
            history.append("----------|---|---|---|------|------|------|------\n");
            for (int i = 0; i < iterates.size(); i++) {
                double xL = iterates.get(i).getX();
                double xR = iterates.get(i).getY();
                double c = (xL + xR) / 2;
                double fa = expr.setVariable("x", xL).evaluate();
                double fb = expr.setVariable("x", xR).evaluate();
                double fc = expr.setVariable("x", c).evaluate();
                double error = Math.abs(xR - xL);
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, xL, xR, c, fa, fb, fc, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, c, fc, fb, error));
            }
            double root = (iterates.get(iterates.size()-1).getX() + iterates.get(iterates.size()-1).getY()) / 2;
            double fxRoot = expr.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nRoot found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nFunction value at root: f(x) = " + String.format(highPrecisionFormat, fxRoot));
            history.append("\nIterations required: " + iterates.size());
            return new Result(root, history.toString(), steps, true);
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
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            ArrayList<Double> iterates = m.fixedPoint(expr, initialGuess, new ArrayList<>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("Fixed-Point Iteration Method for finding root of: x = " + function + "\n");
            history.append(String.format("Starting with initial guess x₀ = " + formatStr + "\n\n", initialGuess));
            history.append("Iteration | x_n | g(x_n) | Error\n");
            history.append("----------|-----|--------|------\n");
            for (int i = 0; i < iterates.size() - 1; i++) {
                double x0 = iterates.get(i);
                double x1 = iterates.get(i+1);
                double gx = expr.setVariable("x", x0).evaluate();
                double error = Math.abs(x1 - x0);
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, x0, gx, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, x0, gx, Double.NaN, error));
            }
            double root = iterates.get(iterates.size()-1);
            Expression f = new ExpressionBuilder(function + "-x").variable("x").build();
            double fValue = f.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nFixed point found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nVerification: g(x) - x = " + String.format(highPrecisionFormat, fValue));
            history.append("\nIterations required: " + (iterates.size()-1));
            return new Result(root, history.toString(), steps, true);
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
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            ArrayList<Pair<Double, Double>> iterates = m.falsePosition(expr, a, b, new ArrayList<Pair<Double, Double>>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("False Position Method for finding root of: " + function + "\n");
            history.append(String.format("Starting with interval [" + formatStr + ", " + formatStr + "]\n\n", a, b));
            history.append("Iteration | a | b | c | f(a) | f(b) | f(c) | Error\n");
            history.append("----------|---|---|---|------|------|------|------\n");
            for (int i = 0; i < iterates.size(); i++) {
                double xL = iterates.get(i).getX();
                double xR = iterates.get(i).getY();
                double fa = expr.setVariable("x", xL).evaluate();
                double fb = expr.setVariable("x", xR).evaluate();
                double c = xL + (((xR-xL) * (-1 * fa)) / (fb - fa));
                double fc = expr.setVariable("x", c).evaluate();
                double error = Math.abs(xR - xL);
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, xL, xR, c, fa, fb, fc, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, c, fc, fb, error));
            }
            double lastXL = iterates.get(iterates.size()-1).getX();
            double lastXR = iterates.get(iterates.size()-1).getY();
            double root = lastXL + (((lastXR-lastXL) * (-1 * expr.setVariable("x", lastXL).evaluate())) / (expr.setVariable("x", lastXR).evaluate() - expr.setVariable("x", lastXL).evaluate()));
            double fxRoot = expr.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nRoot found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nFunction value at root: f(x) = " + String.format(highPrecisionFormat, fxRoot));
            history.append("\nIterations required: " + iterates.size());
            return new Result(root, history.toString(), steps, true);
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    // Matrix multiplication using Methods
    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        Methods m = new Methods(100); // maxIteration not used here
        return m.matrixMultiplication(a, b);
    }

    // Cramer's rule using Methods
    public static double[] solveCramer(double[][] augmentedMatrix) {
        Methods m = new Methods(100); // maxIteration not used here
        return m.cramer(augmentedMatrix);
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
    
    /**
     * Creates a format string with precision based on the tolerance value.
     * @param tolerance The tolerance for calculations
     * @return A format string with appropriate precision (e.g., "%.6f")
     */
    // Removed unused getFormatStringFromTolerance method
}