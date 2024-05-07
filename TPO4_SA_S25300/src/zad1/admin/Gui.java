package zad1.admin;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Stream;

public class Gui extends Application {

    private static AdminDataState adminDataState;

    public static void start(String[] args, AdminDataState adminDataState) {
        Gui.adminDataState = adminDataState;
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ADMIN na TPO 4");
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);

        VBox root = new VBox();

        root.getChildren().add(new Label("Wpisz topics rozdzielone przecinkiem:"));
        TextField topicsTextField = new TextField();
        root.getChildren().add(topicsTextField);
        Button sendTopicsToServer = new Button("Zaktualizuj topics (na serwerze i w menu)");
        root.getChildren().add(sendTopicsToServer);

        VBox newsyRoot = new VBox();
        sendTopicsToServer.setOnAction(e -> {
            String[] topics = topicsTextField.getText().split(",");
            AdminLogger.log("admin chce zaktualizowac topics: " + Arrays.toString(topics));
            Iterator<Node> iterator = newsyRoot.getChildren().iterator();
            while(iterator.hasNext()) {
                Node next = iterator.next();
                if(Stream.of(topics).noneMatch(t -> t.equals(next.getId()))) {
                    iterator.remove();
                }
            }
            for(String topic: topics) {
                boolean noneMatch = newsyRoot.getChildren().stream().noneMatch(n -> n.getId().equals(topic));
                if (noneMatch) {
                    TextField tf = new TextField();
                    tf.setId(topic);
                    newsyRoot.getChildren().add(tf);
                }
            }
            adminDataState.adminWantsToUpdateTopics = true;
            adminDataState.allTopics.clear();
            adminDataState.allTopics.addAll(Arrays.asList(topics));
        });

        root.getChildren().add(new Label("Newsy: (rozdzielone przecinkiem)"));


        root.getChildren().add(newsyRoot);

        Button sendNewsToServer = new Button("Zaktualizuj newsy (na serwerze)");

        sendNewsToServer.setOnAction(e -> {
            Map<String, List<String>> newsOnTopicsFromFields = new HashMap<>();
            newsyRoot.getChildren().forEach(n-> {
                String[] news = ((TextField)n).getText().split(",");
                newsOnTopicsFromFields.put(n.getId(), Arrays.asList(news));
            });
            Map<String, List<String>> newsOnTopicsFromDataState = adminDataState.newsOnTopics;
            adminDataState.newsChanged.clear();
            for(Map.Entry<String, List<String>> entry :newsOnTopicsFromFields.entrySet()) {
                //if (newsOnTopicsFromDataState.get(entry.getKey()) == null || !newsOnTopicsFromDataState.get(entry.getKey()).equals(entry.getValue())) {
                    adminDataState.newsChanged.add(entry.getKey());
                //}
            }
            adminDataState.newsOnTopics = newsOnTopicsFromFields;
        });
        root.getChildren().add(sendNewsToServer);


        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

}