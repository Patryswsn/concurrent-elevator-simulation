package sample;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Controller {


    final static int N=6;//liczba pieter
    final static int Npod=4;//podziemne parkingi
    int M =4;//liczba miejsc w windzie
    Pietro[] pietra = new Pietro[N+Npod+1];//+1 bo pietro nr0
    GraphicsContext gc;
    final Canvas root;
    int cornerSize=5;
    int wymiarWind;//szerokosc i wysokosc windy
    int wymiarPasazera;//szerokosc i wysokosc pasazera



    final int liczbaPasazerow=21;

    static Winda[] windy = new Winda[3];//tablica wind
    Thread[] windyWatki = new Thread[3];
    Pasażer[] pasazerowie = new Pasażer[liczbaPasazerow];//tablica pasazerow
    Thread[] pasazerowieWatki = new Thread[liczbaPasazerow];
    Monitor[] w = new Monitor[3];//tablica monitorow


    public Controller(GraphicsContext gc, Canvas x) {
        this.gc = gc;
        this.root = x;
        wymiarWind=(int) root.getHeight() / (N+Npod+2);
        wymiarPasazera=wymiarWind/3;

        for(int i=0;i<N+Npod+1;i++) {
            pietra[i] = new Pietro((int) root.getHeight() - ((i + 1) * (int) root.getHeight() / (N+Npod+2)));//dziele ekran na N+Npod+1 czesci i rysuje N+Npod pieter
        }

        System.out.println(pietra.length);
        System.out.println(root.getHeight());
        System.out.println(root.getWidth());
    }


    public void wywolajWatki()//tworzenie dzialajacych watkow oraz monitorow
    {

        w[0]=new Monitor(-Npod,0,0,pietra);
        w[1]=new Monitor(0,N/2,1,pietra);
        w[2]=new Monitor(N/2+1,N,2,pietra);


        windy[0]=new Winda(w[0],0,70,pietra[Npod].y);
        windy[1]=new Winda(w[1],1,70+pietra[Npod].y-pietra[Npod+1].y,pietra[Npod].y);
        windy[2]=new Winda(w[2],2,70+2*(pietra[Npod].y-pietra[Npod+1].y),pietra[Npod].y);

        for(int i=0;i<3;i++)
        {
            windyWatki[i]=new Thread(windy[i]);
            windyWatki[i].start();
        }




        for(int i=0;i<liczbaPasazerow;i++)
        {
            pasazerowie[i]= new Pasażer(w,i, (int) root.getWidth(),pietra[Npod].y+wymiarWind/2);
            pasazerowieWatki[i] = new Thread(pasazerowie[i]);
            pasazerowieWatki[i].start();
        }








    }


    public void rysuj()
    {

//rysowanie tla
        gc.setFill(Color.GREY);
        gc.fillRect(0,0,root.getWidth(),root.getHeight());

        gc.setFill(Color.INDIANRED);
        for(int i=0;i<root.getHeight();i+=25)
            for(int j=0;j<root.getWidth();j+=25)
                gc.fillRect(j,i,25,25);

            gc.setFill(Color.BLACK);

            int k=-Npod;
           // int j;
        for(int i=0;i<pietra.length;i++) {
            for (int j = 0; j < root.getWidth(); j += cornerSize + 1)
                gc.fillRect(j, pietra[i].y, cornerSize, cornerSize);

            gc.fillText("P: "+k,0,pietra[i].y+wymiarWind/2);
            k++;
        }


        gc.fillText("Zajete miejsca W0: "+w[0].getLiczbaZajetychMiejsc()+"/"+w[0].getLiczbaMiejsc(),50,wymiarWind/2,100);
        gc.fillText("Zajete miejsca W1: "+w[1].getLiczbaZajetychMiejsc()+"/"+w[1].getLiczbaMiejsc(),200,wymiarWind/2,100);
        gc.fillText("Zajete miejsca W2: "+w[2].getLiczbaZajetychMiejsc()+"/"+w[2].getLiczbaMiejsc(),350,wymiarWind/2,100);

//koniec rysowania tla

        //gc.fillText(W0);





        //rysowanie pasazerow


        for(int i=0;i<pasazerowie.length;i++) {
            gc.setFill(Color.BLACK);
            gc.fillOval(pasazerowie[i].x, pasazerowie[i].y, wymiarPasazera, wymiarPasazera);
            gc.setFill(Color.WHITE);
            gc.fillText("P"+pasazerowie[i].idWatku,pasazerowie[i].x, pasazerowie[i].y-wymiarPasazera/2);

        }








        //rysowanie wind


        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(windy[0].x,windy[0].y,wymiarWind,wymiarWind);
        gc.setFill(Color.WHITE);
        gc.fillText("W0",windy[0].x+wymiarWind/2,windy[0].y+wymiarWind/2);

        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(windy[1].x,windy[1].y,wymiarWind,wymiarWind);
        gc.setFill(Color.WHITE);
        gc.fillText("W1",windy[1].x+wymiarWind/2,windy[1].y+wymiarWind/2);


        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(windy[2].x,windy[2].y,wymiarWind,wymiarWind);
        gc.setFill(Color.WHITE);
        gc.fillText("W2",windy[2].x+wymiarWind/2,windy[2].y+wymiarWind/2);





        for(int i=0;i< windy.length;i++)
        {
            synchronized (w[i].rysowanie) {
                w[i].rysowanie.notifyAll();
            }
        }

    }



}
