package com.cong.potlatch.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.cong.potlatch.Config;

import java.util.List;

/**
 * Created by cong on 10/21/14.
 */
public class GiftContract {
    /**
     * Query parameter to create a distinct query.
     */
    public static final String QUERY_PARAMETER_DISTINCT = "distinct";
    public static final String OVERRIDE_ACCOUNTNAME_PARAMETER = "overrideAccount";


    interface ViewType{
        String VIEW_TYPE_COLUMN = "VIEW_TYPE";
    }

    interface GiftsColumns {
        String GIFT_ID = "gift_id";
        String GIFT_POINTS = "gift_points";
        String GIFT_IMAGE = "gift_image";
        String GIFT_TITLE = "gift_title";
        String GIFT_DESCRIPTION = "gift_description";
        String GIFT_CATEGORY_ID = "gift_category";
        String GIFT_CREATOR_ID = "gift_creator_id";
        String GIFT_VISIBILITY = "gift_visibility";
        String GIFT_IS_FAVORITE = "gift_is_favorite";
        String GIFT_IMPORT_HASHCODE = "gift_import_hashcode";
    }

    interface CommentsColumns {
        String COMMENT_ID = "comment_id";
        String GIFT_ID = "gift_id";
        String COMMENT_CONTENT = "comment_content";
        String COMMENT_CREATOR = "comment_creator";
        String COMMENT_IMPORT_HASHCODE = "comment_import_hashcode";
    }
    interface CategoriesColumns {
        String CATEGORY_ID = "category_id";
        String CATEGORY_NAME = "category_name";
    }

    interface UsersColumns {
        String USER_NAME = "user_name";
        String USER_ACCOUNT_IMAGE = "user_account_image";
        String USER_COVER_IMAGE = "user_cover_image";
        String USER_STATUS = "user_status";
        String USER_GENDER = "user_gender";
    }

    public static final String CONTENT_AUTHORITY = "com.cong.potlatch";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri REMOTE_CONTENT_URI = Uri.parse(Config.BASE_URL);


    private static final String PATH_GIFTS = "gifts";
    private static final String PATH_COMMENTS = "comments";
    private static final String PATH_SEARCH = "search";
    private static final String PATH_SEARCH_INDEX = "search_index";
    private static final String PATH_CATEGORY = "categories";
    private static final String PATH_USER = "users";

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_GIFTS,
            PATH_COMMENTS,
            PATH_SEARCH,
            PATH_CATEGORY,
            PATH_USER
    };

    public static class Gifts implements GiftsColumns, BaseColumns,ViewType {
        public static final int VIEW_TYPE = 100;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GIFTS).build();



        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.potlatch.gift";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.potlatch.gift";


        /** Build {@link Uri} for requested {@link #GIFT_ID}. */
        public static Uri buildUri(String giftId) {
            return CONTENT_URI.buildUpon().appendPath(giftId).build();
        }
        /** Read {@link #GIFT_ID} from {@link GiftContract.Gifts} {@link Uri}. */
        public static String getGiftId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        /**
         * Build {@link Uri} that references sessions that match the query. The query can be
         * multiple words separated with spaces.
         *
         * @param query The query. Can be multiple words separated by spaces.
         * @return {@link Uri} to the sessions
         */
        public static Uri buildSearchUri(String query) {
            if (null == query) {
                query = "";
            }
            // convert "lorem ipsum dolor sit" to "lorem* ipsum* dolor* sit*"
            query = query.replaceAll(" +", " *") + "*";
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SEARCH).appendPath(query).build();
        }

        public static boolean isSearchUri(Uri uri) {
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
        }

        public static String getSearchQuery(Uri uri) {
            List<String> segments = uri.getPathSegments();
            if (2 < segments.size()) {
                return segments.get(2);
            }
            return null;
        }
        public static Uri buildCategoryUri(String categoryId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_CATEGORY).appendPath(categoryId).build();
        }

        public static String getCategoryId(Uri uri) {
            List<String> segments = uri.getPathSegments();
            if (2 < segments.size()) {
                return segments.get(2);
            }
            return null;
        }


        /** Build {@link Uri} for requested {@link #GIFT_ID}. */
        public static Uri buildCommentsDir(String giftId) {
            return CONTENT_URI.buildUpon().appendPath(giftId).appendPath(PATH_COMMENTS).build();
        }

    }
    public static class RemoteGifts {

        public static final Uri CONTENT_URI =
                REMOTE_CONTENT_URI.buildUpon().appendPath(PATH_GIFTS).build();

        public static Uri buildUri(String giftId) {
            return CONTENT_URI.buildUpon().appendPath(giftId).build();
        }

        public static Uri buildLikeGiftUri(String giftId) {
            return CONTENT_URI.buildUpon().appendPath(giftId).appendPath("like").build();
        }
        public static String getGiftId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildCommentsUri(String giftId) {
            return CONTENT_URI.buildUpon().appendPath(giftId).appendPath(PATH_COMMENTS).build();
        }

        public static String buildSearchTitleUri(String s) {
            String result = Config.BASE_URL + "/gifts/search/findByName/" + s;
            return result;
        }
    }
    public static class Comments implements CommentsColumns ,ViewType {
        public static final int VIEW_TYPE = 200;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.potlatch.comment";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.potlatch.comment";

        public static Uri buildUri(String commentId) {
            return CONTENT_URI.buildUpon().appendPath(commentId).build();
        }
        public static String getCommentId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class RemoteComments {

        public static final Uri CONTENT_URI =
                REMOTE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.potlatch.comment";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.potlatch.comment";

        public static Uri buildUri(String commentId) {
            return CONTENT_URI.buildUpon().appendPath(commentId).build();
        }
        public static String getCommentId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
    public static class Categories implements CategoriesColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.potlatch.category";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.potlatch.category";

        public static Uri buildUri(String categoryId){
            return CONTENT_URI.buildUpon().appendPath(categoryId).build();
        }
        public static String getCategoryId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Users implements UsersColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.potlatch.user";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.potlatch.user";

        public static Uri buildUri(String UserName){
            return CONTENT_URI.buildUpon().appendPath(UserName).build();
        }
        public static String getUserName(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class SearchIndex {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_INDEX).build();
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
    }


}
