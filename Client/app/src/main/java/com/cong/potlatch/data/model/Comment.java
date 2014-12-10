package com.cong.potlatch.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cong.potlatch.util.HashUtils;

/**
 * Created by cong on 11/11/14.
 */
public class Comment implements Parcelable {
    public String id;
    public String giftId;
    public String content;
    public String creator;

    public String getImportHashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("id").append(id == null ? "" : id)
                .append("giftId").append(giftId == null ? "" : giftId)
                .append("content").append(content == null ? "" : content);
        return HashUtils.computeWeakHash(sb.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.giftId);
        dest.writeString(this.content);
    }

    public Comment() {
    }

    private Comment(Parcel in) {
        this.id = in.readString();
        this.giftId = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
