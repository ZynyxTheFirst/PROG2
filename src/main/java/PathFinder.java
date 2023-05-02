// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 031
// Hanna Arrhenius haar9434
// Erik Strandberg erst1916
// Robin Westling rowe7856
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;


public class PathFinder extends Application{

    private static final String imageUrl = "Z:\\University\\Prog2\\Grupparbete\\PROG2\\src\\main\\europa.gif";
    public void start(Stage stage){

        ImageView imageView = new ImageView();

        //Drop down menu
        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");
        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");
        MenuItem newMapMI = new MenuItem("New Map");
        newMapMI.setOnAction(event -> {
            Image map = new Image(imageUrl);
            imageView.setFitHeight(map.getHeight());
            imageView.setFitWidth(map.getWidth());
            imageView.setPreserveRatio(true);
            imageView.setImage(map);
            stage.sizeToScene();
        });
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
        VBox borderPane = new VBox();
        HBox buttons = new HBox();

        buttons.getChildren().addAll(findPathButton, showConnectionButton, newPlaceButton, newConnectionButton, changeConnectionButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(5));
        buttons.setSpacing(10);

        borderPane.getChildren().addAll(menuBar, buttons, imageView);
        //Create scene
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("PathFinder");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
