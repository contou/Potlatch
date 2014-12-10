package com.cong.potlatch.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.cong.potlatch.provider.GiftContract.CategoriesColumns;
import com.cong.potlatch.provider.GiftContract.CommentsColumns;
import com.cong.potlatch.provider.GiftContract.Gifts;
import com.cong.potlatch.provider.GiftContract.GiftsColumns;
import com.cong.potlatch.provider.GiftContract.UsersColumns;
import com.cong.potlatch.provider.GiftContract.ViewType;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/21/14.
 */
public class GiftDatabase extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag(GiftDatabase.class);

    private static final String DATABASE_NAME = "gift.db";
    
    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int CUR_DATABASE_VERSION = 1;

    private final Context mContext;


    public GiftDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    interface Tables {
        String GIFTS = "gifts";
        String GIFTS_SEARCH = "gifts_search";
        String COMMENTS = "comments";
        String CATEGORIES = "categories";
        String USERS = "users";

        String GIFTS_SEARCH_JOIN_GIFTS = "gifts_search "
                + "LEFT OUTER JOIN gifts ON gifts_search.gift_id=gifts.gift_id ";
    }

    interface GiftsSearchColumns {
        String GIFT_ID = "gift_id";
        String BODY = "body";
    }
    /** Fully-qualified field names. */
    private interface Qualified {
        String GIFTS_SEARCH = Tables.GIFTS_SEARCH + "(" + GiftsSearchColumns.GIFT_ID
                + "," + GiftsSearchColumns.BODY + ")";
    }
    /** {@code REFERENCES} clauses. */
    private interface References {
        String GIFT_ID = "REFERENCES " + Tables.GIFTS + "(" + Gifts.GIFT_ID + ")";
    }
    

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Tables.GIFTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GiftsColumns.GIFT_ID + " TEXT NOT NULL,"
                + GiftsColumns.GIFT_TITLE + " TEXT NOT NULL,"
                + GiftsColumns.GIFT_IMAGE + " TEXT NOT NULL,"
                + GiftsColumns.GIFT_POINTS + " INTEGER NOT NULL,"
                + GiftsColumns.GIFT_DESCRIPTION + " TEXT,"
                + GiftsColumns.GIFT_CATEGORY_ID + " TEXT NOT NULL,"
                + GiftsColumns.GIFT_CREATOR_ID + " TEXT NOT NULL,"
                + GiftsColumns.GIFT_VISIBILITY + " INTEGER NOT NULL DEFAULT '0',"
                + GiftsColumns.GIFT_IS_FAVORITE + " INTEGER NOT NULL DEFAULT '0',"
                + GiftsColumns.GIFT_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
                + ViewType.VIEW_TYPE_COLUMN + " INTEGER NOT NULL,"
                + "UNIQUE (" + GiftsColumns.GIFT_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.COMMENTS+ " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CommentsColumns.COMMENT_ID + " TEXT NOT NULL,"
                + CommentsColumns.GIFT_ID + " TEXT NOT NULL,"
                + CommentsColumns.COMMENT_CONTENT + " TEXT NOT NULL,"
                + CommentsColumns.COMMENT_CREATOR + " TEXT NOT NULL,"
                + CommentsColumns.COMMENT_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
                + ViewType.VIEW_TYPE_COLUMN + " INTEGER NOT NULL,"
                + "UNIQUE (" + CommentsColumns.COMMENT_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CategoriesColumns.CATEGORY_ID + " TEXT NOT NULL,"
                + CategoriesColumns.CATEGORY_NAME + " TEXT NOT NULL,"
                + "UNIQUE (" + CategoriesColumns.CATEGORY_ID+ ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.USERS+ " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsersColumns.USER_NAME + " TEXT NOT NULL,"
                + UsersColumns.USER_ACCOUNT_IMAGE + " TEXT,"
                + UsersColumns.USER_COVER_IMAGE + " TEXT,"
                + UsersColumns.USER_STATUS + " TEXT,"
                + UsersColumns.USER_GENDER + " TEXT,"
                + "UNIQUE (" + UsersColumns.USER_NAME + ") ON CONFLICT REPLACE)");

        // Full-text search index. Update using updateGiftSearchIndex method.
        // Use the porter tokenizer for simple stemming, so that "frustration" matches "frustrated."
        db.execSQL("CREATE VIRTUAL TABLE " + Tables.GIFTS_SEARCH + " USING fts3("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GiftsSearchColumns.BODY + " TEXT NOT NULL,"
                + GiftsSearchColumns.GIFT_ID
                + " TEXT NOT NULL " + References.GIFT_ID + ","
                + "UNIQUE (" + GiftsSearchColumns.GIFT_ID + ") ON CONFLICT REPLACE,"
                + "tokenize=porter)");

    }

    /**
     * Updates the gift search index. This should be done sparingly, as the queries are rather
     * complex.
     */
    static void updateGiftSearchIndex(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + Tables.GIFTS_SEARCH);

        db.execSQL("INSERT INTO " + Qualified.GIFTS_SEARCH
                + " SELECT s." + Gifts.GIFT_ID + ",("

                // Full text body
                + Gifts.GIFT_TITLE + "||'; '||"
                + Gifts.GIFT_DESCRIPTION + "||'; '||"
                + Gifts.GIFT_CATEGORY_ID + "||'; '||"
                + "'')"

                + " FROM " + Tables.GIFTS + " s ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
