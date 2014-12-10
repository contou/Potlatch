package com.cong.potlatch.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;

import com.cong.potlatch.data.model.User;
import com.cong.potlatch.provider.GiftContract;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/24/14.
 */
public class UserHandler extends JSONHandler<User> {
    private static final String TAG = makeLogTag(UserHandler.class) ;
    ArrayList<User> mUsers;
    private Uri allUsersUri = GiftContract.addCallerIsSyncAdapterParameter(
            GiftContract.Users.CONTENT_URI);

    public UserHandler(Context context) {
        super(context);
        mUsers = new ArrayList<User>();
    }

    @Override
    public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        list.add(ContentProviderOperation.newDelete(allUsersUri).build());
        for(User User: mUsers) {
           build(true,User,list);
        }
    }

    @Override
    public void process(JsonElement element) {
      User[] users = new Gson().fromJson(element, User[].class);
      for(User User : users) {
          mUsers.add(User);
      }
    }

    @Override
    public void build(boolean isNew, User User, ArrayList<ContentProviderOperation> list) {
        Uri thisUserUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Users.buildUri(User.username));

        ContentProviderOperation.Builder builder;
        if(isNew) {
            builder = ContentProviderOperation.newInsert(allUsersUri);
        } else {
            builder = ContentProviderOperation.newUpdate(thisUserUri);
        }

        builder.withValue(GiftContract.Users.USER_NAME, User.username);
        builder.withValue(GiftContract.Users.USER_ACCOUNT_IMAGE, User.accountImage);
        builder.withValue(GiftContract.Users.USER_COVER_IMAGE, User.coverImage);
        builder.withValue(GiftContract.Users.USER_STATUS, User.status);
        builder.withValue(GiftContract.Users.USER_GENDER, User.gender);

        list.add(builder.build());
    }
}
