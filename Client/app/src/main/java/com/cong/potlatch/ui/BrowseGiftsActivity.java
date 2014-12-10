package com.cong.potlatch.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.cong.potlatch.Config;
import com.cong.potlatch.data.DataHandler;
import com.cong.potlatch.data.GiftsHandler;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.ui.Aadapter.CategorySpinnerAdapter;
import com.cong.potlatch.util.CameraUtil;
import com.cong.potlatch.util.LogUtils;
import com.cong.potlatch.util.PrefUtils;
import com.cong.potlatch.util.TimeUtils;
import com.cong.potlatch.util.UIUtils;
import com.cong.potlatch.volley.FetchDataRequest;
import com.cong.potlatch.volley.RequestManager;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

public class BrowseGiftsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final String TAG = makeLogTag(BrowseGiftsActivity.class);

    private CategorySpinnerAdapter mCategorySpinnerAdapter = new CategorySpinnerAdapter(BrowseGiftsActivity.this,true);
    private BrowseGiftsFragment mGiftsFrag = null;
    private View mAddGiftFAB;

    private Cursor mCursor;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_GIFTS;
    }

    @Override
    public void onStart() {
        super.onStart();

        long lastSyncSuccessTime = PrefUtils.getLastSyncSucceededTime(this,GiftsHandler.class.toString());
        long currentTime = UIUtils.getCurrentTime(this);
        long dt = currentTime - lastSyncSuccessTime;
        long syncInterval = PrefUtils.getCurSyncInterval(this);
        if(dt > syncInterval) {
            requestDataRefresh();
        }else {
//            LOGD(TAG, "Dt: " + String.valueOf(dt));
//            LOGD(TAG, "LastSyncSuccessTime " + lastSyncSuccessTime);
//            LOGD(TAG, "SyncInterval: " + String.valueOf(syncInterval));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_list);

        mGiftsFrag = (BrowseGiftsFragment) getFragmentManager().findFragmentById(
                R.id.gift_fragment);
        mAddGiftFAB = findViewById(R.id.footerbar);
        mAddGiftFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.takePicture(BrowseGiftsActivity.this);
            }
        });

        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        mGiftsFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this)+gridPadding);

        setNormalStatusBarColor(getResources().getColor(R.color.theme_primary));

        getLoaderManager().initLoader(CategoryQuery.TOKEN,null,this);

        overridePendingTransition(0, 0);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gifts);
        if (recyclerView != null) {
            enableActionBarAutoHide(recyclerView);
        }

        if(mGiftsFrag != null && savedInstanceState == null) {
            Bundle args = intentToFragmentArguments(getIntent());
            mGiftsFrag.reloadFromArguments(args);
        }

        registerHideableHeaderView(findViewById(R.id.headerbar));
        registerHideableFooterView(findViewById(R.id.footerbar));
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        if (mGiftsFrag != null) {
            return mGiftsFrag.canListViewScrollUp();
        }
        return super.canSwipeRefreshChildScrollUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(BrowseGiftsActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void trySetUpActionbarSpinner(){
        Toolbar toolbar = getActionBarToolbar();

        mCategorySpinnerAdapter.clear();
        mCategorySpinnerAdapter.addItem("", getString(R.string.title_browse_gift), false, 0);
        if(mCursor == null) {
            return;
        }

        mCategorySpinnerAdapter.addHeader("Category");
        mCategorySpinnerAdapter.addItem("default_category", getString(R.string.default_category), false, 0);
        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            String categoryId = mCursor.getString(CategoryQuery.CATEGORY_ID);
            String categoryName = mCursor.getString(CategoryQuery.CATEGORY_NAME);
            mCategorySpinnerAdapter.addItem(categoryId, categoryName, false, 0);
            mCursor.moveToNext();
        }

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.actionbar_spinner,
                toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);


        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);
        spinner.setAdapter(mCategorySpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                if(mCategorySpinnerAdapter.getTag(position) != "") {
                    reloadFromFilter(mCategorySpinnerAdapter.getTag(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void reloadFromFilter(String categoryId){
        Bundle bundle = new Bundle();
        bundle.putParcelable("_uri", GiftContract.Gifts.buildCategoryUri(categoryId));
        mGiftsFrag.reloadFromArguments(bundle);
    }

    @Override
    protected void requestDataRefresh() {
        onRefreshingStateChanged(true);
        StopRefreshListener listener = new StopRefreshListener();
        String uri = GiftContract.RemoteGifts.CONTENT_URI.toString();
        FetchDataRequest fetchDataRequest = new FetchDataRequest(this,new GiftsHandler(this), uri, listener, listener);
        LOGD(TAG, "Refresh Gift!");
        RequestManager.addRequest(fetchDataRequest, this);
    }

    @Override
    protected void onActionBarAutoShowOrHide(boolean shown) {
        super.onActionBarAutoShowOrHide(shown);
        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        if(shown) {
            mGiftsFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this)+gridPadding);
        }else {
            mGiftsFrag.setContentTopClearance(0);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = GiftContract.Categories.CONTENT_URI;

        if (id == CategoryQuery.TOKEN) {
            return new CursorLoader(this, uri,
                    CategoryQuery.PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CategoryQuery.TOKEN) {
            mCursor = data;
        }
        trySetUpActionbarSpinner();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CategoryQuery.TOKEN) {
            mCursor = null;
        }
        trySetUpActionbarSpinner();
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
