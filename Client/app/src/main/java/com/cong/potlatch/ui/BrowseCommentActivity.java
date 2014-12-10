package com.cong.potlatch.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.cong.potlatch.data.CommentHandler;
import com.cong.potlatch.data.model.Comment;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.util.UIUtils;
import com.cong.potlatch.volley.FetchDataRequest;
import com.cong.potlatch.volley.GsonRequestBuilder;
import com.cong.potlatch.volley.RequestManager;
import com.cong.potlatch.volley.ResponseListener.ObjectResponseListener;
import com.google.gson.Gson;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/11/14.
 */
public class BrowseCommentActivity extends BaseActivity{

    private static final String TAG = makeLogTag(BrowseCommentActivity.class);

    private float HEADER_HEIGHT;
    private BrowseCommentFragment mCommentsFrag;
    private RecyclerView mListView;
    private Toolbar mActionBarToolbar;


    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        HEADER_HEIGHT = getResources().getDimension(R.dimen.gift_image_height);

        mActionBarToolbar = getActionBarToolbar();
//        mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        setNormalStatusBarColor(getResources().getColor(R.color.theme_primary));
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_up);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCommentsFrag = BrowseCommentFragment.newInstance(intentToFragmentArguments(getIntent()));
        getFragmentManager().beginTransaction()
                .add(R.id.comment_fragment,mCommentsFrag)
                .commit();


        registerHideableHeaderView(mActionBarToolbar);


        overridePendingTransition(0, 0);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mListView = (RecyclerView) findViewById(R.id.comments);
        enableActionBarAutoHide(mListView);
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        if (mCommentsFrag != null) {
            return mCommentsFrag.canListViewScrollUp();
        }
        return super.canSwipeRefreshChildScrollUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_mute) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GiftContract.Gifts.GIFT_VISIBILITY,1);
            Uri url = GiftContract.Gifts.buildUri(getIntent().getStringExtra(GiftContract.Gifts.GIFT_ID));
            getContentResolver().update(url,contentValues,null,null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void requestDataRefresh() {
        Comment comment = mCommentsFrag.getComment();
        onRefreshingStateChanged(true);
        StopRefreshListener listener = new StopRefreshListener();
        String uri = GiftContract.RemoteGifts.buildCommentsUri(comment.giftId).toString();
        FetchDataRequest fetchDataRequest = new FetchDataRequest(this,new CommentHandler(this),uri,listener,listener);
        RequestManager.addRequest(fetchDataRequest,this);
    }

    public void sendCommentClicked(View view) {
        sendComment();
        //TODO: don't refresh here.
//        requestDataRefresh();
    }

    public void sendComment() {
//        ToastResponseListener toastResponseListener = new ToastResponseListener(this);
        ObjectResponseListener<Comment> listener = new ObjectResponseListener<Comment>(this,new CommentHandler(this),true);
        Comment comment = mCommentsFrag.getComment();

        String url = GiftContract.RemoteGifts.buildCommentsUri(comment.giftId).toString();
        GsonRequestBuilder<Comment> gsonRequestBuilder = new GsonRequestBuilder<Comment>();
        gsonRequestBuilder
                .setContext(this)
                .setMethod(Request.Method.POST)
                .setUrl(url)
                .setResponseType(Comment.class)
                .setRequestBody(new Gson().toJson(comment))
                .setListener(listener)
                .setErrorListener(listener);


        RequestManager.addRequest(gsonRequestBuilder.build(), this);
    }
//    @Override
//    protected void onActionBarAutoShowOrHide(boolean shown) {
//        super.onActionBarAutoShowOrHide(shown);
//        int gridPadding = getResources().getDimensionPixelSize(R.dimen.grid_padding);
//        if(shown) {
//            mCommentsFrag.setContentTopClearance(UIUtils.calculateActionBarSize(this)+gridPadding);
//        }else {
//            mCommentsFrag.setContentTopClearance(0);
//        }
//        super.onActionBarAutoShowOrHide(shown);
//    }


    @Override
    protected void onMainContentScrolled(int currentY, int deltaY) {
        super.onMainContentScrolled(currentY, deltaY);
        if(currentY < HEADER_HEIGHT) {
            int color = getResources().getColor(R.color.theme_primary);
            mActionBarToolbar.setBackgroundColor(UIUtils.setColorAlpha(color, currentY / HEADER_HEIGHT));
        }
    }

    public static void startActivity(Context context,String giftId) {
        Intent i = new Intent(context,BrowseCommentActivity.class);
        i.putExtra(GiftContract.Gifts.GIFT_ID, giftId);
        context.startActivity(i);
    }

}
