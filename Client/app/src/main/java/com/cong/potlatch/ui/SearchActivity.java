package com.cong.potlatch.ui;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cong.potlatch.data.GiftsHandler;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.util.PrefUtils;
import com.cong.potlatch.volley.FetchDataRequest;
import com.cong.potlatch.volley.RequestManager;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.LOGW;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

public class SearchActivity extends BaseActivity {
    private static final String TAG = makeLogTag("SearchActivity");

    private BrowseGiftsFragment mGiftsFragment = null;

    private android.support.v7.widget.SearchView mSearchView = null;
    private String mQuery = "";

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar mActionBarToolbar= getActionBarToolbar();
        mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_up);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO navigateUpToFromChild
                finish();
            }
        });
        FragmentManager fm = getFragmentManager();
        mGiftsFragment = (BrowseGiftsFragment) fm.findFragmentById(R.id.fragment_container);

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQuery = query;

        if (mGiftsFragment == null) {
            mGiftsFragment = new BrowseGiftsFragment();
            Bundle args = intentToFragmentArguments(
                    new Intent(Intent.ACTION_VIEW, GiftContract.Gifts.buildSearchUri(query)));
            mGiftsFragment.setArguments(args);
            fm.beginTransaction().add(R.id.fragment_container, mGiftsFragment).commit();
        }
        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LOGD(TAG, "SearchActivity.onNewIntent: " + intent);
        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
        Bundle args = intentToFragmentArguments(
                new Intent(Intent.ACTION_VIEW, GiftContract.Gifts.buildSearchUri(query)));
        LOGD(TAG, "onNewIntent() now reloading sessions fragment with args: " + args);
        mGiftsFragment.reloadFromArguments(args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final android.support.v7.widget.SearchView view = (android.support.v7.widget.SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {
                LOGW(TAG, "Could not set up search view, view is null.");
            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        view.clearFocus();
                        Context context = SearchActivity.this;
                        if(PrefUtils.shouldSearchOnline(context)) {
                            StopRefreshListener listener = new StopRefreshListener();
                            String uri = GiftContract.RemoteGifts.buildSearchTitleUri(s);

                            LOGD(TAG,"Refresh Gift!" + uri);
                            FetchDataRequest fetchDataRequest = new FetchDataRequest(context,new GiftsHandler(context), uri, listener, listener);
                            LOGD(TAG,"Refresh Gift!");
                            RequestManager.addRequest(fetchDataRequest, this);
                            if (null != mGiftsFragment) {
                                mGiftsFragment.requestQueryUpdate(s);

                            }else {
                                LOGD(TAG,"Fragment is null");
                            }
                            return true;
                        }
                        LOGD(TAG, "request query update" + s);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (null != mGiftsFragment) {
                            mGiftsFragment.requestQueryUpdate(s);

                        }else {
                            LOGD(TAG,"Fragment is null");
                        }
                        return true;
                    }
                });
                view.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }

            if (!TextUtils.isEmpty(mQuery)) {
                view.setQuery(mQuery, false);
            }
        }else {
            LOGD(TAG, "search view is null");
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
