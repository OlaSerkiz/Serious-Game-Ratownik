import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RaportZGry {

    private static final String PLIK_RANKINGU = "ranking.txt";

    public static void zapiszWynik(String imie, int punkty, int poziom) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data = dtf.format(LocalDateTime.now());

        String wpis = String.format("[%s] Gracz: %s | Punkty: %d | Poziom: %d%n",
                data, imie, punkty, poziom);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLIK_RANKINGU, true))) {
            writer.write(wpis);
            System.out.println("Raport: Wynik został zapisany w pliku " + PLIK_RANKINGU);
        } catch (IOException e) {
            System.err.println("Błąd raportowania: " + e.getMessage());
        }
    }
}