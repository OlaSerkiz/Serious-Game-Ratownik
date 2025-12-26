public class Punktacja {
    public void zapiszWynik(Gracz gracz, int punkty){
        if (punkty >= 0) gracz.dodajPunkty(punkty);
        else gracz.odejmijPunkty(-punkty);
    }
}
