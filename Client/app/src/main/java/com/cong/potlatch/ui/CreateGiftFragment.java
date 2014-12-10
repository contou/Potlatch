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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cong.potlatch.data.model.Gift;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.ui.Aadapter.CategorySpinnerAdapter;
import com.cong.potlatch.util.AccountUtils;
import com.cong.potlatch.util.PrefUtils;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 10/24/14.
 */
public class CreateGiftFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = makeLogTag(CreateGiftFragment.class);

    public ImageView mImage;
    public EditText mDescription;
    public EditText mTitle;
    private Spinner mCategorySpinner;
    private Cursor mCursor;
    private String mCurrentCategory;
    private Uri mImageUri;


    public static CreateGiftFragment newInstance() {
        return new CreateGiftFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getLoaderManager().initLoader(CategoryQuery.TOKEN,null,this);
        mImageUri = Uri.parse(PrefUtils.getImageLocation(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_gift, container, false);
        mTitle = (EditText) rootView.findViewById(R.id.create_gift_title);
        mDescription = (EditText) rootView.findViewById(R.id.create_gift_description);
        mCategorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
        mImage = (ImageView) rootView.findViewById(R.id.create_gift_image);
        mImage.setImageURI(mImageUri);

        return rootView;
    }

    public Gift getGift() {
        Gift gift = new Gift();
        gift.title = mTitle.getText().toString();
        gift.category = mCurrentCategory;
        gift.description = mDescription.getText().toString();
        gift.points = 0;
        gift.image = mImageUri.getPath();
        gift.creatorId = AccountUtils.getAccountName(getActivity());
        return gift;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!PrefUtils.getImageLocation(getActivity()).equals("")){
           PrefUtils.setImageLocation(getActivity(),"");
        }

    }

    private void trySetUpCategorySpinner() {
        if(mCursor == null) {
            return;
        }

        final CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(getActivity(),false);
        adapter.addItem("", getString(R.string.share_to_category), false, 0);
        adapter.addItem("default_category", getString(R.string.default_category), false, 0);
        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            String categoryId = mCursor.getString(CategoryQuery.CATEGORY_ID);
            String categoryName = mCursor.getString(CategoryQuery.CATEGORY_NAME);
            adapter.addItem(categoryId, categoryName, false, 0);
            mCursor.moveToNext();
        }
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                if(adapter.getTag(position) != "") {
                    mCurrentCategory = adapter.getTag(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
            mCursor = data;
        }
        trySetUpCategorySpinner();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CategoryQuery.TOKEN) {
            mCursor = null;
        }
        trySetUpCategorySpinner();
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
