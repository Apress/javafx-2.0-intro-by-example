package javafx2introbyexample.chapter1.recipe1_15;

import javafx.application.Application;
import javafx.scene.effect.InnerShadowBuilder;
import javafx.scene.text.TextBuilder;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Organizing UI with Split Views
 * @author cdea
 */
public class OrganizingUIWithSplitViews extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(OrganizingUIWithSplitViews.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chapter 1-15 Organizing UI with Split Views");
        Group root = new Group();
        Scene scene = new Scene(root, 350, 250, Color.WHITE);
        
        // Left and right split pane
        SplitPane splitPane = new SplitPane();
        splitPane.prefWidthProperty().bind(scene.widthProperty());
        splitPane.prefHeightProperty().bind(scene.heightProperty());

        //List<Node> items = splitPane.getItems();
        VBox leftArea = new VBox(10);
        
        for (int i = 0; i < 5; i++) {
            HBox rowBox = new HBox(20);
            final Text leftText = TextBuilder.create()
                .text("Left " + i)
                .translateX(20)
                .fill(Color.BLUE)
                .font(Font.font(null, FontWeight.BOLD, 20))
                .build();

            rowBox.getChildren().add(leftText);
            leftArea.getChildren().add(rowBox);
        }
        leftArea.setAlignment(Pos.CENTER);
        
        // Upper and lower split pane
        SplitPane splitPane2 = new SplitPane();
        splitPane2.setOrientation(Orientation.VERTICAL);
        splitPane2.prefWidthProperty().bind(scene.widthProperty());
        splitPane2.prefHeightProperty().bind(scene.heightProperty());

        HBox centerArea = new HBox();
 
        InnerShadow iShadow = InnerShadowBuilder.create()
            .offsetX(3.5f)
            .offsetY(3.5f)
            .build();
        final Text upperRight = TextBuilder.create()
            .text("Upper Right")
            .x(100)
            .y(50)
            .effect(iShadow)
            .fill(Color.LIME)
            .font(Font.font(null, FontWeight.BOLD, 35))
            .translateY(50)
            .build();
        centerArea.getChildren().add(upperRight);

        HBox rightArea = new HBox();
        
        final Text lowerRight = TextBuilder.create()
            .text("Lower Right")
            .x(100)
            .y(50)
            .effect(iShadow)
            .fill(Color.RED)
            .font(Font.font(null, FontWeight.BOLD, 35))
            .translateY(50)
            .build();
        rightArea.getChildren().add(lowerRight);

        splitPane2.getItems().add(centerArea);
        splitPane2.getItems().add(rightArea);

        // add left area
        splitPane.getItems().add(leftArea);

        // add right area
        splitPane.getItems().add(splitPane2);

        // evenly position divider
        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();
        for (int i = 0; i < dividers.size(); i++) {
            dividers.get(i).setPosition((i + 1.0) / 3);
        }

        HBox hbox = new HBox();
        hbox.getChildren().add(splitPane);
        root.getChildren().add(hbox);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

  
}
