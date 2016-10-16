package javafx2introbyexample.chapter3.recipe3_02;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayerBuilder;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcBuilder;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Playing Video
 * @author cdea
 */
public class PlayingVideo extends Application {
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Point2D anchorPt;
    private Point2D previousLocation;
    private Slider progressSlider;
    private ChangeListener<Duration> progressListener;
    private boolean paused = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Chapter 3-2 Playing Video");
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        final Group root = new Group();
        final Scene scene = new Scene(root, 540, 300, Color.rgb(0, 0, 0, 0));
        
        // rounded rectangle with slightly transparent        
        Node applicationArea = createBackground(scene);
        root.getChildren().add(applicationArea);

        // allow the user to drag window on the desktop
        attachMouseEvents(scene, primaryStage);
        
        // allows the user to see the progress of the video playing
        progressSlider = createSlider(scene);
        root.getChildren().add(progressSlider);
                
        // Dragging over surface
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles() || db.hasUrl() || db.hasString()) {
                    event.acceptTransferModes(TransferMode.COPY);
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                } else {
                    event.consume();
                }
            }
        });
        
        // update slider as video is progressing (later removal)
        progressListener = new ChangeListener<Duration>() {
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                progressSlider.setValue(newValue.toSeconds());
            }
        };
        
        
        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                URI resourceUrlOrFile = null;
                
                // dragged from web browser address line?
                if (db.hasContent(DataFormat.URL)) {
                    try {
                        resourceUrlOrFile = new URI(db.getUrl().toString());
                    } catch (URISyntaxException ex) {
                       ex.printStackTrace();
                    }
                } else if (db.hasFiles()) {
                    // dragged from the file system
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                    }
                    resourceUrlOrFile = new File(filePath).toURI();
                    success = true;    
                }
                // load media
                Media media = new Media(resourceUrlOrFile.toString());
                
                // stop previous media player and clean up
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.currentTimeProperty().removeListener(progressListener);
                    mediaPlayer.setOnPaused(null);
                    mediaPlayer.setOnPlaying(null);
                    mediaPlayer.setOnReady(null);
                }
                
                // create a new media player
                mediaPlayer = MediaPlayerBuilder.create()
                        .media(media)
                        .build();

                // as the media is playing move the slider for progress
                mediaPlayer.currentTimeProperty().addListener(progressListener);


                // play video when ready status
                mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                       progressSlider.setValue(1);
                       progressSlider.setMax(mediaPlayer.getMedia().getDuration().toMillis()/1000);
                       mediaPlayer.play();
                    }
                });
                
                // Lazy init media viewer 
                if (mediaView == null) {
                    mediaView = MediaViewBuilder.create()
                            .mediaPlayer(mediaPlayer)
                            .x(4)
                            .y(4)
                            .preserveRatio(true)
                            .opacity(.85)
                            .smooth(true)
                            .build();
                    
                    mediaView.fitWidthProperty().bind(scene.widthProperty().subtract(220));
                    mediaView.fitHeightProperty().bind(scene.heightProperty().subtract(30));
                    
                    // make media view as the second node on the scene.
                    root.getChildren().add(1, mediaView);
                }
                
                // sometimes loading errors occur
                mediaView.setOnError(new EventHandler<MediaErrorEvent>() {
                    public void handle(MediaErrorEvent event) {
                        event.getMediaError().printStackTrace();
                    }
                });
                
                mediaView.setMediaPlayer(mediaPlayer);
                
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
        // rectangular area holding buttons
        final Group buttonArea = createButtonArea(scene);
        
        // stop button will stop and rewind the media
        Node stopButton = createStopControl();
       
        // play button can resume or start a media 
        final Node playButton = createPlayControl();
        
        // pauses media play
        final Node pauseButton = createPauseControl();

        stopButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                if (mediaPlayer!= null) {
                    buttonArea.getChildren().removeAll(pauseButton, playButton);
                    buttonArea.getChildren().add(playButton);
                    mediaPlayer.stop();                  
                }
            }
        });
        // pause media and swap button with play button
        pauseButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                if (mediaPlayer!=null) {
                    buttonArea.getChildren().removeAll(pauseButton, playButton);
                    buttonArea.getChildren().add(playButton);
                    mediaPlayer.pause();
                    paused = true;
                }
            }
        });
        
        // play media and swap button with pause button
        playButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {  
                if (mediaPlayer != null) {
                    buttonArea.getChildren().removeAll(pauseButton, playButton);
                    buttonArea.getChildren().add(pauseButton);
                    paused = false;
                    mediaPlayer.play();                    
                }
            }
        });
        
        // add stop button to button area
        buttonArea.getChildren().add(stopButton);
        
        // set pause button as default
        buttonArea.getChildren().add(pauseButton);

        // add buttons
        root.getChildren().add(buttonArea);

        // create a close button
        Node closeButton= createCloseButton(scene);
        root.getChildren().add(closeButton);
        
        primaryStage.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                previousLocation = new Point2D(primaryStage.getX(), primaryStage.getY());
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    private Group createButtonArea(final Scene scene) {
        // create button area
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
        
        // move button group when scene is resized
        buttonGroup.translateXProperty().bind(scene.widthProperty().subtract(buttonArea.getWidth() + 6));
        buttonGroup.translateYProperty().bind(scene.heightProperty().subtract(buttonArea.getHeight() + 6));
        return buttonGroup;
    }

    private Node createStopControl() {
        // stop audio control
        Rectangle stopButton = RectangleBuilder.create()
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

        return stopButton;
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

    private Slider createSlider(Scene scene) {
        Slider slider = SliderBuilder.create()
                .min(0)
                .max(100)
                .value(1)
                .showTickLabels(true)
                .showTickMarks(true)
                .build();
        
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (paused) {
                    long dur = newValue.intValue() * 1000;
                    mediaPlayer.seek(new Duration(dur));
                }
            }
        });
        slider.translateYProperty().bind(scene.heightProperty().subtract(30));
        return slider;
    }

    private void attachMouseEvents(Scene scene, final Stage primaryStage) {
        
        // Full screen toggle
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                if (event.getClickCount() == 2) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            }
        });
        
        // starting initial anchor point
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                if (!primaryStage.isFullScreen()) {
                    anchorPt = new Point2D(event.getScreenX(), event.getScreenY());
                }
            }
        });
        
        // dragging the entire stage
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event){
                if (anchorPt != null && previousLocation != null && !primaryStage.isFullScreen()) {
                    primaryStage.setX(previousLocation.getX() + event.getScreenX() - anchorPt.getX());
                    primaryStage.setY(previousLocation.getY() + event.getScreenY() - anchorPt.getY());                    
                }
            }
        });
        
        // set the current location
        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
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

    private Node createPauseControl() {
        // pause control
        final Group pause = new Group();
        final Circle pauseButton = CircleBuilder.create()
                .centerX(12)
                .centerY(16)
                .radius(10)
                .stroke(new Color(1,1,1, .90))
                .translateX(30)
                .build();
        final Line firstLine = LineBuilder.create()
                .startX(6)
                .startY(16 - 10)
                .endX(6)
                .endY(16 - 2)
                .strokeWidth(3)
                .translateX(34)
                .translateY(6)
                .stroke(new Color(1,1,1, .90))
                .build();
        final Line secondLine = LineBuilder.create()
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
        return pause;
    }

    private Node createPlayControl() {
        // play control
        final Arc playButton = ArcBuilder.create()
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

        return playButton;
    }
}