package ru.sirramzi.ssau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

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
    private double[] y0 = { r0, fi0, Vr0, Vfi0 + dVFi, 0, 0 };
    private double[] y0Sun = { r0, fi0, Vr0, Vfi0 + dVFi, 0, 0 };

    private void resetY(double[] y) {
        y[0] = r0;
        y[1] = fi0;
        y[2] = Vr0;
        y[3] = Vfi0 + dVFi;
        y[4] = 0;
        y[5] = 0;
    }

    public void setInitialData(HashMap<String, Double> initialData) {
        if (initialData.containsKey("t0")) {
            t0 = initialData.get("t0");
        }
        if (initialData.containsKey("tn")) {
            tn = initialData.get("tn");
        }
        if (initialData.containsKey("t1")) {
            t1 = initialData.get("t1");
        }
        if (initialData.containsKey("t2")) {
            t2 = initialData.get("t2");
        }
        if (initialData.containsKey("h")) {
            h = initialData.get("h");
        }
        if (initialData.containsKey("mu")) {
            mu = initialData.get("mu");
        }
        if (initialData.containsKey("rk")) {
            rk = initialData.get("rk");
        }
        if (initialData.containsKey("Rz")) {
            Rz = initialData.get("Rz");
        }
        if (initialData.containsKey("muSun")) {
            muSun = initialData.get("muSun");
        }
        if (initialData.containsKey("r0")) {
            r0 = initialData.get("r0");
        }
        if (initialData.containsKey("fi0")) {
            fi0 = initialData.get("fi0");
        }
        if (initialData.containsKey("Vr0")) {
            Vr0 = initialData.get("Vr0");
        }
        if (initialData.containsKey("Vfi0")) {
            Vfi0 = initialData.get("Vfi0");
        }
        if (initialData.containsKey("dVFi")) {
            dVFi = initialData.get("dVFi");
        }
        if (initialData.containsKey("Pa1")) {
            Pa1 = initialData.get("Pa1");
        }
        if (initialData.containsKey("Pa2")) {
            Pa2 = initialData.get("Pa2");
        }
        if (initialData.containsKey("Sm")) {
            Sm = initialData.get("Sm");
        }
        if (initialData.containsKey("m")) {
            m = initialData.get("m");
        }
        if (initialData.containsKey("lam")) {
            lam = initialData.get("lam");
        }
    }

    public List<Series<Number, Number>> getData() {
        List<Series<Number, Number>> seriesList = new ArrayList<>();
        XYChart.Series<Number, Number> drDFiSeries = new XYChart.Series<>();
        drDFiSeries.setName("r_Fi");
        XYChart.Series<Number, Number> drDtSeries = new XYChart.Series<>();
        drDtSeries.setName("r_t");
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
            drDFiSeries.getData().add(new XYChart.Data<>(y0[0] * Math.cos(y0[1]), y0[0] * Math.sin(y0[1])));
            drDtSeries.getData().add(new XYChart.Data<>(t, y0[0]));
        }
        seriesList.add(drDtSeries);
        seriesList.add(drDFiSeries);
        resetY(y0);
        return seriesList;
    }

    public List<Series<Number, Number>> getSunData() {
        List<Series<Number, Number>> seriesList = new ArrayList<>();
        XYChart.Series<Number, Number> PaSeries = new XYChart.Series<>();
        PaSeries.setName("Pa");
        XYChart.Series<Number, Number> drDFiSunSeries = new XYChart.Series<>();
        drDFiSunSeries.setName("r_Fi_Sun");
        XYChart.Series<Number, Number> drDtSunSeries = new XYChart.Series<>();
        drDtSunSeries.setName("r_t_Sun");
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
            PaSeries.getData().add(new XYChart.Data<>(t, Pa));
            drDFiSunSeries.getData()
                    .add(new XYChart.Data<>(y0Sun[0] * Math.cos(y0Sun[1]), y0Sun[0] * Math.sin(y0Sun[1])));
            drDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[0]));
        }
        seriesList.add(PaSeries);
        seriesList.add(drDtSunSeries);
        seriesList.add(drDFiSunSeries);
        resetY(y0Sun);
        return seriesList;
    }

    public List<Series<Number, Number>> getPlanetData() {
        List<Series<Number, Number>> seriesList = new ArrayList<>();
        XYChart.Series<Number, Number> earthSeries = new XYChart.Series<>();
        earthSeries.setName("earh");
        XYChart.Series<Number, Number> marsSeries = new XYChart.Series<>();
        marsSeries.setName("mars");
        for (double i = 0; i <= 360; i++) {
            earthSeries.getData()
                    .add(new XYChart.Data<>(r0 * Math.cos(Math.toRadians(i)), r0 * Math.sin(Math.toRadians(i))));
            marsSeries.getData()
                    .add(new XYChart.Data<>(rk * Math.cos(Math.toRadians(i)), rk * Math.sin(Math.toRadians(i))));
        }
        seriesList.add(earthSeries);
        seriesList.add(marsSeries);
        return seriesList;
    }
}
