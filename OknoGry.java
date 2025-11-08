import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private TextField poleTekstowe;
    private Button zatwierdzBtn;

    // Dodatkowe przyciski menu bocznego
    private Button startBtn;
    private Button stopBtn;
    private Button resetBtn;
    private Button wznowBtn;

    // Layouty (teraz globalne)
    private BorderPane rootPane;
    private StackPane centerStack;
    private VBox menuStartowe;
    private VBox panelCentralny;
    private VBox panelBoczny; // po prawej stronie

    public double width = 900;
    public double height = 600;

    @Override
    public void start(Stage primaryStage) {

        gra = new Gra("Ratownik");
        gra.ustawOkno(this);

        // --- TŁO ---
        Image obrazTla = null;
        try {
            obrazTla = new Image(getClass().getResourceAsStream("/images/plaza.jpg"));
        } catch (Exception ignored) { }

        centerStack = new StackPane();
        if (obrazTla != null) {
            ImageView view = new ImageView(obrazTla);
            view.setPreserveRatio(false);
            view.setFitWidth(width);
            view.setFitHeight(height - 120); // zostaw miejsce na top + bottom
            centerStack.getChildren().add(view);
        } else {
            centerStack.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #F4E1C1);");
        }

        // --- GÓRNY TYTUŁ ---
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font("Arial", 34));
        tytul.setTextFill(Color.WHITE);
        tytul.setStyle("-fx-effect: dropshadow(gaussian, black, 6, 0, 0, 0); -fx-font-weight: bold;");

        StackPane top = new StackPane(tytul);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(12, 0, 12, 0));
        top.setStyle("-fx-background-color: rgba(0,0,0,0.25);");

        // --- DOLNY PASEK ---
        punktyLabel = new Label("Punkty: 0");
        punktyLabel.setTextFill(Color.WHITE);
        punktyLabel.setFont(Font.font(16));

        pasekCzasu = new ProgressBar(1);
        pasekCzasu.setPrefWidth(300);

        HBox bottom = new HBox(20, punktyLabel, pasekCzasu);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: rgba(0,0,0,0.35);");

        // --- MENU STARTOWE ---
        startBtn = new Button("▶ START");
        startBtn.setMinWidth(220);
        startBtn.setFont(Font.font(18));
        startBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95); -fx-border-color: #333; -fx-border-width: 1; -fx-font-weight: bold;"
        );

        Label powitanie = new Label("Witaj w symulacji pracy ratownika!");
        powitanie.setFont(Font.font(22));
        powitanie.setTextFill(Color.WHITE);
        powitanie.setStyle("-fx-effect: dropshadow(gaussian, black, 4, 0, 0, 0);");

        menuStartowe = new VBox(18, powitanie, startBtn);
        menuStartowe.setAlignment(Pos.CENTER);
        menuStartowe.setTranslateY(-80);

        // --- PRZYCISKI BOCZNE (po prawej)
        wznowBtn = new Button("▶ WZNÓW");
        stopBtn = new Button("⏸ STOP");
        resetBtn = new Button("🔁 RESET");

        wznowBtn.setMinWidth(120);
        stopBtn.setMinWidth(120);
        resetBtn.setMinWidth(120);

        wznowBtn.setFont(Font.font(14));
        stopBtn.setFont(Font.font(14));
        resetBtn.setFont(Font.font(14));

        panelBoczny = new VBox(10, wznowBtn, stopBtn, resetBtn);
        panelBoczny.setAlignment(Pos.TOP_CENTER);
        panelBoczny.setPadding(new Insets(20));
        panelBoczny.setStyle("-fx-background-color: rgba(0,0,0,0.28); -fx-border-color: white; -fx-border-width: 2;");
        panelBoczny.setVisible(false);

        // akcje bocznych przycisków (podłączymy później do logiki)
        wznowBtn.setOnAction(e -> wznowGre());
        stopBtn.setOnAction(e -> zatrzymajGre());
        resetBtn.setOnAction(e -> resetujGre());

        // --- PANEL GRY ---
        opisLabel = new Label("Dzień pracy ratownika – przygotuj się!");
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
            b.setOnAction(ev -> {
                zatrzymajLicznik();
                long czasReakcji = System.currentTimeMillis() - czasStart;
                disablePrzyciski(true);
                gra.roztrzygnijWybor(idx, czasReakcji);
            });
            przyciski[i] = b;
            odpowiedziBox.getChildren().add(b);
        }

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

        panelCentralny = new VBox(15, opisLabel, odpowiedziBox, poleTekstowe, zatwierdzBtn);
        panelCentralny.setAlignment(Pos.CENTER);
        panelCentralny.setPadding(new Insets(20));
        panelCentralny.setVisible(false);

        centerStack.getChildren().addAll(menuStartowe, panelCentralny);

        // --- GŁÓWNY UKŁAD ---
        rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(centerStack);
        rootPane.setRight(panelBoczny);
        rootPane.setBottom(bottom);

        Scene scena = new Scene(rootPane, width, height, Color.LIGHTBLUE);
        primaryStage.setTitle("Ratownik - gra szkoleniowa");
        primaryStage.setScene(scena);
        primaryStage.show();

        startBtn.setOnAction(e -> uruchomGre());

        disablePrzyciski(true);
    }

    // ============================= LOGIKA =============================

    private void uruchomGre() {
        // pokaż panel gry, ukryj menu startowe, pokaż panel boczny
        menuStartowe.setVisible(false);
        panelCentralny.setVisible(true);
        panelBoczny.setVisible(true);

        // włącz boczne przyciski (stop/reset/wznów)
        stopBtn.setDisable(true);
        resetBtn.setDisable(true);
        wznowBtn.setDisable(true);

        // odblokuj przyciski odpowiedzi i rozpocznij logikę gry
        disablePrzyciski(true);
        gra.rozpocznij();
    }

    private void wznowGre() {
        startLicznik();
        opisLabel.setText("▶ Gra wznowiona. Działaj dalej!");
        disablePrzyciski(false);
    }

    private void zatrzymajGre() {
        zatrzymajLicznik();
        disablePrzyciski(true);
        opisLabel.setText("⏸ Gra zatrzymana.");
    }

    private void resetujGre() {
        zatrzymajLicznik();
        gra.resetuj();
        opisLabel.setText("🔁 Gra zresetowana. Kliknij WZNÓW, by rozpocząć nową zmianę.");
        disablePrzyciski(true);
        poleTekstowe.setVisible(false);
        zatwierdzBtn.setVisible(false);
    }

    private void disablePrzyciski(boolean stan) {
        for (Button b : przyciski) if (b != null) b.setDisable(stan);
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
                gra.roztrzygnijWybor(1, ms); // domyślne wybieranie po czasie
            }
        }));
        licznik.setCycleCount(Timeline.INDEFINITE);
        licznik.play();
    }

    private void zatrzymajLicznik() {
        if (licznik != null) licznik.stop();
    }

    public void pokazBladKrytyczny() {
        opisLabel.setText("❌ Popełniłeś krytyczny błąd! Koniec gry.");
        disablePrzyciski(true);
        poleTekstowe.setVisible(false);
        zatwierdzBtn.setVisible(false);
    }

    public void pokazKoniecDnia(Gracz g) {
        opisLabel.setText("🏁 Koniec dnia. Wynik: " + g.getPunkty() + " pkt");
        disablePrzyciski(true);
        poleTekstowe.setVisible(false);
        zatwierdzBtn.setVisible(false);
    }

    public void aktualizacjaPunktow(int punkty) {
        punktyLabel.setText("Punkty: " + punkty);
    }

    public void wyswietlScenariusz(Scenariusze s) {
        // pokaz scenariusz i włącz licznik
        opisLabel.setText("📢 " + s.getOpisy());
        startLicznik();

        if (s.isOtwartePytanie()) {
            // pytanie otwarte
            for (Button b : przyciski) if (b != null) b.setVisible(false);
            poleTekstowe.setVisible(true);
            zatwierdzBtn.setVisible(true);
        } else {
            // pytanie zamknięte (przyciski)
            poleTekstowe.setVisible(false);
            zatwierdzBtn.setVisible(false);
            String[] odp = s.getOdpowiedzi();
            for (int i = 0; i < 3; i++) {
                if (przyciski[i] != null) {
                    przyciski[i].setText((i + 1) + ". " + (i < odp.length && odp[i] != null ? odp[i] : ""));
                    przyciski[i].setVisible(true);
                    przyciski[i].setDisable(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
