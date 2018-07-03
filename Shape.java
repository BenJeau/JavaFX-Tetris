import java.util.ArrayList;
import java.util.List;

/**
 * The class <b>Shape</b> represents the tetrominos used in Tetris. This class
 * can create them, rotate them, and move them. A list of points are alse kept
 * to represent the shape.
 * 
 * @author Benoît Jeaurond
 */
class Shape {

    /**
     * Contains the type of shape (an integer from 1 to 7)
     * 
     *  * 1 -- L 
     * 2 -- l 
     * 3 -- T 
     * 4 -- S 
     * 5 -- Z 
     * 6 -- J 
     * 7 -- O
     */
    private int type;

    /**
     * Conatins the current rotation orientation (an integer from 0 to 3)
     */
    private int rotation;

    /**
     * List containing the points of the shape
     */
    private List<Point> points;

    /**
     * Constructor of the Shape class specifying the type of the shape
     * 
     * @param num type of shape
     */
    public Shape(int num) {
        this.type = num;
        this.rotation = 0;
        this.points = new ArrayList<Point>();

        createPoints();        
    }

    /**
     * Constructor of the Shape class copying another shape 
     * 
     * @param shape shape to be copied
     */
    public Shape(Shape shape) {
        this.type = shape.type;
        this.rotation = shape.rotation;
        this.points = new ArrayList<Point>(shape.points.size());
        for (Point i : shape.points) {
            this.points.add(new Point(i.getX(), i.getY(), type));
        }
    }

    /**
     * Sets the starting point for the shape according to its type
     * 
     * Based on http://tetris.wikia.com/wiki/SRS
     */
    private void createPoints() {
        if (type != 7 && type != 4 && type != 1 && type != 3) {
            this.points.add(new Point(3, 0, type));
        }

        if (type != 1 && type != 6) {
            this.points.add(new Point(4, 0, type));
        }

        if (type != 5 && type != 6 && type != 3) {
            this.points.add(new Point(5, 0, type));
        }

        if (type == 2) {
            this.points.add(new Point(6, 0, type));
        } else {
            this.points.add(new Point(4, 1, type));
        }

        if (type != 4 && type != 2) {
            this.points.add(new Point(5, 1, type));
        }

        if (type == 1 || type == 4 || type == 6 || type == 3) {
            this.points.add(new Point(3, 1, type));
        }
    } 

    /**
     * Moves the shape down one spot
     */
    public void moveDown() {
        for (Point i : points) {
            i.modY(1);
        }
    }

    /**
     * Moves the shape left one spot
     */
    public void moveLeft() {
        for (Point i : points) {
            i.modX(-1);
        }
    }

    /**
     * Moves the shape right one spot
     */
    public void moveRight() {
        for (Point i : points) {
            i.modX(1);
        }
    }

    /**
     * Rotates the shape to the right
     * 
     * The algorithm used is based on http://tetris.wikia.com/wiki/SRS
     */
    public void rotate() {
        if (type != 7) {
            int lowX = 100;
            int lowY = 100;

            for (Point i : points) {
                if (i.getX() < lowX) {
                    lowX = i.getX();
                }

                if (i.getY() < lowY) {
                    lowY = i.getY();
                }
            }

            if (type == 2) {
                for (Point i : points) {
                    if (rotation == 0) {
                        i.setLocation(i.getY() - lowY + lowX + 2, i.getX() - lowX + lowY - 1);
                    } else if (rotation == 1) {
                        i.setLocation(i.getY() - lowY + lowX - 2, i.getX() - lowX + lowY + 2);
                    } else if (rotation == 2) {
                        i.setLocation(i.getY() - lowY + lowX + 1, i.getX() - lowX + lowY - 2);
                    } else {
                        i.setLocation(i.getY() - lowY + lowX - 1, i.getX() - lowX + lowY + 1);
                    }
                }
            } else {
                for (Point i : points) {
                    if (rotation == 1 || rotation == 2) {
                        i.setLocation(2 - (i.getY() - lowY) + lowX - 1, (i.getX() - lowX - 1 + (rotation % 2 * 2)) + lowY);
                    } else {
                        i.setLocation(2 - (i.getY() - lowY) + lowX, (i.getX() - lowX) + lowY);
                    }
                }
            }
        }

        rotation = (rotation + 1) % 4;
    }

    /**
     * Getter for the points of the next rotation of the shape
     * 
     * @return list of rotated points
     */
    public List<Point> getRotatePoints() {
        Shape rotated = new Shape(this);

        rotated.rotate();

        return rotated.points;
    }

    /**
     * Getter of type
     * 
     * @return type of shape
     */
    public int getType() {
        return type;
    }

    /**
     * Getter for points
     * 
     * @return list of points
     */
    public List<Point> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        String str = "";

        for (Point i : points) {
            str += "x:" + i.getX() + " y: " + i.getY() + "\n";
        }

        return str;
    }

}