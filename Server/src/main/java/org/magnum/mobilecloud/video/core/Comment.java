package org.magnum.mobilecloud.video.core;


import org.springframework.data.annotation.Id;

/**
 * Created by cong on 11/12/14.
 */
public class Comment {
    @Id
    private String id;

    private String parentId;
    private String giftId;
    private String content;
    private String creator;

    public Comment(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }
}
