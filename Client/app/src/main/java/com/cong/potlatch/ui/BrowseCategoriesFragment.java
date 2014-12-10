package com.cong.potlatch.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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
import com.cong.potlatch.ui.ViewHolder.CategoryViewHolder;
import com.cong.potlatch.util.ImageLoader;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/22/14.
 */
public class BrowseCategoriesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = makeLogTag(BrowseCategoriesFragment.class);

    private RecyclerView mListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryAdapter mAdapter;
    private int mContentTopClearance = 0;
    private ImageLoader mImageLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.getActivity());
        }
        getLoaderManager().initLoader(CategoryQuery.TOKEN,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        mListView = (RecyclerView) rootView.findViewById(R.id.categories);


        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mListView.setHasFixedSize(true);

        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryAdapter(null);
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

        Uri uri = GiftContract.Categories.CONTENT_URI;

        if (id == CategoryQuery.TOKEN) {
            return new CursorLoader(getActivity(), uri,
                    CategoryQuery.PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CategoryQuery.TOKEN) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CategoryQuery.TOKEN) {
            mAdapter.swapCursor(null);
        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        Cursor mCursor;

        public CategoryAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.list_item_category, viewGroup, false);
            CategoryViewHolder vh = new CategoryViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder vh, int i) {
            mCursor.moveToPosition(i);
            String categoryName = mCursor.getString(CategoryQuery.CATEGORY_NAME);

            vh.categoryName.setText(categoryName);
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

    private interface CategoryQuery {

        int TOKEN = 0x1;

        String[] PROJECTION = {
               BaseColumns._ID,
               GiftContract.Categories.CATEGORY_ID,
               GiftContract.Categories.CATEGORY_NAME,
        };

        int _ID = 0;
        int CATEGORY_ID = 1;
        int CATEGORY_NAME = 2;
    }
}
