package zad1.klient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Gui extends Application {

    private static DataState dataState;

    static VBox topicsRoot = new VBox();

    private  static TextArea wynikLabel;
    public static void start(String[] args, DataState dataState) {
        launch(args);
        Gui.dataState = dataState;

    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Klient na TPO 4");
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);
        wynikLabel = new TextArea();

        VBox root = new VBox();

        root.getChildren().add(new Label("Dostepne topics"));





        root.getChildren().add(topicsRoot);
        Button b = new Button("Zaktualizuj topics");
        root.getChildren().add(b);

        root.getChildren().add(new Label("Newsy: "));
        root.getChildren().add(wynikLabel);

        b.setOnAction(e -> {
            dataState.userPickedTopics = new HashSet<>();
            dataState.clientWantsToUpdateTopics = true;
        });

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void updateDataState() {
        setTopicsCheckBoxes();
        setNews();
    }

    private static void setTopicsCheckBoxes() {
        topicsRoot.getChildren().clear();
        List<CheckBox> topicsCheckBoxes = new ArrayList<>();
        for(String topic: dataState.allTopics) {
            CheckBox c = new CheckBox(topic);
            topicsRoot.getChildren().add(c);
            if (dataState.userPickedTopics.contains(topic)) {
                c.setSelected(true);
            }
        }
    }

    private static void setNews() {
        wynikLabel.clear();
        StringBuilder sb = new StringBuilder();

        Map<String, List<String>> newsOnTopics = dataState.newsOnTopics;

        for (Map.Entry<String, List<String>> entry :newsOnTopics.entrySet()) {
            sb.append("Temat: ").append(entry.getKey());
            sb.append("\n");
            entry.getValue().forEach(s -> {
                sb.append(s);
                sb.append("\n");
            });
            sb.append("----------");
        }
        wynikLabel.setText(sb.toString());
    }
}