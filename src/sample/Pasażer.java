package sample;

        import java.util.Random;

public class Pasażer implements Runnable {

    //Pasażer p=this;
    Monitor[] windy;
    Monitor winda;
    Random random = new Random();
    int nrPietra;
    int idWatku;
    int x, y;//poczatkowe polozenie
    int xPoczatkowe,yPoczatkowe;


    public Pasażer(Monitor[] w, int idWatku,int x,int y) {

        this.windy = w;
        this.idWatku = idWatku;
        this.x=x;
        this.y=y;
        xPoczatkowe=x;
        yPoczatkowe=y;
    }

    private void losuj() {//losowanie numeru pietra na ktory chce sie dostac i na jego podstawie wybranie odpowiedniego monitora(odpowieniej windy)


        nrPietra=random.nextInt(windy[windy.length-1].getOstatnieObslugiwanePietro()+1);
        if(nrPietra==0)
            nrPietra-=random.nextInt(((-1)*windy[0].getPierwszeObslugiwanePietro()))+1;//mnozenie przez -1 bo pietra ujemne

        if(nrPietra<windy[0].getOstatnieObslugiwanePietro())//wybor monitora ktorym watek bedzie sie poslugiwac
            winda=windy[0];
        else if(nrPietra<=windy[1].getOstatnieObslugiwanePietro())
            winda=windy[1];
        else if(nrPietra<=windy[2].getOstatnieObslugiwanePietro())
            winda=windy[2];
    }





    @Override
    public void run()
    {
        while(true) {
            losuj();
            winda.poruszajPasazerem(this);
            winda.zajmijMiejsce(this);
            winda.wyjdzZWindy(this);
            winda.poruszajPasazerem2(this);
            x=xPoczatkowe;
            y=yPoczatkowe;
        }
        //for(int j=0;j<10;j++)
        // System.out.println("koniec: p****************************************************************************************************************************"+idWatku);

    }





}

