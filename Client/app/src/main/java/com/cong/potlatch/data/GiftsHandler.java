package com.cong.potlatch.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.cong.potlatch.data.model.Gift;
import com.cong.potlatch.provider.GiftContract;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/21/14.
 */
public class GiftsHandler extends JSONHandler<Gift>{

    private static final String TAG = makeLogTag(GiftsHandler.class);
    private HashMap<String,Gift> mGifts = new HashMap<String, Gift>();
    private Uri targetGiftsUri = GiftContract.addCallerIsSyncAdapterParameter(
            GiftContract.Gifts.CONTENT_URI);

    private boolean isFavorite = true;

    public GiftsHandler(Context context) {
        super(context);
    }
    //Make sure the uri only contain gift data.
    public GiftsHandler(Context context,Uri uri) {
        super(context);
        targetGiftsUri = uri;
    }

    @Override
    public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        HashMap<String, String> giftsHashcodes = loadGiftHashcodes();
        HashSet<String> giftsToKeep = new HashSet<String>();
        boolean isIncrementalUpdate = giftsHashcodes != null && giftsHashcodes.size() > 0;

        if (isIncrementalUpdate) {
            LOGD(TAG, "Doing incremental update for gifts.");
        } else {
            LOGD(TAG, "Doing FULL (non incremental) update for gifts.");
            list.add(ContentProviderOperation.newDelete(targetGiftsUri).build());
        }
        int updatedGifts = 0;
        for(Gift gift : mGifts.values()) {
            String hashCode = gift.getImportHashCode();
            giftsToKeep.add(gift.id);

            // Add the expert, if necessary
            if (!isIncrementalUpdate || !giftsHashcodes.containsKey(gift.id) ||
                    !giftsHashcodes.get(gift.id).equals(hashCode)) {
                ++updatedGifts;
                boolean isNew = !isIncrementalUpdate || !giftsHashcodes.containsKey(gift.id);
                build(isNew, gift, list);
            }
        }

        int deletedGifts = 0;
        if (isIncrementalUpdate) {
            for (String giftId : giftsHashcodes.keySet()) {
                if (!giftsToKeep.contains(giftId)) {
                    buildDeleteOperation(giftId, list);
                    ++deletedGifts;
                }
            }
        }
        LOGD(TAG, "Gifts: " + (isIncrementalUpdate ? "INCREMENTAL" : "FULL") + " update. " +
                updatedGifts + " to update, " + deletedGifts + " to delete. New total: " +
                mGifts.size());
    }

    private void buildDeleteOperation(String giftId, ArrayList<ContentProviderOperation> list) {
        Uri giftUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Gifts.buildUri(giftId));
        list.add(ContentProviderOperation.newDelete(giftUri).build());
    }


    public void build(boolean isNew, Gift gift, ArrayList<ContentProviderOperation> list) {
        Uri thisGiftUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Gifts.buildUri(gift.id));

        ContentProviderOperation.Builder builder;
        if(isNew) {
            builder = ContentProviderOperation.newInsert(targetGiftsUri);
        } else {
            builder = ContentProviderOperation.newUpdate(thisGiftUri);
            if(isFavorite) {
                builder.withValue(GiftContract.Gifts.GIFT_IS_FAVORITE,"0");
            }else {
                builder.withValue(GiftContract.Gifts.GIFT_IS_FAVORITE,"1");
            }
        }

        builder.withValue(GiftContract.Gifts.VIEW_TYPE_COLUMN,GiftContract.Gifts.VIEW_TYPE);
        builder.withValue(GiftContract.Gifts.GIFT_ID, gift.id);
        builder.withValue(GiftContract.Gifts.GIFT_DESCRIPTION, gift.description);
        builder.withValue(GiftContract.Gifts.GIFT_IMAGE, gift.image);
        builder.withValue(GiftContract.Gifts.GIFT_POINTS, gift.points);
        builder.withValue(GiftContract.Gifts.GIFT_TITLE, gift.title);
        builder.withValue(GiftContract.Gifts.GIFT_CATEGORY_ID, gift.category);
        builder.withValue(GiftContract.Gifts.GIFT_CREATOR_ID, gift.creatorId);
        builder.withValue(GiftContract.Gifts.GIFT_IMPORT_HASHCODE, gift.getImportHashCode());


        list.add(builder.build());
    }

    @Override
    public void process(JsonElement element) {
        for (Gift gift : new Gson().fromJson(element, Gift[].class)) {
            mGifts.put(gift.id,gift);
        }
    }

    private HashMap<String, String> loadGiftHashcodes() {
        Cursor cursor = mContext.getContentResolver().query(targetGiftsUri, GiftHashcodeQuery.PROJECTION,
                null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() < 1) {
            return null;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        while (cursor.moveToNext()) {
            String giftId = cursor.getString(GiftHashcodeQuery.GIFT_ID);
            String hashcode = cursor.getString(GiftHashcodeQuery.GIFT_IMPORT_HASHCODE);
            result.put(giftId, hashcode == null ? "" : hashcode);
        }
        cursor.close();
        return result;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    private interface GiftHashcodeQuery {
        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Gifts.GIFT_ID,
                GiftContract.Gifts.GIFT_IMPORT_HASHCODE
        };
        final int _ID = 0;
        final int GIFT_ID = 1;
        final int GIFT_IMPORT_HASHCODE = 2;
    }
}
