import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OknoGry extends Application {
    private Gra gra;
    private Label opisLabel;
    private Button[] przyciski = new Button[3];
    private Label punktyLabel;
    private ProgressBar pasekCzasu;
    private Timeline licznik;
    private long czasStart;
    private final int CZAS_NA_ODPOWIEDZ_MS = 10000; // 10 sekund

    public double width = 900;
    public double height = 600;
    @Override
    public void start(Stage primaryStage) {
        // --- Tworzymy grę i przypisujemy okno
        gra = new Gra("Ratownik");
        gra.ustawOkno(this);

        //double width = 900;
        //double height = 600;

        // --- Rysowana plaża
        Canvas canvas = new Canvas(width,height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        rysujPlaze(gc);

        StackPane rysunek = new StackPane(canvas);

        // --- Opis i przyciski
        opisLabel = new Label("Witaj! Kliknij START, aby rozpocząć dzień pracy ratownika.");
        opisLabel.setFont(Font.font(18));
        opisLabel.setTextFill(Color.WHITE);
        opisLabel.setWrapText(true);
        opisLabel.setMaxWidth(700);
        opisLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 3, 0, 0, 0);");

        VBox odpowiedziBox = new VBox(10);
        odpowiedziBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 3; i++) {
            Button b = new Button("Odpowiedź " + (i + 1));
            b.setMinWidth(600);
            b.setFont(Font.font(14));
            final int idx = i;
            b.setOnAction(e -> {
                zatrzymajLicznik();
                long czasReakcji = System.currentTimeMillis() - czasStart;
                disablePrzyciski(true);
                gra.roztrzygnijWybor(idx, czasReakcji);
            });
            przyciski[i] = b;
            odpowiedziBox.getChildren().add(b);
        }

        VBox panelCentralny = new VBox(15, opisLabel, odpowiedziBox);
        panelCentralny.setAlignment(Pos.CENTER);
        panelCentralny.setPadding(new Insets(20));

        StackPane warstwa = new StackPane(rysunek, panelCentralny);

        // --- Górny pasek tytułu
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font(28));
        tytul.setTextFill(Color.WHITE);
        tytul.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 0, 0);");
        HBox top = new HBox(tytul);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: rgba(0,0,0,0.25);");

        // --- Dolny pasek z punktami i paskiem czasu
        punktyLabel = new Label("Punkty: 0");
        punktyLabel.setTextFill(Color.WHITE);
        punktyLabel.setFont(Font.font(16));

        pasekCzasu = new ProgressBar(1);
        pasekCzasu.setPrefWidth(300);

        HBox bottom = new HBox(20, punktyLabel, pasekCzasu);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: rgba(0,0,0,0.35);");

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(warstwa);
        root.setBottom(bottom);

        Scene scena = new Scene(root, 900, 600, Color.LIGHTBLUE);
        primaryStage.setTitle("Ratownik - gra szkoleniowa");
        primaryStage.setScene(scena);
        primaryStage.show();

        disablePrzyciski(true);

        // --- Przycisk startu
        Button startBtn = new Button("START");
        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            disablePrzyciski(false);
            gra.rozpocznij();
        });
        ((VBox) panelCentralny.getChildren().get(1)).getChildren().addFirst(startBtn);

        // --- Przycisk resetu do dodania
        Button restetBtn = new Button("RESET");
    }

    /**
     * Rysuje prostą plażę z niebem, morzem, piaskiem i słońcem.
     */
    private void rysujPlaze(GraphicsContext gc) {
        double skyHeight = height / 3;

        // Niebo
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0, 0, width, skyHeight);

        // Chmurki
        gc.setFill(Color.rgb(255,255,255,0.8));
        gc.fillOval(100,60,120,60);
        gc.fillOval(160,40,130,70);
        gc.fillOval(220,60,120,60);
        gc.fillOval(450,90,140,80);
        gc.fillOval(515,65,120,60);

        /**
         * Nie wiem czemu nie działa morze??
         * Miało być, niebo to 1/3 wysokości
         * a plaża i morze 2/3 tak po skosie, żeby nie było takiej prostej linii
         * + do dopisania jeszcze fale w różnm kolorze
         */

        // Morze
        for (int i = 0; i < 900; i += 10) {
            double intensity = 0.5 + 0.5 * Math.sin(i / 50.0);
            gc.setFill(Color.color(0, 0, 0.8, intensity));
            gc.fillRect(i, 300, 10, 300);
        }

        // Piasek
        gc.setFill(Color.rgb(250, 240,150));
        gc.fillRect(0, skyHeight, width, height - skyHeight);

        // Słońce
        gc.setFill(Color.YELLOW);
        gc.fillOval(700, 50, 120, 120);

        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(5);
        for (int i = 0; i < 360; i += 20) {
            double angle = Math.toRadians(i);
            double x1 = 760 + 70 * Math.cos(angle);
            double y1 = 110 + 70 * Math.sin(angle);
            double x2 = 760 + 100 * Math.cos(angle);
            double y2 = 110 + 100 * Math.sin(angle);
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Wieża ratownika prawie dobrze
        gc.setFill(Color.WHITE);
        gc.fillRect(650, 300, 100, 100);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(10);
        gc.strokeLine(660, 400, 640, 460);
        gc.strokeLine(740, 400, 760, 460);

        // ----- Łódka wymaga dopracowania-----
        gc.setFill(Color.ORANGE);
        gc.fillPolygon(
                new double[]{300, 450, 470, 280},
                new double[]{470, 470, 490, 490},
                4
        );
    }



    public void pokazKoniecDnia(Gracz g) {
        opisLabel.setText("🏁 Koniec dnia. Wynik: " + g.getPunkty() + " pkt");
        disablePrzyciski(true);
    }

    private void disablePrzyciski(boolean stan) {
        for (Button b : przyciski) b.setDisable(stan);
    }

    private void startLicznik() {
        pasekCzasu.setProgress(1.0);
        if (licznik != null) licznik.stop();
        final int ms = CZAS_NA_ODPOWIEDZ_MS;
        czasStart = System.currentTimeMillis();
        licznik = new Timeline(new KeyFrame(Duration.millis(50), evt -> {
            long uplynelo = System.currentTimeMillis() - czasStart;
            double frac = 1.0 - ((double) uplynelo / ms);
            pasekCzasu.setProgress(Math.max(0, frac));
            if (uplynelo >= ms) {
                zatrzymajLicznik();
                disablePrzyciski(true);
                gra.roztrzygnijWybor(1, ms); // automatycznie wybiera środkową odpowiedź
            }
        }));
        licznik.setCycleCount(Timeline.INDEFINITE);
        licznik.play();
    }

    private void zatrzymajLicznik() {
        if (licznik != null) licznik.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void pokazBladKrytyczny() {
        opisLabel.setText("❌ Popełniłeś krytyczny błąd! Koniec gry.");
        disablePrzyciski(true);
    }

    public void aktualizacjaPunktow(int punkty) {
        punktyLabel.setText("Punkty: " + punkty);
    }

    public void wyswietlScenatriusz(Scenariusze s) {
        opisLabel.setText("📢 " + s.getOpisy());
        String[] odp = s.getOdpowiedzi();
        for (int i = 0; i < 3; i++) {
            przyciski[i].setText((i + 1) + ". " + odp[i]);
            przyciski[i].setDisable(false);
        }
        startLicznik();
    }
}
