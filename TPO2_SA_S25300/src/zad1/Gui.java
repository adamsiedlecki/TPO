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
    public static void start(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("program na TPO 2");
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Label weatherLabel = new Label("Weather");
        Label currencyRateLabel = new Label("Currency rate");


        Button btn = new Button();
        btn.setText("Change Data button");
        btn.setOnAction(event -> System.out.println("Hello World!"));

        hbox.getChildren().add(weatherLabel);
        hbox.getChildren().add(currencyRateLabel);
        hbox.getChildren().add(btn);


        StackPane root = new StackPane();
        root.getChildren().add(hbox);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}