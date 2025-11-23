import java.util.ArrayList;
import java.util.List;

public class Gra {

    private OknoGry okno;
    private Gracz gracz;

    private List<Scenariusze> lista = new ArrayList<>();
    private int indeks = 0;

    public Gra(String imie) {
        gracz = new Gracz(imie);
        przygotujScenariusze();
    }

    public void ustawOkno(OknoGry okno) {
        this.okno = okno;
    }

    public void rozpocznij() {
        indeks = 0;
        gracz.reset();
        wyswietlBiezacy();
    }

    public void resetuj() {
        indeks = 0;
        gracz.reset();
        okno.aktualizacjaPunktow(0);
    }

    private void wyswietlBiezacy() {
        if (indeks >= lista.size()) {
            okno.pokazKoniecDnia(gracz);
            return;
        }
        okno.wyswietlScenariusz(lista.get(indeks));
    }

    public void nastepnePytanie() {
        indeks++;
        wyswietlBiezacy();
    }

    // --------- ROZSTRZYGANIE PYTAŃ ZAMKNIĘTYCH -----------

    public void roztrzygnijWybor(int idx, long czas) {

        Scenariusze s = lista.get(indeks);

        // dobra odpowiedź = indeks 0
        if (idx == 0) {
            gracz.dodajPunkty(s.getDobra());
        }
        // zła odpowiedź = indeksy większe niż 0
        else {

            // jeśli krytyczna → koniec gry
            if (s.isKrytycznaZla()) {
                okno.pokazBladKrytyczny();
                return;
            }

            // zwykła zła → odejmujemy punkty
            gracz.dodajPunkty(-s.getZla());
        }

        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepnePytanie();
    }


    // --------- PYTANIA OTWARTE -----------

    public void obsluzOdpowiedzTekstowa(String txt, long czas) {

        Scenariusze s = lista.get(indeks);

        if (!s.isOtwartePytanie()) return;

        if (txt.trim().equalsIgnoreCase(s.getPoprawnaTekstowa())) {
            gracz.dodajPunkty(s.getDobra());
        } else {
            gracz.dodajPunkty(-s.getZla());
        }

        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepnePytanie();
    }


    // --------- INTERAKCJA DRAG & DROP (koło ratunkowe) -----------

    public void poprawnaInterakcja() {
        Scenariusze s = lista.get(indeks);
        gracz.dodajPunkty(s.getDobra());

        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepnePytanie();
    }

    public void blednaInterakcja() {
        Scenariusze s = lista.get(indeks);

        if (s.isKrytycznaZla()) {
            okno.pokazBladKrytyczny();
            return;
        }

        gracz.dodajPunkty(-s.getZla());
        okno.aktualizacjaPunktow(gracz.getPunkty());
        nastepnePytanie();
    }


    // --------- LISTA PYTAŃ -----------

    private void przygotujScenariusze() {

        lista.add(new Scenariusze(
                "Osoba topi się daleko od brzegu. Co robisz?",
                new String[]{
                        "Wzywasz pomoc",
                        "Wskakujesz bez sprzętu",
                        "Ignorujesz"
                },
                TypPytania.ZAMKNIETE,
                10,    // dobra odpowiedź +10
                5,     // zła odpowiedź -5
                true   // krytyczna zła odpowiedź kończy grę
        ));


        lista.add(new Scenariusze(
                "Jaki skrót ma Wodne Ochotnicze Pogotowie Ratunkowe?",
                "WOPR",
                TypPytania.OTWARTE,
                15,  // dobra
                0    // zła
        ));


        lista.add(new Scenariusze(
                "Przeciągnij koło ratunkowe do tonącego.",
                TypPytania.INTERAKCYJNE,
                20,   // dobra interakcja
                false // błędna NIE jest krytyczna
        ));

    }
}
