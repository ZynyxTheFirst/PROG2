// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 031
// Hanna Arrhenius haar9434
// Erik Strandberg erst1916
// Robin Westling rowe7856
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class PathFinder extends Application{

    private static final String imageUrl = "file:europa.gif";
    boolean eventHandlerActivated = false;
    boolean mapIsLoaded = false;
    public void start(Stage stage){

        ImageView imageView = new ImageView();
        Image map = new Image(imageUrl);

        //Create StackPane to hold the image and circles
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        Pane circlePane = new Pane();
        stackPane.getChildren().add(circlePane);

        //Drop down menu
        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");
        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");
        MenuItem newMapMI = new MenuItem("New Map");
        newMapMI.setOnAction(event -> {
            imageView.setFitHeight(map.getHeight());
            imageView.setFitWidth(map.getWidth());
            imageView.setPreserveRatio(true);
            imageView.setImage(map);

            stackPane.setMinWidth(map.getWidth());
            stackPane.setMinHeight(map.getHeight());

            circlePane.getChildren().clear(); // Clear any existing circles

            stage.sizeToScene();

            // Set the size of the StackPane and all its children to the size of the image
            stackPane.setPrefSize(map.getWidth(), map.getHeight());
            stackPane.setMaxSize(map.getWidth(), map.getHeight());
            stackPane.setMinSize(map.getWidth(), map.getHeight());
            mapIsLoaded = true;
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

        newPlaceButton.setOnAction(event -> {
            if(mapIsLoaded){
                eventHandlerActivated = true;
            }
        });
        Button newConnectionButton = new Button("New Connection");
        Button changeConnectionButton = new Button("Change Connection");
        VBox vBox = new VBox();
        HBox buttons = new HBox();

        stackPane.setOnMouseClicked(event -> {
            if (eventHandlerActivated) {
                Circle circle = new Circle(event.getX(), event.getY(), 10);
                circle.setFill(Color.RED);
                circlePane.getChildren().add(circle);
                circle.toFront();
                eventHandlerActivated = false;
           };
        });

        buttons.getChildren().addAll(findPathButton, showConnectionButton, newPlaceButton, newConnectionButton, changeConnectionButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(5));
        buttons.setSpacing(10);

        vBox.getChildren().addAll(menuBar, buttons, stackPane);
        //Create scene
        Scene scene = new Scene(new Group(vBox), Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("PathFinder");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    class Place extends Circle {

        private String name;
        private Color color;

        public Place(double centerX, double centerY, double radius, Color color, String name) {
            super(centerX, centerY, radius);
            this.color = color;
            this.name = name;
            this.setFill(color);
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setColor(Color color) {
            this.color = color;
            this.setFill(color);
        }
    }
}
