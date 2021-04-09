package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        GUI();
    }

    public void GUI(){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("GUIlogin.fxml"));
            AnchorPane pane = loader.load();

            GUIController guiController = loader.getController();

            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);
            primaryStage.show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
