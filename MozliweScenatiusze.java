import java.util.ArrayList;
import java.util.List;

public class MozliweScenatiusze {
    private List<Scenariusze> lista = new ArrayList<>();
    private int indeks = 0;

    public MozliweScenatiusze() {
        lista.add(new Scenariusze(
                "Osoba topiąca się daleko od brzegu.",
                new String[]{"Płyniesz wpław", "Dzwonisz po pomoc i działasz", "Ignorujesz sytuacje"},
                1,
                2, true
        ));

        /// dodnie dodatkowego pytania pogłebiającej (przesuwamy np. koło ratunkowe)
        /// dodaaie pyania zczytywania z klawiatury np. rozwiń skrót WOPR

        lista.add(new Scenariusze(
                "Turysta ma problemy z oddychaniem.",
                new String[]{"Mówisz, żeby spokojnie oddychał", "Ignorujesz sytuacje", "Podajesz tlen i dzwonisz do pogotowie"},
                2,
                1, true
        ));

        lista.add(new Scenariusze(
                "Jaki skrót ma 'Wodne Ochotnicze Pogotowie Ratunkowe'?",
                new String[]{},
                0, 10, true
        ));

        /**
        * dodać więcej scenariuszy -> rozwinąć żeby nie było że mało
         * potrzebny scenariusz żeby przesuwać elementy
         * - podawać koło
         * - coś z aprteczną
         * - płynięcie łódką
         *
         */
    }

    public boolean maKolejnyScenariusz(){
        return indeks < lista.size();
    }

    public Scenariusze pobierzKolejnyScenariusz(){
        if (!maKolejnyScenariusz()) return null;
        return lista.get(indeks++);
    }

    public void resetuj() {
        indeks = 0;
    }
}

