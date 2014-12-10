package com.cong.potlatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.cong.potlatch.Config;
import com.cong.potlatch.data.CategoryHandler;
import com.cong.potlatch.util.UIUtils;
import com.cong.potlatch.volley.FetchDataRequest;
import com.cong.potlatch.volley.RequestManager;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

public class BrowseCategoriesActivity extends BaseActivity {


    private static final String TAG = makeLogTag(BrowseCategoriesActivity.class);

    private BrowseCategoriesFragment mCategoriesFrag = null;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_CATEGORIES;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mCategoriesFrag = (BrowseCategoriesFragment) getFragmentManager().findFragmentById(
                R.id.gift_fragment);

        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        mCategoriesFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this) + gridPadding);

        setNormalStatusBarColor(getResources().getColor(R.color.theme_primary));

        overridePendingTransition(0, 0);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gifts);
        if (recyclerView != null) {
            enableActionBarAutoHide(recyclerView);
        }

        registerHideableHeaderView(findViewById(R.id.headerbar));
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        if (mCategoriesFrag != null) {
            return mCategoriesFrag.canListViewScrollUp();
        }
        return super.canSwipeRefreshChildScrollUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(BrowseCategoriesActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void requestDataRefresh() {
        onRefreshingStateChanged(true);
        StopRefreshListener listener = new StopRefreshListener();
        String uri = Config.BASE_URL + "/categories";
        LOGD(TAG,"Refresh Category!");
        FetchDataRequest fetchDataRequest = new FetchDataRequest(this,new CategoryHandler(this), uri, listener, listener);
        RequestManager.addRequest(fetchDataRequest,this);
    }

    @Override
    protected void onActionBarAutoShowOrHide(boolean shown) {
        super.onActionBarAutoShowOrHide(shown);
        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        if(shown) {
            mCategoriesFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this) + gridPadding);
        }else {
            mCategoriesFrag.setContentTopClearance(0);
        }
    }

}
