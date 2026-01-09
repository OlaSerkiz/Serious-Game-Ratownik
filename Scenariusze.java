public class Scenariusze {
    private String opisy;
    private String[] odpowiedzi;
    private int poprawna;
    private boolean interakcyjne;
    private boolean otwartePytanie;
    private int poziom;
    private String poprawnaOdpowiedzTekstowa;
    private int poprawnaOdpowiedzIndex;

    private String scenaTyp;
    private String[] grafikiInterakcyjne;
    private String poprawnaGrafika;

    public Scenariusze(String opisy, String[] odpowiedzi, int poprawna, int poziom) {
        this.opisy = opisy;
        this.odpowiedzi = odpowiedzi;
        this.poprawna = poprawna;
        this.poziom = poziom;
        this.interakcyjne = false;
        this.otwartePytanie = false;
    }

    public int getPoziom() {
        return poziom;
    }
    public boolean czyPoprawna(int index) {
        return this.poprawnaOdpowiedzIndex == index;
    }

    public String getOpisy() {
        return opisy;
    }
    public String[] getOdpowiedzi() {
        return odpowiedzi;
    }
    public boolean isInterakcyjne() {
        return interakcyjne;
    }
    public boolean isOtwartePytanie() {
        return otwartePytanie; }
    public String getScenaTyp() {
        return scenaTyp;
    }
    public String[] getGrafikiInterakcyjne() {
        return grafikiInterakcyjne;
    }
    public String getPoprawnaGrafika() {
        return poprawnaGrafika;
    }

    public void setInterakcyjne(String typ, String[] grafiki, String poprawnaG) {
        this.interakcyjne = true;
        this.scenaTyp = typ;
        this.grafikiInterakcyjne = grafiki;
        this.poprawnaGrafika = poprawnaG;
    }

    public String getPoprawnaOdpowiedzTekstowa() {
        return poprawnaOdpowiedzTekstowa;
    }

    public void setPoprawnaOdpowiedzTekstowa(String odp) {
        this.otwartePytanie = true;
        this.poprawnaOdpowiedzTekstowa = odp;
    }
}