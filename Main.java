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

    private Arc halfCircle;
    private Board board;
    private boolean pressed, running, gameOver;
    private BorderPane borderPane, pauseCenter, gameOverCenter;
    private CheckBox checkGravity, checkSound, checkColor;
    private double shadeThick;
    private GridPane tetrisGrid;
    private Group cellGroup;
    private HBox root;
    private ImageView pauseImg, gameOverImg;
    private int colorChoice, shapeSpeed;
    private Label subScore, subLevel, subLine, score, line, level, spacePause, gameOverTitle, gameOverSub;
    private List<Point> points;
    private Map<Integer, Color> color1, color2;
    private Map<Integer, Map<Integer, Color>> colors;
    private MediaPlayer mainThemePlayer, soundEffectPlayer;
    private PauseTransition pauseTransition;
    private Point currentPoint;
    private Polygon topShade, bottomShade;
    private Rectangle boardShade, square, topRec;
    private Scene scene;
    private SequentialTransition shapeTransition;
    private StackPane stackPane;
    private Stage stage;
    private Timeline musicTimeline;
    private VBox vboxTop, vboxBottom, gameOverVbox;
    
    private final static int PIXEL = 30;
    private final static int musicFadeInMilli = 400;
    private final static int musicFadeOutMilli = 200;

    /**
     * Used for initial testing in the terminal. Use the WASD keys to move the shape
     * and press enter after they keystroke(s). You can press the key multiple times
     * and it will move in the specified direction the same amount of shapeSpeed.
     */
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

    /**
     * Paints the stage to reflect the data in the Board class.
     */
    public void paint() {
        if (!board.getGameOver()) {

            // In case the user presses the spacebar too quickly
            if (checkSound.isSelected() && mainThemePlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                playMusic();
            }
            
            // Updates the speed of the falling block if the speed changed
            if (shapeSpeed != board.getTimePerBlock()) {
                shapeTransition.stop();
                createTransition();
            }

            // Updates the stats about the game
            score.setText(String.valueOf(board.getScore()));
            line.setText(String.valueOf(board.getNumClearedLines()));
            level.setText(String.valueOf(board.getLevel()));

            // Gets points of fallen shapes and remove everything from the tetrisGrid
            points = board.getPoints();
            tetrisGrid.getChildren().clear();

            // Iterates through each cell in the tetrisGrid
            for (int i = 0; i < Board.HEIGHT; i++) {
                for (int j = 0; j < Board.WIDTH; j++) {
                    currentPoint = new Point(j, i);
                    cellGroup = new Group();

                    // Shapes used for each cell
                    square = new Rectangle(PIXEL, PIXEL);
                    topShade = new Polygon();                    
                    bottomShade = new Polygon();

                    // Shade's thickness
                    shadeThick = (double) PIXEL / 7.5;

                    // If the current point was or is a part of a shape, color the square
                    if (points.contains(currentPoint)) {
                        // The top and left part of the shade
                        topShade.setOpacity(0.5);
                        topShade.setFill(Color.WHITE);
                        topShade.getPoints().addAll(new Double[] { 
                            0.0, 0.0, 
                            (double) PIXEL, 0.0, 
                            (double) PIXEL - shadeThick, shadeThick,
                            shadeThick, shadeThick, 
                            shadeThick, (double) PIXEL - shadeThick, 
                            0.0, (double) PIXEL 
                        });

                        // The bottom and right part of the shade
                        bottomShade.setOpacity(0.5);
                        bottomShade.setFill(Color.BLACK);
                        bottomShade.getPoints().addAll(new Double[] { 
                            0.0, (double) PIXEL, 
                            (double) PIXEL, (double) PIXEL,
                            (double) PIXEL, 0.0, 
                            (double) PIXEL - shadeThick, shadeThick, 
                            (double) PIXEL - shadeThick, (double) PIXEL - shadeThick,
                             shadeThick, (double) PIXEL - shadeThick 
                        });
                        
                        // Sets the color of the square according to its point type
                        square.setFill(colors.get(colorChoice).get(points.get(points.indexOf(currentPoint)).getType()));

                        cellGroup.getChildren().addAll(square, topShade, bottomShade);
                    } else {
                        // The top and left part of the shade
                        topShade.setOpacity(0.1);
                        topShade.setFill(Color.BLACK);
                        topShade.getPoints().addAll(new Double[] { 
                            0.0, 0.0, 
                            (double) PIXEL, 0.0, 
                            (double) PIXEL - shadeThick, shadeThick,
                            shadeThick, shadeThick, 
                            shadeThick, (double) PIXEL - shadeThick, 
                            0.0, (double) PIXEL 
                        });

                        // The bottom and right part of the shade
                        bottomShade.setOpacity(0.25);
                        bottomShade.setFill(Color.BLACK);
                        bottomShade.getPoints().addAll(new Double[] { 
                            0.0, (double) PIXEL, 
                            (double) PIXEL, (double) PIXEL,
                            (double) PIXEL, 0.0, 
                            (double) PIXEL - shadeThick, shadeThick, 
                            (double) PIXEL - shadeThick, (double) PIXEL - shadeThick, 
                            shadeThick, (double) PIXEL - shadeThick 
                        });

                        // Used to create the glossy effect
                        topRec = new Rectangle(PIXEL, PIXEL / 2.65);
                        topRec.setOpacity(0.05);
                        topRec.setFill(Color.WHITE);

                        halfCircle = new Arc((double) PIXEL / 2.0, (double) PIXEL / 2.0, (double) PIXEL / 2.0, (double) PIXEL / 8.0, 0.0f, 180.0f);
                        halfCircle.setOpacity(0.05);
                        halfCircle.setFill(Color.WHITE);
                        halfCircle.setType(ArcType.ROUND);
                        halfCircle.setRotate(180.0);

                        // Colors the square in a sort of gradient
                        square.setFill(Color.GRAY);
                        square.setOpacity((55.0 / 62.0 - ((double) i + 30.0) / ((double) Board.HEIGHT + 50)));

                        cellGroup.getChildren().addAll(square, topShade, bottomShade, halfCircle, topRec);
                    }
                    tetrisGrid.add(cellGroup, j, i);
                }
            }
        } else {
            // Stops the game, the moving shape, and the music
            gameOver = true;
            running = false;
            setSceneDisable(true);
            shapeTransition.stop();
            stopMusic();

            // Play sound effect
            if (checkSound.isSelected()) {
                soundEffectPlayer.play();
            }

            // Elements of the game over screen
            gameOverImg = new ImageView(new Image("file:resources/gameOver.png"));

            gameOverTitle = new Label("GAME OVER");
            gameOverTitle.setTextFill(Color.WHITE);
            gameOverTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15.0));
            gameOverTitle.setTextAlignment(TextAlignment.CENTER);

            gameOverSub = new Label("You made " + board.getScore() + " points\nand reached level " + board.getLevel() + "!");
            gameOverSub.setTextFill(Color.WHITE);
            gameOverSub.setFont(Font.font("Segoe UI Semilight", 13.0));
            gameOverSub.setTextAlignment(TextAlignment.CENTER);

            gameOverVbox = new VBox();
            gameOverVbox.getChildren().addAll(gameOverImg, gameOverTitle, gameOverSub);
            gameOverVbox.setAlignment(Pos.CENTER);

            gameOverCenter = new BorderPane();
            gameOverCenter.setCenter(gameOverVbox);

            stackPane.getChildren().addAll(boardShade, gameOverCenter);
        }
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
        mainThemePlayer = new MediaPlayer(new Media(new File("resources/Tetris.mp3").toURI().toString()));
        mainThemePlayer.setOnEndOfMedia(() -> {
            mainThemePlayer.seek(Duration.ZERO);
        });

        // Sound effect setup
        soundEffectPlayer = new MediaPlayer(new Media(new File("resources/gameOver.mp3").toURI().toString()));

        // Application icon
        primaryStage.getIcons().add(new Image("file:resources/tetris.png"));

        // Grid setup
        tetrisGrid = new GridPane();
        tetrisGrid.getStyleClass().add("grid");
        tetrisGrid.getStyleClass().add("background");

        for (int i = 0; i < Board.WIDTH; i++) {
            tetrisGrid.getColumnConstraints().add(new ColumnConstraints(PIXEL));
        }

        for (int i = 0; i < Board.HEIGHT; i++) {
            tetrisGrid.getRowConstraints().add(new RowConstraints(PIXEL));
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
                root.requestFocus();
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
                root.requestFocus();
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
                root.requestFocus();
            }
        });
        checkColor.selectedProperty().addListener((observable, oldValue, newValue) -> {
            colorChoice = (colorChoice + 1) % 2;
            paint();
        });

        spacePause = new Label("press space to pause");
        spacePause.getStyleClass().add("pause");

        vboxBottom.getChildren().addAll(checkColor, checkSound, checkGravity, spacePause);

        // Seperate layouts from the top to the bottom
        borderPane = new BorderPane();
        borderPane.getStyleClass().add("background");
        borderPane.setTop(vboxTop);
        borderPane.setBottom(vboxBottom);

        // Allows to overlays layouts for pausing the game
        stackPane = new StackPane();
        stackPane.getChildren().add(tetrisGrid);

        // The main layout of the game
        root = new HBox();
        root.getStyleClass().add("background");
        root.setPadding(new Insets(5.0));
        root.setSpacing(25.0);
        root.getChildren().addAll(borderPane, stackPane);

        // Elements of the pause screen
        pauseImg = new ImageView(new Image("file:resources/pause.png"));

        pauseCenter = new BorderPane();
        pauseCenter.setCenter(pauseImg);

        boardShade = new Rectangle(Board.WIDTH * PIXEL + 2 * (Board.WIDTH - 1), Board.HEIGHT * PIXEL + 2 * (Board.HEIGHT - 1));
        boardShade.setOpacity(0.5);
        boardShade.setFill(Color.BLACK);
        boardShade.toFront();

        // Creates the scene
        scene = new Scene(root);
        scene.getStylesheets().add("file:resources/application.css");
        scene.setOnKeyReleased(ke -> {
            pressed = true;
        });
        scene.setOnKeyPressed((ke) -> {
            if (running) {
                if (ke.getCode().equals(KeyCode.LEFT) || ke.getCode().equals(KeyCode.A)) {
                    board.moveLeft();
                    paint();
                } else if (ke.getCode().equals(KeyCode.DOWN) || ke.getCode().equals(KeyCode.S)) {
                    board.moveDown();
                    paint();
                } else if (ke.getCode().equals(KeyCode.RIGHT) || ke.getCode().equals(KeyCode.D)) {
                    board.moveRight();
                    paint();
                } else if ((ke.getCode().equals(KeyCode.UP) || ke.getCode().equals(KeyCode.W)) && pressed) {
                    pressed = false;
                    board.rotate();
                    paint();
                }
            }
            if (ke.getCode().equals(KeyCode.SPACE)) {
                if (gameOver) {
                    shapeTransition.stop();
                    startNewGame();
                    board.setGravity(checkGravity.isSelected());
                    stackPane.getChildren().removeAll(boardShade, gameOverCenter);
                } else {
                    if (running) {
                        running = false;

                        stopMusic();
                        shapeTransition.stop();
                        stackPane.getChildren().addAll(boardShade, pauseCenter);
                    } else {
                        running = true;

                        playMusic();
                        shapeTransition.play();
                        stackPane.getChildren().removeAll(boardShade, pauseCenter);
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

    /**
     * Creates/Resets variables needed to start a new game and calls .
     */
    public void startNewGame() {
        board = new Board();
        running = true;
        gameOver = false;
        pressed = true;
        shapeSpeed = board.getTimePerBlock();

        paint();
        createTransition();
        playMusic();

        soundEffectPlayer.stop();
        soundEffectPlayer.seek(Duration.ZERO);
        mainThemePlayer.seek(Duration.ZERO);
    }

    /**
     * Creates a new transition for the moving shape. 
     * Mostly called to changed the speed of the moving shape.
     */
    public void createTransition() {
        shapeSpeed = board.getTimePerBlock();

        shapeTransition = new SequentialTransition();
        pauseTransition = new PauseTransition(Duration.millis(shapeSpeed));
        pauseTransition.setOnFinished(evt -> {
            board.moveDown();
            paint();
        });
        shapeTransition.getChildren().add(pauseTransition);
        shapeTransition.setCycleCount(Timeline.INDEFINITE);
        shapeTransition.play();
    }

    /**
     * Plays the Tetris music with a fade in.
     */
    public void playMusic() {
        if (checkSound.isSelected()) {
            mainThemePlayer.play();
            mainThemePlayer.setVolume(0);

            musicTimeline = new Timeline(new KeyFrame(Duration.millis(musicFadeInMilli), new KeyValue(mainThemePlayer.volumeProperty(), 1)));
            musicTimeline.play();
        }
    }

    /**
     * Stops the Tetris music with a fade out.
     */
    public void stopMusic() {
        musicTimeline = new Timeline(new KeyFrame(Duration.millis(musicFadeOutMilli), new KeyValue(mainThemePlayer.volumeProperty(), 0)));
        musicTimeline.setOnFinished(evt -> {
            mainThemePlayer.pause();
        });
        musicTimeline.play();
    }

    /**
     * Disables every element in the scene. Used to pause the game and
     * when the game is finished.
     */
    public void setSceneDisable(boolean value) {
        level.setDisable(value);
        score.setDisable(value);
        line.setDisable(value);
        subLevel.setDisable(value);
        subScore.setDisable(value);
        subLine.setDisable(value);

        checkColor.setDisable(value);
        checkGravity.setDisable(value);
        checkSound.setDisable(value);

        spacePause.setDisable(value);
    }

    /**
     * Main method.
     */
    public static void main(String[] args) {
        launch(args);
    }

}