public class Scenariusze {

    private String opis;
    private String[] odpowiedzi;
    private TypPytania typ;
    private String poprawnaTekstowa;

    private int dobra;      // punkty za dobrą
    private int zla;        // punkty za złą
    private boolean krytycznaZla; // czy błąd krytyczny

    // pytanie zamknięte
    public Scenariusze(String opis, String[] odp, TypPytania typ, int dobra, int zla, boolean krytyczna) {
        this.opis = opis;
        this.odpowiedzi = odp;
        this.typ = typ;
        this.dobra = dobra;
        this.zla = zla;
        this.krytycznaZla = krytyczna;
    }

    // pytanie otwarte
    public Scenariusze(String opis, String poprawna, TypPytania typ, int dobra, int zla) {
        this.opis = opis;
        this.poprawnaTekstowa = poprawna;
        this.typ = typ;
        this.dobra = dobra;
        this.zla = zla;
        this.krytycznaZla = false;
    }

    // pytanie interakcyjne
    public Scenariusze(String opis, TypPytania typ, int dobra, boolean krytyczna) {
        this.opis = opis;
        this.typ = typ;
        this.dobra = dobra;
        this.zla = 0;
        this.krytycznaZla = krytyczna;
    }

    // GETTERY
    public String getOpisy() { return opis; }
    public String[] getOdpowiedzi() { return odpowiedzi; }
    public TypPytania getTyp() { return typ; }
    public String getPoprawnaTekstowa() { return poprawnaTekstowa; }
    public int getDobra() { return dobra; }
    public int getZla() { return zla; }
    public boolean isKrytycznaZla() { return krytycznaZla; }

    public boolean isOtwartePytanie() {
        return typ == TypPytania.OTWARTE;
    }
}
