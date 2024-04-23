package zad1.serwer.klient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui extends Application {

    public static void start(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("program na TPO 3");
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);

        VBox root = new VBox();

        root.getChildren().add(new Label("Podaj polskie slowo do przetlumaczenia: "));
        TextField polskieSlowo = new TextField();
        root.getChildren().add(polskieSlowo);
        root.getChildren().add(new Label("Na jaki jezyk chcesz przetlumaczyc slowo:"));
        TextField jezykField = new TextField();
        root.getChildren().add(jezykField);
        Button b = new Button("Wykonaj zapytanie");
        root.getChildren().add(b);
        Label wynikLabel = new Label();
        root.getChildren().add(wynikLabel);

        b.setOnAction(e -> {
            String slowo = polskieSlowo.getText().trim();
            String jezyk = jezykField.getText().trim();
            if (slowo.isEmpty() || jezyk.isEmpty()) {
                wynikLabel.setText("Slowo i jezyk nie moga byc puste, aby wykonac zapytanie");
            } else {
                ButtonClickAction buttonClickAction = new ButtonClickAction(wynikLabel, slowo, jezyk);
                buttonClickAction.run();
            }
        });

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}