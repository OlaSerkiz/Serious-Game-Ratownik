import java.util.ArrayList;
import java.util.List;

public class MozliweScenatiusze {
    private List<Scenariusze> lista;
    private int indeks;

    public MozliweScenatiusze() {
        lista = new ArrayList<>();
        indeks = 0;
        wprowadzajScenariusze();
    }

    private void wprowadzajScenariusze(){
        lista.add(new Scenariusze(
                "Osoba topiąca się daleko od brzegu.",
                new String[]{"Płyniesz wpław", "Dzwonisz po pomoc i działasz", "Ignorujesz sytuacje"},
                1,
                2
        ));

        lista.add(new Scenariusze(
                "Turysta ma problemy z oddychaniem.",
                new String[]{"Mówisz, żeby spokojnie oddychał", "Ignorujesz sytuacje", "Podajesz tlen i dzwonisz do pogotowie"},
                2,
                1
        ));

        /**
        * dodać więcej scenariuszy -> rozwinąć żeby nie było że mało
         */
    }

    public boolean maKolejnyScenariusz(){
        return indeks < lista.size();
    }

    public Scenariusze pobierzKolejnyScenariusz(){
        if (!maKolejnyScenariusz()) return null;
        return lista.get(indeks++);
    }
}

