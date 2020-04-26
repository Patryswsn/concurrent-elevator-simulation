package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {

    //po rozwiezieniu pasazerow winda wraca na parter

    int x,y;
    private int nrWindy;
    private int pierwszeObslugiwanePietro;
    private int OstatnieObslugiwanePietro;
    private SortedSet<Integer> kolejkaPieter = new ConcurrentSkipListSet<>();
    private int liczbaMiejsc=4;
    private int liczbaZajetychMiejsc=0;
    private int liczbaOczekujacych=0;
    private int obecnePietro=0;
    private List<Pasażer> listaPasazerow = new ArrayList<>();
    private Pietro[] pietra;

    boolean czyWindaMozeJechac=false;



    Object rysowanie = new Object();
    Object mutex = new Object();


    public int getLiczbaMiejsc() {
        return liczbaMiejsc;
    }

    public int getLiczbaZajetychMiejsc() {
        return liczbaZajetychMiejsc;
    }

    public int getPierwszeObslugiwanePietro() {
        return pierwszeObslugiwanePietro;
    }

    public int getOstatnieObslugiwanePietro() {
        return OstatnieObslugiwanePietro;
    }


    public Monitor(int pierwszeObslugiwanePietro, int ostatnieObslugiwanePietro, int nrWindy, Pietro[] pietra) {
        this.pierwszeObslugiwanePietro = pierwszeObslugiwanePietro;
        this.OstatnieObslugiwanePietro = ostatnieObslugiwanePietro;
        this.nrWindy=nrWindy;
        this.pietra=pietra;
    }

    ///do edycji
     void wyjdzZWindy(Pasażer p)//napisac dopuszczenie do wyjscia z windy
    {

        synchronized (mutex) {

            while (p.nrPietra != obecnePietro) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                }
            }

            liczbaZajetychMiejsc--;
            listaPasazerow.remove(p);//animacja wyjscia ma byc w funkcji watku;
            System.out.println("wychodzi p" + p.idWatku + ", liczba zajetych miejsc:" + liczbaZajetychMiejsc + ", liczba oczekujacych: " + liczbaOczekujacych + ", winda nr:" + nrWindy);

            if (listaPasazerow.isEmpty()) {//jezeli nie ma juz pasazerow to winda moze jechac
                czyWindaMozeJechac = true;
                mutex.notifyAll();
            } else {//jezeli sa pasazerowie
                int j = 0;
                for (int i = 0; i < listaPasazerow.size(); i++) {///sprawdza czy sa pasazerowie ktorzy chca wysiasc na obecnym pietrze
                    if (listaPasazerow.get(i).nrPietra == obecnePietro)
                        j++;
                }
                if (j == 0) {//jezeli nie ma to daje sygnal windzie ze moze ruszac
                    czyWindaMozeJechac = true;
                    mutex.notifyAll();

                }
            }


        }

    }


    void zajmijMiejsce(Pasażer p)
    {

        liczbaOczekujacych++;//inkrementacja liczby oczekujacaej jest mozliwa bez jakiejkolwiek synchronizacji

synchronized (mutex) {

    while ((liczbaZajetychMiejsc == liczbaMiejsc) || (obecnePietro != 0)) {//jezeli wszystkie miejsca sa zajete lub winda nie jest na parterze to do spania
        try {
            mutex.wait();
        } catch (InterruptedException e) {
        }
    }


    liczbaZajetychMiejsc++;
    liczbaOczekujacych--;//jezeli winda nie bedzie zapelniona a nie bedzie oczekujacych to rusza
    listaPasazerow.add(p);//dodaje pasazera do listy pasazerow znajdujacych sie w windzie
    kolejkaPieter.add(p.nrPietra);//dodaje pietro na ktore chce jechac
    System.out.println("wchodzi p" + p.idWatku + ", liczba zajetych miejsc:" + liczbaZajetychMiejsc + ", liczba oczekujacych: " + liczbaOczekujacych + ", pietro:" + p.nrPietra + ", winda nr:" + nrWindy);

    if ((liczbaZajetychMiejsc == liczbaMiejsc) || (liczbaOczekujacych == 0)) {//jezeli nie ma oczekujacych lub miejsca wykorzystane to winda rusza
        czyWindaMozeJechac = true;
        mutex.notifyAll();
    }


}


    }














    //funkcje dla windy/////////////////////////////////

     void jedz(Winda w)//ruch windy
    {
int poprzedniePietro=-1;

        synchronized (mutex) {

            while (czyWindaMozeJechac == false) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                }
            }


            while (kolejkaPieter.isEmpty() == false) {//jezeli ciagle sa jakies pietra gdzie ma jechac

                //poruszanie wszystkich elementow


                if (obecnePietro == kolejkaPieter.first()) {//jezeli dojechala na pietro


                    czyWindaMozeJechac = false;//jezeli ta zmienna jest ustawiona na false to iwnda czeka na pozwolenie na ruszenie
                    kolejkaPieter.remove(obecnePietro);

                    System.out.println("winda-" + w.nrWindy + " na pietrze: " + obecnePietro);

                    mutex.notifyAll();

                    while (czyWindaMozeJechac == false) {
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                        }
                    }


                }

                try {//zalozmy ze ten try catch jest poprawnie xd
                    if ((obecnePietro < OstatnieObslugiwanePietro) & (kolejkaPieter.first() - obecnePietro > 0))
                        obecnePietro++;
                    else if ((obecnePietro > pierwszeObslugiwanePietro) & (kolejkaPieter.first() - obecnePietro < 0))
                        obecnePietro--;

                } catch (NoSuchElementException e) {
                    System.out.println("brak elemmentu");

                }//niepotrzebna obsluga poniewaz jezeli lista jest pusta to wraz z koncem tej iteracji
                //zakonczy sie wykonywanie petli


                //rysuj;

                w.y=pietra[obecnePietro+Controller.Npod].y;
                synchronized (rysowanie)//robienie GUI
                {
                    try {
                        for(int i=0;i<listaPasazerow.size();i++)
                        {
                            listaPasazerow.get(i).y=pietra[obecnePietro+Controller.Npod].y+(pietra[0].y-pietra[1].y)/2;

                        }

                        rysowanie.wait();
                    } catch (InterruptedException e) {}

                }


            }

///////////////////////////////

            while(obecnePietro!=0) {//po dostarczenieu wszystkich pasazerow wraca na parter

                if(0-obecnePietro>0)
                    obecnePietro++;
                else if(0-obecnePietro<0)
                    obecnePietro--;


                w.y = pietra[obecnePietro + Controller.Npod].y;

                synchronized (rysowanie) {
                    try {
                        rysowanie.wait();
                        System.out.println("winda-" + w.nrWindy + " na pietrze: " + obecnePietro);

                    } catch (InterruptedException e) {
                    }

                }

            }


/////////////////////////////////

            czyWindaMozeJechac = false;


            mutex.notifyAll();


        }
    }




     void poruszajPasazerem(Pasażer p)
    {//przemieszczanie pasazera do windy


        //System.out.println(p.windy[2].x+30);

            while(p.x>Controller.windy[2].x+100)
            {

                p.x-=Math.random()*30+20;

                synchronized (rysowanie)
                {
                    try {
                        rysowanie.wait();
                    } catch (InterruptedException e) {}
                }

            }


    }




    void poruszajPasazerem2(Pasażer p)
    {//poruszanie pasazera z windy


        while(p.x<p.xPoczatkowe)
        {

            p.x+=Math.random()*30+20;

            synchronized (rysowanie)
            {
                try {
                    rysowanie.wait();
                } catch (InterruptedException e) {}
            }

        }


    }










}
