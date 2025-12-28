import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OknoGry extends Application {

    private Gra gra;
    private Label opisLabel, punktyLabel, wynikKoncowyLabel, pasekInstrukcji;
    private Button[] przyciski = new Button[3];
    private ProgressBar pasekCzasu;
    private Timeline licznik;
    private long czasStart;
    private final int CZAS_NA_ODPOWIEDZ_MS = 10000;

    private Button startBtn, stopBtn, resetBtn, wznowBtn, zatwierdzBtn;
    private TextField poleTekstowe;
    private TextField poleImienia;
    private VBox menuStartowe, panelPytania, panelBoczny, panelKoncowy;
    private StackPane centerStack;
    private Pane panelInterakcji;
    private ImageView czlowiek;
    private BorderPane root;

    private boolean czyPauza = false;
    private long czasPozostaly;

    @Override
    public void start(Stage stage) {
        gra = new Gra("Ratownik");
        gra.ustawOkno(this);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double prefW = screenBounds.getWidth() * 0.9;
        double prefH = screenBounds.getHeight() * 0.9;

        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a1a;");

        // ===== TŁO GŁÓWNE =====
        centerStack = new StackPane();
        try {
            Image obrazTla = new Image(getClass().getResourceAsStream("/images/plaza.jpg"));
            BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
            centerStack.setBackground(new Background(new BackgroundImage(obrazTla,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
        } catch (Exception e) {
            centerStack.setStyle("-fx-background-color: #34495e;");
        }

        // ===== GÓRNY PASEK =====
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font("System", FontWeight.BOLD, 26));
        tytul.setTextFill(Color.WHITE);
        HBox topBar = new HBox(tytul);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #3498db; -fx-border-width: 0 0 2 0;");
        root.setTop(topBar);

        // ===== PANEL BOCZNY =====
        wznowBtn = new Button("▶ WZNÓW");
        stopBtn = new Button("⏸ STOP");
        resetBtn = new Button("🔁 RESET");
        stylizujPrzycisk(wznowBtn, "#2980b9");
        stylizujPrzycisk(stopBtn, "#f39c12");
        stylizujPrzycisk(resetBtn, "#c0392b");
        panelBoczny = new VBox(20, wznowBtn, stopBtn, resetBtn);
        panelBoczny.setPadding(new Insets(30, 15, 15, 15));
        panelBoczny.setStyle("-fx-background-color: rgba(44, 62, 80, 0.9); -fx-border-color: #3498db; -fx-border-width: 0 0 0 2;");
        panelBoczny.setVisible(false);
        root.setRight(panelBoczny);

        // ===== MENU STARTOWE =====
        Label powitanie = new Label("Witaj w symulacji pracy ratownika!");
        powitanie.setFont(Font.font("System", FontWeight.BOLD, 28));
        powitanie.setTextFill(Color.WHITE);

        poleImienia = new TextField();
        poleImienia.setPromptText("Wpisz swoje imię...");
        poleImienia.setMaxWidth(300);
        poleImienia.setOnAction(e -> startBtn.fire());
        poleImienia.setStyle("-fx-font-size: 18; -fx-background-radius: 10;");

        startBtn = new Button("ROZPOCZNIJ DYŻUR");
        stylizujPrzycisk(startBtn, "#27ae60");
        startBtn.setPrefSize(280, 70);

        menuStartowe = new VBox(30, powitanie, poleImienia, startBtn);
        menuStartowe.setAlignment(Pos.CENTER);
        menuStartowe.setStyle("-fx-background-color: rgba(20, 30, 48, 0.95); -fx-background-radius: 30; -fx-padding: 60; -fx-border-color: #27ae60; -fx-border-width: 3;");
        menuStartowe.setMaxWidth(650);

        // ===== PASEK INSTRUKCJI =====
        pasekInstrukcji = new Label();
        pasekInstrukcji.setFont(Font.font("System", FontWeight.BOLD, 20));
        pasekInstrukcji.setTextFill(Color.CYAN);
        pasekInstrukcji.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-padding: 20; -fx-background-radius: 0 0 30 30;");
        pasekInstrukcji.setVisible(false);
        StackPane.setAlignment(pasekInstrukcji, Pos.TOP_CENTER);

        // ===== PANEL PYTANIA =====
        opisLabel = new Label();
        opisLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        opisLabel.setTextFill(Color.WHITE);
        opisLabel.setWrapText(true);
        opisLabel.setTextAlignment(TextAlignment.CENTER);

        VBox boxOdp = new VBox(15);
        boxOdp.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            przyciski[i] = new Button();
            przyciski[i].setMinWidth(500);
            stylizujPrzycisk(przyciski[i], "#3498db");
            przyciski[i].setOnAction(e -> { stopLicznik(); gra.roztrzygnijWybor(idx, System.currentTimeMillis() - czasStart); });
            boxOdp.getChildren().add(przyciski[i]);
        }

        poleTekstowe = new TextField();
        poleTekstowe.setPromptText("Wpisz odpowiedź...");
        poleTekstowe.setMaxWidth(350);
        poleTekstowe.setStyle("-fx-font-size: 20;");
        poleTekstowe.setOnAction(e -> zatwierdzBtn.fire());

        zatwierdzBtn = new Button("ZATWIERDŹ");
        stylizujPrzycisk(zatwierdzBtn, "#8e44ad");
        zatwierdzBtn.setOnAction(e -> {
            stopLicznik();
            gra.obsluzOdpowiedzTekstowa(poleTekstowe.getText(), System.currentTimeMillis() - czasStart);
        });

        panelPytania = new VBox(25, opisLabel, boxOdp, poleTekstowe, zatwierdzBtn);
        panelPytania.setAlignment(Pos.CENTER);
        panelPytania.setStyle("-fx-background-color: rgba(20, 30, 48, 0.95); -fx-background-radius: 30; -fx-padding: 40;");
        panelPytania.setVisible(false);

        // ===== PANEL KOŃCOWY =====
        wynikKoncowyLabel = new Label();
        wynikKoncowyLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        wynikKoncowyLabel.setTextAlignment(TextAlignment.CENTER);

        Button ponownaGraBtn = new Button("ZAGRAJ PONOWNIE");
        stylizujPrzycisk(ponownaGraBtn, "#27ae60");
        ponownaGraBtn.setOnAction(e -> resetujGre());

        panelKoncowy = new VBox(25, new Label("PODSUMOWANIE"), wynikKoncowyLabel, ponownaGraBtn);
        panelKoncowy.setAlignment(Pos.CENTER);
        panelKoncowy.setStyle("-fx-background-color: rgba(10, 10, 20, 0.98); -fx-background-radius: 30; -fx-padding: 50;");
        panelKoncowy.setVisible(false);

        // ===== PANEL INTERAKCJI =====
        panelInterakcji = new Pane();
        panelInterakcji.setVisible(false);
        czlowiek = createImg("czlowiek.png", 375, 150);

        // ===== DOLNY PASEK STATUSU =====
        punktyLabel = new Label("PUNKTY: 0");
        punktyLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        punktyLabel.setTextFill(Color.WHITE);
        pasekCzasu = new ProgressBar(1.0);
        pasekCzasu.setPrefWidth(500);
        HBox bottomBar = new HBox(60, punktyLabel, pasekCzasu);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(20));
        bottomBar.setStyle("-fx-background-color: #2c3e50;");
        root.setBottom(bottomBar);

        centerStack.getChildren().addAll(panelInterakcji, pasekInstrukcji, menuStartowe, panelPytania, panelKoncowy);
        root.setCenter(centerStack);

        // Akcje przycisków
        startBtn.setOnAction(e -> {
            String imie = poleImienia.getText().trim();
            if (imie.isEmpty()) imie = "Ratownik";
            gra = new Gra(imie);
            gra.ustawOkno(this);
            menuStartowe.setVisible(false);
            panelBoczny.setVisible(true);
            gra.rozpocznij();
        });

        stopBtn.setOnAction(e -> {
            if (!czyPauza && licznik != null) {
                stopLicznik();
                czasPozostaly = CZAS_NA_ODPOWIEDZ_MS - (System.currentTimeMillis() - czasStart);
                czyPauza = true;
                panelPytania.setDisable(true);
                panelInterakcji.setDisable(true);
            }
        });

        wznowBtn.setOnAction(e -> {
            if (czyPauza) {
                czasStart = System.currentTimeMillis() - (CZAS_NA_ODPOWIEDZ_MS - czasPozostaly);
                odnowLicznik();
                czyPauza = false;
                panelPytania.setDisable(false);
                panelInterakcji.setDisable(false);
            }
        });

        resetBtn.setOnAction(e -> resetujGre());

        Scene scene = new Scene(root, prefW, prefH);
        stage.setScene(scene);
        stage.setTitle("Ratownik - Symulacja szkoleniowa");
        stage.show();
    }

    private void ladujObrazkiInterakcyjne(Scenariusze s) {
        panelInterakcji.getChildren().clear();
        panelInterakcji.setStyle("-fx-background-color: transparent;");
        panelInterakcji.setMaxSize(850, 440); // 440 (woda) + 60 (pasek polecenia)
        StackPane.setAlignment(panelInterakcji, Pos.CENTER);
        // SCENA MORZA
        Pane scenaMorza = new Pane();
        scenaMorza.setPrefSize(850, 440);
        scenaMorza.setLayoutY(0);

        scenaMorza.setStyle("-fx-background-color: #3498db; -fx-background-radius: 0 0 20 20; -fx-border-color: #3498db; -fx-border-width: 0 2 2 2;");

        try {
            java.io.InputStream is = getClass().getResourceAsStream("/images/woda.jpg");
            if (is != null) {
                Image imgWody = new Image(is);
                ImageView tloWody = new ImageView(imgWody);
                tloWody.setFitWidth(850);
                tloWody.setFitHeight(440);

                tloWody.setLayoutX(0);
                tloWody.setLayoutY(0);
                scenaMorza.getChildren().add(tloWody);

                Rectangle clipTla = new Rectangle(850, 440);
                clipTla.setArcWidth(40);
                clipTla.setArcHeight(40);
                tloWody.setClip(clipTla);

                System.out.println("ImageView wody dodany do sceny.");
            }
        } catch (Exception e) {
            System.out.println("Błąd ImageView: " + e.getMessage());
        }

        //  TONĄCY
        czlowiek.setLayoutX(375);
        czlowiek.setLayoutY(120);
        Rectangle clip = new Rectangle(0, 0, 120, 90);
        czlowiek.setClip(clip);
        scenaMorza.getChildren().add(czlowiek);

        // Animacja
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), czlowiek);
        tt.setByY(10);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setAutoReverse(true);
        tt.play();

        //  PRZEDMIOTY
        double startY = 320;
        scenaMorza.getChildren().addAll(
                createDragImg("kolo.png", 150, startY, true, scenaMorza),
                createDragImg("pilka.png", 400, startY, false, scenaMorza),
                createDragImg("recznik.png", 650, startY, false, scenaMorza)
        );

        panelInterakcji.getChildren().addAll( scenaMorza);
    }

    private ImageView createDragImg(String name, double x, double y, boolean ok, Pane target) {
        ImageView iv = createImg(name, x, y);
        iv.setCursor(javafx.scene.Cursor.HAND);

        iv.setOnMouseDragged(e -> {
            if (!czyPauza) {
                iv.setLayoutX(e.getSceneX() - target.localToScene(0,0).getX() - 50);
                iv.setLayoutY(e.getSceneY() - target.localToScene(0,0).getY() - 50);
                iv.toFront();
            }
        });

        iv.setOnMouseReleased(e -> {
            if (!czyPauza) {
                if (iv.getBoundsInParent().intersects(czlowiek.getBoundsInParent())) {
                    if (ok) gra.poprawnaInterakcja(); else gra.blednaInterakcja();
                } else {
                    iv.setLayoutX(x);
                    iv.setLayoutY(y);
                }
            }
        });
        return iv;
    }

    // --- METODY POMOCNICZE I OBSŁUGA LOGIKI ---

    public void wyswietlScenariusz(Scenariusze s) {
        panelInterakcji.setVisible(false);
        panelPytania.setVisible(false);
        panelKoncowy.setVisible(false);
        pasekInstrukcji.setVisible(false);
        czyPauza = false;
        startLicznik();

        if (s.isInterakcyjne()) {
            panelInterakcji.setVisible(true);
            pasekInstrukcji.setVisible(true);
            pasekInstrukcji.setText(s.getOpisy());
            ladujObrazkiInterakcyjne(s);
        } else {
            panelPytania.setVisible(true);
            opisLabel.setText(s.getOpisy());
            if (s.isOtwartePytanie()) {
                poleTekstowe.clear();
                poleTekstowe.setVisible(true);
                zatwierdzBtn.setVisible(true);
                for (Button b : przyciski) b.setVisible(false);
            } else {
                poleTekstowe.setVisible(false);
                zatwierdzBtn.setVisible(false);
                for (int i = 0; i < 3; i++) {
                    przyciski[i].setText(s.getOdpowiedzi()[i]);
                    przyciski[i].setVisible(true);
                }
            }
        }
    }

    private void startLicznik() {
        stopLicznik();
        czasStart = System.currentTimeMillis();
        odnowLicznik();
    }

    private void odnowLicznik() {
        licznik = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            long uplynelo = System.currentTimeMillis() - czasStart;
            double postep = 1.0 - ((double) uplynelo / CZAS_NA_ODPOWIEDZ_MS);
            pasekCzasu.setProgress(Math.max(0, postep));
            if (uplynelo >= CZAS_NA_ODPOWIEDZ_MS) {
                stopLicznik();
                gra.roztrzygnijWybor(-1, CZAS_NA_ODPOWIEDZ_MS);
            }
        }));
        licznik.setCycleCount(Timeline.INDEFINITE);
        licznik.play();
    }

    private void stopLicznik() {
        if (licznik != null) licznik.stop();
    }

    public void aktualizacjaPunktow(int p) { punktyLabel.setText("PUNKTY: " + p); }

    private void stylizujPrzycisk(Button b, String kolor) {
        b.setStyle("-fx-background-color: " + kolor + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
    }

    private ImageView createImg(String name, double x, double y) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/" + name));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(100); iv.setFitHeight(100);
            iv.setPreserveRatio(true);
            iv.setLayoutX(x); iv.setLayoutY(y);
            return iv;
        } catch (Exception e) { return new ImageView(); }
    }

    public void pokazInfoOPoziomie(int nrPoziomu) {
        String tekst = "POZIOM " + nrPoziomu;
        Label infoLabel = new Label(tekst);
        infoLabel.setFont(Font.font("System", FontWeight.BOLD, 40));
        infoLabel.setTextFill(Color.GOLD);
        infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 30; -fx-background-radius: 20;");
        centerStack.getChildren().add(infoLabel);
        PauseTransition pauza = new PauseTransition(Duration.seconds(1.5));
        pauza.setOnFinished(e -> centerStack.getChildren().remove(infoLabel));
        pauza.play();
    }

    public void pokazKoniecDnia(Gracz g) {
        stopLicznik();
        panelPytania.setVisible(false);
        panelInterakcji.setVisible(false);
        pasekInstrukcji.setVisible(false);
        wynikKoncowyLabel.setText("DZIĘKUJEMY ZA DZIEŃ W PRACY, " + g.getImie() + "!\nTWÓJ WYNIK: " + g.getPunkty() + " PKT");
        wynikKoncowyLabel.setTextFill(Color.GOLD);
        panelKoncowy.setVisible(true);
    }

    public void pokazBladKrytyczny() {
        stopLicznik();
        panelPytania.setVisible(false);
        panelInterakcji.setVisible(false);
        wynikKoncowyLabel.setText("BŁĄD KRYTYCZNY!\nZOSTAJESZ ZWOLNIONY DYSCYPLINARNIE.");
        wynikKoncowyLabel.setTextFill(Color.RED);
        panelKoncowy.setVisible(true);
    }

    private void resetujGre() {
        stopLicznik();
        gra.resetuj();
        panelPytania.setVisible(false);
        panelKoncowy.setVisible(false);
        panelBoczny.setVisible(false);
        menuStartowe.setVisible(true);
        aktualizacjaPunktow(0);
    }

    public static void main(String[] args) { launch(args); }
}