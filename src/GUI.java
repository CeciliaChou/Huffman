import com.sun.javaws.progress.Progress;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.*;
import java.util.concurrent.TimeUnit;

import static com.sun.org.apache.xerces.internal.utils.SecuritySupport.getResourceAsStream;

public class GUI extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Label title = new Label("MyZip");
        title.setId("title");
        TextField field = new TextField();
        field.setId("field");
        Button btFileChoose = new Button("Choose File");
        Button btDirChoose = new Button("Choose A Directory");
        Button btCompress = new Button("Compress");
        Button btDepress = new Button("Depress");
        TextField process = new TextField();
        process.setId("process");


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 50, 10, 100));
        grid.setHgap(8);
        grid.setVgap(30);
        grid.add(title, 1, 0);
        grid.add(field, 1, 1, 13, 2);
        grid.add(btFileChoose, 15, 1);
        grid.add(btDirChoose, 15, 2);
        grid.add(btCompress, 1, 4);
        grid.add(btDepress, 9, 4);
        grid.add(process, 4, 5, 14, 1);


        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 20, 20));
        borderPane.setId("borderPane");

        Label lalHelp = new Label("Help");
        lalHelp.setTextFill(Color.WHITE);
        lalHelp.setFont(Font.font("Jokerman", 20));
        lalHelp.setOnMouseClicked(e -> {
            Label help = new Label(
                    "This is a simple zip app use huffman tree,\n " +
                            "you can start it following the tips.\n" + "\n" +
                            "1. If you want to compress a file, \n" +
                            "click CHOOSE A FILE and compress.\n" + "\n" +
                            "2. If you want to compress a directory, \n" +
                            "click CHOOSE A DIRECTORY and compress.\n" +
                            "3. If you want to depress,\n " +
                            "click CHOOSE A FILE and depress.\n" + "\n" +
                            "p.s Because the codes is very poor,\n" +
                            "it may cause you very long time to compress\n " +
                            "or depress,please wait until MyZip remind \n" +
                            "you the process is finished\n");
            help.setTextFill(Color.WHITE);
            help.setFont(Font.font("Raleway", 20));
            help.setAlignment(Pos.BASELINE_CENTER);
            Pane helpPane = new Pane();
            helpPane.setStyle("-fx-background-image: url(back.jpeg);");
            helpPane.getChildren().add(help);
            Scene scene = new Scene(helpPane, 500, 500);
            Stage stage = new Stage();
            stage.setTitle("HELP");
            stage.setScene(scene);
            stage.show();
        });

        Label lalAuthor = new Label("Authority");
        lalAuthor.setTextFill(Color.WHITE);
        lalAuthor.setFont(Font.font("Jokerman", 20));
        lalAuthor.setOnMouseClicked(e -> {
            Label author = new Label(
                    "This is a simple zip app use huffman tree,\n \n" +
                            "it is created by Xue Zhou.\n\n" +
                            "Anyone may not be copied, otherwise he \n\n" +
                            "or she  will bare legal sanctions");
            author.setTextFill(Color.WHITE);
            author.setFont(Font.font("Raleway", 20));
            author.setAlignment(Pos.BASELINE_CENTER);
            Pane helpPane = new Pane();
            helpPane.setStyle("-fx-background-image: url(back.jpeg);");
            helpPane.getChildren().add(author);
            Scene scene = new Scene(helpPane, 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Authority");
            stage.setScene(scene);
            stage.show();
        });

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(lalHelp, lalAuthor);
        borderPane.setTop(hBox);
        borderPane.setCenter(grid);

        btFileChoose.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            try {
                field.setText(file.getPath());
            } catch (NullPointerException e) {
                process.setText("Error Path");
            }
        });

        btDirChoose.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                field.setText(dir.getAbsolutePath());
            } else {
                field.setText(null);
            }
        });

        btCompress.setOnAction(e -> {
            String path = field.getText();
            try {
                process.setText("");
                Compress file = new Compress();
                long time = file.getTime();
                file.compress(path);
                time = file.getTime() - time;
                process.setText("Finish Compress in " + time + "ms");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btDepress.setOnAction(e -> {
            process.setText("");
            Uncompress file = new Uncompress();
            long time = file.getTime();
            String path = field.getText();
            file.uncompress(path);
            time = file.getTime() - time;
            process.setText("Finish Depress in " + time + "ms");
        });


        Scene scene = new Scene(borderPane, 700, 450);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Compress");
        primaryStage.show();
    }

}