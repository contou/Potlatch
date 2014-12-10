package com.cong.potlatch.ui.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.solo.cong.potlatch.R;

/**
 * Created by cong on 11/24/14.
 */
public class UserViewHolder extends BaseViewHolder {
    public TextView userName;
    public TextView status;
    public TextView gender;
    public ImageView accountImage;
    public ImageView rank;

    public UserViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.profile_name);
        status = (TextView) itemView.findViewById(R.id.profile_status);
        gender = (TextView) itemView.findViewById(R.id.profile_gender);
        accountImage = (ImageView) itemView.findViewById(R.id.profile_image);
        rank = (ImageView) itemView.findViewById(R.id.rank);
    }
}
