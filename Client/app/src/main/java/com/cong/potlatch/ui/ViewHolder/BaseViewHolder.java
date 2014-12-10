package com.cong.potlatch.ui.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cong on 11/15/14.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }
    public void accept (Visitor visitor)
    {
        throw new UnsupportedOperationException("BaseViewHolder::accept() called improperly");
    }
}
