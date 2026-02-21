import java.math.BigInteger;
import java.util.*;

public class PolynomialSolver {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    
    static BigInteger convertToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }

    static BigInteger[] multiply(BigInteger[] a, BigInteger[] b) {
        BigInteger[] result = new BigInteger[a.length + b.length - 1];
        Arrays.fill(result, BigInteger.ZERO);

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i + j] = result[i + j].add(a[i].multiply(b[j]));
            }
        }
        return result;
    }

    static BigInteger[] add(BigInteger[] a, BigInteger[] b) {
        int max = Math.max(a.length, b.length);
        BigInteger[] result = new BigInteger[max];
        Arrays.fill(result, BigInteger.ZERO);

        for (int i = 0; i < a.length; i++)
            result[i] = result[i].add(a[i]);

        for (int i = 0; i < b.length; i++)
            result[i] = result[i].add(b[i]);

        return result;
    }

    static BigInteger[] lagrange(List<Point> points) {
        int k = points.size();
        BigInteger[] result = new BigInteger[]{BigInteger.ZERO};

        for (int i = 0; i < k; i++) {

            BigInteger[] numerator = new BigInteger[]{BigInteger.ONE};
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;

             
                BigInteger[] temp = new BigInteger[]{
                        points.get(j).x.negate(),
                        BigInteger.ONE
                };

                numerator = multiply(numerator, temp);

                denominator = denominator.multiply(
                        points.get(i).x.subtract(points.get(j).x)
                );
            }

            BigInteger scale = points.get(i).y.divide(denominator);

            for (int t = 0; t < numerator.length; t++) {
                numerator[t] = numerator[t].multiply(scale);
            }

            result = add(result, numerator);
        }

        return result;
    }

    public static void main(String[] args) {

        // Manually adding first 7 points from testcase 2
        List<Point> points = new ArrayList<>();

        points.add(new Point(BigInteger.ONE,
                convertToDecimal("13444211440455345511", 6)));

        points.add(new Point(BigInteger.valueOf(2),
                convertToDecimal("aed7015a346d635", 15)));

        points.add(new Point(BigInteger.valueOf(3),
                convertToDecimal("6aeeb69631c227c", 15)));

        points.add(new Point(BigInteger.valueOf(4),
                convertToDecimal("e1b5e05623d881f", 16)));

        points.add(new Point(BigInteger.valueOf(5),
                convertToDecimal("316034514573652620673", 8)));

        points.add(new Point(BigInteger.valueOf(6),
                convertToDecimal("2122212201122002221120200210011020220200", 3)));

        points.add(new Point(BigInteger.valueOf(7),
                convertToDecimal("20120221122211000100210021102001201112121", 3)));

        BigInteger[] polynomial = lagrange(points);

        System.out.println("Polynomial Coefficients:");
        for (int i = 0; i < polynomial.length; i++) {
            System.out.println("x^" + i + " : " + polynomial[i]);
        }
    }
}