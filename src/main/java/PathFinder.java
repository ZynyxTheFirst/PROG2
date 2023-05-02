import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class PathFinder extends Application{

    public void start(Stage stage){
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
        Scene scene = new Scene(menuBar, 800, 600);
        stage.setScene(scene);
        stage.setTitle("PathFinder");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
