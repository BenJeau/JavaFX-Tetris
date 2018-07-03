import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.Arrays;

/**
 * The class <b>Board</b> represents the board/grid used in Tetris. This class
 * contains the moving shape, a list of points of fallen shapes, and stats about the
 * game. It contains the methods used for collision detection, manipulating the 
 * falling shape, and clearing full lines all while keeping up with the score, 
 * level, number of cleared lines, and the speed of the falling shape.
 * 
 * @author Benoît Jeaurond
 */
class Board {

    /**
     * Keeps the state of the game
     */
    private boolean gameOver;
    
    /**
     * True if the gravity feature is on (gravity on blocks above cleared lines with space underneath the cleared line)
     */
    private boolean gravity;

    /**
     * Stats about the current game
     */
    private int numClearedLines, level, score, timePerBlock;

    /**
     * A list of points of the fallen shapes
     */
    private List<Point> points;

    /**
     * Random object to create random numbers for the type of shape
     */
    private Random rand;

    /**
     * The shape that is currently moving
     */
    private Shape currentShape;

    /**
     * Constants specifying the board size
     */
    public static final int WIDTH = 10, HEIGHT = 22;

    /**
     * Constructor of the Board class
     */
    public Board() {
        this.points = new ArrayList<Point>();
        this.rand = new Random();
        this.gameOver = false;
        this.numClearedLines = 0;
        this.level = 0;
        this.score = 0;
        this.gravity = true;
        this.timePerBlock = 800;

        createCurrentShape();
    }

    /**
     * Creates a new shape randomly and sets it as the current shape
     */
    public void createCurrentShape() {
        int num = rand.nextInt(8);

        if (num == 7 || (currentShape != null && num == currentShape.getType())) {
            num = rand.nextInt(7);
        }

        if (currentShape != null) {
            points.addAll(currentShape.getPoints());
        }

        currentShape = new Shape(num + 1);
    }

