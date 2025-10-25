public class Gra {
    private Gracz gracz;
    private MozliweScenatiusze zarzadca;
    private Punktacja punktacja;
    private Czas czas;
    private Scenariusze aktualny;
    private OknoGry okno;

    public Gra(String imie) {
        this.gracz = new Gracz(imie);
        this.zarzadca = new MozliweScenatiusze();
        this.punktacja = new Punktacja();
        this.czas = new Czas();
    }

    public void ustawOkno(OknoGry okno){
        this.okno = okno;
    }

    public void rozpocznij(){
        zarzadca.resetuj();
        wyswietlKolejny();
    }

    public void wyswietlKolejny() {
        if(!zarzadca.maKolejnyScenariusz()) {
            koniecGry();
            return;
        }
        aktualny = zarzadca.pobierzKolejnyScenariusz();
        czas.startPomiaru();
        if (okno != null) okno.wyswietlScenatriusz(aktualny);
    }

    public void roztrzygnijWybor(int indeks, long czasReakciMs) {
        int ocena = aktualny.ocenOdpowiedz(indeks);
        int bonus = czas.obliczDodatkowePunkty(czasReakciMs);
        punktacja.zapiszWynik(gracz, ocena + bonus);
        /**
         * zgodnie z założeniem, że gra ma charakter eduakcyjny wprowadzam błąd krytyczny
         * jeżeli użytkowik ma sume punktów -5 kończy gre
         */
        if(ocena <= -5) {
            if(okno != null) okno.pokazBladKrytyczny();
            RaportZGry.zapis(gracz);
            return;
        }

        if (okno != null) okno.aktualizacjaPunktow(gracz.getPunkty());
        wyswietlKolejny();
    }

    private void koniecGry(){
        if (okno != null) {
            okno.pokazKoniecDnia(gracz);
        }
        RaportZGry.zapis(gracz);
    }
}
