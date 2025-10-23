public class PoleGracza {
    private String imie;
    private int punkty;

    public PoleGracza(String imie) {
        this.imie = imie;
        this.punkty = 0;
    }

    public String getImie(){
        return imie;
    }

    public int getPunkty() {
        return punkty;
    }

    public void dodajPunkty(int p){
        punkty += p;
    }

    public void odejmijPunkty(int p){
        punkty -= p;
    }
}
