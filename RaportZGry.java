import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RaportZGry {

    public static void zapis(Gracz g){
        try (FileWriter w = new FileWriter("raport_z_gry.txt", true)){
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            w.write("Gracz: " + g.getImie() + " | Punkty: " + g.getPunkty() + " | Data: " + data + "\n");
            System.out.println("Raport z gry został zapisany do raport_z_gry.txt");
        } catch (IOException e) {
            System.out.println("Błąd zapisu raportu: " + e.getLocalizedMessage());
        }
    }
}
