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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TextField;

public class OknoGry extends Application {
    private Gra gra;
    private Label opisLabel;
    private Button[] przyciski = new Button[3];
    private Label punktyLabel;
    private ProgressBar pasekCzasu;
    private Timeline licznik;
    private long czasStart;
    private final int CZAS_NA_ODPOWIEDZ_MS = 10000; // 10 sekund
    private TextField poleTekstowe;
    private Button zatwierdzBtn;

    public double width = 900;
    public double height = 600;

    @Override
    public void start(Stage primaryStage) {
        // Tworzymy grę i przypisujemy okno
        gra = new Gra("Ratownik");
        gra.ustawOkno(this);

        // --- Tło ---
        Image obrazTla;
        try {
            obrazTla = new Image(getClass().getResourceAsStream("/images/plaza.jpg"));
        } catch (Exception e) {
            obrazTla = null;
        }

        StackPane center = new StackPane();
        if (obrazTla != null) {
            ImageView view = new ImageView(obrazTla);
            view.setPreserveRatio(false);
            view.setFitWidth(900);
            view.setFitHeight(450);
            center.getChildren().add(view);
        } else {
            center.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #F4E1C1);");
        }

        // --- Etykieta opisu ---
        opisLabel = new Label("Witaj! Kliknij START, aby rozpocząć dzień pracy ratownika.");
        opisLabel.setFont(Font.font(18));
        opisLabel.setTextFill(Color.WHITE);
        opisLabel.setWrapText(true);
        opisLabel.setMaxWidth(700);
        opisLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 3, 0, 0, 0);");

        // --- Przyciski odpowiedzi ---
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

        // --- Pole tekstowe (inicjalizujemy PRZED VBox'em)
        poleTekstowe = new TextField();
        poleTekstowe.setPromptText("Wpisz odpowiedź...");
        poleTekstowe.setMaxWidth(300);
        poleTekstowe.setVisible(false);

        zatwierdzBtn = new Button("Zatwierdź");
        zatwierdzBtn.setVisible(false);
        zatwierdzBtn.setOnAction(e -> {
            String wpisana = poleTekstowe.getText();
            zatrzymajLicznik();
            long czasReakcji = System.currentTimeMillis() - czasStart;
            gra.obsluzOdpowiedzTekstowa(wpisana, czasReakcji);
            poleTekstowe.clear();
            poleTekstowe.setVisible(false);
            zatwierdzBtn.setVisible(false);
        });

        // --- Panel centralny (dopiero teraz tworzymy, gdy wszystko istnieje)
        VBox panelCentralny = new VBox(15, opisLabel, odpowiedziBox, poleTekstowe, zatwierdzBtn);
        panelCentralny.setAlignment(Pos.CENTER);
        panelCentralny.setPadding(new Insets(20));

        StackPane warstwa = new StackPane(center, panelCentralny);

        // --- Pasek tytułu ---
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font(28));
        tytul.setTextFill(Color.WHITE);
        tytul.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 0, 0);");
        HBox top = new HBox(tytul);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: rgba(0,0,0,0.25);");

        // --- Dolny pasek ---
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

        Scene scena = new Scene(root, width, height, Color.LIGHTBLUE);
        primaryStage.setTitle("Ratownik - gra szkoleniowa");
        primaryStage.setScene(scena);
        primaryStage.show();

        // --- Przycisk START ---
        Button startBtn = new Button("START");
        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            disablePrzyciski(false);
            gra.rozpocznij();
        });
        odpowiedziBox.getChildren().add(0, startBtn); // dodaj START na górze

        disablePrzyciski(true);
    }

    public void pokazKoniecDnia(Gracz g) {
        opisLabel.setText("🏁 Koniec dnia. Wynik: " + g.getPunkty() + " pkt");
        disablePrzyciski(true);
        poleTekstowe.setVisible(false);
        zatwierdzBtn.setVisible(false);
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
                gra.roztrzygnijWybor(1, ms);
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
        poleTekstowe.setVisible(false);
        zatwierdzBtn.setVisible(false);
    }

    public void aktualizacjaPunktow(int punkty) {
        punktyLabel.setText("Punkty: " + punkty);
    }

    public void wyswietlScenariusz(Scenariusze s) {
        opisLabel.setText("📢 " + s.getOpisy());
        startLicznik();

        if (s.isOtwartePytanie()) {
            for (Button b : przyciski) b.setVisible(false);
            poleTekstowe.setVisible(true);
            zatwierdzBtn.setVisible(true);
        } else {
            poleTekstowe.setVisible(false);
            zatwierdzBtn.setVisible(false);
            String[] odp = s.getOdpowiedzi();
            for (int i = 0; i < 3; i++) {
                przyciski[i].setText((i + 1) + ". " + odp[i]);
                przyciski[i].setVisible(true);
                przyciski[i].setDisable(false);
            }
        }
    }
}
