package com.cong.potlatch.data;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.cong.potlatch.provider.GiftContract;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.LOGE;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/23/14.
 */
public class DataHandler {
    private static final String TAG = makeLogTag(DataHandler.class);

    // Shared preferences key under which we store the timestamp that corresponds to
    // the data we currently have in our content provider.
    private static final String SP_KEY_DATA_TIMESTAMP = "data_timestamp";

    // symbolic timestamp to use when we are missing timestamp data (which means our data is
    // really old or nonexistent)
    private static final String DEFAULT_TIMESTAMP = "Sat, 1 Jan 2000 00:00:00 GMT";


    Context mContext = null;

    JSONHandler mDataHandler = null;

    // Tally of total content provider operations we carried out (for statistical purposes)
    private int mContentProviderOperationsDone = 0;

    public DataHandler(Context mContext,JSONHandler mDataHandler) {
        this.mContext = mContext;
        this.mDataHandler = mDataHandler;
    }

    public void applyData(String dataBody, String dataTimestamp,
                          boolean downloadsAllowed) throws IOException {


        processDataBody(dataBody);

        // produce the necessary content provider operations
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        mDataHandler.makeContentProviderOperations(batch);
        applyBatch(batch);


        // update our data timestamp
        setDataTimestamp(dataTimestamp);
        LOGD(TAG, "Done applying data.");

    }

    public void applyBatch(ArrayList<ContentProviderOperation> batch) {
        // finally, push the changes into the Content Provider
        LOGD(TAG, "Applying " + batch.size() + " content provider operations.");
        try {
            int operations = batch.size();
            if (operations > 0) {
                mContext.getContentResolver().applyBatch(GiftContract.CONTENT_AUTHORITY, batch);
            }
            LOGD(TAG, "Successfully applied " + operations + " content provider operations.");
            mContentProviderOperationsDone += operations;
        } catch (RemoteException ex) {
            LOGE(TAG, "RemoteException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        } catch (OperationApplicationException ex) {
            LOGE(TAG, "OperationApplicationException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        }

        // notify all top-level paths
        LOGD(TAG, "Notifying changes on all top-level paths on Content Resolver.");
        performPostSyncChores(mContext);
        ContentResolver resolver = mContext.getContentResolver();
        for (String path : GiftContract.TOP_LEVEL_PATHS) {
            Uri uri = GiftContract.BASE_CONTENT_URI.buildUpon().appendPath(path).build();
            resolver.notifyChange(uri, null);
        }
    }

    public int getContentProviderOperationsDone() {
        return mContentProviderOperationsDone;
    }

    /**
     * Processes a conference data body and calls the appropriate data type handlers
     * to process each of the objects represented therein.
     *
     * @param dataBody The body of data to process
     * @throws java.io.IOException If there is an error parsing the data.
     */
    private void processDataBody(String dataBody) throws IOException {
        JsonParser parser = new JsonParser();

        mDataHandler.process(parser.parse(dataBody));

    }

    // Returns the timestamp of the data we have in the content provider.
    public static String getDataTimestamp(Context context,String handlerClass) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                handlerClass, DEFAULT_TIMESTAMP);
    }

    // Sets the timestamp of the data we have in the content provider.
    public void setDataTimestamp(String timestamp) {
        LOGD(TAG, "Setting data timestamp to: " + timestamp);
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(
                mDataHandler.getClass().toString(), timestamp).commit();
    }


    public static void performPostSyncChores(final Context context) {
        // Update search index
        LOGD(TAG, "Updating search index.");
        context.getContentResolver().update(GiftContract.SearchIndex.CONTENT_URI,
                new ContentValues(), null, null);
    }

}
