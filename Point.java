/**
 * The class <b>Point</b> helps keeping the position of the shape in the 
 * Tetris game. They have a x, y coordinate and keep the kind of shape that
 * point in particular is.
 * 
 * @author Benoît Jeaurond
 */
class Point {

    /**
     * Coordinates of the point
     */
    private int x, y;

    /**
     * Type of shape of the point
     */
    private int type;

    /**
     * Constructor of the Point class specifying the coordinates of the point
     * 
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor of the Point class specifying the coordinates of the point and its type
     * 
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param type the type of shape of the point
     */
    public Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Setter for both coordinates
     * 
     * @param x the new x coordinate of the point
     * @param y the new y coordinate of the point
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for type
     * 
     * @return the type of the point
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for x
     * 
     * @param x the new x coordinate of the point
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter for x
     * 
     * @return the x coordinate of the point
     */
    public int getX() {
        return x;
    }

    /**
     * Modify the value of the x coordinate
     * 
     * @param mod the value to be added to the x coordinate
     */
    public void modX(int mod) {
        x += mod;
    }

    /**
     * Setter for y
     * 
     * @param y the new y coordinate of the point
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Getter for y
     * 
     * @return the y coordinate of the point
     */
    public int getY() {
        return y;
    }

    /**
     * Modify the value of the y coordinate
     * 
     * @param mod the value to be added to the y coordinate
     */
    public void modY(int mod) {
        y += mod;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            return x == ((Point) obj).x && y == ((Point) obj).y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Point X: " + x + " Y: " + y + " Type: " + type;
    }
}