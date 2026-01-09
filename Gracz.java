public class Gracz {
    private String imie;
    private int punkty;

    public Gracz(String imie) {
        this.imie = imie;
        this.punkty = 0;
    }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public int getPunkty() { return punkty; }
    public void setPunkty(int punkty) { this.punkty = punkty; }

    public void dodajPunkty(int p) {
        this.punkty += p;
        if (this.punkty < 0) this.punkty = 0;
    }

    public void odejmijPunkty(int p) {
        this.punkty -= p;
        if (this.punkty < 0) {
            this.punkty = 0;
        }
    }
}