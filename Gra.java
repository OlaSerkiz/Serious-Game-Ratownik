public class Gra {
    private OknoGry okno;
    private Gracz gracz;
    private MozliweScenatiusze scenariusze;
    private Scenariusze aktualny;
    private String imieRatownika;

    public Gra(String imie) {
        this.imieRatownika = imie;
        this.gracz = new Gracz(imie);
        this.scenariusze = new MozliweScenatiusze();
    }

    public void ustawOkno(OknoGry okno) { this.okno = okno; }

    public void rozpocznij() {
        gracz.reset();
        scenariusze.resetuj();
        nastepny();
    }

    public void resetuj() {
        this.gracz = new Gracz(imieRatownika);
        this.scenariusze.resetuj();
    }

    private void nastepny() {
        aktualny = scenariusze.nastepny();
        if (aktualny == null) {
            RaportZGry.zapis(gracz); // Zapis przy wygranej
            okno.pokazKoniecDnia(gracz);
            return;
        }
        okno.wyswietlScenariusz(aktualny);
    }

    public void roztrzygnijWybor(int idx, long czas) {
        if (idx == -1) { przegrana(); return; }
        if (aktualny.sprawdzCzyPoprawna(idx)) {
            gracz.dodajPunkty(10);
            okno.aktualizacjaPunktow(gracz.getPunkty());
            nastepny();
        } else if (aktualny.isKrytyczna()) {
            przegrana();
        } else {
            gracz.odejmijPunkty(5);
            okno.aktualizacjaPunktow(gracz.getPunkty());
            nastepny();
        }
    }

    public void obsluzOdpowiedzTekstowa(String txt, long czas) {
        if (aktualny.poprawnaTekst(txt)) {
            gracz.dodajPunkty(15);
            okno.aktualizacjaPunktow(gracz.getPunkty());
            nastepny();
        } else {
            przegrana();
        }
    }

    public void poprawnaInterakcja() {
        gracz.dodajPunkty(20);
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepny();
    }

    public void blednaInterakcja() { przegrana(); }

    private void przegrana() {
        RaportZGry.zapis(gracz); // Zapis przy błędzie
        okno.pokazBladKrytyczny();
    }
}