package com.cong.potlatch.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;

import com.cong.potlatch.data.model.Category;
import com.cong.potlatch.provider.GiftContract;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/24/14.
 */
public class CategoryHandler extends JSONHandler<Category> {
    private static final String TAG = makeLogTag(CategoryHandler.class) ;
    ArrayList<Category> mCategories;
    private Uri allCategoriesUri = GiftContract.addCallerIsSyncAdapterParameter(
            GiftContract.Categories.CONTENT_URI);

    public CategoryHandler(Context context) {
        super(context);
        mCategories = new ArrayList<Category>();
    }

    @Override
    public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        list.add(ContentProviderOperation.newDelete(allCategoriesUri).build());
        for(Category category: mCategories) {
           build(true,category,list);
        }
    }

    @Override
    public void process(JsonElement element) {
        LOGD(TAG,element.toString());
      Category[] categories = new Gson().fromJson(element, Category[].class);
      for(Category category : categories) {
          mCategories.add(category);
      }
    }

    @Override
    public void build(boolean isNew, Category category, ArrayList<ContentProviderOperation> list) {
        Uri thisCategoryUri = GiftContract.addCallerIsSyncAdapterParameter(
                GiftContract.Categories.buildUri(category.name));

        ContentProviderOperation.Builder builder;
        if(isNew) {
            builder = ContentProviderOperation.newInsert(allCategoriesUri);
        } else {
            builder = ContentProviderOperation.newUpdate(thisCategoryUri);
        }

        builder.withValue(GiftContract.Categories.CATEGORY_ID, category.id);
        builder.withValue(GiftContract.Categories.CATEGORY_NAME, category.name);

        list.add(builder.build());
    }
}
