package javafx2introbyexample.chapter3.recipe3_01;

import java.io.File;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayerBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcBuilder;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Playing Audio
 * @author cdea
 */
public class PlayingAudio extends Application {
    private MediaPlayer mediaPlayer;
    private Point2D anchorPt;
    private Point2D previousLocation;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Chapter 3-1 Playing Audio");
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        Group root = new Group();
        Scene scene = new Scene(root, 551, 270, Color.rgb(0, 0, 0, 0));
        
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
        root.getChildren().add(applicationArea);
        applicationArea.widthProperty().bind(scene.widthProperty());
        applicationArea.heightProperty().bind(scene.heightProperty());
        
        final Group phaseNodes = new Group();        
        root.getChildren().add(phaseNodes);
        

        // starting initial anchor point
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                anchorPt = new Point2D(event.getScreenX(), event.getScreenY());
            }
        });
        
        // dragging the entire stage
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                if (anchorPt != null && previousLocation != null) {
                    primaryStage.setX(previousLocation.getX() + event.getScreenX() - anchorPt.getX());
                    primaryStage.setY(previousLocation.getY() + event.getScreenY() - anchorPt.getY());                    
                }
            }
        });
        
        // set the current location
        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                previousLocation = new Point2D(primaryStage.getX(), primaryStage.getY());
            }
        });
        
        
                
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
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        System.out.println(filePath);
                    }
                    // play file
                    Media media = new Media(new File(filePath).toURI().toString());

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                    
                    mediaPlayer = MediaPlayerBuilder.create()
                            .media(media)
                            .audioSpectrumListener(new AudioSpectrumListener() {
                        @Override
                        public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                            phaseNodes.getChildren().clear();
                            int i = 0;
                            int x = 10;
                            int y = 150;
                            final Random rand = new Random(System.currentTimeMillis());
                            for(float phase:phases) {
                                int red = rand.nextInt(255);
                                int green = rand.nextInt(255);
                                int blue = rand.nextInt(255);
                                
                                Circle circle = new Circle(10);
                                circle.setCenterX(x + i);
                                circle.setCenterY(y + (phase * 100));
                                circle.setFill(Color.rgb(red, green, blue, .70));
                                phaseNodes.getChildren().add(circle);
                                i+=5;
                            }
                        }
                    })
                    .build();
                    
                    mediaPlayer.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.play();
                        }
                    }); 
                }
                
                event.setDropCompleted(success);
                event.consume();
            } // end of handle() method
        }); // end of setOnDragDropped

        
        // create slide controls
        final Group buttonGroup = new Group();
        
        // rounded rect
        Rectangle buttonArea = RectangleBuilder.create()
                .arcWidth(15)
                .arcHeight(20)
                .fill(new Color(0, 0, 0, .55))
                .x(0)
                .y(0)
                .width(60)
                .height(30)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();
        
        buttonGroup.getChildren().add(buttonArea);
        // stop audio control
        Node stopButton = RectangleBuilder.create()
                .arcWidth(5)
                .arcHeight(5)
                .fill(Color.rgb(255, 255, 255, .80))
                .x(0)
                .y(0)
                .width(10)
                .height(10)
                .translateX(15)
                .translateY(10)
                .stroke(Color.rgb(255, 255, 255, .70))
                .build();
        
        stopButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               if (mediaPlayer!= null) {
                mediaPlayer.stop();                   
               }
            }
        });
        buttonGroup.getChildren().add(stopButton);
        
        // play control
        final Node playButton = ArcBuilder.create()
                .type(ArcType.ROUND)
                .centerX(12)
                .centerY(16)
                .radiusX(15)
                .radiusY(15)
                .startAngle(180-30)
                .length(60)
                .fill(new Color(1,1,1, .90))
                .translateX(40)
                .build();
        playButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {                
                mediaPlayer.play();
            }
        });

        
        // pause control
        final Group pause = new Group();
        final Node pauseButton = CircleBuilder.create()
                .centerX(12)
                .centerY(16)
                .radius(10)
                .stroke(new Color(1,1,1, .90))
                .translateX(30)
                .build();
        final Node firstLine = LineBuilder.create()
                .startX(6)
                .startY(16 - 10)
                .endX(6)
                .endY(16 - 2)
                .strokeWidth(3)
                .translateX(34)
                .translateY(6)
                .stroke(new Color(1,1,1, .90))
                .build();
        
        final Node secondLine = LineBuilder.create()
                .startX(6)
                .startY(16 - 10)
                .endX(6)
                .endY(16 - 2)
                .strokeWidth(3)
                .translateX(38)
                .translateY(6)
                .stroke(new Color(1,1,1, .90))
                .build();
        pause.getChildren().addAll(pauseButton, firstLine, secondLine);
        
        pause.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                if (mediaPlayer!=null) {
                    buttonGroup.getChildren().remove(pause);
                    buttonGroup.getChildren().add(playButton);
                    mediaPlayer.pause();
                }
            }
        });
                
        playButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {  
                if (mediaPlayer != null) {
                    buttonGroup.getChildren().remove(playButton);
                    buttonGroup.getChildren().add(pause);
                    mediaPlayer.play();                    
                }
            }
        });

        buttonGroup.getChildren().add(pause);
        // move button group when scene is resized
        buttonGroup.translateXProperty().bind(scene.widthProperty().subtract(buttonArea.getWidth() + 6));
        buttonGroup.translateYProperty().bind(scene.heightProperty().subtract(buttonArea.getHeight() + 6));
        root.getChildren().add(buttonGroup);
        
        
        // close button
        final Group closeApp = new Group();
        Node closeButton = CircleBuilder.create()
                .centerX(5)
                .centerY(0)
                .radius(7)
                .fill(Color.rgb(255, 255, 255, .80))
                .build();
        Node closeXmark = new Text(2, 4, "X");
        closeApp.translateXProperty().bind(scene.widthProperty().subtract(15));
        closeApp.setTranslateY(10);
        closeApp.getChildren().addAll(closeButton, closeXmark);
        closeApp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });
        
        root.getChildren().add(closeApp);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        previousLocation = new Point2D(primaryStage.getX(), primaryStage.getY());
        
    }
}

