// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 031
// Hanna Arrhenius haar9434
// Erik Strandberg erst1916
// Robin Westling rowe7856
import Main.ListGraph;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import org.w3c.dom.events.Event;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;


public class PathFinder extends Application{
    ListGraph<Location> listGraph = new ListGraph<>();
    private static final String imageUrl = "file:europa.gif";
    private static final String fileName = "europa.graph";
    boolean eventHandlerActivated = false;
    boolean mapIsLoaded = false;
    boolean changed = false;
    ImageView imageView;
    Image map;
    StackPane stackPane;
    Pane circlePane;

    public void start(Stage stage){

        imageView = new ImageView();
        map = new Image(imageUrl);

        //Create StackPane to hold the image and circles
        stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        circlePane = new Pane();
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
        openMI.setOnAction(event -> open());
        MenuItem saveMI = new MenuItem("Save");
        saveMI.setOnAction(new SaveHandler());
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

        String tempName = "name";
        stackPane.setOnMouseClicked(event -> {
            if (eventHandlerActivated) {
                Location location = new Location(tempName, event.getX(), event.getY());
                location.setFill(Color.RED);
                circlePane.getChildren().add(location);
                listGraph.add(location);
                location.toFront();
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

    String locationsListAsString = "";
    StringBuilder connectionsList = new StringBuilder();
    String[] locationsArray;

    private void open() {
        try {
            System.out.println("test");
            FileReader fileReader = new FileReader(fileName);
            BufferedReader lineReader = new BufferedReader(fileReader);
            lineReader.readLine(); // Skip the first line in europa.graph
            locationsListAsString = lineReader.readLine(); // Store the second line in locationsListAsString

            // Save all the lines in europa.graph after the second line in connectionsList
            connectionsList = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                connectionsList.append(line).append(";");
            }

            lineReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationsArray = locationsListAsString.split(";");

        for (int i = 0; i < locationsArray.length - 2; i += 3) {
            double x1 = Double.parseDouble(locationsArray[i + 1]);
            double y1 = Double.parseDouble(locationsArray[i + 2]);
            Location newLocation = new Location(locationsArray[i], x1, y1);
            newLocation.setOnMouseClicked(new ClickerHandler()); // Make the locations clickable
            newLocation.setFill(Color.RED);
            listGraph.add(newLocation);
            circlePane.getChildren().add(newLocation);
            newLocation.toFront();
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        String citiesString = "";
        String edgesString = "";
        @Override
        public void handle(ActionEvent actionEvent) {

            System.out.println("JOEL TIME");
            try {
                citiesString = "";
                edgesString = "";
                FileWriter writer = new FileWriter("europa.graph");
                PrintWriter printWriter = new PrintWriter(writer);
                boolean first = false;
                for (Location location : listGraph.getNodes()) {
                    if (!first) {
                        citiesString += location.toString();
                        first = true;
                    } else {
                        citiesString += ";" + location.toString();


                    }
                    for (var v : listGraph.getEdgesFrom(location)) {
                        edgesString += location.getName() + ";" + v.getDestination().getName() + ";" + v.getName() + ";" + v.getWeight() + "\n";
                    }
                }
                printWriter.println("file:europa.gif");
                printWriter.println(citiesString);
                printWriter.println(edgesString);
                System.out.println("Saving file");
                writer.close();
                printWriter.close();
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }
    Location selectedLocation1;
    Location selectedLocation2;
    class ClickerHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            // Skapar en temporär cirkel vilket blir cirklen man precis tryckt på.
            Location location = (Location) event.getSource();

            // Kollar om cirkelmarked1 är tom och att c (cirkeln man tryckt) på inte är lika med circlemarked2.
            // Tilldelar sedan c till cirkelmarked1.
            if (selectedLocation1 == null && !location.equals(selectedLocation2)) {
                selectedLocation1 = location;
                selectedLocation1.setFill(Color.RED); // RÖD


            }
            // Kollar om cirkelmarked2 är tom och att c (cirkeln man tryckt) på inte är lika med circlemarked1.
            // Tilldelar sedan c till cirkelmarked2.
            else if (selectedLocation2 == null && !location.equals(selectedLocation1)) {
                selectedLocation2 = location;
                selectedLocation2.setFill(Color.RED); // RÖD
            }
            // Kollar om c (cirkeln man tryckt) är lika med circleMarked1 och inte lika med circleMarked2 och
            // isåfall omarkerar circleMarked1 genom att göra den blå och sätta den till null.
            else if (location.equals(selectedLocation1) && !location.equals(selectedLocation2)) {
                location.setFill(Color.BLUE); // BLÅ
                selectedLocation1 = null;
            }
            // Kollar om c (cirkeln man tryckt) är lika med circleMarked2 och inte lika med circleMarked1 och
            // isåfall omarkerar circleMarked2 genom att göra den blå och sätta den till null.
            else if (location.equals(selectedLocation2) && !location.equals(selectedLocation1)) {
                location.setFill(Color.BLUE); // BLÅ
                selectedLocation2 = null;
            }
        }
    }

    static class Location extends Circle {

        private String name;

        public Location(String name, double centerX, double centerY) {
            super(centerX, centerY, 10);
            this.name = name;
            this.setFill(Color.RED);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString(){
            return name + ";" + getCenterX() + ";" + getCenterY();
        }
    }
    //Spara en snappshot tror 4.1.4
    //SnapShotParameters parameter = new SnapShotParameters();
    //WriteableImage image = center.snapshot(parameters, null);
    class SaveImageHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            try{
                WritableImage image = center.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            }catch(IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR,"IO-fel" + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    //4.1.5
    class ExitHandler implements EventHandler<WindowEvent>{
        @Override public void handle(WindowEvent event){
            if(changed){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, exit anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if(res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    event.consume();
            }
        }
    }
}
