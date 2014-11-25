package bowlpicker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Initializer extends Application {
    
    protected static final Game[] schedule = new Game[16];
    private final int sceneWidth = 400;
    protected static ArrayList<SpecButton> buttons = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        scheduler();
        launch(args);
    }
    
    private static void scheduler() throws IOException {
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader("teams.txt"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int counter = 0;
        String thisLine = new String();
        if (input != null) {
            thisLine = input.readLine();
        }
        while (thisLine != null) {
            String[] singleGame = thisLine.split(" ");
            schedule[counter] = new Game(new Team(singleGame[0]), new Team(singleGame[1]), 0);
            if (input != null) {
                thisLine = input.readLine();
            }
            counter++;
        }
        if (input != null) {
            input.close();
        }

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Make Your Bowl Picks");
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        for (int i = 0; i < schedule.length; i++) {
            Game curGame = schedule[i];
            SpecButton btn1 = new SpecButton(curGame.awayTeam.getName());
            SpecButton btn2 = new SpecButton(curGame.homeTeam.getName());
            btn1.setPrefSize(100, 10);
            btn2.setPrefSize(100, 10);
            btn1.setStyle("-fx-background-color: #FFFFFF;");
            btn2.setStyle("-fx-background-color: #FFFFFF;");
            btn1.setOnAction(new ButtonHandler<>(btn1));
            btn2.setOnAction(new ButtonHandler<>(btn2));
            buttons.add(btn1);
            buttons.add(btn2);
            HBox gameRow = new HBox();
            gameRow.setPrefSize(sceneWidth, 10);
            gameRow.setPadding(new Insets(5, 12, 5, 12));
            gameRow.setSpacing(sceneWidth - (2 * btn1.getPrefWidth()) - 24);
            gameRow.getChildren().addAll(btn1, btn2);
            pane.add(gameRow, 0, i + 1);
        }
        HBox submitRow = new HBox();
        SpecButton submit = new SpecButton("Submit");
        submit.setPrefWidth(80);
        submit.setOnAction(new SubmitHandler<>());
        submitRow.setPrefSize(sceneWidth, 10);
        submitRow.setPadding(new Insets(5, 12, 5, (sceneWidth - submit.getPrefWidth()) / 2));
        submitRow.getChildren().addAll(submit);
        pane.add(submitRow, 0, schedule.length + 1);
        primaryStage.setScene(new Scene(pane, sceneWidth, 800));
        primaryStage.show();
    }
}