package com.cong.potlatch.ui.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.solo.cong.potlatch.R;

public class GiftViewHolder extends BaseViewHolder {
    public TextView commentBtn;
    public TextView pointsBtn;
    public TextView description;
    public TextView title;

    public ImageView image;
//            public ImageButton like;
//            public ImageButton unlike;

    public GiftViewHolder(View itemView) {
        super(itemView);
        pointsBtn = (TextView) itemView.findViewById(R.id.gift_points);
        image = (ImageView) itemView.findViewById(R.id.gift_image);
        description = (TextView) itemView.findViewById(R.id.gift_description);
        title = (TextView) itemView.findViewById(R.id.gift_title);
        commentBtn = (TextView) itemView.findViewById(R.id.comment);

//                like = (ImageButton) itemView.findViewById(R.id.like);
//                unlike = (ImageButton) itemView.findViewById(R.id.unlike);

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}