package world.augma.asset;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

    private String userID;
    private String username;
    private String bio;
    private String email;
    private String name;
    private String password;
    private String profilePic;

    private String birthdate;
    private int type;
    private int rating;

    private List<Circle> memberships;
    private List<Invitation> invitations;
    private List<Note> ownedNotes;
    private List<Circle> ownedCircles;

    public User(String userID, String username, String bio, String email, String name, String password,
                String profilePic, String birthdate, int type, List<Circle> memberships, List<Invitation> invitations,
                List<Note> ownedNotes, List<Circle> ownedCircles, int rating) {
        this.userID = userID;
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.name = name;
        this.password = password;
        this.profilePic = profilePic;
        this.birthdate = birthdate;
        this.type = type;
        this.memberships = memberships;
        this.invitations = invitations;
        this.ownedNotes = ownedNotes;
        this.ownedCircles = ownedCircles;
        this.rating = rating;
    }


    public boolean checkCircleMembership(String circleID){
        for (Circle circle:memberships) {
            if(circle.getCircleID().equals(circleID)){
                return true;
            }

        }
        return false;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Circle> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Circle> memberships) {
        this.memberships = memberships;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public List<Note> getOwnedNotes() {
        return ownedNotes;
    }

    public void setOwnedNotes(List<Note> ownedNotes) {
        this.ownedNotes = ownedNotes;
    }

    public List<Circle> getOwnedCircles() {
        return ownedCircles;
    }

    public void setOwnedCircles(List<Circle> ownedCircles) {
        this.ownedCircles = ownedCircles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return userID + " " + username + " " + email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
