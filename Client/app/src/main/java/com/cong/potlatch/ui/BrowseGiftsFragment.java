package com.cong.potlatch.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.cong.potlatch.Config;
import com.cong.potlatch.data.GiftsHandler;
import com.cong.potlatch.data.model.Gift;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.ui.ViewHolder.GiftViewHolder;
import com.cong.potlatch.util.ImageLoader;
import com.cong.potlatch.util.PrefUtils;
import com.cong.potlatch.volley.GsonRequest;
import com.cong.potlatch.volley.RequestManager;
import com.cong.potlatch.volley.ResponseListener.ObjectResponseListener;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/22/14.
 */
public class BrowseGiftsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = makeLogTag(BrowseGiftsFragment.class);

    private static final int MESSAGE_QUERY_UPDATE = 1;
    private static final int QUERY_UPDATE_DELAY_MILLIS = 100;

    private RecyclerView mListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GiftAdapter mAdapter;
    private int mContentTopClearance = 0;
    private ImageLoader mImageLoader;
    private Bundle mArguments;
    private Uri mCurrentUri = GiftContract.Gifts.CONTENT_URI;
    private int mSessionQueryToken;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_QUERY_UPDATE) {
                String query = (String) msg.obj;
                reloadFromArguments(BaseActivity.intentToFragmentArguments(
                        new Intent(Intent.ACTION_SEARCH, GiftContract.Gifts.buildSearchUri(query))));
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gift_list, container, false);
        mListView = (RecyclerView) rootView.findViewById(R.id.gifts);


        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mListView.setHasFixedSize(true);

        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new GiftAdapter(null);
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mListView.setLayoutManager(mLayoutManager);
    }

    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            mListView.setPadding(mListView.getPaddingLeft(), mContentTopClearance,
                    mListView.getPaddingRight(), mListView.getPaddingBottom());
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * fix the touch event conflict with {@link com.cong.potlatch.ui.widget.MultiSwipeRefreshLayout }
     */
    public boolean canListViewScrollUp() {
        return ViewCompat.canScrollVertically(mListView, -1);
    }

    void reloadFromArguments(Bundle arguments) {
        // Load new arguments
        if (arguments == null) {
            arguments = new Bundle();
        } else {
            // since we might make changes, don't meddle with caller's copy
            arguments = (Bundle) arguments.clone();
        }

        // save arguments so we can reuse it when reloading from content observer events
        mArguments = arguments;

        LOGD(TAG, "SessionsFragment reloading from arguments: " + arguments);
        mCurrentUri = arguments.getParcelable("_uri");
        if (mCurrentUri == null) {
            // if no URI, default to all sessions URI
            LOGD(TAG, "SessionsFragment did not get a URL, defaulting to all sessions.");
            arguments.putParcelable("_uri", GiftContract.Gifts.CONTENT_URI);
            mCurrentUri = GiftContract.Gifts.CONTENT_URI;
        }

        if (GiftContract.Gifts.isSearchUri(mCurrentUri)) {
            mSessionQueryToken = GiftQuery.SEARCH_TOKEN;
        } else {
            mSessionQueryToken = GiftQuery.NORMAL_TOKEN;
        }

        LOGD(TAG, "GiftsFragment reloading from arguments: " + arguments);

        reloadSessionData(true);
    }

    private void reloadSessionData(boolean fullReload) {
        getLoaderManager().restartLoader(mSessionQueryToken, mArguments, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final Intent intent = BaseActivity.fragmentArgumentsToIntent(args);
        Uri GiftsUri = intent.getData();
        String selection = null;
        String[] selectionArgs = null;
        if(!PrefUtils.shouldShowInappropriateItem(getActivity())) {
            selection = GiftContract.Gifts.GIFT_VISIBILITY + "=?";
            selectionArgs = new String[]{"0"};
        }

        if (id == GiftQuery.NORMAL_TOKEN) {
            return new CursorLoader(getActivity(), GiftsUri,
                    GiftQuery.PROJECTION, selection, selectionArgs, null);
        } else if (id == GiftQuery.SEARCH_TOKEN) {
            return new CursorLoader(getActivity(), GiftsUri,
                    GiftQuery.PROJECTION, selection, selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == GiftQuery.NORMAL_TOKEN) {
            mAdapter.swapCursor(data);
        } else if (loader.getId() == GiftQuery.SEARCH_TOKEN) {
            LOGD(TAG, String.valueOf(data.getCount()));
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == GiftQuery.NORMAL_TOKEN) {
            mAdapter.swapCursor(null);
        } else if (loader.getId() == GiftQuery.SEARCH_TOKEN) {
            mAdapter.swapCursor(null);
        }
    }

    public void requestQueryUpdate(String query) {
        mHandler.removeMessages(MESSAGE_QUERY_UPDATE);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MESSAGE_QUERY_UPDATE, query),
                QUERY_UPDATE_DELAY_MILLIS);
    }


    public class GiftAdapter extends RecyclerView.Adapter<GiftViewHolder> {

        Cursor mCursor;

        public GiftAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public GiftViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.list_item_gift, viewGroup, false);
            GiftViewHolder vh = new GiftViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(GiftViewHolder vh, int i) {
            mCursor.moveToPosition(i);
            final String giftId = mCursor.getString(GiftQuery.GIFT_ID);
            int giftPoints = mCursor.getInt(GiftQuery.GIFT_POINTS);
            String giftTitle = mCursor.getString(GiftQuery.GIFT_TITLE);
            final String giftImage = mCursor.getString(GiftQuery.GIFT_IMAGE);
            final String giftDescription = mCursor.getString(GiftQuery.GIFT_DESCRIPTION);
            final int giftIsFavorite = mCursor.getInt(GiftQuery.GIFT_IS_FAVORITE);
            final boolean isFavorite = giftIsFavorite == 1;

            vh.title.setText(giftTitle);
            vh.description.setText(giftDescription);


            vh.commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowseCommentActivity.startActivity(getActivity(), giftId);
                }
            });

            vh.pointsBtn.setSelected(isFavorite);
            vh.pointsBtn.setText(String.valueOf(giftPoints));
            vh.pointsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ToastResponseListener toastResponseListener = new ToastResponseListener(getActivity());
                    GiftsHandler giftsHandler = new GiftsHandler(getActivity());
                    giftsHandler.setFavorite(isFavorite);
                    ObjectResponseListener<Gift> listener = new ObjectResponseListener<Gift>(getActivity(), giftsHandler, false);
                    String uri = GiftContract.RemoteGifts.buildLikeGiftUri(giftId).toString();
                    GsonRequest<Gift> stringRequest = new GsonRequest<Gift>(getActivity(), Request.Method.POST, uri, Gift.class, null,
                            listener,
                            listener);
                    RequestManager.addRequest(stringRequest, this);
                }
            });

            mImageLoader.loadImage(Config.BASE_URL + giftImage, vh.image, true);
        }

        public void swapCursor(Cursor mCursor) {
            this.mCursor = mCursor;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            } else {
                return mCursor.getCount();
            }
        }


    }

    private interface GiftQuery {

        int NORMAL_TOKEN = 0x1;
        int SEARCH_TOKEN = 0x3;

        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Gifts.GIFT_ID,
                GiftContract.Gifts.GIFT_POINTS,
                GiftContract.Gifts.GIFT_TITLE,
                GiftContract.Gifts.GIFT_IMAGE,
                GiftContract.Gifts.GIFT_DESCRIPTION,
                GiftContract.Gifts.GIFT_IS_FAVORITE,
        };

        int _ID = 0;
        int GIFT_ID = 1;
        int GIFT_POINTS = 2;
        int GIFT_TITLE = 3;
        int GIFT_IMAGE = 4;
        int GIFT_DESCRIPTION = 5;
        int GIFT_IS_FAVORITE = 6;
    }
}
