package org.magnum.mobilecloud.video.core;


import org.springframework.data.annotation.Id;

/**
 * Created by cong on 11/13/14.
 */
public class Category {
    @Id
    private String id;
    private String name;
    public Category(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
