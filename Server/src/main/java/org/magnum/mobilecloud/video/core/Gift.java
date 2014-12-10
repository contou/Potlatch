package org.magnum.mobilecloud.video.core;

import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;

import javax.persistence.ElementCollection;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple object to represent a video and its URL for viewing.
 * <p/>
 * You probably need to, at a minimum, add some annotations to this
 * class.
 * <p/>
 * You are free to add annotations, members, and methods to this
 * class. However, you probably should not change the existing
 * methods or member variables. If you do change them, you need
 * to make sure that they are serialized into JSON in a way that
 * matches what is expected by the auto-grader.
 *
 * @author mitchell
 */
public class Gift {

    @Id
    private String id;

    private String image;
    private long points;
    private String description;
    private String title;
    private String category;
    private String creatorId;

    @ElementCollection
    private Set<String> likesUsernames = new HashSet<String>();

    public Gift(){
    }

    public Gift(String title, String category, String description, long points, String creatorId) {
        super();
        this.title = title;
        this.points = points;
        this.category = category;
        this.description = description;
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Set<String> getLikesUsernames() {
        return likesUsernames;
    }

    public void setLikesUsernames(Set<String> likesUsernames) {
        this.likesUsernames = likesUsernames;
    }

    /**
     * Two Videos will generate the same hashcode if they have exactly the same
     * values for their title, url, and duration.
     */
    @Override
    public int hashCode() {
        // Google Guava provides great utilities for hashing
        return Objects.hashCode(title, description);
    }

    /**
     * Two Videos are considered equal if they have exactly the same values for
     * their title, url, and duration.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gift) {
            Gift other = (Gift) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(title, other.title)
                    && Objects.equal(description, other.description);
        } else {
            return false;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Gift{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", points=" + points +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", likesUsernames=" + likesUsernames +
                '}';
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
