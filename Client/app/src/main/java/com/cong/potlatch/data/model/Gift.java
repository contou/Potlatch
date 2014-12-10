package com.cong.potlatch.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cong.potlatch.util.HashUtils;

/**
 * Created by cong on 10/21/14.
 */
public class Gift implements Image, Parcelable {
    public String id;
    public String creatorId;
    public int points;
    public String title;
    public String image;
    public String description;

    public String category;

    @Override
    public String getImage() {
        return image;
    }

    public String getImportHashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("id").append(id == null ? "" : id)
                .append("points").append(points)
                .append("title").append(title == null ? "" : title)
                .append("image").append(image == null ? "" : image)
                .append("description").append(description == null ? "" : description);
        return HashUtils.computeWeakHash(sb.toString());
    }

    public Gift() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.creatorId);
        dest.writeInt(this.points);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.description);
        dest.writeString(this.category);
    }

    private Gift(Parcel in) {
        this.id = in.readString();
        this.creatorId = in.readString();
        this.points = in.readInt();
        this.title = in.readString();
        this.image = in.readString();
        this.description = in.readString();
        this.category = in.readString();
    }

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        public Gift createFromParcel(Parcel source) {
            return new Gift(source);
        }

        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };
}
