public class Scenariusze {
    private String opisy;
    private String[] odpowiedzi;
    private int decyzja_poprawna;
    private int decyzja_krytyczna;

    public Scenariusze(String opisy, String[] odpowiedzi, int decyzja_poprawna, int decyzja_krytyczna) {
        this.opisy = opisy;
        this.odpowiedzi = odpowiedzi;
        this.decyzja_poprawna = decyzja_poprawna;
        this.decyzja_krytyczna = decyzja_krytyczna;
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
    public int ocenOdpowiedz(int wybrana) {
        if (wybrana == decyzja_poprawna) return 3;
        if (wybrana == decyzja_krytyczna) return -5;
        return -2;
    }
}
