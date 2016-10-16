package javafx2introbyexample.chapter4.recipe4_01;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Embedding JavaFX in a Web Page
 * @author cdea
 */
public class EmbeddingJavaFXInAWebPage extends Application {
    private Point2D anchorPt;
    private Point2D previousLocation;
    static int dx = 2;
    static int dy = -2;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Chapter 4-1 Embedding JavaFX in a Web Page");
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        final Group root = new Group();
        final Scene scene = new Scene(root, 540, 300, Color.rgb(0, 0, 0, 0));
        
        // rounded rectangle with slightly transparent        
        Node applicationArea = createBackground(scene);
        root.getChildren().add(applicationArea);

        // allow the user to drag window on the desktop
        attachMouseEvents(scene, primaryStage);

        // create a close button
        Node closeButton= createCloseButton(scene);
        root.getChildren().add(closeButton);
        
        primaryStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                previousLocation = new Point2D(primaryStage.getX(), primaryStage.getY());
            }
        });
        addBouncyBall(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }


    private Node createBackground(Scene scene) {
        // application area
        Rectangle applicationArea = RectangleBuilder.create()
                .arcWidth(20)
                .arcHeight(20)
                .fill(Color.rgb(0, 0, 0, .80))
                .x(0)
                .y(0)
                .strokeWidth(2)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();
        applicationArea.widthProperty().bind(scene.widthProperty());
        applicationArea.heightProperty().bind(scene.heightProperty());
        return applicationArea;
    }

    private void attachMouseEvents(Scene scene, final Stage primaryStage) {
        
        // starting initial anchor point
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                if (!primaryStage.isFullScreen()) {
                    anchorPt = new Point2D(event.getScreenX(), event.getScreenY());
                }
            }
        });
        
        // dragging the entire stage
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                if (anchorPt != null && previousLocation != null && !primaryStage.isFullScreen()) {
                    primaryStage.setX(previousLocation.getX() + event.getScreenX() - anchorPt.getX());
                    primaryStage.setY(previousLocation.getY() + event.getScreenY() - anchorPt.getY());                    
                }
            }
        });
        
        // set the current location
        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                if (!primaryStage.isFullScreen()) {
                    previousLocation = new Point2D(primaryStage.getX(), primaryStage.getY());
                }
            }
        });
    }

    private Node createCloseButton(Scene scene) {
        // close button
        final Group closeApp = new Group();
        Circle closeButton = CircleBuilder.create()
                .centerX(5)
                .centerY(0)
                .radius(7)
                .fill(Color.rgb(255, 255, 255, .80))
                .build();
        Text closeXmark = new Text(2, 4, "X");
        closeApp.translateXProperty().bind(scene.widthProperty().subtract(15));
        closeApp.setTranslateY(10);
        closeApp.getChildren().addAll(closeButton, closeXmark);
        closeApp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });
        return closeApp;
    }
    private void addBouncyBall(final Scene scene) {

        final Circle ball = new Circle(100, 100, 20);
        RadialGradient gradient1 = new RadialGradient(0,
                .1,
                100,
                100,
                20,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLACK));

        ball.setFill(gradient1);

        final Group root = (Group) scene.getRoot();
        root.getChildren().add(ball);

        Timeline tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame moveBall = new KeyFrame(Duration.seconds(.0200),
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event) {

                        double xMin = ball.getBoundsInParent().getMinX();
                        double yMin = ball.getBoundsInParent().getMinY();
                        double xMax = ball.getBoundsInParent().getMaxX();
                        double yMax = ball.getBoundsInParent().getMaxY();

                        // Collision - boundaries
                        if (xMin < 0 || xMax > scene.getWidth()) {
                            dx = dx * -1;
                        }
                        if (yMin < 0 || yMax > scene.getHeight()) {
                            dy = dy * -1;
                        }

                        ball.setTranslateX(ball.getTranslateX() + dx);
                        ball.setTranslateY(ball.getTranslateY() + dy);

                    }
                });

        tl.getKeyFrames().add(moveBall);
        tl.play();
    }
}