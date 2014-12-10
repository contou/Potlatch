package com.cong.potlatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.cong.potlatch.Config;
import com.cong.potlatch.data.UserHandler;
import com.cong.potlatch.util.UIUtils;
import com.cong.potlatch.volley.FetchDataRequest;
import com.cong.potlatch.volley.RequestManager;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

public class BrowseUsersActivity extends BaseActivity {


    private static final String TAG = makeLogTag(BrowseUsersActivity.class);

    private BrowseUsersFragment mUsersFrag = null;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_USERS;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUsersFrag = (BrowseUsersFragment) getFragmentManager().findFragmentById(
                R.id.gift_fragment);

        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        mUsersFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this) + gridPadding);

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
        if (mUsersFrag != null) {
            return mUsersFrag.canListViewScrollUp();
        }
        return super.canSwipeRefreshChildScrollUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(BrowseUsersActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void requestDataRefresh() {
        onRefreshingStateChanged(true);
        StopRefreshListener listener = new StopRefreshListener();
        String uri = Config.BASE_URL + "/users/topUser";
        FetchDataRequest fetchDataRequest = new FetchDataRequest(this,new UserHandler(this), uri, listener, listener);
        RequestManager.addRequest(fetchDataRequest,this);
    }

    @Override
    protected void onActionBarAutoShowOrHide(boolean shown) {
        super.onActionBarAutoShowOrHide(shown);
        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
        if(shown) {
            mUsersFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this) + gridPadding);
        }else {
            mUsersFrag.setContentTopClearance(0);
        }
    }

}
