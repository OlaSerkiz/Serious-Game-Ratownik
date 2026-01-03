public class Gra {
    private OknoGry okno;
    private Gracz gracz;
    private MozliweScenatiusze scenariusze;
    private Scenariusze aktualny;
    private String imieRatownika;

    private int poziom = 1; // 1: Zamknięte, 2: Otwarte, 3: Iteracyjne (Interakcyjne)
    private int licznikPytanPoziomu = 0;
    private final int MAX_PYTAN_NA_POZIOM = 6;

    public Gra(String imie) {
        this.imieRatownika = imie;
        this.gracz = new Gracz(imie);
        this.scenariusze = new MozliweScenatiusze();
    }

    public void ustawOkno(OknoGry okno) { this.okno = okno; }

    public void rozpocznij() {
        gracz.reset();
        scenariusze.resetuj();
        poziom = 1;
        licznikPytanPoziomu = 0;
        nastepny();
    }

    public void resetuj() {
        this.gracz = new Gracz(imieRatownika);
        this.scenariusze.resetuj();
        this.poziom = 1;
        this.licznikPytanPoziomu = 0;
    }

    private void nastepny() {
        if (licznikPytanPoziomu >= MAX_PYTAN_NA_POZIOM) {
            poziom++;
            licznikPytanPoziomu = 0;
            if (poziom <= 3) {
                okno.pokazInfoOPoziomie(poziom);
            }
        }

        // Kończymy grę po 3 poziomach
        if (poziom > 3) {
            RaportZGry.zapis(gracz);
            okno.pokazKoniecDnia(gracz);
            return;
        }

        aktualny = pobierzOdpowiedniScenariusz();

        if (aktualny == null) {
            RaportZGry.zapis(gracz);
            okno.pokazKoniecDnia(gracz);
            return;
        }

        licznikPytanPoziomu++;
        okno.wyswietlScenariusz(aktualny);
    }

    private Scenariusze pobierzOdpowiedniScenariusz() {

        Scenariusze s;
        int proby = 0;

        while (proby < 100) {
            s = scenariusze.nastepny();
            if (s == null) return null;

            if (poziom == 1 && !s.isOtwartePytanie() && !s.isInterakcyjne()) {
                return s; // Poziom 1: Tylko zamknięte
            } else if (poziom == 2 && s.isOtwartePytanie()) {
                return s; // Poziom 2: Tylko otwarte
            } else if (poziom == 3 && s.isInterakcyjne()) {
                return s; // Poziom 3: Tylko iteracyjne
            }
            proby++;
        }
        return null;
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

    public void obsluzOdpowiedzTekstowa(String odpowiedz, long czas) {

        if (aktualny.poprawnaTekst(odpowiedz)) {

            int p = (int) Math.max(10, 100 - (czas / 200));
            gracz.dodajPunkty(p);
            okno.aktualizacjaPunktow(gracz.getPunkty());
            nastepny();

        } else {

            gracz.dodajPunkty(-10);
            okno.aktualizacjaPunktow(gracz.getPunkty());

            System.out.println("Błąd w pytaniu otwartym! Tracisz 10 pkt.");

            nastepny();
        }
    }

    public void poprawnaInterakcja() {
        gracz.dodajPunkty(20);
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepny();
    }

    public void blednaInterakcja() {
        przegrana();
    }

    private void przegrana() {
        RaportZGry.zapis(gracz);
        okno.pokazBladKrytyczny();
    }
}