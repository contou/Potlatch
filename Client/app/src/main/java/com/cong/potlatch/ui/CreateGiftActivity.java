package com.cong.potlatch.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cong.potlatch.data.model.Gift;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.volley.RequestManager;
import com.cong.potlatch.volley.ResponseListener.ToastResponseListener;
import com.cong.potlatch.volley.UploadImageRequest;
import com.google.gson.Gson;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/24/14.
 */
public class CreateGiftActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CreateGiftActivity.class);


    public int mId = 1;

    private CreateGiftFragment mGiftsFrag = null;
    private Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gift);
        if (savedInstanceState == null) {
            mGiftsFrag = CreateGiftFragment.newInstance();

            mGiftsFrag.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.gift_fragment, mGiftsFrag).commit();
        }
        mActionBarToolbar = getActionBarToolbar();
        mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_up);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        enableDisableSwipeRefresh(true);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Method to be called when Photo Clicked button is pressed.
     */
    public void uploadClicked(View view) {
        uploadGift();
    }

    private void uploadGift() {
        Gift gift = mGiftsFrag.getGift();

        ToastResponseListener toastResponseListener = new ToastResponseListener(this);
        UploadImageRequest<Gift> uploadImageRequest;
        String uri = GiftContract.RemoteGifts.CONTENT_URI.toString();
        uploadImageRequest = new UploadImageRequest<Gift>(this, uri,gift.image,Gift.class,new Gson().toJson(gift),
                toastResponseListener,
                toastResponseListener);

        RequestManager.addRequest(uploadImageRequest, this);

    }
}
