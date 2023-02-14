package ru.sirramzi.ssau;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    private LineChart<Number, Number> lineChartDec;

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

        Canvas canvas = new Canvas(400, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.LIGHTBLUE);
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        for (double t = t0; t <= tn; t += h) {
            double[] y = RungeKutta4.solve(y0, t, t + h, f);
            y0 = y;
            drDtSeries.getData().add(new XYChart.Data<>(t, y0[0]));
            dFiDtSeries.getData().add(new XYChart.Data<>(t, y0[1]));
            dVrDtSeries.getData().add(new XYChart.Data<>(t, y0[2]));
            dVFiDtSeries.getData().add(new XYChart.Data<>(t, y0[3]));

            double xx = centerX + y0[0] * 0.001 * Math.cos(y0[1]);
            double yy = centerY + y0[0] * 0.001 * Math.sin(y0[1]);
            gc.setFill(Color.BLACK);
            gc.fillOval(xx, yy, 2, 2);
        }

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time, s");
        yAxis.setLabel("Value");

        lineChartDec = new LineChart<>(new NumberAxis(), new NumberAxis());
        lineChartDec.setCreateSymbols(false);
        lineChartDec.getData().add(drDtSeries);
        lineChartDec.getData().add(dFiDtSeries);
        lineChartDec.getData().add(dVrDtSeries);
        lineChartDec.getData().add(dVFiDtSeries);

        // create a BorderPane to hold the menu and the charts
        BorderPane borderPane = new BorderPane();

        // create the VBox to hold the navigation buttons
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        // create the navigation buttons
        Button polarChartButton = new Button("Polar Chart");
        Button decChartButton = new Button("Decrt Chart");

        // add the buttons to the VBox
        vBox.getChildren().addAll(polarChartButton, decChartButton);

        // create the StackPane to hold the charts
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        // add the line chart and the bar chart to the StackPane
        stackPane.getChildren().addAll(lineChartDec, canvas);

        // set the visibility of the bar chart to false
        canvas.setVisible(false);

        // set the action for the line button
        polarChartButton.setOnAction((event) -> {
            lineChartDec.setVisible(true);
            canvas.setVisible(false);
        });

        // set the action for the bar button
        decChartButton.setOnAction((event) -> {
            lineChartDec.setVisible(false);
            canvas.setVisible(true);
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