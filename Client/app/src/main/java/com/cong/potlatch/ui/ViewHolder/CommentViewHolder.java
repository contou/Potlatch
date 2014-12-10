package com.cong.potlatch.ui.ViewHolder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.solo.cong.potlatch.R;

/**
 * Created by cong on 11/11/14.
 */
public class CommentViewHolder  extends BaseViewHolder{
    public TextView content;
    public TextView creator;

    public CommentViewHolder(View itemView) {
        super(itemView);
        content = (TextView) itemView.findViewById(R.id.content);
        creator = (TextView) itemView.findViewById(R.id.creator);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
