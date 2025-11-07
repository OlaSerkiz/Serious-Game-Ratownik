public class Scenariusze {
    private String opisy;
    private String[] odpowiedzi;
    private int decyzja_poprawna;
    private int decyzja_krytyczna;
    private boolean otwartePytanie;
    private int punkty;

///    public Scenariusze(String opisy, String[] odpowiedzi, int decyzja_poprawna, int punkty) {
       /// this(opisy, odpowiedzi, decyzja_poprawna, punkty, false);
//    }

    public Scenariusze(String opisy, String[] odpowiedzi, int decyzja_poprawna, int decyzja_krytyczna, boolean otwartePytanie) {
        this.opisy = opisy;
        this.odpowiedzi = odpowiedzi;
        this.decyzja_poprawna = decyzja_poprawna;
        this.decyzja_krytyczna = decyzja_krytyczna;
        this.otwartePytanie = otwartePytanie;
    }

    public boolean isOtwartePytanie(){
        return otwartePytanie;
    }

    public String getOpisy() {
        return opisy;
    }

    public String[] getOdpowiedzi() {
        return odpowiedzi;
    }

    /**
     * Zwraca punktację określoną czy dobra, zła (np. +3 / -2 / -5)  -> założenie z opisu podejmowanie decyzji
     */
    public int ocenOdpowiedz(int index) {
        return (index == decyzja_poprawna) ? punkty : -punkty / 2;
    }

    public int ocenOdpowiedzTekstowa(String wpisana) {
        if (wpisana != null && wpisana.trim().equalsIgnoreCase("WOPR")) {
            return punkty;
        } else {
            return -punkty / 2;
        }
    }
}
