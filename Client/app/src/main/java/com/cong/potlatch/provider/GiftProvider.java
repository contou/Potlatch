package com.cong.potlatch.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.cong.potlatch.util.SelectionBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.cong.potlatch.util.LogUtils.LOGV;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/21/14.
 */
public class GiftProvider extends ContentProvider{
    private static final String TAG = makeLogTag(GiftProvider.class);

    private GiftDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int GIFTS = 100;
    private static final int GIFTS_ID = 101;
    private static final int GIFTS_COMMENTS = 102;
    private static final int GIFTS_SEARCH = 103;
    private static final int GIFTS_IN_CATEGORY = 104;

    private static final int COMMENTS = 200;
    private static final int COMMENTS_ID = 201;

    private static final int CATEGORIES = 300;
    private static final int CATEGORIES_ID = 301;

    private static final int USERS = 400;
    private static final int USERS_ID = 401;

    private static final int SEARCH_INDEX = 900;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GiftContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "gifts", GIFTS);
        matcher.addURI(authority, "gifts/search/*", GIFTS_SEARCH);
        matcher.addURI(authority, "gifts/categories/*", GIFTS_IN_CATEGORY);
        matcher.addURI(authority, "gifts/*", GIFTS_ID);
        matcher.addURI(authority, "gifts/*/comments", GIFTS_COMMENTS);

        matcher.addURI(authority, "comments", COMMENTS);
        matcher.addURI(authority, "comments/*", COMMENTS_ID);

        matcher.addURI(authority, "categories", CATEGORIES);
        matcher.addURI(authority, "categories/*", CATEGORIES);

        matcher.addURI(authority, "users", USERS);
        matcher.addURI(authority, "users/*", USERS);

        matcher.addURI(authority, "search_index", SEARCH_INDEX); // 'update' only

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new GiftDatabase(getContext());
        return true;
    }
    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        GiftDatabase.deleteDatabase(context);
        mOpenHelper = new GiftDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GIFTS:
                return GiftContract.Gifts.CONTENT_TYPE;
            case GIFTS_ID:
                return GiftContract.Gifts.CONTENT_ITEM_TYPE;
            case GIFTS_COMMENTS:
                return GiftContract.Comments.CONTENT_ITEM_TYPE;
            case COMMENTS:
                return GiftContract.Comments.CONTENT_TYPE;
            case COMMENTS_ID:
                return GiftContract.Comments.CONTENT_ITEM_TYPE;
            case CATEGORIES:
                return GiftContract.Categories.CONTENT_TYPE;
            case CATEGORIES_ID:
                return GiftContract.Categories.CONTENT_ITEM_TYPE;
            case USERS:
                return GiftContract.Users.CONTENT_TYPE;
            case USERS_ID:
                return GiftContract.Users.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** Returns a tuple of question marks. For example, if count is 3, returns "(?,?,?)". */
    private String makeQuestionMarkTuple(int count) {
        if (count < 1) {
            return "()";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(?");
        for (int i = 1; i < count; i++) {
            stringBuilder.append(",?");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            LOGV(TAG, "uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +
                    " selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");
        }
        switch (match) {
            default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildSimpleSelection(uri);


                boolean distinct = !TextUtils.isEmpty(
                        uri.getQueryParameter(GiftContract.QUERY_PARAMETER_DISTINCT));

                Cursor cursor = builder
                        .where(selection, selectionArgs)
                        .query(db, distinct, projection, sortOrder, null);
                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }

        }

    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString()
                + ", account=" +")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GIFTS: {
                db.insertOrThrow(GiftDatabase.Tables.GIFTS, null, values);
                notifyChange(uri);
                return GiftContract.Gifts.buildUri(values.getAsString(GiftContract.Gifts.GIFT_ID));
            }
            case COMMENTS: {
                db.insertOrThrow(GiftDatabase.Tables.COMMENTS, null, values);
                notifyChange(uri);
                return GiftContract.Comments.buildUri(values.getAsString(GiftContract.Comments.COMMENT_ID));
            }
            case CATEGORIES: {
                db.insertOrThrow(GiftDatabase.Tables.CATEGORIES, null, values);
                notifyChange(uri);
                return GiftContract.Categories.buildUri(values.getAsString(GiftContract.Categories.CATEGORY_ID));
            }
            case USERS: {
                db.insertOrThrow(GiftDatabase.Tables.USERS, null, values);
                notifyChange(uri);
                return GiftContract.Users.buildUri(values.getAsString(GiftContract.Users.USER_NAME));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri == GiftContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
//        final int match = sUriMatcher.match(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString()
                + ", account=" +")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        if (match == SEARCH_INDEX) {
            // update the search index
            GiftDatabase.updateGiftSearchIndex(db);
            return 1;
        }
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }
    /**
     * Apply the given set of {@link android.content.ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private void notifyChange(Uri uri) {
        // We only notify changes if the caller is not the sync adapter.
        // The sync adapter has the responsibility of notifying changes (it can do so
        // more intelligently than we can -- for example, doing it only once at the end
        // of the sync instead of issuing thousands of notifications for each record).
        if (!GiftContract.hasCallerIsSyncAdapterParameter(uri)) {
            Context context = getContext();
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GIFTS: {
                return builder.table(GiftDatabase.Tables.GIFTS);
            }
            case GIFTS_ID: {
                final String giftId = GiftContract.Gifts.getGiftId(uri);
                return builder.table(GiftDatabase.Tables.GIFTS)
                        .where(GiftContract.Gifts.GIFT_ID + "=?", giftId);
            }
            case GIFTS_COMMENTS: {
                final String giftId = GiftContract.Gifts.getGiftId(uri);
                return builder.table(GiftDatabase.Tables.COMMENTS)
                        .where(GiftContract.CommentsColumns.GIFT_ID + "=?", giftId);
            }
            case GIFTS_SEARCH: {
                final String query = GiftContract.Gifts.getSearchQuery(uri);
                return builder.table(GiftDatabase.Tables.GIFTS_SEARCH_JOIN_GIFTS)
                        .mapToTable(GiftContract.Gifts._ID, GiftDatabase.Tables.GIFTS)
                        .mapToTable(GiftContract.Gifts.GIFT_ID, GiftDatabase.Tables.GIFTS)
                        .where(GiftDatabase.GiftsSearchColumns.BODY + " MATCH ?", query);

            }
            case GIFTS_IN_CATEGORY: {
                final String categoryId = GiftContract.Gifts.getCategoryId(uri);
                return builder.table(GiftDatabase.Tables.GIFTS)
                        .where(GiftContract.Gifts.GIFT_CATEGORY_ID + " =?",categoryId);
            }

            case COMMENTS: {
                return builder.table(GiftDatabase.Tables.COMMENTS);
            }
            case COMMENTS_ID: {
                final String commentId = GiftContract.Comments.getCommentId(uri);
                return builder.table(GiftDatabase.Tables.COMMENTS)
                        .where(GiftContract.Comments.COMMENT_ID + "=?", commentId);
            }
            case CATEGORIES: {
                return builder.table(GiftDatabase.Tables.CATEGORIES);
            }
            case CATEGORIES_ID: {
                final String categoryId = GiftContract.Categories.getCategoryId(uri);
                return builder.table(GiftDatabase.Tables.CATEGORIES)
                        .where(GiftContract.Categories.CATEGORY_ID + "=?", categoryId);
            }
            case USERS: {
                return builder.table(GiftDatabase.Tables.USERS);
            }
            case USERS_ID: {
                final String userName = GiftContract.Users.getUserName(uri);
                return builder.table(GiftDatabase.Tables.USERS)
                        .where(GiftContract.Users.USER_NAME + "=?", userName);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case GIFTS: {
                return builder.table(GiftDatabase.Tables.GIFTS);
            }
            case GIFTS_ID: {
                final String giftId = GiftContract.Gifts.getGiftId(uri);
                return builder.table(GiftDatabase.Tables.GIFTS)
                        .where(GiftContract.Gifts.GIFT_ID + "=?", giftId);
            }
            case GIFTS_COMMENTS: {
                final String giftId = GiftContract.Gifts.getGiftId(uri);
                return builder.table(GiftDatabase.Tables.COMMENTS)
                        .where(GiftContract.CommentsColumns.GIFT_ID + "=?", giftId);
            }
            case COMMENTS: {
                return builder.table(GiftDatabase.Tables.COMMENTS);
            }
            case COMMENTS_ID: {
                final String commentId = GiftContract.Comments.getCommentId(uri);
                return builder.table(GiftDatabase.Tables.COMMENTS)
                        .where(GiftContract.Comments.COMMENT_ID + "=?", commentId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