    /**
     * Helper method for collision detection
     * 
     * @return true if there are point(s) down the current shape
     */
    private boolean hasPointsDown() {
        for (Point i : currentShape.getPoints()) {
            if (points.contains(new Point(i.getX(), i.getY() + 1))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for collision detection
     * 
     * @return true if there are point(s) right of the current shape
     */
    private boolean hasPointsRight() {
        for (Point i : currentShape.getPoints()) {
            if (points.contains(new Point(i.getX() + 1, i.getY()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for collision detection
     * 
     * @return true if there are point(s) left of the current shape
     */
    private boolean hasPointsLeft() {
        for (Point i : currentShape.getPoints()) {
            if (points.contains(new Point(i.getX() - 1, i.getY()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for ending the game
     * 
     * @return true if the current shape is close to the top
     */
    private boolean closeToTopBorder() {
        for (Point i : currentShape.getPoints()) {
            if (i.getY() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for moving the shape left
     * 
     * @return true if the current shape is close to the left
     */
    private boolean closeToLeftBorder() {
        for (Point i : currentShape.getPoints()) {
            if (i.getX() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for moving the shape right
     * 
     * @return true if the current shape is close to the right
     */
    private boolean closeToRightBorder() {
        for (Point i : currentShape.getPoints()) {
            if (i.getX() == WIDTH - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for moving the shape down
     * 
     * @return true if the current shape is close to the bottom
     */
    private boolean closeToBottomBorder() {
        for (Point i : currentShape.getPoints()) {
            if (i.getY() == HEIGHT - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for rotating the current shape
     * 
     * @return true if the next rotation is within the board and does not collide
     *         with other shapes
     */
    private boolean canRotate() {
        List<Point> rotated = currentShape.getRotatePoints();

        for (Point i : rotated) {
            if (i.getX() >= WIDTH || i.getY() >= HEIGHT || i.getX() < 0 || i.getY() < 0 || points.contains(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Rotates the current shape
     */
    public void rotate() {
        if (canRotate()) {
            currentShape.rotate();
        }
    }

    /**
     * Moves the current shape left
     */
    public void moveLeft() {
        if (!hasPointsLeft() && !closeToLeftBorder()) {
            currentShape.moveLeft();
        }
    }

    /**
     * Moves the current shape right
     */
    public void moveRight() {
        if (!hasPointsRight() && !closeToRightBorder()) {
            currentShape.moveRight();
        }
    }

    /**
     * Moves the current shape down, checks if the game is finished and creates
     * another shape if it can't move down
     */
    public void moveDown() {
        if (!hasPointsDown() && !closeToBottomBorder()) {
            currentShape.moveDown();
        } else {
            if (closeToTopBorder()) {
                gameOver = true;
            } else {
                createCurrentShape();
                removeLines();
            }
        }
    }

    /**
     * Removes full lines (if present), updates score and level
     */
    private void removeLines() {
        boolean gravityTriggerd;

        do {
            gravityTriggerd = false;
            List<Integer> fullLines = new ArrayList<Integer>(HEIGHT);

            List<Point> allPoints = getPoints();

            if (allPoints.size() != 0) {
                for (int i = 0; i < HEIGHT; i++) {
                    boolean full = true;
                    row: for (int j = 0; j < WIDTH; j++) {
                        if (!allPoints.contains(new Point(j, i))) {
                            full = false;
                            break row;
                        }
                    }

                    if (full) {
                        fullLines.add(i);
                    }
                }
            }

            if (fullLines.size() != 0) {
                numClearedLines += fullLines.size();
                score += calculateCurrentScore(fullLines.size());

                int mostBottomLine = 0;

                for (int i : fullLines) {
                    if (i > mostBottomLine) {
                        mostBottomLine = i;
                    }

                    Predicate<Point> pointsPredicate = p -> p.getY() == i;
                    points.removeIf(pointsPredicate);

                    for (int j = 0; j < points.size(); j++) {
                        if (points.get(j).getY() < i) {
                            points.get(j).modY(1);
                        }
                    }
                }

                if (mostBottomLine != HEIGHT - 1 && gravity) {

                    allPoints = getPoints();

                    for (int i = 0; i < WIDTH; i++) {
                        int numOfEmpty = 0;

                        for (int j = mostBottomLine + 1; j < HEIGHT; j++) {
                            if (!allPoints.contains(new Point(i, j))) {
                                numOfEmpty++;
                            } else {
                                break;
                            }
                        }

                        if (numOfEmpty != 0) {
                            gravityTriggerd = false;
                            for (int j = 0; j < points.size(); j++) {
                                if (points.get(j).getX() == i && points.get(j).getY() <= mostBottomLine) {
                                    points.get(j).modY(numOfEmpty);
                                    gravityTriggerd = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (gravityTriggerd);

        // Based on this website
        // https://en.wikipedia.org/wiki/Tetris#Variations
        level = numClearedLines / 10;

        updateSpeed();
    }

    /**
     * Calculates the score of the current cleared lines Based on
     * http://tetris.wikia.com/wiki/Scoring
     * 
     * @param num number of cleared lines at once
     * @return the score for the that number of lines
     */
    private int calculateCurrentScore(int num) {
        int baseNum = 40;

        if (num == 2) {
            baseNum = 100;
        } else if (num == 3) {
            baseNum = 300;
        } else if (num == 4) {
            baseNum = 1000;
        }

        return baseNum * (level + 1);
    }

    /**
     * Updates the time (milliseconds) per block on screen according to the level
     * and this source 
     * https://gaming.stackexchange.com/questions/13057/tetris-difficulty
     */
    private void updateSpeed() {
        double baseFrame = 48.0;

        if (-1 < level && level < 9) {
            timePerBlock = (int) (((baseFrame - (level * 5.0)) / 60.0) * 1000.0);
        } else if (level == 9) {
            timePerBlock = (int) ((6.0 / 60.0) * 1000.0);
        } else if (9 < level && level < 19) {
            timePerBlock = (int) (((8.0 - ((13.0 - 1.0) / 3.0)) / 60.0) * 1000.0);
        } else if (18 < level && level < 29) {
            timePerBlock = (int) ((2.0 / 60.0) * 1000.0);
        } else {
            timePerBlock = (int) ((1.0 / 60.0) * 1000.0);
        }
    }

    /**
     * Returns a list of points
     * 
     * @return list containing all the points on the board (including the current
     *         shape)
     */
    public List<Point> getPoints() {
        List<Point> points = new ArrayList<Point>();

        points.addAll(this.points);
        points.addAll(currentShape.getPoints());

        Set<Point> set = new HashSet<Point>();
        set.addAll(points);
        points.clear();
        points.addAll(set);

        return points;
    }

    /**
     * Print the board and the value of some methods, used for testing this class
     */
    public void getStatus() {
        StringBuffer sb = new StringBuffer();

        sb.append(toString());
        sb.append("--- Border ---\n");
        sb.append("Left " + closeToLeftBorder() + "\n");
        sb.append("Right " + closeToRightBorder() + "\n");
        sb.append("Bottom " + closeToBottomBorder() + "\n");
        sb.append("--- Points ---\n");
        sb.append("Left " + hasPointsLeft() + "\n");
        sb.append("Right " + hasPointsRight() + "\n");
        sb.append("Bottom " + hasPointsDown() + "\n");
        sb.append("--- Rotate ---\n");
        sb.append(canRotate());

        System.out.println(sb.toString());
    }

    /**
     * Getter of numClearedLines
     * 
     * @return the total number of cleared lines
     */
    public int getNumClearedLines() {
        return numClearedLines;
    }

    /**
     * Getter of gravity
     * 
     * @return true if gravity is on, false otherwise
     */
    public boolean getGravity() {
        return gravity;
    }

    /**
     * Setter for gravity
     * 
     * @param gravity the new value of gravity
     */
    public void setGravity(boolean gravity) {
        this.gravity = gravity;
    }

    /**
     * Getter of gameOver
     * 
     * @return true if the game is finished
     */
    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * Getter of level
     * 
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter of timePerBlock
     * 
     * @return the time for each block to be on the screen
     */
    public int getTimePerBlock() {
        return timePerBlock;
    }

    /**
     * Getter of score
     * 
     * @return the score of the current game
     */
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        int[][] board = new int[HEIGHT][WIDTH];
        for (Point i : points) {
            board[i.getY()][i.getX()] = i.getType();
        }

        for (Point i : currentShape.getPoints()) {
            board[i.getY()][i.getX()] = currentShape.getType();
        }

        String str = "";
        for (int[] i : board) {
            str += Arrays.toString(i) + "\n";
        }
        return str;
    }

}