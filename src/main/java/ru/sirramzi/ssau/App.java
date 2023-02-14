package ru.sirramzi.ssau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Function;

public class App extends Application {

    static double fi0 = 0;
    static double r0 = 1;
    static double mu = 1;
    static double rk = 1.524;
    static double rz = 149.6 * (Math.pow(10, 9));
    static double muSun = 1.3273 * (Math.pow(10, 20));
    static double deltaVFi = Math.sqrt((2 * rk) / (r0 + rk) * (1 / r0)) - Math.sqrt(1 / r0);
    static double Vfi0 = Math.sqrt(mu / r0);
    static double Vr0 = 0;
    static double Pa = 9.1 * (Math.pow(10, -6));
    static double Pa2 = 9.1 * (Math.pow(10, -4));
    static double Sm = 250;
    static double m = 450;
    static double aSun = ((Pa * Sm) / (r0 * r0 * m)) * ((Math.pow(rz, 2)) / muSun);
    static double aSun2 = ((Pa2 * Sm) / (r0 * r0 * m)) * (Math.pow(rz, 2) / muSun);
    static double lam = 0;
    private static double[] y0 = { r0, fi0, Vr0, Vfi0 + deltaVFi };

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        double t0 = 0.0; // start time
        double tn = 4.4; // end time
        double h = 0.1; // step size
        Function<Double, double[]> f = t -> {
            double[] dydt = new double[4];
            dydt[0] = y0[2]; // drDt
            dydt[1] = y0[3] / y0[0]; // dFiDt
            dydt[2] = Math.pow(y0[3], 2) / y0[0] - 1 / Math.pow(y0[0], 2); // dVrDt
            dydt[3] = -(y0[2] * y0[3]) / y0[0]; // dVFiDt
            return dydt;
        };
        XYChart.Series<Number, Number> drDtSeries = new XYChart.Series<>();
        drDtSeries.setName("dr_dt");
        XYChart.Series<Number, Number> dFiDtSeries = new XYChart.Series<>();
        dFiDtSeries.setName("dFi_dt");
        XYChart.Series<Number, Number> dVrDtSeries = new XYChart.Series<>();
        dVrDtSeries.setName("dVr_dt");
        XYChart.Series<Number, Number> dVFiDtSeries = new XYChart.Series<>();
        dVFiDtSeries.setName("dVFi_dt");
        for (double t = t0; t <= tn; t += h) {
            double[] y = RungeKutta4.solve(y0, t, t + h, f);
            y0 = y;
            drDtSeries.getData().add(new XYChart.Data<>(t, y0[0]));
            dFiDtSeries.getData().add(new XYChart.Data<>(t, y0[1]));
            dVrDtSeries.getData().add(new XYChart.Data<>(t, y0[2]));
            dVFiDtSeries.getData().add(new XYChart.Data<>(t, y0[3]));
        }
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time, s");
        yAxis.setLabel("Value");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.getData().add(drDtSeries);
        lineChart.getData().add(dFiDtSeries);
        lineChart.getData().add(dVrDtSeries);
        lineChart.getData().add(dVFiDtSeries);
        scene = new Scene(lineChart, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}