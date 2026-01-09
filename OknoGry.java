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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Animation;
import java.util.ArrayList;
import java.util.List;

public class OknoGry extends Application {

    private Gra gra;
    private Label opisLabel, punktyLabel, wynikKoncowyLabel, pasekInstrukcji;
    private Button[] przyciski = new Button[3];
    private ProgressBar pasekCzasu;
    private Timeline licznik;
    private long czasStart;
    private long czasPozostalyMs;
    private final int CZAS_NA_ODPOWIEDZ_MS = 20000;

    private Button startBtn, wznowBtn, stopBtn, resetBtn, menuLvlBtn, zatwierdzBtn;
    private TextField poleTekstowe, poleImienia;
    private VBox menuStartowe, panelPytania, panelBoczny, panelKoncowy;
    private StackPane centerStack;
    private Pane panelInterakcji, mapaPoziomow;
    private ImageView celInterakcji, ratownikAvatar;
    private BorderPane root;

    private double ratownikX = 100, ratownikY = 300;
    private final double PREDKOSC = 6.0;
    private boolean wPressed, aPressed, sPressed, dPressed;
    private StackPane[] polaPoziomow = new StackPane[3];
    private int aktualniePodswietlony = -1;
    private AnimationTimer gameLoop;
    private boolean czyPauza = false;

    private List<Animation> aktywneAnimacje = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        gra = new Gra("Ratownik");
        gra.ustawOkno(this);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a1a;");

        centerStack = new StackPane();
        ustawTloGlowne();

        // Górny Pasek
        Label tytul = new Label("RATOWNIK — Symulacja szkoleniowa");
        tytul.setFont(Font.font("System", FontWeight.BOLD, 26));
        tytul.setTextFill(Color.WHITE);
        HBox topBar = new HBox(tytul);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #3498db; -fx-border-width: 0 0 2 0;");
        root.setTop(topBar);

        // Panel Boczny
        inicjalizujPanelBoczny();

        // Panele
        inicjalizujMenuStartowe();
        inicjalizujMapePoziomow();
        inicjalizujPaneleGry();

        centerStack.getChildren().clear();
        centerStack.getChildren().addAll(
                mapaPoziomow,
                menuStartowe,
                panelPytania,
                panelInterakcji,
                pasekInstrukcji,
                panelKoncowy
        );
        root.setRight(panelBoczny);
        root.setCenter(centerStack);

        inicjalizujBottomBar();

        Scene scene = new Scene(root, screenBounds.getWidth() * 0.9, screenBounds.getHeight() * 0.9);
        przygotujSterowanie(scene);

