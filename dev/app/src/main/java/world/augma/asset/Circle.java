package world.augma.asset;

import java.util.List;

public class Circle {

    private static final float MIN_SIZE = 200.0f;
    private static final float MAX_SIZE = 500.0f;
    private static final int MAX_CHARS_FOR_MIN_SIZE = 8;
    private static final int EXTRA_CHAR_OFFSET = 30;

    private String circleID;
    private int circleType;
    private String name;
    private String description;
    private List<User> memberList;
    private User owner;
    private float radius;

    public Circle(String circleID, String name, String description, int circleType, List<User> memberList, User owner, float radius) {

        this.circleID = circleID;
        this.circleType = circleType;

        if(radius < MIN_SIZE) {
            this.radius = MIN_SIZE;
        } else if(radius > MAX_SIZE) {
            this.radius = MAX_SIZE;
        } else {
            this.radius = radius;
        }

        if(radius == MIN_SIZE && name.length() > MAX_CHARS_FOR_MIN_SIZE) {
            this.radius += EXTRA_CHAR_OFFSET * (name.length() - MAX_CHARS_FOR_MIN_SIZE);

            if(this.radius > MAX_SIZE) {
                this.radius = MAX_SIZE;
            }
        }

        this.name = name;
        this.description = description;
        this.memberList = memberList;
        this.owner = owner;
    }

    public Circle(String circleID, String name) {
        this.circleID = circleID;
        this.name = name;

        this.radius = 200;
        this.circleType = 0;
        this.description = null;
        this.memberList = null;
        this.owner = null;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {

        if(radius < MIN_SIZE) {
            this.radius = MIN_SIZE;
        } else if(radius > MAX_SIZE) {
            this.radius = MAX_SIZE;
        } else {
            this.radius = radius;
        }
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

    public List<User> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<User> memberList) {
        this.memberList = memberList;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getCircleID() {
        return circleID;
    }

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }

    @Override
    public String toString() {
        return name.concat("@").concat(owner == null ? "[NULL]" : owner.toString());
    }

    public int getCircleType() {
        return circleType;
    }

    public void setCircleType(int circleType) {
        this.circleType = circleType;
    }
}
