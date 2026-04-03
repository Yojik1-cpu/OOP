package snake.model;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final LinkedList<Point> body = new LinkedList<>();
    private Direction currentDirection = Direction.RIGHT;
    private final LinkedList<Direction> directionQueue = new LinkedList<>();
    private int segmentsToGrow = 0;

    public Snake(Point start) {
        body.add(start);
    }

    public void move() {
        if (!directionQueue.isEmpty()) {
            currentDirection = directionQueue.poll();
        }

        Point head = body.getFirst();
        Point newHead = new Point(head.x + currentDirection.x, head.y + currentDirection.y);
        body.addFirst(newHead);
        if (segmentsToGrow > 0) {
            segmentsToGrow--;
        } else {
            body.removeLast();
        }
    }
    public void grow(int amount) {
        this.segmentsToGrow += amount;
    }

    public boolean isCollisionWithSelf() {
        Point head = getHead();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public List<Point> getBody() {
        return body;
    }

    public void setDirection(Direction newDirection) {
        Direction lastDirection = directionQueue.isEmpty() ? currentDirection : directionQueue.get(directionQueue.size() - 1);
        
        if (!lastDirection.isOpposite(newDirection) && lastDirection != newDirection) {
            if (directionQueue.size() < 3) {
                directionQueue.add(newDirection);
            }
        }
    }
}