import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {

        // Change filename here
        String filename = "input1.json"; 
        // String filename = "input2.json";

        String content = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filename)));

        // Extract k value
        int kIndex = content.indexOf("\"k\":");
        int commaIndex = content.indexOf(",", kIndex);
        int k = Integer.parseInt(content.substring(kIndex + 4, commaIndex).trim());

        List<Point> points = new ArrayList<>();

        // Extract each root
        String[] parts = content.split("\"[0-9]+\":");

        for (int i = 1; i < parts.length; i++) {

            String part = parts[i];

            int xStart = content.indexOf("\"", content.indexOf(parts[i-1]) + parts[i-1].length());
            String xStr = content.substring(xStart + 1, content.indexOf("\"", xStart + 1));

            int baseIndex = part.indexOf("\"base\":");
            int valueIndex = part.indexOf("\"value\":");

            if (baseIndex == -1 || valueIndex == -1) continue;

            String baseStr = part.substring(baseIndex + 7, part.indexOf(",", baseIndex)).replace("\"", "").trim();
            String valueStr = part.substring(valueIndex + 8, part.indexOf("\"", valueIndex + 8)).trim();

            try {
                int x = Integer.parseInt(xStr);
                int base = Integer.parseInt(baseStr);
                BigInteger y = new BigInteger(valueStr, base);

                points.add(new Point(BigInteger.valueOf(x), y));
            } catch (Exception e) {
                continue;
            }
        }

        // Sort by x
        points.sort(Comparator.comparing(p -> p.x));

        // Take first k
        List<Point> selected = points.subList(0, k);

        BigInteger[] polynomial = lagrange(selected);

        System.out.println("Polynomial Coefficients:");
        for (int i = 0; i < polynomial.length; i++) {
            System.out.println("x^" + i + " : " + polynomial[i]);
        }
    }

    static BigInteger[] multiply(BigInteger[] a, BigInteger[] b) {
        BigInteger[] result = new BigInteger[a.length + b.length - 1];
        Arrays.fill(result, BigInteger.ZERO);

        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
                result[i + j] = result[i + j].add(a[i].multiply(b[j]));

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

            for (int t = 0; t < numerator.length; t++)
                numerator[t] = numerator[t].multiply(scale);

            result = add(result, numerator);
        }

        return result;
    }
}