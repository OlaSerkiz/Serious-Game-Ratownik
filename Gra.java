public class Gra {
    private Gracz gracz;
    private OknoGry okno;
    private MozliweScenatiusze baza;
    private Scenariusze aktualny;
    private int poziom = 1;

    public Gra(String imie) {
        this.gracz = new Gracz(imie);
        this.baza = new MozliweScenatiusze();
    }

    public void ustawOkno(OknoGry okno) {
        this.okno = okno;
    }

    public Gracz getGracz() {
        return gracz;
    }

    public void setPoziom(int p) {
        this.poziom = p;
        baza.ustawNaPoziom(p);
    }
    public int getPoziom() {
        return this.poziom;
    }

    public void rozpocznij() {
        okno.pokazInfoOPoziomie(poziom);
        nastepny();
    }

    public void nastepny() {
        if (baza.maKolejny()) {
            aktualny = baza.pobierz();
            if (okno != null) {
                okno.wyswietlScenariusz(aktualny);
            }
        } else {
            okno.pokazKoniecDnia(gracz);
        }
    }

    public void poprawnaInterakcja() {
        gracz.dodajPunkty(100);
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepny();
    }

    public void blednaInterakcja() {
        gracz.dodajPunkty(-80);
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepny();
    }

    public void roztrzygnijWybor(int index, long czas) {
        if (aktualny.czyPoprawna(index)) {
            gracz.dodajPunkty(100);
        } else {
            gracz.odejmijPunkty(50);
        }
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepny();
    }

    public void obsluzOdpowiedzTekstowa(String odpowiedzGracza, long czas) {
        String poprawna = aktualny.getPoprawnaOdpowiedzTekstowa();

        if (poprawna == null) {
            nastepny();
            return;
        }

        if (odpowiedzGracza.trim().equalsIgnoreCase(poprawna.trim())) {
            poprawnaInterakcja();
        } else {
            blednaInterakcja();
        }
    }
    public void resetuj() {
        gracz.setPunkty(0);
        if (okno != null) {
            okno.aktualizacjaPunktow(0);
        }
    }
}