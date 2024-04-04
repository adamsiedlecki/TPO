package zad1;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PromptWindow {

    public static void showNewWindow(Gui parent, String country, String city, String currency) {
        Stage newStage = new Stage();

        Label countrylabel = new Label("Country");
        TextField countryField = new TextField(country);
        countrylabel.setLabelFor(countryField);

        Label citylabel = new Label("City");
        TextField cityField = new TextField(city);
        citylabel.setLabelFor(cityField);


        Label currencyLabel = new Label("Currency");
        TextField currencyField = new TextField(currency);
        currencyLabel.setLabelFor(currencyField);

        Button button = new Button("Zatwierdź");
        button.setOnAction(event -> {
            String text1 = countryField.getText();
            String text2 = cityField.getText();
            String text3 = currencyField.getText();

            System.out.println("Country picked: " + text1);

            parent.setUserInput(text1,text2, text3);
            newStage.close();
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(countrylabel, 0, 0);
        gridPane.add(countryField, 1, 0);

        gridPane.add(citylabel, 0, 1);
        gridPane.add(cityField, 1, 1);

        gridPane.add(currencyLabel, 0, 2);
        gridPane.add(currencyField, 1, 2);

        gridPane.add(button, 0, 3);
        newStage.setScene(new Scene(gridPane, 300, 200));
        newStage.setTitle("Please insert data");
        newStage.show();
    }
}