        stage.setScene(scene);
        stage.show();
    }

    public void wyswietlScenariusz(Scenariusze s) {
        czyPauza = false;
        startLicznik();
        mapaPoziomow.setVisible(false);
        menuStartowe.setVisible(false);
        panelKoncowy.setVisible(false);

        if (s.isInterakcyjne()) {
            panelPytania.setVisible(false);
            panelInterakcji.setVisible(true);
            pasekInstrukcji.setVisible(true);

            panelInterakcji.toFront();
            pasekInstrukcji.toFront();

            pasekInstrukcji.setText(s.getOpisy());
            ladujObrazkiInterakcyjne(s);
        } else {
            panelInterakcji.setVisible(false);
            panelPytania.setVisible(true);

            panelPytania.toFront();

            opisLabel.setText(s.getOpisy());
            boolean czyOtwarte = s.isOtwartePytanie();
            poleTekstowe.setVisible(czyOtwarte);
            poleTekstowe.setManaged(czyOtwarte);
            zatwierdzBtn.setVisible(czyOtwarte);
            zatwierdzBtn.setManaged(czyOtwarte);
            for (Button btn : przyciski) {
                btn.setVisible(!czyOtwarte);
                btn.setManaged(!czyOtwarte);
            }
            if (czyOtwarte) {
                poleTekstowe.clear();
                poleTekstowe.requestFocus();
            } else {
                String[] odp = s.getOdpowiedzi();
                for (int i = 0; i < 3; i++) {
                    if (i < odp.length) przyciski[i].setText(odp[i]);
                }
            }
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
                case "WIEZA": tloPlik = "/images/plaza_wieza.jpg"; celX = 200; celY = 120; celGrafika = "wieza.png"; break;
                case "MORZE": tloPlik = "/images/woda.jpg"; celX = 375; celY = 120; celGrafika = "czlowiek.png"; break;
                case "PIERWSZA_POMOC": tloPlik = "/images/plaza_blisko.png"; celX = 200; celY = 150; celGrafika = "poszkodowany.png"; szerokoscCelu = 350; break;
                case "HORYZONT": tloPlik = "/images/obserwacja.jpg"; celX = 350; celY = 280; celGrafika = "ratownik.png"; break;
                case "KOMUNIKACJA": tloPlik = "/images/wieze.png"; celX = 640; celY = 100; celGrafika = "praca.png"; szerokoscCelu = 90; break;
                case "DZIECKO": tloPlik = "/images/plaza.png"; celX = 430; celY = 190; celGrafika = "dziecko.png"; szerokoscCelu = 200; break;
            }
        }

        try {
            ImageView tloView = new ImageView(new Image(getClass().getResourceAsStream(tloPlik)));
            tloView.setFitWidth(850); tloView.setFitHeight(440);
            scena.getChildren().add(tloView);
        } catch (Exception e) {}

        celInterakcji = createImg(celGrafika, celX, celY);
        celInterakcji.setFitWidth(szerokoscCelu);
        celInterakcji.setPreserveRatio(true);
        scena.getChildren().add(celInterakcji);

        // --- ANIMACJE SPECJALNE ---
        if ("MORZE".equals(s.getScenaTyp())) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), celInterakcji);
            tt.setByY(15); tt.setCycleCount(Animation.INDEFINITE); tt.setAutoReverse(true); tt.play();
            aktywneAnimacje.add(tt);
        }
        if ("KOMUNIKACJA".equals(s.getScenaTyp())) {
            RotateTransition rt = new RotateTransition(Duration.millis(300), celInterakcji);
            rt.setFromAngle(-5);
            rt.setToAngle(5);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setAutoReverse(true);
            rt.play();
            aktywneAnimacje.add(rt);

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), celInterakcji);
            tt.setByY(-5);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
            aktywneAnimacje.add(tt);
        }

        if ("DZIECKO".equals(s.getScenaTyp())) {
            TranslateTransition chodzenie = new TranslateTransition(Duration.seconds(10), celInterakcji);
            chodzenie.setByX(-600);
            chodzenie.setByY(-150);
            chodzenie.setCycleCount(Animation.INDEFINITE);
            chodzenie.setAutoReverse(false);
            chodzenie.play();
            aktywneAnimacje.add(chodzenie);

            RotateTransition bujanie = new RotateTransition(Duration.millis(600), celInterakcji);
            bujanie.setFromAngle(-3);
            bujanie.setToAngle(3);
            bujanie.setCycleCount(Animation.INDEFINITE);
            bujanie.setAutoReverse(true);
            bujanie.play();
            aktywneAnimacje.add(bujanie);

            ScaleTransition skala = new ScaleTransition(Duration.seconds(10), celInterakcji);
            skala.setToX(0.5);
            skala.setToY(0.5);
            skala.setCycleCount(Animation.INDEFINITE);
            skala.play();
            aktywneAnimacje.add(skala);
        }

        Rectangle poleczka = new Rectangle(750, 110);
        poleczka.setArcWidth(30); poleczka.setArcHeight(30);
        poleczka.setFill(Color.web("#2c3e50", 0.85));
        poleczka.setLayoutX(50); poleczka.setLayoutY(315);
        scena.getChildren().add(poleczka);

        String[] grafiki = s.getGrafikiInterakcyjne();
        if (grafiki != null) {
            HBox kontener = new HBox(70);
            kontener.setAlignment(Pos.CENTER);
            kontener.setPrefWidth(750); kontener.setLayoutX(50); kontener.setLayoutY(325);
            for (String g : grafiki) {
                boolean ok = g.equals(s.getPoprawnaGrafika());
                kontener.getChildren().add(createDragImg(g, ok, scena));
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
        iv.setFitWidth(80); iv.setFitHeight(80);
        final double[] offset = new double[2];
        final HBox[] oryginalnyRodzic = new HBox[1];

        iv.setOnMousePressed(e -> {
            if (!czyPauza) {
                oryginalnyRodzic[0] = (HBox) iv.getParent();
                double sceneX = iv.localToScene(0, 0).getX();
                double sceneY = iv.localToScene(0, 0).getY();
                double localX = sceneX - scena.localToScene(0, 0).getX();
                double localY = sceneY - scena.localToScene(0, 0).getY();
                offset[0] = e.getX(); offset[1] = e.getY();
                oryginalnyRodzic[0].getChildren().remove(iv);
                scena.getChildren().add(iv);
                iv.setLayoutX(localX); iv.setLayoutY(localY);
                iv.toFront();
            }
        });

        iv.setOnMouseDragged(e -> {
            if (!czyPauza) {
                iv.setLayoutX(e.getSceneX() - scena.localToScene(0, 0).getX() - offset[0]);
                iv.setLayoutY(e.getSceneY() - scena.localToScene(0, 0).getY() - offset[1]);
            }
        });

        iv.setOnMouseReleased(e -> {
            if (!czyPauza) {
                if (iv.getBoundsInParent().intersects(celInterakcji.getBoundsInParent())) {
                    stopLicznik();
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
        blysk.setFill(Color.WHITE); blysk.setOpacity(0);
        scena.getChildren().add(blysk);
        FadeTransition ft = new FadeTransition(Duration.millis(100), blysk);
        ft.setFromValue(0); ft.setToValue(0.8); ft.setCycleCount(4); ft.setAutoReverse(true);
        ft.setOnFinished(e -> scena.getChildren().remove(blysk));
        ft.play();
    }

    private void inicjalizujMapePoziomow() {
        mapaPoziomow = new Pane();
        mapaPoziomow.setVisible(false);
        try {
            ImageView tloMapy = new ImageView(new Image(getClass().getResourceAsStream("/images/plaza.jpg")));
            tloMapy.setFitWidth(1200); tloMapy.setFitHeight(800);
            mapaPoziomow.getChildren().add(tloMapy);
        } catch (Exception e) { }
        for (int i = 0; i < 3; i++) {
            StackPane pole = new StackPane();
            pole.setPrefSize(220, 140);
            pole.setLayoutX(150 + (i * 320)); pole.setLayoutY(220);
            pole.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                    "-fx-background-radius: 25; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2;");

            Label nr = new Label("POZIOM " + (i + 1));
            nr.setTextFill(Color.WHITE);
            nr.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 22));

            int numerPoziomu = i + 1;
            String gwiazdki = "";

            for (int g = 0; g < numerPoziomu; g++) {
                gwiazdki += "★";
            }

            for (int g = numerPoziomu; g < 3; g++) {
                gwiazdki += "☆";
            }

            Label status = new Label(gwiazdki);
            status.setTextFill(Color.GOLD);
            status.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            status.setTranslateY(35);

            pole.getChildren().addAll(nr, status);
            polaPoziomow[i] = pole;
            mapaPoziomow.getChildren().add(pole);

            TranslateTransition floating = new TranslateTransition(Duration.seconds(2 + i), pole);
            floating.setByY(10);
            floating.setCycleCount(Animation.INDEFINITE);
            floating.setAutoReverse(true);
            floating.play();
        }
        ratownikAvatar = createImg("ratownik.png", ratownikX, ratownikY);
        ratownikAvatar.setFitWidth(500);
        mapaPoziomow.getChildren().add(ratownikAvatar);
    }

    private void przygotujSterowanie(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> wPressed = true; case S -> sPressed = true;
                case A -> aPressed = true; case D -> dPressed = true;
                case ENTER -> {
                    if (mapaPoziomow.isVisible() && aktualniePodswietlony != -1) {
                        uruchomPoziom(aktualniePodswietlony + 1);
                    }
                }
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> wPressed = false; case S -> sPressed = false;
                case A -> aPressed = false; case D -> dPressed = false;
            }
        });
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!mapaPoziomow.isVisible()) return;
                if (wPressed) ratownikY -= PREDKOSC; if (sPressed) ratownikY += PREDKOSC;
                if (aPressed) { ratownikX -= PREDKOSC; ratownikAvatar.setScaleX(-1); }
                if (dPressed) { ratownikX += PREDKOSC; ratownikAvatar.setScaleX(1); }

                if (wPressed || sPressed || aPressed || dPressed) {
                    ratownikAvatar.setRotate(Math.sin(now * 0.00000002) * 15);
                } else { ratownikAvatar.setRotate(0); }

                ratownikAvatar.setLayoutX(ratownikX); ratownikAvatar.setLayoutY(ratownikY);
                sprawdzKolizjeZPoziomami();
            }
        };
    }

    private void sprawdzKolizjeZPoziomami() {
        aktualniePodswietlony = -1;

        double srodekRatownikaX = ratownikAvatar.getLayoutX() + (ratownikAvatar.getFitWidth() / 2);
        double srodekRatownikaY = ratownikAvatar.getLayoutY() + (ratownikAvatar.getBoundsInLocal().getHeight() / 2);

        for (int i = 0; i < polaPoziomow.length; i++) {
            if (polaPoziomow[i].getBoundsInParent().contains(srodekRatownikaX, srodekRatownikaY)) {
                polaPoziomow[i].setStyle("-fx-background-color: rgba(39, 174, 96, 0.9); " +
                        "-fx-border-color: #f1c40f; " +
                        "-fx-border-width: 4; " +
                        "-fx-background-radius: 20;");
                aktualniePodswietlony = i;
            } else {
                polaPoziomow[i].setStyle("-fx-background-color: rgba(44, 62, 80, 0.7); " +
                        "-fx-border-color: white; " +
                        "-fx-background-radius: 20;");
            }
        }
    }

    private void uruchomPoziom(int nr) {
        gameLoop.stop();
        mapaPoziomow.setVisible(false);
        czyPauza = false;
        panelPytania.setDisable(false);
        panelInterakcji.setDisable(false);
        panelBoczny.setDisable(false);
        panelPytania.setOpacity(1.0);      // Przywraca 100% widoczności
        panelInterakcji.setOpacity(1.0);   // Przywraca 100% widoczności
        pasekInstrukcji.setOpacity(1.0);   // Przywraca 100% widoczności
        panelBoczny.setVisible(true);
        panelBoczny.toFront();

        gra.setPoziom(nr);
        gra.rozpocznij();
    }

    private void startLicznik() {
        stopLicznik();
        pasekCzasu.setProgress(1.0);
        czasPozostalyMs = CZAS_NA_ODPOWIEDZ_MS;
        odnowLicznik();
    }
    private void stopLicznik() { if (licznik != null) licznik.stop(); }
    private void odnowLicznik() {
        if (licznik != null) licznik.stop();

        licznik = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            czasPozostalyMs -= 50;

            double postep = (double) czasPozostalyMs / CZAS_NA_ODPOWIEDZ_MS;
            pasekCzasu.setProgress(postep);

            if (czasPozostalyMs <= 0) {
                stopLicznik();
                pasekCzasu.setProgress(0);
                gra.roztrzygnijWybor(-1, CZAS_NA_ODPOWIEDZ_MS);
            }
        }));
        licznik.setCycleCount(Timeline.INDEFINITE);
        licznik.play();
    }

    private void inicjalizujPaneleGry() {
        panelPytania = new VBox(25);
        panelPytania.setAlignment(Pos.CENTER);
        panelPytania.setVisible(false);
        panelPytania.setStyle("-fx-background-color: #1c1c1c; " +
                "-fx-padding: 40; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 2;");
        panelPytania.setMaxWidth(800);
        panelPytania.setMinHeight(500);

        opisLabel = new Label();
        opisLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        opisLabel.setTextFill(Color.WHITE);
        opisLabel.setWrapText(true);
        opisLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        opisLabel.setMaxWidth(600);

        poleTekstowe = new TextField();
        poleTekstowe.setMaxWidth(400);
        poleTekstowe.setStyle("-fx-font-size: 18px; -fx-background-radius: 10;");

        zatwierdzBtn = new Button("ZATWIERDŹ");
        stylizujPrzycisk(zatwierdzBtn, "#8e44ad");

        VBox boxOdp = new VBox(15);
        boxOdp.setAlignment(Pos.CENTER);

        boxOdp.visibleProperty().bind(poleTekstowe.visibleProperty().not());
        boxOdp.managedProperty().bind(poleTekstowe.managedProperty().not());

        for(int i = 0; i < 3; i++) {
            int idx = i;
            przyciski[i] = new Button();
            stylizujPrzycisk(przyciski[i], "#3498db");
            przyciski[i].setOnAction(e -> {
                stopLicznik();
                gra.roztrzygnijWybor(idx, System.currentTimeMillis() - czasStart);
            });
            boxOdp.getChildren().add(przyciski[i]);
        }

        poleTekstowe.setOnAction(e -> zatwierdzBtn.fire());

        zatwierdzBtn.setOnAction(e -> {
            String wpisanaOdpowiedz = poleTekstowe.getText();
            if (wpisanaOdpowiedz != null && !wpisanaOdpowiedz.trim().isEmpty()) {
                stopLicznik();

                long czasOdpowiedzi = System.currentTimeMillis() - czasStart;

                gra.obsluzOdpowiedzTekstowa(wpisanaOdpowiedz, czasOdpowiedzi);

                poleTekstowe.clear();
            }
        });

        panelPytania.getChildren().addAll(opisLabel, boxOdp, poleTekstowe, zatwierdzBtn);

        panelInterakcji = new Pane();
        panelInterakcji.setVisible(false);

        pasekInstrukcji = new Label();
        pasekInstrukcji.setFont(Font.font("System", FontWeight.BOLD, 20));
        pasekInstrukcji.setTextFill(Color.WHITE);
        pasekInstrukcji.setWrapText(true);
        pasekInstrukcji.setMaxWidth(800);
        pasekInstrukcji.setAlignment(Pos.CENTER);
        pasekInstrukcji.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); " +
                "-fx-padding: 20; -fx-background-radius: 0 0 15 15; " +
                "-fx-border-color: #3498db; -fx-border-width: 0 0 2 0;");
        pasekInstrukcji.setVisible(false);
        StackPane.setAlignment(pasekInstrukcji, Pos.TOP_CENTER);

        panelKoncowy = new VBox(25);
        panelKoncowy.setAlignment(Pos.CENTER);
        panelKoncowy.setVisible(false);
        panelKoncowy.setStyle("-fx-background-color: rgba(10,10,20,0.95); -fx-padding: 50; -fx-background-radius: 30;");

        wynikKoncowyLabel = new Label();
        wynikKoncowyLabel.setTextFill(Color.GOLD);
        wynikKoncowyLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        wynikKoncowyLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button powrotBtn = new Button("GRAJ DALEJ (MAPA)");
        stylizujPrzycisk(powrotBtn, "#27ae60");
        powrotBtn.setOnAction(e -> {
            panelKoncowy.setVisible(false);
            mapaPoziomow.setVisible(true);
            panelBoczny.setVisible(false);
            ratownikX = 100; ratownikY = 300;
            ratownikAvatar.setLayoutX(ratownikX);
            ratownikAvatar.setLayoutY(ratownikY);

            gameLoop.start();
        });
        Button zakonczBtn = new Button("ZAKOŃCZ I ZAPISZ");
        stylizujPrzycisk(zakonczBtn, "#c0392b");
        zakonczBtn.setOnAction(e -> {
            RaportZGry.zapiszWynik(gra.getGracz().getImie(), gra.getGracz().getPunkty(), 1);

            panelKoncowy.getChildren().clear();
            panelKoncowy.setSpacing(15);
            panelKoncowy.setPadding(new Insets(40));
            panelKoncowy.setAlignment(Pos.CENTER);

            panelKoncowy.setStyle(
                    "-fx-background-color: #1a1a1a; " +
                            "-fx-border-color: #f1c40f; " +
                            "-fx-border-width: 8; " +
                            "-fx-border-radius: 20; " +
                            "-fx-background-radius: 25; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(241, 196, 15, 0.6), 20, 0, 0, 0);"
            );

            Label naglowek = new Label("KONIEC DNIA");
            naglowek.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
            naglowek.setTextFill(Color.web("#f1c40f"));

            Label imieLabel = new Label("RATOWNIK: " + gra.getGracz().getImie().toUpperCase());
            imieLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 22));
            imieLabel.setTextFill(Color.WHITE);

            Label punktyFinal = new Label("ZDOBYTE PUNKTY: " + gra.getGracz().getPunkty());
            punktyFinal.setFont(Font.font("System", 28));
            punktyFinal.setTextFill(Color.web("#2ecc71"));

            Label dobraRobota = new Label("DOBRA ROBOTA!");
            dobraRobota.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
            dobraRobota.setTextFill(Color.WHITE);
            dobraRobota.setEffect(new javafx.scene.effect.InnerShadow(5, Color.BLACK));

            Button wyjscieBtn = new Button("POWRÓT DO MENU");
            stylizujPrzycisk(wyjscieBtn, "#f1c40f");
            wyjscieBtn.setTextFill(Color.BLACK);

            wyjscieBtn.setOnAction(ev -> {
                gra.resetuj();
                panelKoncowy.setVisible(false);
                menuStartowe.setVisible(true);
                panelBoczny.setVisible(false);
                inicjalizujPaneleGry();
            });

            panelKoncowy.getChildren().addAll(naglowek, imieLabel, punktyFinal, dobraRobota, new Label(""), wyjscieBtn);
            panelKoncowy.toFront();
        });

    }

    private void inicjalizujMenuStartowe() {
        menuStartowe = new VBox(30);
        menuStartowe.setAlignment(Pos.CENTER);

        Label l = new Label("RATOWNIK WOPR");
        l.setFont(Font.font("Impact", 50));
        l.setTextFill(Color.WHITE);

        poleImienia = new TextField();
        poleImienia.setPromptText("Wpisz swoje imię...");
        poleImienia.setMaxWidth(300);
        poleImienia.setOnAction(e -> startBtn.fire());
        poleImienia.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-background-radius: 10;");

        startBtn = new Button("ROZPOCZNIJ DYŻUR");
        stylizujPrzycisk(startBtn, "#27ae60");

        startBtn.setOnAction(e -> {
            String imie = poleImienia.getText().trim();
            gra.getGracz().setImie(imie.isEmpty() ? "Ratownik" : imie);

            menuStartowe.setVisible(false);
            mapaPoziomow.setVisible(true);

            gameLoop.start();

            root.requestFocus();

            System.out.println("Gra rozpoczęta przez: " + gra.getGracz().getImie());
        });

        menuStartowe.getChildren().addAll(l, poleImienia, startBtn);
        menuStartowe.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #2c3e50, #000000);");
    }

    private void inicjalizujBottomBar() {
        punktyLabel = new Label("PUNKTY: 0");
        punktyLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 22));
        punktyLabel.setTextFill(Color.AQUAMARINE);

        pasekCzasu = new ProgressBar(1.0);
        pasekCzasu.setPrefWidth(600);
        pasekCzasu.setPrefHeight(25);
        pasekCzasu.setStyle("-fx-accent: #e74c3c; -fx-control-inner-background: #2c3e50; " +
                "-fx-background-radius: 15; -fx-padding: 2;");

        HBox bot = new HBox(60, punktyLabel, pasekCzasu);
        bot.setAlignment(Pos.CENTER);
        bot.setPadding(new Insets(25));
        bot.setStyle("-fx-background-color: linear-gradient(to top, #000000, transparent);");
        root.setBottom(bot);
    }

    private void inicjalizujPanelBoczny() {
        wznowBtn = new Button("WZNÓW");
        stopBtn = new Button("PAUZA");
        resetBtn = new Button("RESET");
        menuLvlBtn = new Button("MENU");

        stylizujPrzycisk(wznowBtn, "#2980b9");
        stylizujPrzycisk(stopBtn, "#f39c12");
        stylizujPrzycisk(resetBtn, "#c0392b");
        stylizujPrzycisk(menuLvlBtn, "#7f8c8d");

        wznowBtn.setMinWidth(120);
        stopBtn.setMinWidth(120);
        resetBtn.setMinWidth(120);
        menuLvlBtn.setMinWidth(120);

        panelBoczny = new VBox(20, wznowBtn, stopBtn, resetBtn, menuLvlBtn);
        panelBoczny.setVisible(false);
        panelBoczny.setPadding(new Insets(20, 10, 20, 10));
        panelBoczny.setAlignment(Pos.TOP_CENTER);

        panelBoczny.setStyle(
                "-fx-background-color: rgba(44, 62, 80, 0.9); " +
                        "-fx-border-color: #3498db; " +
                        "-fx-border-width: 0 0 0 2; " +
                        "-fx-pref-width: 160;"
        );

        stopBtn.setOnAction(e -> pauza());
        wznowBtn.setOnAction(e -> wznow());
        resetBtn.setOnAction(e -> {
            pauza();
            gra.resetuj();
            pasekCzasu.setProgress(1.0);
            panelBoczny.setVisible(false);
            panelPytania.setVisible(false);
            panelInterakcji.setVisible(false);
            pasekInstrukcji.setVisible(false);
            pasekInstrukcji.setText("");
            mapaPoziomow.setVisible(false);
            menuStartowe.setVisible(true);
            gameLoop.stop();
        });

        menuLvlBtn.setOnAction(e -> {
            pauza();
            pasekCzasu.setProgress(1.0);
            panelPytania.setOpacity(1.0);
            panelInterakcji.setOpacity(1.0);
            panelPytania.setDisable(false);
            panelInterakcji.setDisable(false);
            panelBoczny.setVisible(false);
            panelPytania.setVisible(false);
            panelInterakcji.setVisible(false);
            pasekInstrukcji.setVisible(false);
            mapaPoziomow.setVisible(true);
            gameLoop.start();
        });
    }

    private void pauza() {
        czyPauza = true;
        stopLicznik();
        for (Animation anim : aktywneAnimacje) {
            anim.pause();
        }
        panelPytania.setDisable(true);
        panelInterakcji.setDisable(true);
        panelPytania.setOpacity(0.4);
        panelInterakcji.setOpacity(0.4);

        pasekInstrukcji.setOpacity(0.4);
    }

    private void wznow() {
        czyPauza = false;
        for (Animation anim : aktywneAnimacje) {
            anim.play();
        }
        panelPytania.setDisable(false);
        panelInterakcji.setDisable(false);

        panelPytania.setOpacity(1.0);
        panelInterakcji.setOpacity(1.0);
        pasekInstrukcji.setOpacity(1.0);

        odnowLicznik();
    }
    public void aktualizacjaPunktow(int p) { punktyLabel.setText("PUNKTY: " + p); }
    public void pokazInfoOPoziomie(int nr) {
        System.out.println("Rozpoczynasz poziom: " + nr);

    }
    public void pokazKoniecDnia(Gracz g) {
        stopLicznik();
        gameLoop.stop();

        panelPytania.setVisible(false);
        panelInterakcji.setVisible(false);
        pasekInstrukcji.setVisible(false);
        mapaPoziomow.setVisible(false);

        panelKoncowy.getChildren().clear();
        panelKoncowy.setSpacing(20);

        Label infoLabel = new Label("POZIOM " + gra.getPoziom() + " UKOŃCZONY!");
        infoLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        infoLabel.setTextFill(Color.GOLD);

        Label punktyInfo = new Label("Zdobyte punkty: " + g.getPunkty());
        punktyInfo.setFont(Font.font("System", 22));
        punktyInfo.setTextFill(Color.WHITE);

        Button grajDalejBtn = new Button("GRAJ DALEJ (MAPA)");
        stylizujPrzycisk(grajDalejBtn, "#27ae60");
        grajDalejBtn.setOnAction(e -> {
            panelKoncowy.setVisible(false);
            mapaPoziomow.setVisible(true);
            panelBoczny.setVisible(false);
            ratownikX = 100; ratownikY = 300;
            ratownikAvatar.setLayoutX(ratownikX);
            ratownikAvatar.setLayoutY(ratownikY);
            gameLoop.start();
        });

        Button koniecDniaBtn = new Button("KONIEC DNIA");
        stylizujPrzycisk(koniecDniaBtn, "#c0392b");
        koniecDniaBtn.setOnAction(e -> {
            panelBoczny.setVisible(false);
            wyswietlPodsumowanieZIlustracja(g);
        });

        panelKoncowy.getChildren().addAll(infoLabel, punktyInfo, grajDalejBtn, koniecDniaBtn);
        panelKoncowy.setVisible(true);
        panelKoncowy.toFront();
    }
    private void wyswietlPodsumowanieZIlustracja(Gracz g) {
        RaportZGry.zapiszWynik(g.getImie(), g.getPunkty(), gra.getPoziom());

        panelKoncowy.getChildren().clear();

        ImageView ilustracja = createImg("koniec_dnia.jpg", 0, 0);
        ilustracja.setFitWidth(400);
        ilustracja.setPreserveRatio(true);

        Label gratulacje = new Label("GRATULACJE, " + g.getImie().toUpperCase() + "!");
        gratulacje.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        gratulacje.setTextFill(Color.web("#f1c40f"));

        Label wynikLabel = new Label("TWÓJ WYNIK TO: " + g.getPunkty() + " PKT");
        wynikLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        wynikLabel.setTextFill(Color.web("#2ecc71"));

        Button powrotMenuBtn = new Button("POWRÓT DO MENU");
        stylizujPrzycisk(powrotMenuBtn, "#3498db");
        powrotMenuBtn.setOnAction(ev -> {
            gra.resetuj();
            panelKoncowy.setVisible(false);
            menuStartowe.setVisible(true);
            panelBoczny.setVisible(false);
            inicjalizujPaneleGry();
        });

        panelKoncowy.getChildren().addAll(ilustracja, gratulacje, wynikLabel, powrotMenuBtn);
    }
    private void stylizujPrzycisk(Button b, String kolorBase) {
        String styleNormal = "-fx-background-color: linear-gradient(to bottom, " + kolorBase + ", derive(" + kolorBase + ", -20%)); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: rgba(255,255,255,0.2); " +
                "-fx-border-width: 1; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 4);";

        String styleHover = "-fx-background-color: linear-gradient(to bottom, derive(" + kolorBase + ", 20%), " + kolorBase + "); " +
                "-fx-scale-x: 1.05; -fx-scale-y: 1.05;";

        b.setStyle(styleNormal);
        b.setOnMouseEntered(e -> b.setStyle(styleNormal + styleHover));
        b.setOnMouseExited(e -> b.setStyle(styleNormal));
        b.setMinWidth(280);
    }
    private void ustawTloGlowne() {
        centerStack.setStyle("-fx-background-color: #34495e;");
    }
    private ImageView createImg(String name, double x, double y) {
        try { Image img = new Image(getClass().getResourceAsStream("/images/" + name));
            ImageView iv = new ImageView(img);
            iv.setLayoutX(x); iv.setLayoutY(y);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    public static void main(String[] args) { launch(args); }
}