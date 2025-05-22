package src;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;


public class CalculatorBackend {
    public static Result newtonRaphson(String function, double initialGuess, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        try {
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            java.util.Stack<Double> iterates = m.newtonRaphson(expr, initialGuess, new java.util.Stack<>());
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
                // Compute the derivative at x0
                double fpx;
                try {
                    fpx = m.numericalDerivative(expr, x0);
                } catch (Exception ex) {
                    fpx = Double.NaN;
                }
                String iterInfo = String.format("%9d | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, x0, fx, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, x0, fx, fpx, error));
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
    
    public static Result bisection(String function, double a, double b, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        try {
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            java.util.LinkedList<Pair<Double, Double>> iterates = m.bisection(expr, a, b, new java.util.LinkedList<Pair<Double, Double>>());
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
                steps.add(new IterationStep(i+1, c, fc, fb, error, xL, xR));
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
    
    public static Result fixedPoint(String function, double initialGuess, double tolerance, int maxIterations) {
        StringBuilder history = new StringBuilder();
        List<IterationStep> steps = new ArrayList<>();
        try {
            Methods m = new Methods(maxIterations);
            m.setTolerance(BigDecimal.valueOf(tolerance));
            Expression expr = new ExpressionBuilder(function).variable("x").build();
            java.util.Queue<Double> iterates = m.fixedPoint(expr, initialGuess, new java.util.LinkedList<>());
            int digits = (int)Math.ceil(-Math.log10(tolerance));
            String formatStr = "%." + digits + "f";
            history.append("Fixed-Point Iteration Method for finding root of: x = " + function + "\n");
            history.append(String.format("Starting with initial guess x₀ = " + formatStr + "\n\n", initialGuess));
            history.append("Iteration | x_n | g(x_n) | Error\n");
            history.append("----------|-----|--------|------\n");
            java.util.Iterator<Double> it = iterates.iterator();
            if (!it.hasNext()) {
                history.append("No iterations performed.\n");
                return new Result(Double.NaN, history.toString(), steps, false);
            }
            double prev = it.next();
            int i = 1;
            while (it.hasNext()) {
                double curr = it.next();
                double gx = expr.setVariable("x", prev).evaluate();
                double error = Math.abs(curr - prev);
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i, prev, gx, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i, prev, gx, Double.NaN, error));
                prev = curr;
                i++;
            }
            double root = prev;
            Expression f = new ExpressionBuilder(function + "-x").variable("x").build();
            double fValue = f.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nFixed point found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nVerification: g(x) - x = " + String.format(highPrecisionFormat, fValue));
            history.append("\nIterations required: " + (i-1));
            return new Result(root, history.toString(), steps, true);
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    
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
                
                double c;
                double fc;
                double error = Math.abs(xR - xL);
                
              
                if (Math.abs(xL - xR) < 1e-10) {
                    c = xL;
                    fc = fa;
                } else {
                    c = xL + (((xR-xL) * (-1 * fa)) / (fb - fa));
                    fc = expr.setVariable("x", c).evaluate();
                }
                
                String iterInfo = String.format("%10d | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + " | " + formatStr + "\n", i+1, xL, xR, c, fa, fb, fc, error);
                history.append(iterInfo);
                steps.add(new IterationStep(i+1, c, fc, fb, error, xL, xR));
            }
            if (iterates.size() == 0) {
                history.append("\nMethod failed to produce valid iterations.\n");
                return new Result(Double.NaN, history.toString(), steps, false);
            }
            
            Pair<Double, Double> lastPair = iterates.get(iterates.size()-1);
            double lastXL = lastPair.getX();
            double lastXR = lastPair.getY();

            double root;
            if (Math.abs(lastXL - lastXR) < tolerance) {
                root = lastXL; 
            } else {
          
                double fL = expr.setVariable("x", lastXL).evaluate();
                double fR = expr.setVariable("x", lastXR).evaluate();
                
               
                if (Math.abs(fR - fL) < 1e-10) {
                    root = (lastXL + lastXR) / 2; 
                } else {
                    root = lastXL + (((lastXR-lastXL) * (-1 * fL)) / (fR - fL));
                }
            }
            
            double fxRoot = expr.setVariable("x", root).evaluate();
            String highPrecisionFormat = "%." + (digits + 2) + "f";
            history.append("\nRoot found: x = " + String.format(highPrecisionFormat, root));
            history.append("\nFunction value at root: f(x) = " + String.format(highPrecisionFormat, fxRoot));
            history.append("\nIterations required: " + iterates.size());
            
            System.out.println("Final root: " + root + ", F(root): " + fxRoot);
            return new Result(root, history.toString(), steps, true);
        } catch (Exception e) {
            history.append("Error in calculation: " + e.getMessage());
            return new Result(Double.NaN, history.toString(), steps, false);
        }
    }
    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        Methods m = new Methods(100); 
        return m.matrixMultiplication(a, b);
    }

    public static double[] solveCramer(double[][] augmentedMatrix) {
        Methods m = new Methods(100);
        return m.cramer(augmentedMatrix);
    }
    
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
    
    public static class IterationStep {
        private final int iteration;
        private final double x;
        private final double fx;
        private final double fpx;
        private final double error;
        private final Double a;
        private final Double b;
        
        public IterationStep(int iteration, double x, double fx, double fpx, double error) {
            this(iteration, x, fx, fpx, error, null, null);
        }
        
        public IterationStep(int iteration, double x, double fx, double fpx, double error, Double a, Double b) {
            this.iteration = iteration;
            this.x = x;
            this.fx = fx;
            this.fpx = fpx;
            this.error = error;
            this.a = a;
            this.b = b;
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
        
        public Double getA() {
            return a;
        }
        
        public Double getB() {
            return b;
        }
    }
    
}