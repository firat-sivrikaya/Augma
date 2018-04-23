package world.augma.asset;

import java.util.List;

public class Circle {

    private String name;
    private String description;
    private List<User> subscribers;
    private User owner;

    public Circle( String name, String description, List<User> subscribers, User owner) {
        this.name = name;
        this.description = description;
        this.subscribers = subscribers;
        this.owner = owner;
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


}
