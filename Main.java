import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private boolean pressed, running, gameOver;
    private final static int PIXEL = 30;
    private int colorChoice, time;
    private Board board;
    private BorderPane borderPane, bp, sss;
    private CheckBox checkGravity, checkSound, checkColor;
    private GridPane grid;
    private HBox hbox;
    private Image image;
    private ImageView imageView;
    private Label subScore, subLevel, subLine, bot, score, line, level;
    private Map<Integer, Color> color1, color2;
    private Map<Integer, Map<Integer, Color>> colors;
    private MediaPlayer player;
    private PauseTransition pTransition;
    private Rectangle recc;
    private Scene scene;
    private SequentialTransition sTransition;
    private StackPane stack;
    private Stage stage;
    private VBox vboxTop, vboxBottom;

    public static void playTerminal() {
        Board board = new Board();
        Scanner reader = new Scanner(System.in);

        while (!board.getGameOver()) {
            System.out.println(board);
            String in = reader.nextLine();

            if (in.contains("a") | in.contains("s") || in.contains("d") || in.contains("w")) {
                if (in.contains("a")) {
                    for (int i = 0; i < in.length(); i++) {
                        board.moveLeft();
                    }
                } else if (in.contains("s")) {
                    for (int i = 0; i < in.length(); i++) {
                        board.moveDown();
                    }
                } else if (in.contains("d")) {
                    for (int i = 0; i < in.length(); i++) {
                        board.moveRight();
                    }
                } else {
                    board.rotate();
                }
            }
        }

        reader.close();
    }

    public void paint() {
        if (!board.getGameOver()) {
            score.setText(String.valueOf(board.getScore()));
            line.setText(String.valueOf(board.getNumClearedLines()));
            level.setText(String.valueOf(board.getLevel()));

            if (time != board.getTimePerBlock()) {
                sTransition.stop();
                createTimer();
            }

            List<Point> points = board.getPoints();
            grid.getChildren().clear();

            for (int i = 0; i < Board.HEIGHT; i++) {

                for (int j = 0; j < Board.WIDTH; j++) {
                    Point currentPoint = new Point(j, i);

                    Rectangle r = new Rectangle(PIXEL, PIXEL);
                    Polygon topShade = new Polygon();
                    Polygon bottomShade = new Polygon();
                    double offset = (double) PIXEL / 7.5;

                    if (points.contains(currentPoint)) {
                        topShade.getPoints()
                                .addAll(new Double[] { 0.0, 0.0, (double) PIXEL, 0.0, (double) PIXEL - offset, offset,
                                        offset, offset, offset, (double) PIXEL - offset, 0.0, (double) PIXEL });

                        bottomShade.getPoints()
                                .addAll(new Double[] { 0.0, (double) PIXEL, (double) PIXEL, (double) PIXEL,
                                        (double) PIXEL, 0.0, (double) PIXEL - offset, offset, (double) PIXEL - offset,
                                        (double) PIXEL - offset, offset, (double) PIXEL - offset });

                        topShade.setFill(Color.WHITE);
                        topShade.setOpacity(0.5);

                        bottomShade.setFill(Color.BLACK);
                        bottomShade.setOpacity(0.5);

                        r.setFill(colors.get(colorChoice).get(points.get(points.indexOf(currentPoint)).getType()));

                        grid.add(r, j, i);
                        grid.add(topShade, j, i);
                        grid.add(bottomShade, j, i);
                    } else {
                        topShade.setFill(Color.BLACK);
                        topShade.setOpacity(0.1);
                        topShade.getPoints()
                                .addAll(new Double[] { 0.0, 0.0, (double) PIXEL, 0.0, (double) PIXEL - offset, offset,
                                        offset, offset, offset, (double) PIXEL - offset, 0.0, (double) PIXEL });

                        bottomShade.setFill(Color.BLACK);
                        bottomShade.setOpacity(0.25);
                        bottomShade.getPoints()
                                .addAll(new Double[] { 0.0, (double) PIXEL, (double) PIXEL, (double) PIXEL,
                                        (double) PIXEL, 0.0, (double) PIXEL - offset, offset, (double) PIXEL - offset,
                                        (double) PIXEL - offset, offset, (double) PIXEL - offset });

                        Rectangle rec = new Rectangle(PIXEL, PIXEL / 2.65);
                        rec.setOpacity(0.05);
                        rec.setFill(Color.WHITE);

                        Arc arc = new Arc((double) PIXEL / 2.0, (double) PIXEL / 2.0, (double) PIXEL / 2.0,
                                (double) PIXEL / 8.0, 0.0f, 180.0f);
                        arc.setOpacity(0.05);
                        arc.setFill(Color.WHITE);
                        arc.setType(ArcType.ROUND);
                        arc.setRotate(180.0);

                        r.setFill(Color.GRAY);
                        r.setOpacity((55.0 / 62.0 - ((double) i + 30.0) / ((double) Board.HEIGHT + 50)));

                        Group group = new Group();
                        group.getChildren().addAll(r, topShade, bottomShade, arc, rec);

                        grid.add(group, j, i);
                    }
                }
            }
        } else {
            gameOver = true;

            running = false;
            sTransition.stop();
            stopMusic();
            setSceneDisable(true);

            Image img = new Image("resources/gameOver.png");
            ImageView ii = new ImageView(img);
            Label de = new Label("GAME OVER");
            Label dee = new Label(
                    "You made " + board.getScore() + " points\nand reached level " + board.getLevel() + "!");
            de.setTextFill(Color.WHITE);
            de.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15.0));
            de.setTextAlignment(TextAlignment.CENTER);

            dee.setTextFill(Color.WHITE);
            dee.setFont(Font.font("Segoe UI Semilight", 13.0));
            dee.setTextAlignment(TextAlignment.CENTER);

            VBox vv = new VBox();
            vv.getChildren().addAll(ii, de, dee);
            vv.setAlignment(Pos.CENTER);
            sss = new BorderPane();
            sss.setCenter(vv);
            stack.getChildren().addAll(recc, sss);
        }
    }

    public void setSceneDisable(boolean value) {
        level.setDisable(value);
        subLevel.setDisable(value);
        score.setDisable(value);
        subScore.setDisable(value);
        line.setDisable(value);
        subLine.setDisable(value);

        bot.setDisable(value);

        checkColor.setDisable(value);
        checkGravity.setDisable(value);
        checkSound.setDisable(value);
    }

    @Override
    public void start(Stage primaryStage) {
        // Sets the colors
        colorChoice = 0;
        colors = new HashMap<Integer, Map<Integer, Color>>();

        color1 = new HashMap<Integer, Color>();
        color1.put(1, Color.ORANGE);
        color1.put(2, Color.CYAN);
        color1.put(3, Color.PURPLE);
        color1.put(4, Color.GREEN);
        color1.put(5, Color.RED);
        color1.put(6, Color.BLUE);
        color1.put(7, Color.YELLOW);
        colors.put(0, color1);

        color2 = new HashMap<Integer, Color>();
        color2.put(1, Color.rgb(248, 121, 41));
        color2.put(2, Color.rgb(11, 165, 223));
        color2.put(3, Color.rgb(192, 58, 180));
        color2.put(4, Color.rgb(135, 212, 47));
        color2.put(5, Color.rgb(215, 23, 53));
        color2.put(6, Color.rgb(44, 87, 220));
        color2.put(7, Color.rgb(251, 187, 49));
        colors.put(1, color2);

        // Background music setup
        player = new MediaPlayer(new Media(new File("resources/Tetris.wav").toURI().toString()));
        player.setOnEndOfMedia(() -> {
            player.seek(Duration.ZERO);
        });

        // Application icon
        primaryStage.getIcons().add(new Image("resources/tetris.png"));

        // Grid setup
        grid = new GridPane();
        grid.getStyleClass().add("grid");
        grid.getStyleClass().add("background");

        for (int i = 0; i < Board.WIDTH; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(PIXEL));
        }

        for (int i = 0; i < Board.HEIGHT; i++) {
            grid.getRowConstraints().add(new RowConstraints(PIXEL));
        }

        // Bop left side of the game
        vboxBottom = new VBox();
        vboxBottom.getStyleClass().add("background");

        score = new Label();
        level = new Label();
        line = new Label();

        score.getStyleClass().add("score");
        level.getStyleClass().add("score");
        line.getStyleClass().add("score");

        subScore = new Label("score");
        subLevel = new Label("level");
        subLine = new Label("lines cleared");

        subScore.getStyleClass().add("subScore");
        subLevel.getStyleClass().add("subScore");
        subLine.getStyleClass().add("subScore");

        vboxTop = new VBox();
        vboxTop.getStyleClass().add("background");
        vboxTop.getChildren().addAll(score, subScore, level, subLevel, line, subLine);

        // Bottom left side of the game
        checkGravity = new CheckBox("gravity");
        checkGravity.setSelected(true);
        checkGravity.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hbox.requestFocus();
            }
        });
        checkGravity.selectedProperty().addListener((observable, oldValue, newValue) -> {
            board.setGravity(newValue);
            checkGravity.setSelected(newValue);
        });

        checkSound = new CheckBox("music");
        checkSound.setSelected(true);
        checkSound.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hbox.requestFocus();
            }
        });
        checkSound.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                playMusic();
            } else {
                stopMusic();
            }
        });

        checkColor = new CheckBox("original colors");
        checkColor.setSelected(true);
        checkColor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hbox.requestFocus();
            }
        });
        checkColor.selectedProperty().addListener((observable, oldValue, newValue) -> {
            colorChoice = (colorChoice + 1) % 2;
            paint();
        });

        bot = new Label("press space to pause");
        bot.getStyleClass().add("pause");

        vboxBottom.getChildren().addAll(checkColor, checkSound, checkGravity, bot);

        // Seperate layouts from the top to the bottom
        borderPane = new BorderPane();
        borderPane.getStyleClass().add("background");
        borderPane.setTop(vboxTop);
        borderPane.setBottom(vboxBottom);

        // Allows to overlays layouts for pausing the game
        stack = new StackPane();
        stack.getChildren().add(grid);

        // The main layout of the game
        hbox = new HBox();
        hbox.getStyleClass().add("background");
        hbox.setPadding(new Insets(5.0));
        hbox.setSpacing(25.0);
        hbox.getChildren().addAll(borderPane, stack);

        image = new Image("resources/pause.png");
        imageView = new ImageView(image);

        bp = new BorderPane();
        bp.setCenter(imageView);
        recc = new Rectangle(Board.WIDTH * PIXEL + 2 * (Board.WIDTH - 1),
                Board.HEIGHT * PIXEL + 2 * (Board.HEIGHT - 1));
        recc.setOpacity(0.5);
        recc.setFill(Color.BLACK);
        recc.toFront();

        // Create the scene
        scene = new Scene(hbox);
        scene.getStylesheets().add("resources/application.css");
        scene.setOnKeyReleased(ke -> {
            pressed = true;
        });
        scene.setOnKeyPressed((ke) -> {
            if (running) {
                if (ke.getCode().equals(KeyCode.LEFT)) {
                    board.moveLeft();
                    paint();
                } else if (ke.getCode().equals(KeyCode.DOWN)) {
                    board.moveDown();
                    paint();
                } else if (ke.getCode().equals(KeyCode.RIGHT)) {
                    board.moveRight();
                    paint();
                } else if (ke.getCode().equals(KeyCode.UP) && pressed) {
                    pressed = false;
                    board.rotate();
                    paint();
                }
            }
            if (ke.getCode().equals(KeyCode.SPACE)) {
                if (gameOver) {
                    sTransition.stop();
                    startNewGame();
                    board.setGravity(checkGravity.isSelected());
                    stack.getChildren().removeAll(recc, sss);
                } else {
                    if (running) {
                        running = false;

                        stopMusic();
                        sTransition.stop();
                        stack.getChildren().addAll(recc, bp);
                    } else {
                        running = true;

                        playMusic();
                        sTransition.play();
                        stack.getChildren().removeAll(recc, bp);
                    }
                }
                setSceneDisable(!running);
            }
        });

        startNewGame();

        // Shows the stage
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle("Tetris");
        stage.show();
    }

    public void startNewGame() {
        board = new Board();

        time = board.getTimePerBlock();

        running = true;
        gameOver = false;
        pressed = true;

        paint();
        createTimer();
        player.seek(Duration.ZERO);
        playMusic();
    }

    public void createTimer() {
        time = board.getTimePerBlock();

        sTransition = new SequentialTransition();
        pTransition = new PauseTransition(Duration.millis(time));
        pTransition.setOnFinished(evt -> {
            board.moveDown();
            paint();
        });
        sTransition.getChildren().add(pTransition);
        sTransition.setCycleCount(Timeline.INDEFINITE);
        sTransition.play();
    }

    public void playMusic() {
        if (checkSound.isSelected()) {
            player.play();
            player.setVolume(0);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(400), new KeyValue(player.volumeProperty(), 1)));
            timeline.play();
        }
    }

    public void stopMusic() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(player.volumeProperty(), 0)));
        timeline.setOnFinished(evt -> {
            player.pause();
        });
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

}