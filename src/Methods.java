package src;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Methods {
    private double roundToTolerance(double value) {
        BigDecimal tol = tolerance;
        BigDecimal val = new BigDecimal(value);
        int scale = Math.max(tol.stripTrailingZeros().scale(), 0);
        BigDecimal divided = val.divide(tol, 10, RoundingMode.HALF_UP);
        BigDecimal rounded = divided.setScale(0, RoundingMode.HALF_UP);
        BigDecimal result = rounded.multiply(tol).setScale(scale, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    private int maxIteration = 100;
    private BigDecimal tolerance;

    public Methods(int maxIteration) {
        this.maxIteration = maxIteration;
        this.tolerance = new BigDecimal(1e-3);
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public void setTolerance(BigDecimal tolerance) {
        this.tolerance = tolerance;
    }

    public double numericalDerivative(Expression expression, double x) {
        double h = 1e-5;
        expression.setVariable("x", x + h);
        double f1 = expression.evaluate();
        expression.setVariable("x", x - h);
        double f2 = expression.evaluate();
        return (f1 - f2) / (2 * h);
    }

    public Queue<Double> fixedPoint(Expression expression, double x, Queue<Double> xn){
        if(xn.isEmpty()) {
            xn.add(roundToTolerance(x));
        }
        if(xn.size() == maxIteration + 1){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        double nextX = expression.setVariable("x", x).evaluate();
        nextX = roundToTolerance(nextX);
        if(Math.abs(nextX - x) <= tolerance.doubleValue()){
            return xn;
        } else {
            xn.add(nextX);
            return fixedPoint(expression, nextX, xn);
        }
    }

    public Stack<Double> newtonRaphson(Expression expression, double x, Stack<Double> xn){
        if(xn.isEmpty()) {
            xn.push(roundToTolerance(x));
        }
        if(xn.size() == maxIteration + 1){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        double xd = numericalDerivative(expression, x);
        if (new BigDecimal(xd).setScale(4, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Derivative is zero. Cannot proceed.");
            return xn;
        }
        double nextX = x - (expression.setVariable("x", x).evaluate() / xd);
        nextX = roundToTolerance(nextX);
        if(Math.abs(nextX - x) <= tolerance.doubleValue()){
            return xn;
        } else {
            xn.push(nextX);
            return newtonRaphson(expression, nextX, xn);
        }
    }

    public ArrayList<Double> secant(Expression expression, double x0, double x1, ArrayList<Double> xn){
        if(xn.size() == 0){
            xn.add(roundToTolerance(x0));
            xn.add(roundToTolerance(x1));
        }
        if(xn.size() == maxIteration + 1){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        double nextX = x1 - (expression.setVariable("x", x1).evaluate() * ((x1 - x0) / (expression.setVariable("x", x1).evaluate() - expression.setVariable("x", x0).evaluate())));
        nextX = roundToTolerance(nextX);
        if(Math.abs(nextX - x1) <= tolerance.doubleValue()){
            xn.add(nextX);
            return xn;
        } else {
            xn.add(nextX);
            return secant(expression, x1, nextX, xn);
        }
    }

    public LinkedList<Pair<Double, Double>> bisection(Expression expression, double xL, double xR, LinkedList<Pair<Double, Double>> xn){

        if(xn.isEmpty()){
            xn.add(new Pair<Double, Double>(xL, xR));
        }
        if(xn.size() == maxIteration + 1){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        
        double xM = (xL + xR) / 2;
        if(expression.setVariable("x", xL).evaluate() * expression.setVariable("x", xR).evaluate()> 0){
            System.out.println("xL and xR should have opposite signs");
            return xn;
        }
        double fxM = expression.setVariable("x", xM).evaluate();
        
        if(Math.abs(xR - xL) <= tolerance.doubleValue()){
            xn.add(new Pair<Double, Double>(xM, xM));
            return xn;
        } else if (expression.setVariable("x", xL).evaluate() * fxM < 0){
            xn.add(new Pair<Double, Double>(xL, xM));
            return bisection(expression, xL, xM, xn);
        } else {
            xn.add(new Pair<Double, Double>(xM, xR));
            return bisection(expression, xM, xR, xn);
        }
    }

    public ArrayList<Pair<Double, Double>> falsePosition(Expression expression, double xL, double xR, ArrayList<Pair<Double, Double>> xn){
        if(xn.size() == maxIteration){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        if(expression.setVariable("x", xL).evaluate() * expression.setVariable("x", xR).evaluate()> 0){
            System.out.println("xL and xR should have opposite signs");
            return xn;
        }
        double nextX = xL + (((xR-xL) * (-1 * expression.setVariable("x", xL).evaluate())) / (expression.setVariable("x", xR).evaluate() - expression.setVariable("x", xL).evaluate()));
        nextX = roundToTolerance(nextX);
        if(Math.abs(nextX - xL) <= tolerance.doubleValue() || Math.abs(nextX - xR) < tolerance.doubleValue()){
            xn.add(new Pair<Double,Double>(nextX, nextX + tolerance.doubleValue()));
            return xn;
        } else if (expression.setVariable("x", xL).evaluate() * expression.setVariable("x", nextX).evaluate() < 0){
            xn.add(new Pair<Double,Double>(xL, nextX));
            return falsePosition(expression, xL, nextX, xn);
        } else {
            xn.add(new Pair<Double,Double>(nextX, xR));
            return falsePosition(expression, nextX, xR, xn);
        }
    }

    public double[][] matrixMultiplication(double[][] a, double[][] b) {
        int aRows = a.length;
        int aCols = a[0].length;
        int bRows = b.length;
        int bCols = b[0].length;
        if (aCols != bRows) {
            throw new IllegalArgumentException("Number of columns of A must equal number of rows of B");
        }
        double[][] result = new double[aRows][bCols];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                for (int k = 0; k < aCols; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public double[] cramer(double[][] matrix) {
        int n = matrix.length;
        double[][] coeff = new double[n][n];
        double[] constants = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coeff[i][j] = matrix[i][j];
            }
            constants[i] = matrix[i][n];
        }
        double detMain = determinant(coeff);
        if (Math.abs(detMain) < 1e-12) {
            throw new IllegalArgumentException("Determinant is zero, system has no unique solution");
        }
        double[] result = new double[n];
        for (int var = 0; var < n; var++) {
            double[][] temp = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    temp[i][j] = (j == var) ? constants[i] : coeff[i][j];
                }
            }
            result[var] = determinant(temp) / detMain;
        }
        return result;
    }

    private double determinant(double[][] mat) {
        int n = mat.length;
        if (n == 1) return mat[0][0];
        if (n == 2) return mat[0][0]*mat[1][1] - mat[0][1]*mat[1][0];
        double det = 0;
        for (int col = 0; col < n; col++) {
            double[][] subMat = new double[n-1][n-1];
            for (int i = 1; i < n; i++) {
                int subCol = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    subMat[i-1][subCol++] = mat[i][j];
                }
            }
            det += Math.pow(-1, col) * mat[0][col] * determinant(subMat);
        }
        return det;
    }

    public Expression parseEquation(String equation){
        String[] parsedEquation = new String[2];
        parsedEquation[0] = equation.substring(0, equation.indexOf('=')).trim();
        parsedEquation[1] = equation.substring(equation.indexOf('=') + 1, equation.length()).trim();
        return new ExpressionBuilder(parsedEquation[0]).variable("x").build();
    }
}