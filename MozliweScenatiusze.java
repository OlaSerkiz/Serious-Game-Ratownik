import java.util.ArrayList;
import java.util.List;

public class MozliweScenatiusze {

    private List<Scenariusze> lista = new ArrayList<>();
    private int indeks = 0;

    public MozliweScenatiusze() {

        // ===== PYTANIE ZAMKNIĘTE =====
        lista.add(new Scenariusze(
                "Osoba topi się daleko od brzegu. Co robisz?",
                new String[]{
                        "Wzywasz pomoc",
                        "Skaczesz bez sprzętu",
                        "Ignorujesz sytuację"
                },
                TypPytania.ZAMKNIETE,
                0,          // poprawna odpowiedź
                true        // błąd krytyczny przy złej
        ));
        lista.add(new Scenariusze(
                "Na wieży ratowniczej wywieszono CZERWONĄ flagę. Co to oznacza?",
                new String[]{"Woda jest ciepła", "Kąpiel jest zabroniona", "Można skakać na główkę"},
                TypPytania.ZAMKNIETE, 1, true
        ));
        lista.add(new Scenariusze(
                "Pływak wzywa pomocy, bo złapał go silny skurcz w nodze. Co powienien zrobić?",
                new String[]{"Panikuj i machaj rękami", "Połóż się na plecach i odpoczywaj", "Próbuj płynąć jak najszybciej"},
                TypPytania.ZAMKNIETE, 1, false
        ));

        // ===== PYTANIE OTWARTE =====
        lista.add(new Scenariusze(
                "Jaki skrót oznacza Wodne Ochotnicze Pogotowie Ratunkowe?",
                "WOPR",
                TypPytania.OTWARTE
        ));
        lista.add(new Scenariusze(
                "Ile uciśnięć klatki piersiowej wykonujemy w jednym cyklu RKO u dorosłego?",
                "30",
                TypPytania.OTWARTE
        ));
        lista.add(new Scenariusze(
                "Podaj ogólnoeuropejski numer alarmowy (1 cyfra na raz):",
                "112",
                TypPytania.OTWARTE
        ));

        // ===== PYTANIE INTERAKCYJNE =====
        lista.add(new Scenariusze(
                "Przeciągnij koło ratunkowe do tonącego.",
                TypPytania.INTERAKCYJNE
        ));
        lista.add(new Scenariusze(
                "Poszkodowany nie oddycha. Przeciągnij defibrylator AED, aby mu pomóc.",
                TypPytania.INTERAKCYJNE
        ));
        lista.add(new Scenariusze(
                "Widzisz coś niepokojącego daleko na horyzoncie. Użyj odpowiednigo sprzętu.",
                TypPytania.INTERAKCYJNE
        ));
        lista.add(new Scenariusze(
                "Jest burza z piorunami. Jaką flage powinieneś wywiesić",
                TypPytania.INTERAKCYJNE
        ));
    }

    public boolean maKolejny() {
        return indeks < lista.size();
    }

    public Scenariusze nastepny() {
        if (!maKolejny()) return null;
        return lista.get(indeks++);
    }

    public void resetuj() {
        indeks = 0;
    }
}
