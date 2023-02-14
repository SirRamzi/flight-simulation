package ru.sirramzi.ssau;

import java.util.function.Function;

public class RungeKutta4 {
    private static final double h = 0.01; // step size

    public static double[] solve(double[] y0, double t0, double tn, Function<Double, double[]> f) {
        double[] y = y0;
        for (double t = t0; t < tn; t += h) {
            double[] k1 = f.apply(t);
            double[] k2 = f.apply(t + h / 2.0);
            double[] k3 = f.apply(t + h / 2.0);
            double[] k4 = f.apply(t + h);

            for (int i = 0; i < 4; i++) {
                y[i] = y[i] + h / 6.0 * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
            }
        }
        return y;
    }
}