import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RaportZGry {
    public static void zapis(Gracz g) {
        try (FileWriter w = new FileWriter("raport_z_gry.txt", true)) {
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            w.write("Data: " + data + " | Ratownik: " + g.getImie() + " | Wynik: " + g.getPunkty() + "\n");
            System.out.println("Raport zapisany pomyślnie.");
        } catch (IOException e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }
    }
}