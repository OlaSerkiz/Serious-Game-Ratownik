import java.util.ArrayList;
import java.util.List;

public class MozliweScenatiusze {

    private List<Scenariusze> bazaWszystkichPytan = new ArrayList<>();
    private List<Scenariusze> wybraneDlaPoziomu = new ArrayList<>();
    private int obecnyIndeks = 0;

    public MozliweScenatiusze() {
        inicjalizujBaze();
    }
    private void inicjalizujBaze() {
        // --- POZIOM 1: PYTANIA ZAMKNIĘTE ---
        bazaWszystkichPytan.add(new Scenariusze("Osoba topi się daleko od brzegu. Co robisz?",
                new String[]{"Wołam pomoc i rzucam koło", "Skaczę na główkę", "Czekam"}, 0, 1));
        bazaWszystkichPytan.add(new Scenariusze("Na wieży ratowniczej wywieszono CZERWONĄ flagę. Co to oznacza?",
                new String[]{"Kąpiel jest zabroniona", "Woda jest ciepła", "Można skakać na główkę"}, 0, 1));
        bazaWszystkichPytan.add(new Scenariusze("Pływak wzywa pomocy, bo złapał go silny skurcz w nodze. Co powienien zrobić?",
                new String[]{"Połóż się na plecach i odpoczywaj", "Panikuj i machaj rękami", "Próbuj płynąć jak najszybciej"}, 0, 1));
        bazaWszystkichPytan.add(new Scenariusze("Zauważyłeś osobę, która nagle złapała się za klatkę piersiową i upadła na piasek. Co robisz w pierwszej kolejności?",
                new String[]{"Sprawdzam przytomność i oddech", "Podaję butelkę zimnej wody", "Czekam, aż sama wstanie"}, 0, 1));
        bazaWszystkichPytan.add(new Scenariusze("Widzisz grupę młodzieży skaczącą 'na główkę' z falochronu. Twoja reakcja?",
                new String[]{"Używam gwizdka i nakazuję opuszczenie falochronu","Ignoruję to, to nie mój teren", "Robię im zdjęcie"}, 0, 1));
        bazaWszystkichPytan.add(new Scenariusze("Plażowicz skarży się na silne nudności, ból głowy i ma dreszcze po całym dniu na słońcu. Co podejrzewasz?",
                new String[]{"Udar słoneczny", "Zatrucie pokarmowe", "Alergię na piasek"}, 0, 1));


        // ===== PYTANIE OTWARTE =====
        Scenariusze s1 = new Scenariusze("Jaki skrót oznacza Wodne Ochotnicze Pogotowie Ratunkowe?", null, -1, 2);
        s1.setPoprawnaOdpowiedzTekstowa("WOPR");
        bazaWszystkichPytan.add(s1);
        Scenariusze s2 = new Scenariusze("Ile uciśnięć klatki piersiowej wykonujemy w jednym cyklu RKO u dorosłego?", null, -1, 2);
        s2.setPoprawnaOdpowiedzTekstowa("30");
        bazaWszystkichPytan.add(s2);
        Scenariusze s3 = new Scenariusze("Podaj ogólnoeuropejski numer alarmowy (1 cyfra na raz):", null, -1, 2);
        s3.setPoprawnaOdpowiedzTekstowa("112");
        bazaWszystkichPytan.add(s3);
        Scenariusze s4 = new Scenariusze("Jaki kolor ma flaga, która pozwala na wejście do wody?", null, -1, 2);
        s4.setPoprawnaOdpowiedzTekstowa("Biały");
        bazaWszystkichPytan.add(s4);
        Scenariusze s5 = new Scenariusze("Jak nazywają się toksyczne mikroorganizmy, których zakwit powoduje wywieszenie czerwonej flagi?", null, -1, 2);
        s5.setPoprawnaOdpowiedzTekstowa("Sinice");
        bazaWszystkichPytan.add(s5);
        Scenariusze s6 = new Scenariusze("Jak nazywa się długa, sztywna płyta służąca do transportu poszkodowanego z podejrzeniem urazu kręgosłupa?", null, -1, 2);
        s6.setPoprawnaOdpowiedzTekstowa("Deska");
        bazaWszystkichPytan.add(s6);

        // --- POZIOM 3: ZADANIA INTERAKCYJNE ---
        Scenariusze i1 = new Scenariusze("Przeciągnij koło ratunkowe do tonącego.", null, -1, 3);
        i1.setInterakcyjne("MORZE", new String[]{"kolo.png", "pilka.png", "recznik.png"}, "kolo.png");
        bazaWszystkichPytan.add(i1);
        Scenariusze i2 = new Scenariusze("Poszkodowany nie oddycha. Wybierz odpowiedni sprzęt, aby mu pomóc.", null, -1, 3);
        i2.setInterakcyjne("PIERWSZA_POMOC", new String[]{"defibrylator.png", "strzykawka.png", "tlen.png"}, "defibrylator.png");
        bazaWszystkichPytan.add(i2);
        Scenariusze i3 = new Scenariusze("Widzisz coś niepokojącego daleko na horyzoncie. Użyj odpowiednigo sprzętu.", null, -1, 3);
        i3.setInterakcyjne("HORYZONT", new String[]{"okulary.png", "teleskop.png","lornetka.png"}, "lornetka.png");
        bazaWszystkichPytan.add(i3);
        Scenariusze i4 = new Scenariusze("Jest burza z piorunami. Jaką flage powinieneś wywiesić", null, -1, 3);
        i4.setInterakcyjne("WIEZA", new String[]{"zoltaflaga.png", "czerwonaflaga.png", "bialaflaga.png"}, "czerwonaflaga.png");
        bazaWszystkichPytan.add(i4);
        Scenariusze i5 = new Scenariusze("Musisz pilnie wezwać wsparcie drugiej wieży ratowniczej. Czego użyjesz do komunikacji?", null, -1, 3);
        i5.setInterakcyjne("KOMUNIKACJA", new String[]{"krotkofalowka.png", "telefon.png", "gwizdek.png"}, "krotkofalowka.png");
        bazaWszystkichPytan.add(i5);
        Scenariusze i6 = new Scenariusze("Dziecko wchodzi do głębokiej wody, a nie potrafi pływać. Co powinno mieć na sobie?", null, -1, 3);
        i6.setInterakcyjne("DZIECKO", new String[]{"pletwy.png", "maska.png", "rekawki.png"}, "rekawki.png");
        bazaWszystkichPytan.add(i6);
    }
    public void ustawNaPoziom(int nr) {
        wybraneDlaPoziomu.clear();
        for (Scenariusze s : bazaWszystkichPytan) {
            if (s.getPoziom() == nr) {
                wybraneDlaPoziomu.add(s);
            }
        }
        obecnyIndeks = 0;
    }

    public boolean maKolejny() { return obecnyIndeks < wybraneDlaPoziomu.size(); }
    public Scenariusze pobierz() {
        if (maKolejny()) {
            return wybraneDlaPoziomu.get(obecnyIndeks++);
        }
        return null;
    }

}