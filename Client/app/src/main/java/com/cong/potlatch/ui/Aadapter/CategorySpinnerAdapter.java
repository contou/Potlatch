package com.cong.potlatch.ui.Aadapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.solo.cong.potlatch.R;

import java.util.ArrayList;

public class CategorySpinnerAdapter extends BaseAdapter {
    private class CategorySpinnerItem {
        boolean isHeader;
        String tag, title;
        int color;
        boolean indented;

        CategorySpinnerItem(boolean isHeader, String tag, String title, boolean indented, int color) {
            this.isHeader = isHeader;
            this.tag = tag;
            this.title = title;
            this.indented = indented;
            this.color = color;
        }
    }

    private int mDotSize;
    private boolean mTopLevel;
    private final Context mContext;


    public CategorySpinnerAdapter(Context context,boolean topLevel) {
        this.mTopLevel = topLevel;
        this.mContext = context;
    }

    // pairs of (tag, title)
    private ArrayList<CategorySpinnerItem> mItems = new ArrayList<CategorySpinnerItem>();

    public void clear() {
        mItems.clear();
    }

    public void addItem(String tag, String title, boolean indented, int color) {
        mItems.add(new CategorySpinnerItem(false, tag, title, indented, color));
    }

    public void addHeader(String title) {
        mItems.add(new CategorySpinnerItem(true, "", title, false, 0));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private boolean isHeader(int position) {
        return position >= 0 && position < mItems.size()
                && mItems.get(position).isHeader;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.explore_spinner_item_dropdown,
                    parent, false);
            view.setTag("DROPDOWN");
        }

        TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
        View dividerView = view.findViewById(R.id.divider_view);
        TextView normalTextView = (TextView) view.findViewById(android.R.id.text1);

        if (isHeader(position)) {
            headerTextView.setText(getTitle(position));
            headerTextView.setVisibility(View.VISIBLE);
            normalTextView.setVisibility(View.GONE);
            dividerView.setVisibility(View.VISIBLE);
        } else {
            headerTextView.setVisibility(View.GONE);
            normalTextView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.GONE);

            setUpNormalDropdownView(position, normalTextView);
        }

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = LayoutInflater.from(mContext).inflate(mTopLevel
                            ? R.layout.explore_spinner_item_actionbar
                            : R.layout.explore_spinner_item,
                    parent, false
            );
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        if(!mTopLevel) {
            textView.setTextColor(mContext.getResources().getColor(R.color.body_text_1));
        }
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
    }

    private int getColor(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).color : 0;
    }

    public String getTag(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).tag : "";
    }

    private void setUpNormalDropdownView(int position, TextView textView) {
        textView.setText(getTitle(position));
        ShapeDrawable colorDrawable = (ShapeDrawable) textView.getCompoundDrawables()[2];
        int color = getColor(position);
        if (color == 0) {
            if (colorDrawable != null) {
                textView.setCompoundDrawables(null, null, null, null);
            }
        } else {
            if (mDotSize == 0) {
                mDotSize = mContext.getResources().getDimensionPixelSize(
                        R.dimen.tag_color_dot_size);
            }
            if (colorDrawable == null) {
                colorDrawable = new ShapeDrawable(new OvalShape());
                colorDrawable.setIntrinsicWidth(mDotSize);
                colorDrawable.setIntrinsicHeight(mDotSize);
                colorDrawable.getPaint().setStyle(Paint.Style.FILL);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, colorDrawable, null);
            }
            colorDrawable.getPaint().setColor(color);
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}