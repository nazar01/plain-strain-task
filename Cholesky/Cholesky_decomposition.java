import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Cholesky_decomposition {

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    static int get_band(double[][] a, int size) {
        int max_band = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (a[i][j] != 0 && Math.abs(i - j) > max_band) {
                    max_band = Math.abs(i - j);
                }
            }
        }
        return max_band + 1;
    }

    static double[] generate_x(int size) {
        double[] x = new double[size];
        int range = 21;
        double g;
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            g = (double) rand.nextInt(range) - 10;
            x[i] = g != 0 ? g : 1.0;
        }
        return x;
    }

    static double[][] generate_matrix(int size, int l) {
        double[][] a = new double[size][size];
        double g;
        Random rand = new Random();
        int range = 21;
        for (int j = 0; j < size; j++) {
            for (int i = j; i < size && j - i + l - 1 >= 0; i++) {
                g = (double) rand.nextInt(range) - 10;
                a[i][j] = g != 0 ? g : 1.0;
                if (i != j) {
                    a[j][i] = g != 0 ? g : 1.0;
                }
            }
        }
        return a;
    }

    static double[][] read_matrix_from_file(Scanner input, int size) {
        double[][] amax = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                amax[i][j] = input.nextDouble();
            }
        }
        return amax;
    }

    static double[][] convert_matrix(double[][] amax, int size, int l) {
        double[][] a = new double[size][l];
        for (int j = 0; j < size; j++) {
            for (int i = j; i < size && j - i + l - 1 >= 0; i++) {
                a[i][j - i + l - 1] = amax[i][j];
            }
        }
        return a;
    }

    static double[] solve_f(double[][] a, double[] x, int size, int l) {
        double[] f = new double[size];
        for (int i = 0; i < size; i++) {
            f[i] = a[i][l - 1] * x[i];
            for (int j = 1; j < l + 1 && l - 1 - j >= 0 && i - j >= 0; j++) {
                f[i] += a[i][l - 1 - j] * x[i - j];
            }
            for (int j = 1; j < l + 1 && l - 1 - j >= 0 && i + j < size; j++) {
                f[i] += a[i + j][l - 1 - j] * x[i + j];
            }
        }
        return f;
    }

    static void print_matrix(double[][] matrix, int imax, int jmax) {
        for (int i = 0; i < imax; i++) {
            for (int j = 0; j < jmax; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }


    static double[] Cholesky(double[][] a, double[] f, int n, int L) {
        double sum;
        for (int j = 0; j < n; j++) {
            for (int i = j; i < n && j - i + L - 1 >= 0; i++) {
                sum = 0;
                for (int k = 0; k < j; k++) {
                    if (k - i + L - 1 >= 0 && k - j + L - 1 >= 0) {
                        if (a[k][L - 1] == 0) {
                            throw new ArithmeticException("Division by zero!");
                        }
                        sum += a[i][k - i + L - 1] * a[j][k - j + L - 1] / a[k][L - 1];
                    }
                }
                a[i][j - i + L - 1] -= sum;
            }
        }
        for (int i = 0; i < n; i++) {
            sum = 0;
            for (int k = 0; k < i; k++) {
                if (k - i + L - 1 >= 0)
                    sum += a[i][k - i + L - 1] * f[k];
            }
            if (a[i][L - 1] == 0) {
                throw new ArithmeticException("Division by zero!");
            }
            f[i] = (f[i] - sum) / a[i][L - 1];
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            sum = 0;
            for (int k = i + 1; k < n && i - k + L - 1 >= 0; k++) {
                sum += a[k][i - k + L - 1] * x[k];
            }
            x[i] = f[i] - sum / a[i][L - 1];
        }
        return x;
    }

    public static void main(String[] args) {
        Scanner input = null;
        try {
            input = new Scanner(new File("../stiffness_matrix_and_f.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        input.useLocale(Locale.US);
        int size = input.nextInt();
        double[][] amax = read_matrix_from_file(input, size);
        System.out.println("Matrix amax");
        print_matrix(amax, size, size);
        int l = get_band(amax,size);
        double[][] a = convert_matrix(amax, size, l);
        System.out.println("Matrix a");
        print_matrix(a, size, l);
        double[] f = new double[size];
        for (int i = 0; i < size; i++) {
            f[i] = input.nextDouble();
        }
        double[] x1;
        try {
            x1 = Cholesky(a, f, size, l);
            System.out.println("Vector x");
            for (int i = 0; i < size; i++) {
                System.out.print(x1[i] + " ");
            }
            System.out.println();
            for (int i = 0; i < size; i++) {
                System.out.print(round(x1[i], 4) + " ");
            }
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("../solved_u_v.txt"));
            for (int i = 0; i < size; i=i+2) {
                outputWriter.write(round(x1[i], 4) + " "+round(x1[i+1], 4)+"\n");
            }
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
