package com.cong.potlatch.ui.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.solo.cong.potlatch.R;

/**
 * Created by cong on 11/24/14.
 */
public class CategoryViewHolder extends BaseViewHolder{
    public TextView categoryName;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        categoryName = (TextView) itemView.findViewById(R.id.category_name);
    }
}
