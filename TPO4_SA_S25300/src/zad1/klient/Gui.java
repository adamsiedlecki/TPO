package zad1.klient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Gui extends Application {

    private static DataState dataState;

    static VBox topicsRoot = new VBox();

    private  static TextArea wynikLabel;
    public static void start(String[] args, DataState dataState) {
        Gui.dataState = dataState;
        launch(args);

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
        Button aktualizacjaSubskrybowanychTopics = new Button("Zaktualizuj subskrybcje topics");
        root.getChildren().add(aktualizacjaSubskrybowanychTopics);

        root.getChildren().add(new Label("Newsy: "));
        root.getChildren().add(wynikLabel);

        aktualizacjaSubskrybowanychTopics.setOnAction(e -> {
            dataState.userPickedTopics.clear();
            topicsRoot.getChildren().forEach(n -> {
                if (((CheckBox)n).isSelected()) {
                    dataState.userPickedTopics.add(n.getId());
                }
            });

            dataState.clientWantsToUpdateTopics = true;
        });

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void updateDataState() {

        Platform.runLater(() -> {
            // do your GUI stuff here
            setTopicsCheckBoxes();
            setNews();
        });
    }

    private static void setTopicsCheckBoxes() {
        topicsRoot.getChildren().clear();
        for(String topic: dataState.allTopics) {
            CheckBox c = new CheckBox(topic);
            c.setId(topic);
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
            sb.append("\n");
        }
        wynikLabel.setText(sb.toString());
    }
}