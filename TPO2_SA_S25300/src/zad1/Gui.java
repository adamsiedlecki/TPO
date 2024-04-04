package zad1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui extends Application {

    private static Service service;
    public static void start(String[] args, Service service) {
        Gui.service = service;
        launch(args);
    }

    private String pickedCountry;
    private String pickedCity;
    private String pickedCurrency;
    private Label weatherContentLabel = new Label(service.getWeatherPretty("Warsaw"));
    private Label currencyRateLabel = new Label("Currency rate: USD");
    private Label currencyRateContentLabel = new Label("" + service.getRateFor("USD"));

    private Label currencyRateNbpLabel = new Label("Currency Nbp PLN rate");
    private Label currencyRateNbpContentLabel = new Label("" + service.getNBPRate());

    public void setUserInput(String pickedCountry, String pickedCity, String pickedCurrency) {
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

        Label weatherLabel = new Label("Weather");
        weatherLabel.setLabelFor(weatherContentLabel);


        Button btn = new Button();
        btn.setText("Change Data button");
        btn.setOnAction(event -> PromptWindow.showNewWindow(this, pickedCountry, pickedCity, pickedCurrency));

        hbox.getChildren().add(weatherLabel);
        hbox.getChildren().add(weatherContentLabel);

        hbox.getChildren().add(currencyRateLabel);
        hbox.getChildren().add(currencyRateContentLabel);

        hbox.getChildren().add(currencyRateNbpLabel);
        hbox.getChildren().add(currencyRateNbpContentLabel);
        hbox.getChildren().add(btn);


        StackPane root = new StackPane();
        root.getChildren().add(hbox);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private void updateContents() {
        service = new Service(pickedCountry);

        weatherContentLabel.setText(service.getWeatherPretty(pickedCity));
        currencyRateContentLabel.setText("" + service.getRateFor(pickedCurrency));
        currencyRateLabel.setText("Currency rate: " + pickedCurrency);

        currencyRateNbpContentLabel.setText("" + service.getNBPRate());
    }
}