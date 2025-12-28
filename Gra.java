public class Gra {
    private OknoGry okno;
    private Gracz gracz;
    private MozliweScenatiusze scenariusze;
    private Scenariusze aktualny;
    private String imieRatownika;

    private int poziom = 1; // 1: Zamknięte, 2: Otwarte, 3: Iteracyjne (Interakcyjne)
    private int licznikPytanPoziomu = 0;
    private final int MAX_PYTAN_NA_POZIOM = 3; // Możesz to zmienić na np. 5

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
        // Sprawdzamy, czy czas na zmianę poziomu
        if (licznikPytanPoziomu >= MAX_PYTAN_NA_POZIOM) {
            poziom++;
            licznikPytanPoziomu = 0;
            if (poziom <= 3) {
                okno.pokazInfoOPoziomie(poziom); // Wywołanie nowej metody
            }
        }

        // Kończymy grę po 3 poziomach
        if (poziom > 3) {
            RaportZGry.zapis(gracz);
            okno.pokazKoniecDnia(gracz);
            return;
        }

        // Pobieramy scenariusz pasujący do obecnego poziomu
        aktualny = pobierzOdpowiedniScenariusz();

        if (aktualny == null) {
            // Jeśli zabrakło pytań w danym typie, idziemy do raportu
            RaportZGry.zapis(gracz);
            okno.pokazKoniecDnia(gracz);
            return;
        }

        licznikPytanPoziomu++;
        okno.wyswietlScenariusz(aktualny);
    }

    private Scenariusze pobierzOdpowiedniScenariusz() {
        // Ta metoda szuka w liście scenariusza, który pasuje do etapu gry
        // i nie był jeszcze użyty (zakładając, że scenariusze.nastepny() to obsługuje)

        Scenariusze s;
        int proby = 0;

        while (proby < 100) { // Próbujemy znaleźć pasujący typ
            s = scenariusze.nastepny();
            if (s == null) return null;

            if (poziom == 1 && !s.isOtwartePytanie() && !s.isInterakcyjne()) {
                return s; // Poziom 1: Tylko zamknięte
            } else if (poziom == 2 && s.isOtwartePytanie()) {
                return s; // Poziom 2: Tylko otwarte
            } else if (poziom == 3 && s.isInterakcyjne()) {
                return s; // Poziom 3: Tylko iteracyjne
            }
            // Jeśli wylosowany nie pasuje do poziomu, pętla szuka dalej
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

    public void blednaInterakcja() {
        przegrana();
    }

    private void przegrana() {
        RaportZGry.zapis(gracz);
        okno.pokazBladKrytyczny();
    }
}