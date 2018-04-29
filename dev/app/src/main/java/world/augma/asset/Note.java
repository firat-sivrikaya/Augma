package world.augma.asset;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Note {

    private String noteID;
    private List<Circle> circleList;
    private String fileLoc;
    private float longitude;
    private float latitude;
    private User owner;
    private int type;
    private int rating;
    private int superRating;

    public Note(String noteID, List<Circle> circleList, String fileLoc, float longitude,
                float latitude, User owner, int type, int rating, int superRating)
    {
        this.noteID = noteID;
        this.circleList = circleList;
        this.fileLoc = fileLoc;
        this.longitude = longitude;
        this.latitude = latitude;
        this.owner = owner;
        this.type = type;
        this.rating = rating;
        this.superRating = superRating;
    }
    public Note(String noteID,  int rating, int superRating)
    {
        this.noteID = noteID;
        this.rating = rating;
        this.superRating = superRating;
    }

    public void setLongitudeFromLatLng(LatLng noteLoc)
    {
        this.longitude = (float)noteLoc.longitude;
    }

    public void setLatitudeFromLatLng(LatLng noteLoc)
    {
        this.latitude = (float)noteLoc.latitude;
    }

    public String getNoteID() {
        return noteID;
    }

    public List<Circle> getCircleList() {
        return circleList;
    }

    public void setCircleList(List<Circle> circleList) {
        this.circleList = circleList;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public User getOwner() {
        return owner;
    }

    public int getType() {
        return type;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getSuperRate() {
        return superRating;
    }

    public void setSuperRate(int superRate) {
        this.superRating = superRating;
    }
}
