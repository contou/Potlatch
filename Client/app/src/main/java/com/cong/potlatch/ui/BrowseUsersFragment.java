package com.cong.potlatch.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.ui.ViewHolder.UserViewHolder;
import com.cong.potlatch.util.ImageLoader;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/22/14.
 */
public class BrowseUsersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = makeLogTag(BrowseUsersFragment.class);

    private RecyclerView mListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserAdapter mAdapter;
    private int mContentTopClearance = 0;
    private ImageLoader mImageLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.getActivity());
        }
        getLoaderManager().initLoader(UserQuery.TOKEN,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);
        mListView = (RecyclerView) rootView.findViewById(R.id.users);


        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mListView.setHasFixedSize(true);

        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new UserAdapter(null);
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = GiftContract.Users.CONTENT_URI;

        if (id == UserQuery.TOKEN) {
            return new CursorLoader(getActivity(), uri,
                    UserQuery.PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == UserQuery.TOKEN) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == UserQuery.TOKEN) {
            mAdapter.swapCursor(null);
        }
    }

    public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

        private final int[] RANKING_ICON_RES_ID = new int[]{
                R.drawable.ic_rank_1,
                R.drawable.ic_rank_2,
                R.drawable.ic_rank_3,
                R.drawable.ic_rank_4,
                R.drawable.ic_rank_5,
                R.drawable.ic_rank_6,
                R.drawable.ic_rank_7,
                R.drawable.ic_rank_8,
                R.drawable.ic_rank_9,
                R.drawable.ic_rank_10,
        };


        Cursor mCursor;

        public UserAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.list_item_user, viewGroup, false);
            UserViewHolder vh = new UserViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(UserViewHolder vh, int i) {
            mCursor.moveToPosition(i);
            String userName = mCursor.getString(UserQuery.USER_NAME);
            String accountImage = mCursor.getString(UserQuery.USER_ACCOUNT_IMAGE);
            String status = mCursor.getString(UserQuery.USER_STATUS);
            String gender = mCursor.getString(UserQuery.USER_GENDER);

            vh.userName.setText(userName);
            Drawable drawable = getResources().getDrawable(RANKING_ICON_RES_ID[i]);
            vh.rank.setImageDrawable(drawable);
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

    private interface UserQuery {

        int TOKEN = 0x1;

        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Users.USER_NAME,
                GiftContract.Users.USER_ACCOUNT_IMAGE,
                GiftContract.Users.USER_COVER_IMAGE,
                GiftContract.Users.USER_STATUS,
                GiftContract.Users.USER_GENDER,
        };

        int _ID = 0;
        int USER_NAME = 1;
        int USER_ACCOUNT_IMAGE = 2;
        int USER_COVER_IMAGE = 3;
        int USER_STATUS = 4;
        int USER_GENDER = 5;
    }
}
