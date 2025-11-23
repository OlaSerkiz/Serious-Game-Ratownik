import java.util.ArrayList;
import java.util.List;

public class MozliweScenatiusze {

    private List<Scenariusze> lista = new ArrayList<>();
    private int indeks = 0;

    public MozliweScenatiusze() {

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
                "Turysta ma problemy z oddychaniem. Co robisz?",
                new String[]{
                        "Każesz spokojnie oddychać",
                        "Ignorujesz sytuację",
                        "Wzywasz pomoc i udzielasz pierwszej pomocy"
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

    public boolean maKolejnyScenariusz() {
        return indeks < lista.size();
    }

    public Scenariusze pobierzKolejnyScenariusz() {
        if (!maKolejnyScenariusz()) return null;
        return lista.get(indeks++);
    }

    public void resetuj() {
        indeks = 0;
    }
}
