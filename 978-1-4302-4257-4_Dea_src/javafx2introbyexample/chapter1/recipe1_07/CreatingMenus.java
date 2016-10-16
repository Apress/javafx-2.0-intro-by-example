package javafx2introbyexample.chapter1.recipe1_07;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Creating Menus
 * @author cdea
 */
public class CreatingMenus extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chapter 1-7 Creating Menus");
        Group root = new Group();
        Scene scene = new Scene(root, 300, 250, Color.WHITE);
        
        MenuBar menuBar = new MenuBar();
        
        // File menu - new, save, exit
        Menu menu = new Menu("File");
        menu.getItems().add(new MenuItem("New"));
        menu.getItems().add(new MenuItem("Save"));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(new MenuItem("Exit"));
        
        menuBar.getMenus().add(menu);
        
        // Cameras menu - camera 1, camera 2
        Menu tools = new Menu("Cameras");
        tools.getItems().add(CheckMenuItemBuilder.create()
                .text("Show Camera 1")
                .selected(true)
                .build());
        
        tools.getItems().add(CheckMenuItemBuilder.create()
                .text("Show Camera 2")
                .selected(true)
                .build());
        menuBar.getMenus().add(tools);
  
        
        // Alarm
        Menu alarm = new Menu("Alarm");
        ToggleGroup tGroup = new ToggleGroup();
        RadioMenuItem soundAlarmItem = RadioMenuItemBuilder.create()
                .toggleGroup(tGroup)
                .text("Sound Alarm")
                .build();
        RadioMenuItem stopAlarmItem = RadioMenuItemBuilder.create()
                .toggleGroup(tGroup)
                .text("Alarm Off")
                .selected(true)
                .build();
        
        alarm.getItems().add(soundAlarmItem);
        alarm.getItems().add(stopAlarmItem);
        
        Menu contingencyPlans = new Menu("Contingent Plans");
        contingencyPlans.getItems().add(new CheckMenuItem("Self Destruct in T minus 50"));
        contingencyPlans.getItems().add(new CheckMenuItem("Turn off the coffee machine "));
        contingencyPlans.getItems().add(new CheckMenuItem("Run for your lives! "));
        
        alarm.getItems().add(contingencyPlans);
        menuBar.getMenus().add(alarm);
        
        
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        
        root.getChildren().add(menuBar); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
