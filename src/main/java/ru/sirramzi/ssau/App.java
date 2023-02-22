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

    static double fi0 = 0;
    static double r0 = 1;
    static double mu = 1;
    static double rk = 1.524;
    static double Rz = 149.6 * (Math.pow(10, 9));
    static double muSun = 1.3273 * (Math.pow(10, 20));
    static double deltaVFi = Math.sqrt((2 * rk) / (r0 + rk) * (1 / r0)) - Math.sqrt(1 / r0);
    static double Vfi0 = Math.sqrt(mu / r0);
    static double Vr0 = 0;
    static double Pa = 9.1 * (Math.pow(10, -1));
    static double Pa2 = 9.1 * (Math.pow(10, -4));
    static double Sm = 250;
    static double m = 450;
    static double lam = 0;
    private static double[] y0 = { r0, fi0, Vr0, Vfi0 + deltaVFi };
    private static double[] y0Sun = { r0, fi0, Vr0, Vfi0 + deltaVFi };

    private LineChart<Number, Number> lineChartDec;
    private LineChart<Number, Number> lineChartPolar;

    @Override
    public void start(Stage stage) throws IOException {
        double t0 = 0.0; // start time
        double tn = 4.4; // end time
        double h = 0.01; // step size

        Function<Double, double[]> f = t -> {
            double[] dydt = new double[4];
            dydt[0] = y0[2]; // drDt
            dydt[1] = y0[3] / y0[0]; // dFiDt
            dydt[2] = Math.pow(y0[3], 2) / y0[0] - 1 / Math.pow(y0[0], 2); // dVrDt
            dydt[3] = -(y0[2] * y0[3]) / y0[0]; // dVFiDt
            return dydt;
        };

        Function<Double, double[]> fSun = t -> {
            double[] dydt = new double[4];
            dydt[0] = y0Sun[2]; // drDt
            dydt[1] = y0Sun[3] / y0Sun[0]; // dFiDt
            dydt[2] = (Math.pow(y0Sun[3], 2) / y0Sun[0] - mu / Math.pow(y0Sun[0], 2))
                    + ((((Pa * Sm * Math.pow(Rz, 2) * Math.pow(Math.cos(Math.toRadians(lam)), 2))
                            / (Math.pow(y0Sun[0], 2) * m * mu))) * Math.cos(Math.toRadians(lam))); // dVrDt
            dydt[3] = -((y0Sun[2] * y0Sun[3]) / y0Sun[0])
                    + ((((Pa * Sm * Math.pow(Rz, 2) * Math.pow(Math.cos(Math.toRadians(lam)), 2))
                    / (Math.pow(y0Sun[0], 2) * m * mu))) * Math.sin(Math.toRadians(lam))); // dVFiDt
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
        XYChart.Series<Number, Number> drDFiSeries = new XYChart.Series<>();
        drDFiSeries.setName("dr_dFi");

        XYChart.Series<Number, Number> drDtSunSeries = new XYChart.Series<>();
        drDtSunSeries.setName("dr_dtSun");
        XYChart.Series<Number, Number> dFiDtSunSeries = new XYChart.Series<>();
        dFiDtSunSeries.setName("dFi_dtSun");
        XYChart.Series<Number, Number> dVrDtSunSeries = new XYChart.Series<>();
        dVrDtSunSeries.setName("dVr_dtSun");
        XYChart.Series<Number, Number> dVFiDtSunSeries = new XYChart.Series<>();
        dVFiDtSunSeries.setName("dVFi_dtSun");
        XYChart.Series<Number, Number> drDFiSunSeries = new XYChart.Series<>();
        drDFiSunSeries.setName("dr_dFiSun");

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
            double[] y = RungeKutta4.solve(y0, t, t + h, f);
            double[] ySun = RungeKutta4.solve(y0Sun, t, t + h, fSun);
            y0 = y;
            y0Sun = ySun;
            drDtSeries.getData().add(new XYChart.Data<>(t, y0[0]));
            dFiDtSeries.getData().add(new XYChart.Data<>(t, y0[1]));
            dVrDtSeries.getData().add(new XYChart.Data<>(t, y0[2]));
            dVFiDtSeries.getData().add(new XYChart.Data<>(t, y0[3]));
            drDFiSeries.getData().add(new XYChart.Data<>(y0[0] * Math.cos(y0[1]), y0[0] * Math.sin(y0[1])));

            drDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[0]));
            dFiDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[1]));
            dVrDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[2]));
            dVFiDtSunSeries.getData().add(new XYChart.Data<>(t, y0Sun[3]));
            drDFiSunSeries.getData()
                    .add(new XYChart.Data<>(y0Sun[0] * Math.cos(y0Sun[1]), y0Sun[0] * Math.sin(y0Sun[1])));

        }

        NumberAxis xAxisDec = new NumberAxis();
        NumberAxis yAxisDec = new NumberAxis();
        xAxisDec.setLabel("Time, s");
        yAxisDec.setLabel("Value");

        lineChartDec = new LineChart<Number, Number>(xAxisDec, yAxisDec);
        lineChartDec.setCreateSymbols(false);
        lineChartDec.setAxisSortingPolicy(SortingPolicy.NONE);
        lineChartDec.getData().add(drDtSeries);
        lineChartDec.getData().add(dFiDtSeries);
        lineChartDec.getData().add(dVrDtSeries);
        lineChartDec.getData().add(dVFiDtSeries);

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
        Button polarChartButton = new Button("Polar Chart");
        Button decChartButton = new Button("Decart Chart");

        // add the buttons to the VBox
        vBox.getChildren().addAll(polarChartButton, decChartButton);

        // create the StackPane to hold the charts
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        // add the line chart and the bar chart to the StackPane
        stackPane.getChildren().addAll(lineChartDec, lineChartPolar);

        // set the visibility of the bar chart to false
        lineChartPolar.setVisible(false);

        // set the action for the line button
        polarChartButton.setOnAction((event) -> {
            lineChartDec.setVisible(true);
            lineChartPolar.setVisible(false);
        });

        // set the action for the bar button
        decChartButton.setOnAction((event) -> {
            lineChartDec.setVisible(false);
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