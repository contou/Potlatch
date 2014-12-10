package com.cong.potlatch.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cong on 11/24/14.
 */
public class User implements Parcelable {
    public String username;
    public String accountImage;
    public String coverImage;
    public String status;
    public String gender;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
    }

    public User() {
    }

    private User(Parcel in) {
        this.username = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
