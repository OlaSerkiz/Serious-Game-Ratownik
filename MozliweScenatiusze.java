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
        // Pytanie o pierwszą pomoc
        lista.add(new Scenariusze(
                "Zauważyłeś osobę, która nagle złapała się za klatkę piersiową i upadła na piasek. Co robisz w pierwszej kolejności?",
                new String[]{"Podaję butelkę zimnej wody", "Sprawdzam przytomność i oddech", "Czekam, aż sama wstanie"},
                TypPytania.ZAMKNIETE, 1, true
        ));
        lista.add(new Scenariusze(
                "Widzisz grupę młodzieży skaczącą 'na główkę' z falochronu. Twoja reakcja?",
                new String[]{"Ignoruję to, to nie mój teren", "Robię im zdjęcie", "Używam gwizdka i nakazuję opuszczenie falochronu"},
                TypPytania.ZAMKNIETE, 2, false
        ));

        lista.add(new Scenariusze(
                "Plażowicz skarży się na silne nudności, ból głowy i ma dreszcze po całym dniu na słońcu. Co podejrzewasz?",
                new String[]{"Udar słoneczny", "Zatrucie pokarmowe", "Alergię na piasek"},
                TypPytania.ZAMKNIETE, 0, false
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
        lista.add(new Scenariusze(
                "Jaki kolor ma flaga, która pozwala na wejście do wody?",
                "Biały",
                TypPytania.OTWARTE
        ));
        lista.add(new Scenariusze(
                "Jak nazywają się toksyczne mikroorganizmy, których zakwit powoduje wywieszenie czerwonej flagi (kolor wody staje się zielony)?",
                "Sinice",
                TypPytania.OTWARTE
        ));
        lista.add(new Scenariusze(
                "Jak nazywa się długa, sztywna płyta służąca do transportu poszkodowanego z podejrzeniem urazu kręgosłupa?",
                "Deska",
                TypPytania.OTWARTE
        ));

        // ===== PYTANIE INTERAKCYJNE =====
        lista.add(new Scenariusze(
                "Przeciągnij koło ratunkowe do tonącego.",
                TypPytania.INTERAKCYJNE,
                new String[]{"kolo.png", "pilka.png", "recznik.png"},
                "kolo.png",
                "MORZE"
        ));
        lista.add(new Scenariusze(
                "Poszkodowany nie oddycha. Wybierz odpowiedni sprzęt, aby mu pomóc.",
                TypPytania.INTERAKCYJNE,
                new String[]{"defibrylator.png", "strzykawka.png", "tlen.png"},
                "defibrylator.png",
                "PIERWSZA_POMOC"
        ));
        lista.add(new Scenariusze(
                "Widzisz coś niepokojącego daleko na horyzoncie. Użyj odpowiednigo sprzętu.",
                TypPytania.INTERAKCYJNE,
                new String[]{"lornetka.png", "okulary.png", "teleskop.png"},
                "lornetka.png",
                "HORYZONT"
        ));
        lista.add(new Scenariusze(
                "Jest burza z piorunami. Jaką flage powinieneś wywiesić",
                TypPytania.INTERAKCYJNE,
                new String[]{"czerwonaflaga.png", "bialaflaga.png", "zoltaflaga.png"},
                "czerwonaflaga.png",
                "WIEZA"
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