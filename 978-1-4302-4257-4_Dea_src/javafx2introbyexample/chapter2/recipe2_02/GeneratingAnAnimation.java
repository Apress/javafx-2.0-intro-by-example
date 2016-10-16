package javafx2introbyexample.chapter2.recipe2_02;

import java.io.File;
import java.util.ArrayList;

import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcBuilder;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Generating an Animation
 * @author cdea
 */
public class GeneratingAnAnimation extends Application {

    List<String> imagesFiles = new ArrayList<>();
    int currentIndexImageFile = -1;
    public static int NEXT = 1;
    public static int PREV = -1;
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chapter 2-2 Generating an Animation");
        Group root = new Group();
        final Scene scene = new Scene(root, 300, 250, Color.BLACK);

        // image view
        final ImageView currentImageView = new ImageView();
        // maintain aspect ratio
        currentImageView.setPreserveRatio(true);

        // resize based on the scene
        currentImageView.fitWidthProperty().bind(scene.widthProperty());

        final HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(currentImageView);
        root.getChildren().add(pictureRegion);

        // Dragging over surface
        scene.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });

        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    for (File file : db.getFiles()) {
                        String filePath = file.getAbsolutePath();
                        Image imageimage = new Image(filePath);
                        currentImageView.setImage(imageimage);
                       
                        currentIndexImageFile += 1;
                        imagesFiles.add(currentIndexImageFile, filePath);
                        System.out.println("Dropfile: " + filePath);
                        System.out.println("currentIndexImageFile: " + currentIndexImageFile);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        // create slide controls
        final Group buttonGroup = new Group();

        // rounded rect
        Rectangle buttonRect = RectangleBuilder.create()
                .arcWidth(15)
                .arcHeight(20)
                .fill(new Color(0, 0, 0, .55))
                .x(0)
                .y(0)
                .width(60)
                .height(30)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();

        buttonGroup.getChildren().add(buttonRect);
        // previous button
        Arc prevButton = ArcBuilder.create()
                .type(ArcType.ROUND)
                .centerX(12)
                .centerY(16)
                .radiusX(15)
                .radiusY(15)
                .startAngle(-30)
                .length(60)
                .fill(new Color(1, 1, 1, .90))
                .build();
        
        prevButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent me) {
                int indx = gotoImageIndex(PREV);
                if (indx > -1) {
                    String namePict = imagesFiles.get(indx);
                    final Image nextImage = new Image(new File(namePict).getAbsolutePath());
                    SequentialTransition seqTransition = transitionByFading(nextImage, currentImageView);
                    seqTransition.play();
                }
            }
        });

        buttonGroup.getChildren().add(prevButton);

        // next button
        Arc nextButton = ArcBuilder.create()
                .type(ArcType.ROUND)
                .centerX(12)
                .centerY(16)
                .radiusX(15)
                .radiusY(15)
                .startAngle(180 - 30)
                .length(60)
                .fill(new Color(1, 1, 1, .90))
                .translateX(40).build();
        buttonGroup.getChildren().add(nextButton);

        nextButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent me) {
                int indx = gotoImageIndex(NEXT);
                if (indx > -1) {
                    String namePict = imagesFiles.get(indx);
                    final Image nextImage = new Image(new File(namePict).getAbsolutePath());
                    SequentialTransition seqTransition = transitionByFading(nextImage, currentImageView);
                    seqTransition.play();

                }
            }
        });

        // move button group when scene is resized
        buttonGroup.translateXProperty().bind(scene.widthProperty().subtract(buttonRect.getWidth() + 6));
        buttonGroup.translateYProperty().bind(scene.heightProperty().subtract(buttonRect.getHeight() + 6));
        root.getChildren().add(buttonGroup);

        // Fade in button controls
        scene.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                FadeTransition fadeButtons = new FadeTransition(Duration.millis(500), buttonGroup);
                fadeButtons.setFromValue(0.0);
                fadeButtons.setToValue(1.0);
                fadeButtons.play();
            }
        });
        
        // Fade out button controls
        scene.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                FadeTransition fadeButtons = new FadeTransition(Duration.millis(500), buttonGroup);
                fadeButtons.setFromValue(1);
                fadeButtons.setToValue(0);
                fadeButtons.play();
            }
        });

        // create ticker area
        final Group tickerArea = new Group();
        final Rectangle tickerRect = RectangleBuilder.create()
                .arcWidth(15)
                .arcHeight(20)
                .fill(new Color(0, 0, 0, .55))
                .x(0)
                .y(0)
                .width(scene.getWidth() - 6)
                .height(30)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();
        
        
        Rectangle clipRegion = RectangleBuilder.create()
                .arcWidth(15)
                .arcHeight(20)
                .x(0)
                .y(0)
                .width(scene.getWidth() - 6)
                .height(30)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();
        
        tickerArea.setClip(clipRegion);
        
        // Resize the ticker area when the window is resized
        tickerArea.setTranslateX(6);
        tickerArea.translateYProperty().bind(scene.heightProperty().subtract(tickerRect.getHeight() + 6));
        tickerRect.widthProperty().bind(scene.widthProperty().subtract(buttonRect.getWidth() + 16));
        clipRegion.widthProperty().bind(scene.widthProperty().subtract(buttonRect.getWidth() + 16));
        tickerArea.getChildren().add(tickerRect);
        
        
        root.getChildren().add(tickerArea);
        
        
        
        // add news text
        Text news = TextBuilder.create()
                .text("JavaFX 2.0 News! | 85 and sunny | :)")
                .translateY(18)
                .fill(Color.WHITE)
                .build();
        tickerArea.getChildren().add(news);
        
        final TranslateTransition ticker = TranslateTransitionBuilder.create()
                .node(news)
                .duration(Duration.millis((scene.getWidth()/300) * 15000))
                .fromX(scene.widthProperty().doubleValue())
                .toX(-scene.widthProperty().doubleValue())
                .fromY(19)
                .interpolator(Interpolator.LINEAR)
                .cycleCount(1)
                .build();
       
        // when ticker has finished reset and replay ticker animation
        ticker.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae){
                ticker.stop();
                ticker.setFromX(scene.getWidth());
                ticker.setDuration(new Duration((scene.getWidth()/300) * 15000));
                ticker.playFromStart();
            }
        });
        
        ticker.play();
        
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public int gotoImageIndex(int direction) {
        int size = imagesFiles.size();
        if (size == 0) {
            currentIndexImageFile = -1;
        } else if (direction == NEXT && size > 1 && currentIndexImageFile < size - 1) {
            currentIndexImageFile += 1;
        } else if (direction == PREV && size > 1 && currentIndexImageFile > 0) {
            currentIndexImageFile -= 1;
        }

        return currentIndexImageFile;
    }
    public SequentialTransition transitionByFading(final Image nextImage, final ImageView imageView) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                imageView.setImage(nextImage);
            }
        });
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), imageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        SequentialTransition seqTransition = SequentialTransitionBuilder.create().children(fadeOut, fadeIn).build();
        return seqTransition;
    }
}