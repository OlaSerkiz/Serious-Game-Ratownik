public class Gracz {

    private String imie;
    private int punkty;

    public Gracz(String imie) {
        this.imie = imie;
        this.punkty = 0;
    }

    public String getImie() {
        return imie;
    }


    public void dodajPunkty(int p) {
        this.punkty += p;
    }

    public void odejmijPunkty(int p){
        this.punkty -= p;
    }

    public int getPunkty() {
        return punkty;
    }

    public void reset() {
        punkty = 0;
    }
}
