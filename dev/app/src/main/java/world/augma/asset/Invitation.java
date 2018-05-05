package world.augma.asset;

import java.io.Serializable;

public class Invitation implements Serializable {

    private String senderID;
    private String senderName;
    private String circleID;
    private String circleName;

    public Invitation(String senderID, String senderName, String circleID, String circleName) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.circleID = circleID;
        this.circleName = circleName;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getCircleID() {
        return circleID;
    }

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
}
