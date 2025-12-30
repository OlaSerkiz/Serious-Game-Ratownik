public class Scenariusze {
    private String opis;
    private String[] odpowiedzi;
    private TypPytania typ;
    private int poprawnaOdpIdx; // indeks dla pytań zamkniętych
    private boolean krytyczne;
    private String poprawnaTekstowa; // dla pytań otwartych

    private String[] grafikiInterakcyjne;
    private String poprawnaGrafika;
    private String scenaTyp;

    public Scenariusze(String opis, String[] odpowiedzi, TypPytania typ, int poprawnaOdpIdx, boolean krytyczne) {
        this.opis = opis;
        this.odpowiedzi = odpowiedzi;
        this.typ = typ;
        this.poprawnaOdpIdx = poprawnaOdpIdx;
        this.krytyczne = krytyczne;
    }

    public Scenariusze(String opis, String poprawnaTekstowa, TypPytania typ) {
        this.opis = opis;
        this.poprawnaTekstowa = poprawnaTekstowa;
        this.typ = typ;
    }

    public Scenariusze(String opis, TypPytania typ, String[] grafiki, String poprawna, String scenaTyp) {
        this.opis = opis;
        this.typ = typ;
        this.grafikiInterakcyjne = grafiki;
        this.poprawnaGrafika = poprawna;
        this.scenaTyp = scenaTyp;
    }

    public boolean sprawdzCzyPoprawna(int idx) {
        return idx == this.poprawnaOdpIdx;
    }

    public boolean poprawnaTekst(String txt) {
        return txt != null && poprawnaTekstowa != null && txt.trim().equalsIgnoreCase(poprawnaTekstowa);
    }

    public String getOpisy() { return opis; }
    public String[] getOdpowiedzi() { return odpowiedzi; }
    public TypPytania getTyp() { return typ; }
    public boolean isOtwartePytanie() { return typ == TypPytania.OTWARTE; }
    public boolean isInterakcyjne() { return typ == TypPytania.INTERAKCYJNE; }
    public boolean isKrytyczna() { return krytyczne; }
    public String getScenaTyp() { return scenaTyp; }
    public String[] getGrafikiInterakcyjne() { return grafikiInterakcyjne; }
    public String getPoprawnaGrafika() { return poprawnaGrafika; }
}