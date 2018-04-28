package world.augma.asset;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Circle {

    private static final int MIN_SIZE = 200;

    private String name;
    private String description;
    private List<User> subscribers;
    private User owner;
    private int x;
    private int y;
    private int radius;
    private boolean isPlaced;

    public Circle(String name, String description, List<User> subscribers, User owner, int x, int y, int radius) {
        this.name = name;
        this.description = description;
        this.subscribers = subscribers;
        this.owner = owner;
        this.x = x - radius / 2;
        this.y = y - radius / 2;
        this.radius = radius < MIN_SIZE ? MIN_SIZE : radius;
        isPlaced = false;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius < MIN_SIZE ? MIN_SIZE : radius;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x - radius / 2;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y - radius / 2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return name.concat("@").concat(owner == null ? "[NULL]" : owner.toString());
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    public void computePositionOnScreen(List<Circle> circleList) {
        int centerX = circleList.get(0).getX();
        int centerY = circleList.get(0).getY();
        boolean areColliding;
        Point point = new Point();
        List<Point> goodPoints = new ArrayList<>();

        if(isPlaced) {
            return;
        }

        for(int i = 0; i < circleList.size(); i++) {
            if(circleList.get(i).isPlaced()) {
                for(int angle = 0; angle < 360; angle++) {
                    areColliding = false;
                    point.x = circleList.get(i).getX() + (int) (Math.cos(angle * Math.PI / 180) * (radius + circleList.get(i).getRadius() + 1));
                    point.y = circleList.get(i).getY() + (int) (Math.sin(angle * Math.PI / 180) * (radius + circleList.get(i).getRadius() + 1));

                    for(int j = 0; j < circleList.size(); j++) {
                        if(circleList.get(j).isPlaced() && !areColliding && Math.hypot(point.x - circleList.get(j).getX(), point.y - circleList.get(j).getY()) < (radius + circleList.get(j).getRadius())) {
                            areColliding = true;
                        }
                    }

                    if(!areColliding) {
                        goodPoints.add(point);
                    }
                }
            }
        }

        double minDistance = -1;
        int best = 0;

        for(int i = 0; i < goodPoints.size(); i++) {
            if(minDistance == -1 || Math.hypot(centerX - goodPoints.get(i).x, centerY - goodPoints.get(i).y) < minDistance) {
                best = i;
                minDistance = Math.hypot(centerX - goodPoints.get(i).x, centerY - goodPoints.get(i).y);
            }
        }

        if(!goodPoints.isEmpty()) {
            x = goodPoints.get(best).x;
            y = goodPoints.get(best).y;
        }
        isPlaced = true;
    }
}
