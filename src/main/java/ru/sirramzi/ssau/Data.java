package ru.sirramzi.ssau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Data {
    private double t0 = 0.0; // start time
    private double tn = 4.4; // end time
    private double t1 = 1;
    private double t2 = 3;
    private double h = 0.01; // step size
    private double mu = 1;
    private double rk = 1.524;
    private double Rz = 149.6 * (Math.pow(10, 9));
    private double muSun = 1.3273 * (Math.pow(10, 20));
    private double r0 = 1;
    private double fi0 = 0;
    private double Vr0 = 0;
    private double Vfi0 = Math.sqrt(mu / r0);
    private double dVFi = Math.sqrt((2 * rk) / (r0 + rk) * (1 / r0)) - Math.sqrt(1 / r0);
    private double Pa1 = 9.1 * (Math.pow(10, -6));
    private double Pa2 = 9.1 * (Math.pow(10, -4.5));
    private double Sm = 250;
    private double m = 450;
    private double lam = 35.26;
    private double[] y0 = { r0, fi0, Vr0, Vfi0 + dVFi };
    private double[] y0Sun = { r0, fi0, Vr0, Vfi0 + dVFi };

    private void resetY(double[] y) {
        y[0] = r0;
        y[1] = fi0;
        y[2] = Vr0;
        y[3] = Vr0;
        y[4] = Vfi0 + dVFi;
    }

    public void setInitialData(HashMap<String, Double> initialData) {

    }

    public List<double[]> getData() {
        List<double[]> data = new ArrayList<>();
        double[] y;
        Function<Double, double[]> f = t -> {
            double[] dydt = new double[4];
            dydt[0] = y0[2]; // drDt
            dydt[1] = y0[3] / y0[0]; // dFiDt
            dydt[2] = Math.pow(y0[3], 2) / y0[0] - 1 / Math.pow(y0[0], 2); // dVrDt
            dydt[3] = -(y0[2] * y0[3]) / y0[0]; // dVFiDt
            return dydt;
        };
        for (double t = t0; t <= tn; t += h) {
            y = RungeKutta4.solve(y0, t, t + h, f);
            y0 = y;
            data.add(y);
        }
        resetY(y0);
        return data;
    }

    public List<double[]> getSunData() {
        List<double[]> data = new ArrayList<>();
        double[] y;
        for (double t = t0; t <= tn; t += h) {
            double Pa;
            if (t < t1 || t > t2) {
                Pa = Pa1;
            } else {
                Pa = (Pa2 - Pa1) * (1 - Math.pow(t - (t2 + t1) / 2, 2) / Math.pow(t1 - (t2 + t1) / 2, 2)) + Pa1;
            }
            Function<Double, double[]> fSun = tt -> {
                double[] dydt = new double[4];
                dydt[0] = y0Sun[2]; // drDt
                dydt[1] = y0Sun[3] / y0Sun[0]; // dFiDt
                dydt[2] = Math.pow(y0Sun[3], 2) / y0Sun[0] - mu / Math.pow(y0Sun[0], 2)
                        + ((Pa * Sm) / (y0Sun[0] * y0Sun[0] * m)) * ((Rz * Rz) / muSun)
                                * Math.pow(Math.cos(Math.toRadians(lam)), 2) * Math.cos(Math.toRadians(lam)); // dVrDt
                dydt[3] = (-(y0Sun[2] * y0Sun[3]) / y0Sun[0])
                        + ((Pa * Sm) / (y0Sun[0] * y0Sun[0] * m)) * ((Rz * Rz) / muSun)
                                * Math.pow(Math.cos(Math.toRadians(lam)), 2) * Math.sin(Math.toRadians(lam)); // dVFiDt
                return dydt;
            };
            y = RungeKutta4.solve(y0Sun, t, t + h, fSun);
            y0Sun = y;
            data.add(y);
        }
        resetY(y0Sun);
        return data;
    }
}
