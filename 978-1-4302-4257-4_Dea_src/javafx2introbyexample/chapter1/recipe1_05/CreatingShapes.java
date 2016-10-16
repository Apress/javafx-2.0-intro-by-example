package javafx2introbyexample.chapter1.recipe1_05;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

/**
 * Creating Shapes
 * @author cdea
 */
public class CreatingShapes extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chapter 1-5 Creating Shapes");
        Group root = new Group();
        Scene scene = new Scene(root, 306, 550, Color.WHITE);

        // CubicCurve
        CubicCurve cubicCurve = CubicCurveBuilder.create()
                .startX(50).startY(75)          // start pt (x1,y1)
                .controlX1(80).controlY1(-25)   // control pt1
                .controlX2(110).controlY2(175)  // control pt2
                .endX(140).endY(75)             // end pt (x2,y2)
                .strokeType(StrokeType.CENTERED).strokeWidth(1)
                .stroke(Color.BLACK)
                .strokeWidth(3)
                .fill(Color.WHITE)
                .build();
        root.getChildren().add(cubicCurve);

        // Ice cream 
        Path path = new Path();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(50);
        moveTo.setY(150);

        QuadCurveTo quadCurveTo = new QuadCurveTo();
        quadCurveTo.setX(150);
        quadCurveTo.setY(150);
        quadCurveTo.setControlX(100);
        quadCurveTo.setControlY(50);

        LineTo lineTo1 = new LineTo();
        lineTo1.setX(50);
        lineTo1.setY(150);

        LineTo lineTo2 = new LineTo();
        lineTo2.setX(100);
        lineTo2.setY(275);

        LineTo lineTo3 = new LineTo();
        lineTo3.setX(150);
        lineTo3.setY(150);
        path.getElements().add(moveTo);
        path.getElements().add(quadCurveTo);
        path.getElements().add(lineTo1);
        path.getElements().add(lineTo2);
        path.getElements().add(lineTo3);
        path.setTranslateY(30);
        path.setStrokeWidth(3);
        path.setStroke(Color.BLACK);
        
        root.getChildren().add(path);
        
        // QuadCurve create a smile
        QuadCurve quad =QuadCurveBuilder.create()
                .startX(50)
                .startY(50)
                .endX(150)
                .endY(50)
                .controlX(125)
                .controlY(150)
                .translateY(path.getBoundsInParent().getMaxY())
                .strokeWidth(3)
                .stroke(Color.BLACK)
                .fill(Color.WHITE)
                .build();

        root.getChildren().add(quad);
        
        // outer donut
        Ellipse bigCircle = EllipseBuilder.create()
                .centerX(100)
                .centerY(100)
                .radiusX(50)
                .radiusY(75/2)
                .translateY(quad.getBoundsInParent().getMaxY())
                .strokeWidth(3)
                .stroke(Color.BLACK)
                .fill(Color.WHITE)
                .build();
        
        // donut hole
        Ellipse smallCircle = EllipseBuilder.create()
                .centerX(100)
                .centerY(100)
                .radiusX(35/2)
                .radiusY(25/2)
                
                .build();
        
        // make a donut
        Shape donut = Path.subtract(bigCircle, smallCircle);
        donut.setStrokeWidth(1);
        donut.setStroke(Color.BLACK);
        // orange glaze
        donut.setFill(Color.rgb(255, 200, 0));
        
        // add drop shadow
        DropShadow dropShadow = DropShadowBuilder.create()
            .offsetX(2.0f)
            .offsetY(2.0f)
            .color(Color.rgb(50, 50, 50, .588))
            .build();
        donut.setEffect(dropShadow);
        
        // move slightly down
        donut.setTranslateY(quad.getBoundsInParent().getMinY() + 30);
        
        root.getChildren().add(donut);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
