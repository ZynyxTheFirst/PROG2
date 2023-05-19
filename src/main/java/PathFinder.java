// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 031
// Hanna Arrhenius haar9434
// Erik Strandberg erst1916
// Robin Westling rowe7856
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;

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
    String locationsListAsString = "";
    StringBuilder connectionsList = new StringBuilder();
    String[] locationsArray;
    String[] connectionsArray;
    Location selectedLocation1;
    Location selectedLocation2;
    boolean continueAction = false;

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
            checkForChanges(event);
            if(continueAction){
                newMap(stage);
                continueAction=false;
            }
        });
        MenuItem openMI = new MenuItem("Open");
        openMI.setOnAction(event -> {
            checkForChanges(event);
            if(continueAction){
                open();
                continueAction=false;
            }
        });
        MenuItem saveMI = new MenuItem("Save");
        saveMI.setOnAction(new SaveHandler());
        MenuItem saveImageMI = new MenuItem("Save Image");
        saveImageMI.setOnAction(new SaveImageHandler(stackPane));
        MenuItem exitMI = new MenuItem("Exit");
        exitMI.setOnAction(event -> {
            checkForChanges(event);
            if(continueAction){
                System.exit(0);
            }
        });
        fileMenu.getItems().addAll(newMapMI, openMI, saveMI, saveImageMI, exitMI);
        menuBar.getMenus().add(fileMenu);

        //Buttons
        Button findPathButton = new Button("Find Path");
        findPathButton.setOnAction(event -> findPath());
        Button showConnectionButton = new Button("Show Connection");
        showConnectionButton.setOnAction(event -> showConnection());
        Button newPlaceButton = new Button("New Place");

        Button newConnectionButton = new Button("New Connection");
        newConnectionButton.setOnAction(event -> connect());
        Button changeConnectionButton = new Button("Change Connection");
        changeConnectionButton.setOnAction(event -> changeConnection());
        VBox vBox = new VBox();
        HBox buttons = new HBox();

        ///New Place
        newPlaceButton.setOnAction(event -> {
            if(mapIsLoaded){
                eventHandlerActivated = true;
                stackPane.setCursor(Cursor.CROSSHAIR);
                newPlaceButton.setDisable(true);
            }
        });

        stage.setOnCloseRequest(event -> {
            checkForChanges(event);
            if(continueAction)
                System.exit(0);
        });

        stackPane.setOnMouseClicked(event -> {
            if (eventHandlerActivated) {
                changed = true;
                stackPane.setCursor(Cursor.DEFAULT);
                newPlaceButton.setDisable(false);

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("New Place");
                dialog.setHeaderText("Enter the name of the new place:");
                dialog.setContentText("Name:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    changed = true;
                    Location location = new Location(name, event.getX(), event.getY());
                    location.setOnMouseClicked(new ClickerHandler());
                    circlePane.getChildren().add(location);
                    listGraph.add(location);
                    location.toFront();
                });
                eventHandlerActivated = false;
           }
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
    private void open() {
        try {
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
            newLocation.setFill(Color.BLUE);
            listGraph.add(newLocation);
            circlePane.getChildren().add(newLocation);
            newLocation.toFront();
        }

        connectionsArray = connectionsList.toString().split(";");

        if (connectionsArray.length != 0) {
            // Ritar ut alla förbindelser
            for (int i = 0; i < connectionsArray.length - 3; i += 4) {
                Location destination1 = null;
                Location destination2 = null;
                // Letar igenom listgraphs map och hittar städerna via deras namn.
                for (Location city : listGraph.getNodes()) {
                    if (city.getName().equals(connectionsArray[i])) {
                        destination1 = city;
                    }
                    if (city.getName().equals(connectionsArray[i + 1])) {
                        destination2 = city;
                    }
                }

                if (destination1 != null && destination2 != null) {
                    if(listGraph.getEdgeBetween(destination1, destination2) == null){
                        int weight = Integer.parseInt(connectionsArray[i + 3]);
                        listGraph.connect(destination1, destination2, connectionsArray[i + 2], weight);

                        // Skapar en linje, ritar ut den mellan två stöder och sen lägger till den i europePane.
                        Line drawLine = new Line(destination1.getCenterX(), destination1.getCenterY(), destination2.getCenterX(), destination2.getCenterY());
                        circlePane.getChildren().add(drawLine);
                    }
                }
            }
        }
    }
    void newMap(Stage stage){
        imageView.setFitHeight(map.getHeight());
        imageView.setFitWidth(map.getWidth());
        imageView.setPreserveRatio(true);
        imageView.setImage(map);

        stage.sizeToScene();

        stackPane.setMinWidth(map.getWidth());
        stackPane.setMinHeight(map.getHeight());

        circlePane.getChildren().clear(); // Clear any existing circles

        // Set the size of the StackPane and all its children to the size of the image
        stackPane.setPrefSize(map.getWidth(), map.getHeight());
        stackPane.setMaxSize(map.getWidth(), map.getHeight());
        stackPane.setMinSize(map.getWidth(), map.getHeight());
        mapIsLoaded = true;
    }
    void connect(){
        if (selectedLocation1 == null || selectedLocation2 == null){
            showAlert("Two places must be selected!");
            return;
        }
        if (listGraph.getEdgeBetween(selectedLocation1, selectedLocation2) != null){
            showAlert("A connection already exists between the selected places!");
            return;
        }
        //Connect
        ConnectionAlert connectionAlert = new ConnectionAlert();
        connectionAlert.showAndWait().ifPresent(buttonType -> {
            if(buttonType == ButtonType.OK){
                listGraph.connect(selectedLocation1, selectedLocation2, connectionAlert.getName(), connectionAlert.getTime());
                Line drawLine = new Line(selectedLocation1.getCenterX(), selectedLocation1.getCenterY(), selectedLocation2.getCenterX(), selectedLocation2.getCenterY());
                circlePane.getChildren().add(drawLine);
            }
        });
    }
    void changeConnection(){
        if (selectedLocation1 == null || selectedLocation2 == null){
            showAlert("Two places must be selected!");
            return;
        }
        if(listGraph.getEdgeBetween(selectedLocation1, selectedLocation2) == null){
            showAlert("No connection found!");
            return;
        }
        ConnectionAlert connectionAlert = new ConnectionAlert(listGraph.getEdgeBetween(selectedLocation1, selectedLocation2), false);
        connectionAlert.showAndWait().ifPresent(buttonType -> {
            if(buttonType == ButtonType.OK) listGraph.setConnectionWeight(selectedLocation1, selectedLocation2, connectionAlert.getTime());
        });
    }
    void showConnection(){
        if (selectedLocation1 == null || selectedLocation2 == null){
            showAlert("Two places must be selected!");
            return;
        }
        if(listGraph.getEdgeBetween(selectedLocation1, selectedLocation2) == null){
            showAlert("No connection found!");
            return;
        }
        ConnectionAlert connectionAlert = new ConnectionAlert(listGraph.getEdgeBetween(selectedLocation1, selectedLocation2), true);
        connectionAlert.showAndWait();
    }
    void findPath(){
        if(listGraph.getPath(selectedLocation1, selectedLocation1) == null){
            showAlert("No path found!");
            return;
        }
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        int count = 0;
        for(Edge<Location> e : listGraph.getPath(selectedLocation1, selectedLocation2)){
            stringBuilder.append(e.toString()).append("\n");
            count += e.getWeight();
        }
        stringBuilder.append(count);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("The path from " + selectedLocation1 + " to " + selectedLocation2 + ":");
        alert.setContentText(stringBuilder.toString());
        alert.showAndWait();
    }
    void showAlert(String message) {
        Alert msgBox = new Alert(Alert.AlertType.ERROR);
        msgBox.setTitle("Error!");
        msgBox.setHeaderText(null);
        msgBox.setContentText(message);
        msgBox.showAndWait();
    }
    static class ConnectionAlert extends Alert{
        private final TextField nameField = new TextField();
        private final TextField timeField = new TextField();
        GridPane gridPane;
        public ConnectionAlert(){
            super(AlertType.CONFIRMATION);
            setUpGrid();
            getDialogPane().setContent(gridPane);
        }
        public ConnectionAlert(Edge<Location> edge, boolean displayOnly){
            super(AlertType.CONFIRMATION);
            setUpGrid();
            nameField.setEditable(false);
            nameField.setText(edge.getName());
            if(displayOnly){
                timeField.setEditable(false);
                timeField.setText(String.valueOf(edge.getWeight()));
            }else {
                timeField.setEditable(true);
            }
            getDialogPane().setContent(gridPane);
        }
        private void setUpGrid(){
            gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setPadding(new Insets(10));
            gridPane.setHgap(5);
            gridPane.setVgap(10);
            gridPane.addRow(0, new Label("Name:"), nameField);
            gridPane.addRow(1, new Label("Time:"), timeField);
        }
        public String getName(){
            return nameField.getText();
        }
        public int getTime(){
            return Integer.parseInt(timeField.getText());
        }
    }
    class SaveHandler implements EventHandler<ActionEvent> {
        String citiesString = "";
        String edgesString = "";
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                citiesString = "";
                edgesString = "";
                FileWriter writer = new FileWriter("europa.graph");
                PrintWriter printWriter = new PrintWriter(writer);
                boolean first = false;
                for (Location location : listGraph.getNodes()) {
                    if (!first) {
                        citiesString += location.saveInformation();
                        first = true;
                    } else {
                        citiesString += ";" + location.saveInformation();
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
            this.setFill(Color.BLUE);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString(){
            return name;
        }
        public String saveInformation(){
            return name + ";" + getCenterX() + ";" + getCenterY();
        }
    }
    //Spara en snapshot tror 4.1.4
    //SnapShotParameters parameter = new SnapShotParameters();
    //WriteableImage image = center.snapshot(parameters, null);
    static class SaveImageHandler implements EventHandler<ActionEvent> {
        private final StackPane stackPane;

        public SaveImageHandler(StackPane stackPane) {
            this.stackPane = stackPane;
        }

        public void handle(ActionEvent event) {
            try {
                WritableImage image = stackPane.snapshot(new SnapshotParameters(), null);
                File file = new File("capture.png");
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("IO-fel: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    //4.1.5
    void checkForChanges(Event event){
        if(changed){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Unsaved changes, continue anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.CANCEL){
                event.consume();
            }else{
                continueAction = true;
            }
        }else{continueAction = true;}
    }
}
