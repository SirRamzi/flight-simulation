package ru.sirramzi.ssau;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class App extends Application {

    private LineChart<Number, Number> rtlineChartDec;
    private LineChart<Number, Number> PalineChartDec;
    private LineChart<Number, Number> lineChartPolar;

    @Override
    public void start(Stage stage) throws IOException {
        Data marsData = new Data();

        // Pa chart
        NumberAxis PaxAxisDec = new NumberAxis();
        NumberAxis PayAxisDec = new NumberAxis();
        PaxAxisDec.setLabel("Time, s");
        PayAxisDec.setLabel("Pa");
        PalineChartDec = new LineChart<Number, Number>(PaxAxisDec, PayAxisDec);
        PalineChartDec.setCreateSymbols(false);
        PalineChartDec.setAxisSortingPolicy(SortingPolicy.NONE);

        // rt chart
        NumberAxis rtxAxisDec = new NumberAxis();
        NumberAxis rtyAxisDec = new NumberAxis();
        rtxAxisDec.setLabel("Time, s");
        rtyAxisDec.setLabel("r, ae");
        rtlineChartDec = new LineChart<Number, Number>(rtxAxisDec, rtyAxisDec);
        rtlineChartDec.setCreateSymbols(false);
        rtlineChartDec.setAxisSortingPolicy(SortingPolicy.NONE);

        // rFi polar chart
        NumberAxis xAxisPolar = new NumberAxis(-2, 2, 0.25);
        NumberAxis yAxisPolar = new NumberAxis(-2, 2, 0.25);
        xAxisPolar.setLabel("r");
        yAxisPolar.setLabel("fi");
        lineChartPolar = new LineChart<Number, Number>(xAxisPolar, yAxisPolar);
        lineChartPolar.setCreateSymbols(false);
        lineChartPolar.setAxisSortingPolicy(SortingPolicy.NONE);

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
        TextField t1Field = new TextField(Double.toString(marsData.getT1()));
        t1Field.setPromptText("t1");
        TextField t2Field = new TextField(Double.toString(marsData.getT2()));
        t2Field.setPromptText("t2");
        TextField P1Field = new TextField(Double.toString(marsData.getPa1()));
        P1Field.setPromptText("P1");
        TextField P2Field = new TextField(Double.toString(marsData.getPa2()));
        P2Field.setPromptText("P2");
        Button refreshButton = new Button("Reload");

        // add the buttons to the VBox
        vBox.getChildren().addAll(rtChartButton, PaChartButton, polarChartButton, t1Field, t2Field, P1Field, P2Field,
                refreshButton);

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

        refreshButton.setOnAction((event) -> {
            marsData.setT1(Double.parseDouble(t1Field.getText()));
            marsData.setT2(Double.parseDouble(t2Field.getText()));
            marsData.setPa1(Double.parseDouble(P1Field.getText()));
            marsData.setPa2(Double.parseDouble(P2Field.getText()));
            List<Series<Number, Number>> darkData = marsData.getDarkData();
            List<Series<Number, Number>> sunData = marsData.getSunData();
            List<Series<Number, Number>> planetData = marsData.getPlanetData();
            PalineChartDec.getData().clear();
            rtlineChartDec.getData().clear();
            lineChartPolar.getData().clear();
            PalineChartDec.getData().add(sunData.get(0));
            rtlineChartDec.getData().add(darkData.get(0));
            rtlineChartDec.getData().add(sunData.get(1));
            lineChartPolar.getData().add(darkData.get(1));
            lineChartPolar.getData().add(sunData.get(2));
            lineChartPolar.getData().add(planetData.get(0));
            lineChartPolar.getData().add(planetData.get(1));
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