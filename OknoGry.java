import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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
    private final int CZAS_NA_ODPOWIEDZ_MS = 20000;

    private Button startBtn, stopBtn, resetBtn, wznowBtn, zatwierdzBtn;
    private TextField poleTekstowe, poleImienia;
    private VBox menuStartowe, panelPytania, panelBoczny, panelKoncowy;
    private StackPane centerStack;
    private Pane panelInterakcji;
    private ImageView celInterakcji;
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

        centerStack = new StackPane();
        ustawTloGlowne();

        // --- GÓRNY PASEK ---
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font("System", FontWeight.BOLD, 26));
        tytul.setTextFill(Color.WHITE);
        HBox topBar = new HBox(tytul);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #3498db; -fx-border-width: 0 0 2 0;");
        root.setTop(topBar);

        // --- PANEL STEROWANIA (PRAWY) ---
        wznowBtn = new Button("▶ WZNÓW");
        stopBtn = new Button("⏸ STOP");
        resetBtn = new Button("🔁 RESET");
        stylizujPrzycisk(wznowBtn, "#2980b9");
        stylizujPrzycisk(stopBtn, "#f39c12");
        stylizujPrzycisk(resetBtn, "#c0392b");
        panelBoczny = new VBox(20, wznowBtn, stopBtn, resetBtn);
        panelBoczny.setPadding(new Insets(30, 15, 15, 15));
        panelBoczny.setStyle("-fx-background-color: rgba(44, 62, 80, 0.95); -fx-border-color: #3498db; -fx-border-width: 0 0 0 2;");
        panelBoczny.setVisible(false);
        root.setRight(panelBoczny);

        // --- MENU STARTOWE ---
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
        menuStartowe.setEffect(new DropShadow(30, Color.BLACK));

        // --- PASEK INSTRUKCJI (INTERAKCJA) ---
        pasekInstrukcji = new Label();
        pasekInstrukcji.setFont(Font.font("System", FontWeight.BOLD, 22));
        pasekInstrukcji.setTextFill(Color.CYAN);
        pasekInstrukcji.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-padding: 15 40; -fx-background-radius: 0 0 20 20;");
        pasekInstrukcji.setVisible(false);
        StackPane.setAlignment(pasekInstrukcji, Pos.TOP_CENTER);

        // --- PANEL PYTAŃ ZAMKNIĘTYCH/OTWARTYCH ---
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
        panelPytania.setMaxWidth(800);
        panelPytania.setVisible(false);
        panelPytania.setEffect(new DropShadow(20, Color.BLACK));

        // --- PANEL KONCOWY ---
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

        panelInterakcji = new Pane();
        panelInterakcji.setVisible(false);

        // --- DOLNY PASEK (PUNKTY I CZAS) ---
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

        // --- OBSŁUGA PRZYCISKÓW MENU ---
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

    private void ustawTloGlowne() {
        try {
            Image obrazTla = new Image(getClass().getResourceAsStream("/images/plaza.jpg"));
            BackgroundSize bgSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
            centerStack.setBackground(new Background(new BackgroundImage(obrazTla,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
        } catch (Exception e) {
            centerStack.setStyle("-fx-background-color: #34495e;");
        }
    }

    private void ladujObrazkiInterakcyjne(Scenariusze s) {
        panelInterakcji.getChildren().clear();
        panelInterakcji.setMaxSize(850, 440);
        StackPane.setAlignment(panelInterakcji, Pos.CENTER);

        Pane scena = new Pane();
        scena.setPrefSize(850, 440);

        String tloPlik = "/images/woda.jpg";
        double celX = 375, celY = 120;
        String celGrafika = "czlowiek.png";
        double szerokoscCelu = 180;

        if (s.getScenaTyp() != null) {
            switch (s.getScenaTyp()) {
                case "WIEZA": tloPlik = "/images/plaza_wieza.jpg"; celX = 290; celY = 180; celGrafika = "wieza.png"; break;
                case "MORZE": tloPlik = "/images/woda.jpg"; celX = 375; celY = 120; celGrafika = "czlowiek.png"; break;
                case "PIERWSZA_POMOC": tloPlik = "/images/plaza_blisko.png"; celX = 400; celY = 200; celGrafika = "poszkodowany.png";
                    szerokoscCelu = 350;; break;
                case "HORYZONT": tloPlik = "/images/obserwacja.jpg"; celX = 350; celY = 280; celGrafika = "ratownik.png"; break;
                case "KOMUNIKACJA": tloPlik = "/images/wieze.png"; celX = 680; celY = 170; celGrafika = "praca.png"; break;
                case "DZIECKO": tloPlik = "/images/plaza.png"; celX = 550; celY = 230; celGrafika = "dziecko.png"; szerokoscCelu = 300; break;
            }
        }

        try {
            Image imgTla = new Image(getClass().getResourceAsStream(tloPlik));
            ImageView tloView = new ImageView(imgTla);
            tloView.setFitWidth(850); tloView.setFitHeight(440);
            scena.getChildren().add(tloView);
        } catch (Exception e) { System.out.println("Błąd tła: " + tloPlik); }

        celInterakcji = createImg(celGrafika, celX, celY);
        celInterakcji.setFitWidth(szerokoscCelu);
        celInterakcji.setPreserveRatio(true);
        scena.getChildren().add(celInterakcji);

        if ("MORZE".equals(s.getScenaTyp())) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), celInterakcji);
            tt.setByY(15);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
        }

        if ("KOMUNIKACJA".equals(s.getScenaTyp())) {
            RotateTransition rt = new RotateTransition(Duration.millis(300), celInterakcji);
            rt.setFromAngle(-5);
            rt.setToAngle(5);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setAutoReverse(true);
            rt.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), celInterakcji);
            tt.setByY(-5);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
        }

        if ("DZIECKO".equals(s.getScenaTyp())) {
            TranslateTransition chodzenie = new TranslateTransition(Duration.seconds(10), celInterakcji);
            chodzenie.setByX(-600);
            chodzenie.setByY(-150);
            chodzenie.setCycleCount(Animation.INDEFINITE);
            chodzenie.setAutoReverse(false);
            chodzenie.play();

            RotateTransition bujanie = new RotateTransition(Duration.millis(600), celInterakcji);
            bujanie.setFromAngle(-3);
            bujanie.setToAngle(3);
            bujanie.setCycleCount(Animation.INDEFINITE);
            bujanie.setAutoReverse(true);
            bujanie.play();

            ScaleTransition skala = new ScaleTransition(Duration.seconds(10), celInterakcji);
            skala.setToX(0.5);
            skala.setToY(0.5);
            skala.setCycleCount(Animation.INDEFINITE);
            skala.play();
        }

        Rectangle poleczka = new Rectangle(750, 110);
        poleczka.setArcWidth(30); poleczka.setArcHeight(30);
        poleczka.setFill(Color.web("#2c3e50", 0.85));
        poleczka.setStroke(Color.web("#3498db"));
        poleczka.setStrokeWidth(3);
        poleczka.setLayoutX(50); poleczka.setLayoutY(315);
        scena.getChildren().add(poleczka);

        String[] grafiki = s.getGrafikiInterakcyjne();
        if (grafiki != null) {
            HBox kontener = new HBox(70);
            kontener.setAlignment(Pos.CENTER);
            kontener.setPrefWidth(750);
            kontener.setLayoutX(50);
            kontener.setLayoutY(325);
            kontener.setPickOnBounds(false);

            for (String g : grafiki) {
                boolean ok = g.equals(s.getPoprawnaGrafika());
                ImageView item = createDragImg(g, ok, scena);
                kontener.getChildren().add(item);
            }
            scena.getChildren().add(kontener);
        }

        if (s.getOpisy().toLowerCase().contains("burza")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> efektBlysku(scena));
            delay.play();
        }

        panelInterakcji.getChildren().add(scena);
    }

    private ImageView createDragImg(String name, boolean ok, Pane scena) {
        ImageView iv = createImg(name, 0, 0);
        iv.setCursor(javafx.scene.Cursor.HAND);
        iv.setFitWidth(80);
        iv.setFitHeight(80);

        final double[] offset = new double[2];
        final HBox[] oryginalnyRodzic = new HBox[1];

        iv.setOnMousePressed(e -> {
            if (!czyPauza) {
                oryginalnyRodzic[0] = (HBox) iv.getParent();

                double sceneX = iv.localToScene(0, 0).getX();
                double sceneY = iv.localToScene(0, 0).getY();

                double localX = sceneX - scena.localToScene(0, 0).getX();
                double localY = sceneY - scena.localToScene(0, 0).getY();

                offset[0] = e.getX();
                offset[1] = e.getY();

                oryginalnyRodzic[0].getChildren().remove(iv);
                scena.getChildren().add(iv);

                iv.setLayoutX(localX);
                iv.setLayoutY(localY);
                iv.toFront();
            }
        });

        iv.setOnMouseDragged(e -> {
            if (!czyPauza) {
                double newX = e.getSceneX() - scena.localToScene(0, 0).getX() - offset[0];
                double newY = e.getSceneY() - scena.localToScene(0, 0).getY() - offset[1];

                iv.setLayoutX(newX);
                iv.setLayoutY(newY);
            }
        });

        iv.setOnMouseReleased(e -> {
            if (!czyPauza) {
                if (iv.getBoundsInParent().intersects(celInterakcji.getBoundsInParent())) {
                    if (ok) gra.poprawnaInterakcja(); else gra.blednaInterakcja();
                } else {
                    scena.getChildren().remove(iv);
                    oryginalnyRodzic[0].getChildren().add(iv);
                }
            }
        });
        return iv;
    }

    private void efektBlysku(Pane scena) {
        Rectangle blysk = new Rectangle(0, 0, 850, 440);
        blysk.setFill(Color.WHITE);
        blysk.setOpacity(0);
        scena.getChildren().add(blysk);
        FadeTransition ft = new FadeTransition(Duration.millis(100), blysk);
        ft.setFromValue(0); ft.setToValue(0.8); ft.setCycleCount(4); ft.setAutoReverse(true);
        ft.setOnFinished(e -> scena.getChildren().remove(blysk));
        ft.play();
    }

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

    private void odnowLicznik() {
        licznik = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            long uplynelo = System.currentTimeMillis() - czasStart;
            double postep = 1.0 - ((double) uplynelo / CZAS_NA_ODPOWIEDZ_MS);
            pasekCzasu.setProgress(Math.max(0, postep));

            if (postep > 0.6) pasekCzasu.setStyle("-fx-accent: #27ae60;");
            else if (postep > 0.3) pasekCzasu.setStyle("-fx-accent: #f1c40f;");
            else pasekCzasu.setStyle("-fx-accent: #e74c3c;");

            if (uplynelo >= CZAS_NA_ODPOWIEDZ_MS) {
                stopLicznik();
                gra.roztrzygnijWybor(-1, CZAS_NA_ODPOWIEDZ_MS);
            }
        }));
        licznik.setCycleCount(Timeline.INDEFINITE);
        licznik.play();
    }

    private void startLicznik() { stopLicznik(); czasStart = System.currentTimeMillis(); odnowLicznik(); }
    private void stopLicznik() { if (licznik != null) licznik.stop(); }
    public void aktualizacjaPunktow(int p) { punktyLabel.setText("PUNKTY: " + p); }

    private void stylizujPrzycisk(Button b, String kolor) {
        String base = "-fx-background-color: " + kolor + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);"));
        b.setOnMouseExited(e -> b.setStyle(base));
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
        Label infoLabel = new Label("POZIOM " + nrPoziomu);
        infoLabel.setFont(Font.font("System", FontWeight.BOLD, 50));
        infoLabel.setTextFill(Color.GOLD);
        infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 40; -fx-background-radius: 20;");
        centerStack.getChildren().add(infoLabel);
        PauseTransition pauza = new PauseTransition(Duration.seconds(2));
        pauza.setOnFinished(e -> centerStack.getChildren().remove(infoLabel));
        pauza.play();
    }

    public void pokazKoniecDnia(Gracz g) {
        stopLicznik();
        panelPytania.setVisible(false); panelInterakcji.setVisible(false); pasekInstrukcji.setVisible(false);
        wynikKoncowyLabel.setText("DZIĘKUJEMY ZA DZIEŃ W PRACY, " + g.getImie() + "!\nTWÓJ WYNIK: " + g.getPunkty() + " PKT");
        wynikKoncowyLabel.setTextFill(Color.GOLD);
        panelKoncowy.setVisible(true);
    }

    public void pokazBladKrytyczny() {
        stopLicznik();
        panelPytania.setVisible(false); panelInterakcji.setVisible(false);
        wynikKoncowyLabel.setText("BŁĄD KRYTYCZNY!\nZOSTAJESZ ZWOLNIONY DYSCYPLINARNIE.");
        wynikKoncowyLabel.setTextFill(Color.RED);
        panelKoncowy.setVisible(true);
    }

    private void resetujGre() {
        stopLicznik();
        gra.resetuj();
        panelPytania.setVisible(false); panelKoncowy.setVisible(false); panelBoczny.setVisible(false);
        menuStartowe.setVisible(true);
        aktualizacjaPunktow(0);
    }

    public static void main(String[] args) { launch(args); }
}