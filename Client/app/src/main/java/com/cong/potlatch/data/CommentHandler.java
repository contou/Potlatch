package com.cong.potlatch.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.cong.potlatch.data.model.Comment;
import com.cong.potlatch.provider.GiftContract;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/11/14.
 */
public class CommentHandler extends JSONHandler<Comment>{
    private static final String TAG = makeLogTag(CommentHandler.class);
    private HashMap<String,Comment> mComments = new HashMap<String, Comment>();
    private String mGiftId;
    public CommentHandler(Context context) {
        super(context);
    }

    @Override
    public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        Uri uri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Gifts.buildCommentsDir(mGiftId));
        HashMap<String, String> commentsHashcodes = loadCommentHashcodes();
        HashSet<String> commentsToKeep = new HashSet<String>();
        boolean isIncrementalUpdate = commentsHashcodes != null && commentsHashcodes.size() > 0;

        if (isIncrementalUpdate) {
            LOGD(TAG, "Doing incremental update for comments.");
        } else {
            LOGD(TAG, "Doing FULL (non incremental) update for comments.");
            list.add(ContentProviderOperation.newDelete(uri).build());
        }
        int updatedComments = 0;
        for(Comment comment : mComments.values()) {
            String hashCode = comment.getImportHashCode();
            commentsToKeep.add(comment.id);

            // Add the expert, if necessary
            if (!isIncrementalUpdate || !commentsHashcodes.containsKey(comment.id) ||
                    !commentsHashcodes.get(comment.id).equals(hashCode)) {
                ++updatedComments;
                boolean isNew = !isIncrementalUpdate || !commentsHashcodes.containsKey(comment.id);
                build(isNew, comment, list);
            }
        }

        int deletedComments = 0;
        if (isIncrementalUpdate) {
            for (String commentId : commentsHashcodes.keySet()) {
                if (!commentsToKeep.contains(commentId)) {
                    buildDeleteOperation(commentId, list);
                    ++deletedComments;
                }
            }
        }
        LOGD(TAG, "Comments: " + (isIncrementalUpdate ? "INCREMENTAL" : "FULL") + " update. " +
                updatedComments + " to update, " + deletedComments + " to delete. New total: " +
                mComments.size());

    }

    private void buildDeleteOperation(String commentId, ArrayList<ContentProviderOperation> list) {
        Uri commentUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Comments.buildUri(commentId));
        list.add(ContentProviderOperation.newDelete(commentUri).build());
    }

    public void build(boolean isNew, Comment comment, ArrayList<ContentProviderOperation> list) {
        Uri targetCommentsUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Comments.CONTENT_URI);
        Uri thisCommentUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Comments.buildUri(comment.id));

        ContentProviderOperation.Builder builder;
        if(isNew) {
            builder = ContentProviderOperation.newInsert(targetCommentsUri);
        }else {
            builder = ContentProviderOperation.newUpdate(thisCommentUri);
        }

        builder.withValue(GiftContract.Comments.VIEW_TYPE_COLUMN,GiftContract.Comments.VIEW_TYPE);
        builder.withValue(GiftContract.Comments.COMMENT_ID, comment.id);
        builder.withValue(GiftContract.Comments.GIFT_ID, comment.giftId);
        builder.withValue(GiftContract.Comments.COMMENT_CONTENT, comment.content);
        builder.withValue(GiftContract.Comments.COMMENT_CREATOR, comment.creator);
        builder.withValue(GiftContract.Comments.COMMENT_IMPORT_HASHCODE, comment.getImportHashCode());

        list.add(builder.build());
    }

    @Override
    public void process(JsonElement element) {
        Comment[] comments = new Gson().fromJson(element, Comment[].class);
        if(comments != null)
        mGiftId = comments[0].giftId;
        for (Comment comment : comments) {
            mComments.put(comment.id,comment);
        }
    }

    private HashMap<String, String> loadCommentHashcodes() {
        Uri uri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Gifts.buildCommentsDir(mGiftId));
        Cursor cursor = mContext.getContentResolver().query(uri, CommentHashcodeQuery.PROJECTION,
                null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        while (cursor.moveToNext()) {
            String commentId = cursor.getString(CommentHashcodeQuery.COMMENT_ID);
            String hashcode = cursor.getString(CommentHashcodeQuery.COMMENT_IMPORT_HASHCODE);
            result.put(commentId, hashcode == null ? "" : hashcode);
        }
        cursor.close();
        return result;
    }

    private interface CommentHashcodeQuery {
        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Comments.COMMENT_ID,
                GiftContract.Comments.COMMENT_IMPORT_HASHCODE
        };
        final int _ID = 0;
        final int COMMENT_ID = 1;
        final int COMMENT_IMPORT_HASHCODE = 2;
    }
}
