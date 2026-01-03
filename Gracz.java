public class Gracz {

    private String imie;
    private int punkty;

    public Gracz(String imie) {
        this.imie = imie;
        this.punkty = 0;
    }

    public void dodajPunkty(int p) {
        this.punkty += p;
        if (this.punkty < 0) {
            this.punkty = 0; // Zabezpieczenie przed ujemnym wynikiem końcowym
        }
    }

    public void odejmijPunkty(int p) {
        punkty -= p;
    }

    public int getPunkty() {
        return punkty;
    }

    public String getImie() {
        return imie;
    }

    public void reset() {
        punkty = 0;
    }
}