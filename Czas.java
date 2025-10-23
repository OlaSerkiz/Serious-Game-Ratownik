public class Czas {
    private long start;

    public void startPomiaru(){
        start = System.currentTimeMillis();
    }

    public long stopPomiaru(){
        return System.currentTimeMillis() - start;
    }

    /**
     * Zgodnie z założeniem że ma się działać szybko, charakter gry
     * dodaje punkty z szybkość +1 - 3000ms, 0 <6000ms
     * możliwość ujemnych punktów!!! -> jeżeli za wolno
     */

    public int obliczDodatkowePunkty(long czasMs){
        if (czasMs < 3000) return 1;
        if (czasMs < 6000) return 0;
        return -1;
    }
}
