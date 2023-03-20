package ru.sirramzi.ssau;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Function;

public class App extends Application {

    private double t0 = 0.0; // start time
    private double tn = 4.4; // end time
    private double t1 = 1;
    private double t2 = 3;
    private double h = 0.01; // step size
    private double[] y;
    private double[] ySun;
    private double fi0 = 0;
    private double r0 = 1;
    private double mu = 1;
    private double rk = 1.524;
    private double Rz = 149.6 * (Math.pow(10, 9));
    private double muSun = 1.3273 * (Math.pow(10, 20));
    private double dVFi = Math.sqrt((2 * rk) / (r0 + rk) * (1 / r0)) - Math.sqrt(1 / r0);
    private double Vfi0 = Math.sqrt(mu / r0);
    private double Vr0 = 0;
    private double Pa1 = 9.1 * (Math.pow(10, -6));
    private double Pa2 = 9.1 * (Math.pow(10, -4.5));
    private double Sm = 250;
    private double m = 450;
    private double lam = 35.26;
    private double[] y0 = { r0, fi0, Vr0, Vfi0 + dVFi };
    private double[] y0Sun = { r0, fi0, Vr0, Vfi0 + dVFi };

    private LineChart<Number, Number> rtlineChartDec;
    private LineChart<Number, Number> PalineChartDec;
    private LineChart<Number, Number> lineChartPolar;

    @Override
    public void start(Stage stage) throws IOException {
        double[] initialData = { t0, tn, t1, t2, h, r0, mu, rk, Rz, muSun, Pa1, Pa2 };

        Function<Double, double[]> f = t -> {
            double[] dydt = new double[4];
            dydt[0] = y0[2]; // drDt
            dydt[1] = y0[3] / y0[0]; // dFiDt
            dydt[2] = Math.pow(y0[3], 2) / y0[0] - 1 / Math.pow(y0[0], 2); // dVrDt
            dydt[3] = -(y0[2] * y0[3]) / y0[0]; // dVFiDt
            return dydt;
        };

        XYChart.Series<Number, Number> drDFiSeries = new XYChart.Series<>();
        drDFiSeries.setName("r_Fi");
        XYChart.Series<Number, Number> drDFiSunSeries = new XYChart.Series<>();
        drDFiSunSeries.setName("r_Fi_Sun");
        XYChart.Series<Number, Number> drDtSeries = new XYChart.Series<>();
        drDtSeries.setName("r_t");
        XYChart.Series<Number, Number> drDtSunSeries = new XYChart.Series<>();
        drDtSunSeries.setName("r_t_Sun");
        XYChart.Series<Number, Number> PaSeries = new XYChart.Series<>();
        PaSeries.setName("Pa");
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

            y = RungeKutta4.solve(y0, t, t + h, f);
            ySun = RungeKutta4.solve(y0Sun, t, t + h, fSun);
            y0 = y;
            y0Sun = ySun;

            PaSeries.getData().add(new XYChart.Data<>(t, Pa));
            drDFiSeries.getData().add(new XYChart.Data<>(y0[0] * Math.cos(y0[1]), y0[0] * Math.sin(y0[1])));
            drDFiSunSeries.getData()
                    .add(new XYChart.Data<>(y0Sun[0] * Math.cos(y0Sun[1]), y0Sun[0] * Math.sin(y0Sun[1])));
            drDtSeries.getData().add(new XYChart.Data<>(t, y0[0]));
            drDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[0]));

        }

        NumberAxis PaxAxisDec = new NumberAxis();
        NumberAxis PayAxisDec = new NumberAxis();
        PaxAxisDec.setLabel("Time, s");
        PayAxisDec.setLabel("Pa");
        PalineChartDec = new LineChart<Number, Number>(PaxAxisDec, PayAxisDec);
        PalineChartDec.setCreateSymbols(false);
        PalineChartDec.setAxisSortingPolicy(SortingPolicy.NONE);
        PalineChartDec.getData().add(PaSeries);
        // lineChartDec.getData().add(drDtSeries);
        // lineChartDec.getData().add(drDtSunSeries);

        NumberAxis rtxAxisDec = new NumberAxis();
        NumberAxis rtyAxisDec = new NumberAxis();
        rtxAxisDec.setLabel("Time, s");
        rtyAxisDec.setLabel("r, ae");
        rtlineChartDec = new LineChart<Number, Number>(rtxAxisDec, rtyAxisDec);
        rtlineChartDec.setCreateSymbols(false);
        rtlineChartDec.setAxisSortingPolicy(SortingPolicy.NONE);
        rtlineChartDec.getData().add(drDtSeries);
        rtlineChartDec.getData().add(drDtSunSeries);

        NumberAxis xAxisPolar = new NumberAxis(-2, 2, 0.25);
        NumberAxis yAxisPolar = new NumberAxis(-2, 2, 0.25);
        xAxisPolar.setLabel("r");
        yAxisPolar.setLabel("fi");

        lineChartPolar = new LineChart<Number, Number>(xAxisPolar, yAxisPolar);
        lineChartPolar.setCreateSymbols(false);
        lineChartPolar.setAxisSortingPolicy(SortingPolicy.NONE);
        lineChartPolar.getData().add(drDFiSeries);
        lineChartPolar.getData().add(drDFiSunSeries);
        lineChartPolar.getData().add(earthSeries);
        lineChartPolar.getData().add(marsSeries);

        // create a BorderPane to hold the menu and the charts
        BorderPane borderPane = new BorderPane();

        // create the VBox to hold the navigation buttons
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        // create the navigation buttons
        Button rtChartButton = new Button("rt Chart");
        Button PaChartButton = new Button("Pa Chart");
        Button polarChartButton = new Button("Polar Chart");

        // add the buttons to the VBox
        vBox.getChildren().addAll(rtChartButton, PaChartButton, polarChartButton);

        // create the StackPane to hold the charts
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        // add the line chart and the bar chart to the StackPane
        stackPane.getChildren().addAll(rtlineChartDec, PalineChartDec, lineChartPolar);

        // set the visibility of the bar chart to false
        lineChartPolar.setVisible(false);
        PalineChartDec.setVisible(false);

        rtChartButton.setOnAction((event) -> {
            rtlineChartDec.setVisible(true);
            PalineChartDec.setVisible(false);
            lineChartPolar.setVisible(false);
        });

        // set the action for the line button
        PaChartButton.setOnAction((event) -> {
            rtlineChartDec.setVisible(false);
            PalineChartDec.setVisible(true);
            lineChartPolar.setVisible(false);
        });

        // set the action for the bar button
        polarChartButton.setOnAction((event) -> {
            rtlineChartDec.setVisible(false);
            PalineChartDec.setVisible(false);
            lineChartPolar.setVisible(true);
        });

        // add the VBox and the StackPane to the BorderPane
        borderPane.setLeft(vBox);
        borderPane.setCenter(stackPane);

        // create a Scene to hold the BorderPane
        Scene scene = new Scene(borderPane, 800, 600);

        // set the Scene in the Stage
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}