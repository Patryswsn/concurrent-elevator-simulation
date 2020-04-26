package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    int width=200;
    int height=150;
    int cornerSize=5;



    @Override
    public void start(Stage primaryStage) throws Exception{


            GridPane root = new GridPane();
            Canvas c = new Canvas(width * cornerSize, height * cornerSize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            Controller con = new Controller(gc,c);

            con.wywolajWatki();



//            con.rysuj();





       // root.add(linia,0,0);

       // root.add();

       // w.wypisz();

        new AnimationTimer()
        {

             long lastTick=0;
            public void handle(long now)
            {

                if(now-lastTick>1000000000)
                {
                    lastTick=now;
                    con.rysuj();

                }

            }
        }.start();


       Scene scene = new Scene(root, width * cornerSize, height * cornerSize);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });


            primaryStage.setTitle("windy");
            primaryStage.setScene(scene);
            primaryStage.show();


        }

    public static void main(String[] args) {
        launch(args);
    }
}
