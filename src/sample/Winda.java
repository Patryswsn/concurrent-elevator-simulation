package sample;

public class Winda implements Runnable {

    Monitor mon;
    int nrWindy;
    volatile int x,y;

    public Winda(Monitor mon, int nrWindy,int x, int y) {
        this.mon = mon;
        this.nrWindy=nrWindy;
        this.x=x;
        this.y=y;
    }

    @Override
    public void run() {

       while(true) {
           mon.jedz(this);
       }
    }
}
