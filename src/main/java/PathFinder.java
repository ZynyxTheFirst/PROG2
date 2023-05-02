// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 031
// Hanna Arrhenius haar9434
// Erik Strandberg erst1916
// Robin Westling rowe7856
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PathFinder extends Application{


    public void start(Stage stage){

        //Drop down menu
        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");
        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");
        MenuItem newMapMI = new MenuItem("New Map");
        MenuItem openMI = new MenuItem("Open");
        MenuItem saveMI = new MenuItem("Save");
        MenuItem saveImageMI = new MenuItem("Save Image");
        MenuItem exitMI = new MenuItem("Exit");
        fileMenu.getItems().addAll(newMapMI, openMI, saveMI, saveImageMI, exitMI);
        menuBar.getMenus().add(fileMenu);

        //Buttons
        Button findPathButton = new Button("Find Path");
        Button showConnectionButton = new Button("Show Connection");
        Button newPlaceButton = new Button("New Place");
        Button newConnectionButton = new Button("New Connection");
        Button changeConnectionButton = new Button("Change Connection");
        BorderPane borderPane = new BorderPane();
        FlowPane buttons = new FlowPane();
        buttons.getChildren().addAll(findPathButton, showConnectionButton, newPlaceButton, newConnectionButton, changeConnectionButton);
        borderPane.setTop(menuBar);
        borderPane.getChildren().add(buttons);

        //Create scene
        Scene scene = new Scene(borderPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("PathFinder");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}