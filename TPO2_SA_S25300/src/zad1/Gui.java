package zad1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Gui extends Application {

    private static Service service;
    public static void start(String[] args, Service service) {
        Gui.service = service;
        launch(args);
    }

    private String pickedCountry = "Poland";
    private String pickedCity = "Warsaw";
    private String pickedCurrency = "USD";
    private Label weatherContentLabel = new Label(service.getWeatherPretty(pickedCity));
    private Label currencyRateLabel = new Label("Currency rate: " + pickedCurrency);
    private Label currencyRateContentLabel = new Label("" + service.getRateFor(pickedCurrency));

    private Label currencyRateNbpLabel = new Label("Currency Nbp PLN rate: ");
    private Label currencyRateNbpContentLabel = new Label("" + service.getNBPRate());
    private WebView webView;
    private WebEngine webEngine;

    public void setUserInputAndUpdate(String pickedCountry, String pickedCity, String pickedCurrency) {
        this.pickedCountry = pickedCountry;
        this.pickedCity = pickedCity;
        this.pickedCurrency = pickedCurrency;
        updateContents();
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("program na TPO 2");
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        //hbox.setStyle("-fx-background-color: #336699;");

        Label weatherLabel = new Label("Weather: ");
        weatherLabel.setLabelFor(weatherContentLabel);


        Button btn = new Button();
        btn.setText("Change Data button");
        btn.setOnAction(event -> PromptWindow.showNewWindow(this, pickedCountry, pickedCity, pickedCurrency));

        addVbox(hbox, weatherLabel, weatherContentLabel);
        addVbox(hbox, currencyRateLabel, currencyRateContentLabel);
        addVbox(hbox, currencyRateNbpLabel, currencyRateNbpContentLabel);

        hbox.getChildren().add(btn);

        webView = new WebView();
//        webView.setPrefHeight(500);
//        webView.setPrefWidth(500);
        webView.setContextMenuEnabled(false);
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.load(getWikipediaUrl(pickedCity));

        VBox root = new VBox();
        root.getChildren().add(hbox);

        VBox webViewVBox = new VBox();
        webViewVBox.setPrefWidth(root.getWidth());
        //webViewVBox.setPrefHeight(200);
        webViewVBox.getChildren().add(webView);
        root.getChildren().add(webViewVBox);

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private void addVbox(HBox root, Label... items) {
        VBox vBoxWeather = new VBox();
        if (items.length > 1) {
            items[0].setStyle("-fx-font-weight: bold");
        }
        for(Label item: items) {
            vBoxWeather.getChildren().add(item);
        }
        root.getChildren().add(vBoxWeather);
    }

    private void updateContents() {
        service = new Service(pickedCountry);

        weatherContentLabel.setText(service.getWeatherPretty(pickedCity));
        currencyRateContentLabel.setText("" + service.getRateFor(pickedCurrency));
        currencyRateLabel.setText("Currency rate: " + pickedCurrency);

        currencyRateNbpContentLabel.setText("" + service.getNBPRate());

        webEngine.load(getWikipediaUrl(pickedCity));
    }

    private String getWikipediaUrl(String phrase) {
        return "http://pl.wikipedia.org/wiki/" + phrase;
    }
}