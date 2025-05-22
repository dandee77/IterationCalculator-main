package src;
import java.util.ArrayList;
import java.util.Arrays;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class Methods {


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
        double h = 1e-5;  // Small change in x (step size)

        // Create the expression with the variable 'x'
        

        // Evaluate the function at x + h
        expression.setVariable("x", x + h);
        double f1 = expression.evaluate();

        // Evaluate the function at x - h
        expression.setVariable("x", x - h);
        double f2 = expression.evaluate();

        // Approximate the derivative using the central difference formula
        return Math.round((f1 - f2) / (2 * h));
    }

    //need a error handling where |g'(x)| < 1
    public ArrayList<Double> fixedPoint(Expression expression, double x, ArrayList<Double> xn){
        if(xn.size() == 0){ // para masama yung initial guess
            xn.add(x);
        }
        if(xn.size() == maxIteration + 1){ // para masama yung initial guess
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        double nextX = expression.setVariable("x", x).evaluate();
        if(Math.abs(nextX - x) <= tolerance.doubleValue()){
            return xn;
        } else {
            nextX = new BigDecimal(nextX).divide(tolerance, 0, RoundingMode.HALF_UP).multiply(tolerance).doubleValue(); 
            xn.add(nextX); 
            return fixedPoint(expression, nextX, xn);
        }
    }  

    public ArrayList<Double> newtonRaphson(Expression expression, double x, ArrayList<Double> xn){
        if(xn.size() == 0){ // para masama yung initial guess
            xn.add(x);
        }
        if(xn.size() == maxIteration + 1){ // para masama yung initial guess
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        double xd = numericalDerivative(expression, x);
        if (new BigDecimal(xd).setScale(4, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Derivative is zero. Cannot proceed.");
            return xn;
        }

        double nextX = x - (expression.setVariable("x", x).evaluate() / xd);
        if(Math.abs(nextX - x) <= tolerance.doubleValue()){
            return xn;
        } else {
            nextX = new BigDecimal(nextX).divide(tolerance, 0, RoundingMode.HALF_UP).multiply(tolerance).doubleValue();// ginagawa lang neto is niroroundoff ung x base dun sa tolerance
            xn.add(nextX); 
            return newtonRaphson(expression, nextX, xn);
        }
    }

    public ArrayList<Double> secant(Expression expression, double x0, double x1, ArrayList<Double> xn){
        if(xn.size() == 0){ // para masama yung initial guess
            xn.add(x0);
            xn.add(x1);
        }
        if(xn.size() == maxIteration + 1){ // para masama yung initial guess
            System.out.println("Max iterations reached. Cannot proceed.");
            return xn;
        }
        
        double nextX = x1 - (expression.setVariable("x", x1).evaluate() * ((x1 - x0) / (expression.setVariable("x", x1).evaluate() - expression.setVariable("x", x0).evaluate())));
        

        if(Math.abs(nextX - x1) <= tolerance.doubleValue()){
            return xn;
        } else {
            nextX = new BigDecimal(nextX).divide(tolerance, 0, RoundingMode.HALF_UP).multiply(tolerance).doubleValue();
            xn.add(nextX); 
            return secant(expression, x1, nextX, xn);
        }
    }

    public ArrayList<Pair<Double, Double>> bisection(Expression expression, double xL, double xR, ArrayList<Pair<Double, Double>> xn){
        //base case:
        if(xn.size() == 0){
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
        xM = new BigDecimal(xM).divide(tolerance, 0, RoundingMode.HALF_UP).multiply(tolerance).doubleValue();
        double fxM = expression.setVariable("x", xM).evaluate();
        
        
        if(Math.abs(xR - xL) < tolerance.doubleValue()){
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
        nextX = new BigDecimal(nextX).divide(tolerance, 0, RoundingMode.HALF_UP).multiply(tolerance).doubleValue();
        if(Math.abs(nextX - xL) < tolerance.doubleValue() || Math.abs(nextX - xR) < tolerance.doubleValue()){
            xn.add(new Pair<Double,Double>(nextX, nextX));
            return xn;
        } else if (expression.setVariable("x", xL).evaluate() * expression.setVariable("x", nextX).evaluate() < 0){
            xn.add(new Pair<Double,Double>(xL, nextX));
            return falsePosition(expression, xL, nextX, xn);
        } else {
            xn.add(new Pair<Double,Double>(nextX, xR));
            return falsePosition(expression, nextX, xR, xn);
        }
    }
// TODO: EDIT TS
    // General matrix multiplication for any compatible 2D matrices
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

    //can only solve 3x3 matrix (x, y, z)
    // General Cramer's Rule for n x n systems
    // matrix: n x (n+1) augmented matrix
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

    // Helper: determinant of n x n matrix (recursive, Laplace expansion)
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

    //dapat iparse muna as matrix
    public double[] guassianElimination(double[][] matrix){
            
        for(int i = 0; i < matrix.length; i++){

            for(int j = i + 1; j < matrix.length; j++){
                if(Math.abs(matrix[j][i]) > Math.abs(matrix[i][i])){
                    double[] temp = matrix[i];
                    matrix[i] = matrix[j];
                    matrix[j] = temp;
                }
            }

            

            for (int j = i + 1; j < matrix.length; j++) {
                double factor = matrix[j][i] / matrix[i][i];
                for (int k = i; k <= matrix.length; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
            
        }


        // Back Substitution
        double[] solution = new double[matrix.length];
        for (int i = matrix.length - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < matrix.length; j++) {
                sum += matrix[i][j] * solution[j];
            }
            solution[i] = (matrix[i][matrix.length] - sum) / matrix[i][i];
        }

        return solution;
    }

    //iparse muna as matrix
    public ArrayList<Double[]> jacobi(double[][] matrix, ArrayList<Double[]> xyz){
        
        
        //still cant wrap my head around this shit man
        for (int i = 0; i < matrix.length; i++) {
            int maxRow = i;
            for (int j = i + 1; j < matrix.length; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = j;
                }
            }

            // Swap if a more dominant row is found
            if (maxRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
            }
        }
        
        //insert initial guess
        Double[] initialGuess = {0.0, 0.0, 0.0};
        
        
        return jacobiEvaluate(matrix, initialGuess, xyz);

        

    }

    public ArrayList<Double[]> jacobiEvaluate(double[][] matrix, Double[] guess, ArrayList<Double[]> xyz){
        if(xyz.size() == maxIteration){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xyz;
        }
        Double nextGuess[] = new Double[3];
        nextGuess[0] = (-matrix[0][1] * guess[1] + -matrix[0][2] * guess[2] + matrix[0][3]) / matrix[0][0];
        nextGuess[1] = (-matrix[1][0] * guess[0] + -matrix[1][2] * guess[2] + matrix[1][3]) / matrix[1][1];
        nextGuess[2] = (-matrix[2][0] * guess[0] + -matrix[2][1] * guess[1] + matrix[2][3]) / matrix[2][2];
        System.out.println(Arrays.toString(nextGuess));
        if(Math.abs(nextGuess[0] - guess[0]) < 1e-3 && Math.abs(nextGuess[1] - guess[1]) < 1e-3 && Math.abs(nextGuess[2] - guess[2]) < 1e-3){
            return xyz;
        } else {
            xyz.add(nextGuess);
            return jacobiEvaluate(matrix, nextGuess, xyz);
        }
    }

    public ArrayList<Double[]> gaussSeidel(double[][] matrix, ArrayList<Double[]> xyz) {
       
        //still cant wrap my head around this shit man
        for (int i = 0; i < matrix.length; i++) {
            int maxRow = i;
            for (int j = i + 1; j < matrix.length; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = j;
                }
            }

            // Swap if a more dominant row is found
            if (maxRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
            }
        }
        
        //insert initial guess
        Double[] initialGuess = {0.0, 0.0, 0.0};
        
        return gaussSeidelEvaluate(matrix, initialGuess, xyz);
    }

    public ArrayList<Double[]> gaussSeidelEvaluate(double[][] matrix, Double[] guess, ArrayList<Double[]> xyz){
        if(xyz.size() == maxIteration){
            System.out.println("Max iterations reached. Cannot proceed.");
            return xyz;
        }
        Double nextGuess[] = new Double[3];
        nextGuess[0] = (-matrix[0][1] * guess[1] + -matrix[0][2] * guess[2] + matrix[0][3]) / matrix[0][0];
        nextGuess[1] = (-matrix[1][0] * nextGuess[0] + -matrix[1][2] * guess[2] + matrix[1][3]) / matrix[1][1];
        nextGuess[2] = (-matrix[2][0] * nextGuess[0] + -matrix[2][1] * nextGuess[1] + matrix[2][3]) / matrix[2][2];
        System.out.println(Arrays.toString(nextGuess));
        if(Math.abs(nextGuess[0] - guess[0]) < 1e-3 && Math.abs(nextGuess[1] - guess[1]) < 1e-3 && Math.abs(nextGuess[2] - guess[2]) < 1e-3){
            return xyz;
        } else {
            xyz.add(nextGuess);
            return gaussSeidelEvaluate(matrix, nextGuess, xyz);
        }
    }

    
    public Expression parseEquation(String equation){
        String[] parsedEquation = new String[2];
        
        parsedEquation[0] = equation.substring(0, equation.indexOf('=')).trim();
        parsedEquation[1] = equation.substring(equation.indexOf('=') + 1, equation.length()).trim();
        return new ExpressionBuilder(parsedEquation[0]).variable("x").build();
    }
    
    
    public double[][] parseEquation(String[] equations){
        double[][] matrix = new double[3][4];
        for(int i = 0; i < equations.length; i++){
            String[] parsedEquation = new String[2];
            equations[i].indexOf('=');
            parsedEquation[0] = equations[i].substring(0, equations[i].indexOf('=')).trim();
            parsedEquation[1] = equations[i].substring(equations[i].indexOf('=') + 1, equations[i].length()).trim();
            
            matrix[i][0] = new ExpressionBuilder(parsedEquation[0])
                                .variables("x", "y", "z")
                                .build()
                                .setVariable("x", 1)
                                .setVariable("y", 0)
                                .setVariable("z", 0)
                                .evaluate();
            matrix[i][1] = new ExpressionBuilder(parsedEquation[0])
                                .variables("x", "y", "z")
                                .build()
                                .setVariable("x", 0)
                                .setVariable("y", 1)
                                .setVariable("z", 0)
                                .evaluate();
            matrix[i][2] = new ExpressionBuilder(parsedEquation[0])
                                .variables("x", "y", "z")
                                .build()
                                .setVariable("x", 0)
                                .setVariable("y", 0)
                                .setVariable("z", 1)
                                .evaluate();
            matrix[i][3] = Double.parseDouble(parsedEquation[1]);                  
        }
        return matrix;
    }
}