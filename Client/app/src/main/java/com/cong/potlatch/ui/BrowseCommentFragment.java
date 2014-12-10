package com.cong.potlatch.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.cong.potlatch.Config;
import com.cong.potlatch.data.GiftsHandler;
import com.cong.potlatch.data.model.Comment;
import com.cong.potlatch.data.model.Gift;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.ui.ViewHolder.BaseViewHolder;
import com.cong.potlatch.ui.ViewHolder.CommentViewHolder;
import com.cong.potlatch.ui.ViewHolder.GiftViewHolder;
import com.cong.potlatch.ui.ViewHolder.Visitor;
import com.cong.potlatch.util.ImageLoader;
import com.cong.potlatch.volley.GsonRequest;
import com.cong.potlatch.volley.RequestManager;
import com.cong.potlatch.volley.ResponseListener.ObjectResponseListener;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/11/14.
 */
public class BrowseCommentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(BrowseCommentFragment.class);

    private RecyclerView mListView;
    private LinearLayoutManager mLayoutManager;
    private CommentAdapter mAdapter;
    private int mContentTopClearance = 0;
    private String mGiftId;
    private EditText mContent;
    private ImageLoader mImageLoader;


    public static BrowseCommentFragment newInstance(Bundle bundle) {
        BrowseCommentFragment browseCommentFragment = new BrowseCommentFragment();
        browseCommentFragment.setArguments(bundle);
        return browseCommentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!getActivity().isFinishing()) {
            getLoaderManager().initLoader(CommentQuery.TOKEN, null, this);
            getLoaderManager().initLoader(GiftQuery.TOKEN, null, this);
        }
        mImageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment_list, container, false);

        mContent = (EditText) getActivity().findViewById(R.id.edit_text_comment);

        mListView = (RecyclerView) rootView.findViewById(R.id.comments);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentAdapter(null, null);
        mListView.setAdapter(mAdapter);

        return rootView;
    }


    public boolean canListViewScrollUp() {
        return ViewCompat.canScrollVertically(mListView, -1);
    }

    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            mListView.setPadding(mListView.getPaddingLeft(), mContentTopClearance,
                    mListView.getPaddingRight(), mListView.getPaddingBottom());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CommentQuery.TOKEN) {
            mGiftId = getArguments().getString(GiftContract.Gifts.GIFT_ID);
//            LOGD(TAG,"comment query:" + mGiftId);
            return new CursorLoader(getActivity(), GiftContract.Gifts.buildCommentsDir(mGiftId), CommentQuery.PROJECTION, null, null, null);
        } else if (id == GiftQuery.TOKEN) {
            mGiftId = getArguments().getString(GiftContract.Gifts.GIFT_ID);
//            LOGD(TAG,"gift query:" + mGiftId);
            return new CursorLoader(getActivity(), GiftContract.Gifts.buildUri(mGiftId), GiftQuery.PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CommentQuery.TOKEN) {
            mAdapter.swapCommentCursor(data);
        } else if (loader.getId() == GiftQuery.TOKEN) {
//            LOGD(TAG, "Gift Count" + String.valueOf(data.getCount()));
            mAdapter.swapGiftCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CommentQuery.TOKEN) {
            mAdapter.swapCommentCursor(null);
        } else if (loader.getId() == GiftQuery.TOKEN) {
            mAdapter.swapGiftCursor(null);
        }
    }

    public Comment getComment() {

        Comment comment = new Comment();
        comment.giftId = mGiftId;
        comment.content = mContent.getText().toString();

        return comment;
    }


    public class CommentAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        private BindViewVisitor bindViewVisitor;

        private Cursor mGiftCursor;
        private Cursor mCommentCursor;
        private MergeCursor mergeCursor;

        public CommentAdapter(Cursor mGiftCursor, Cursor mCommentCursor) {
            this.mGiftCursor = mGiftCursor;
            this.mCommentCursor = mCommentCursor;
            mergeCursor = new MergeCursor(new Cursor[]{mGiftCursor,mCommentCursor});
            bindViewVisitor = new BindViewVisitor();
        }

        @Override
        public int getItemViewType(int position) {
           mergeCursor.moveToPosition(position);
           return mergeCursor.getInt(GiftQuery.VIEW_TYPE);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
//            if (mGiftCursor != null) {
                if (getItemViewType(i) == GiftContract.Gifts.VIEW_TYPE) {
//                    LOGD(TAG, String.valueOf(i));
                    View view = layoutInflater.inflate(R.layout.list_item_gift2, viewGroup, false);
                    GiftViewHolder vh = new GiftViewHolder(view);
                    return vh;
                } else {
                    View itemView = layoutInflater.inflate(R.layout.list_item_comment, viewGroup, false);
                    CommentViewHolder vh = new CommentViewHolder(itemView);
                    return vh;
                }
//            }else {
//                View itemView = layoutInflater.inflate(R.layout.list_item_comment, viewGroup, false);
//                CommentViewHolder vh = new CommentViewHolder(itemView);
//                return vh;
//            }

        }

        @Override
        public void onBindViewHolder(BaseViewHolder viewHolder, int i) {
            //TODO:move the cursor to the right position.
//            mergeCursor.moveToPosition(i);
            viewHolder.accept(bindViewVisitor);
        }

        @Override
        public int getItemCount() {
            return mergeCursor.getCount();
        }

        public void swapGiftCursor(Cursor data) {
            mGiftCursor = data;
            mergeCursor = new MergeCursor(new Cursor[]{mGiftCursor,mCommentCursor});
            notifyDataSetChanged();
        }

        public void swapCommentCursor(Cursor data) {
            mCommentCursor = data;
            mergeCursor = new MergeCursor(new Cursor[]{mGiftCursor,mCommentCursor});
            notifyDataSetChanged();
        }

        private class BindViewVisitor implements Visitor {
            @Override
            public void visit(GiftViewHolder vh) {
                final String giftId = mergeCursor.getString(GiftQuery.GIFT_ID);
                int giftPoints = mergeCursor.getInt(GiftQuery.GIFT_POINTS);
                String giftTitle = mergeCursor.getString(GiftQuery.GIFT_TITLE);
                final String giftImage = mergeCursor.getString(GiftQuery.GIFT_IMAGE);
                String giftDescription = mergeCursor.getString(GiftQuery.GIFT_DESCRIPTION);

                vh.commentBtn.setText(String.valueOf(getItemCount() - 1));
                vh.title.setText(giftTitle);
                vh.description.setText(giftDescription);

                vh.pointsBtn.setText(String.valueOf(giftPoints));
                mImageLoader.loadImage(Config.BASE_URL + giftImage, vh.image, true);


                vh.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        BrowseCommentActivity.startActivity(getActivity(), giftId);

//                        getView().findViewById(R.id.edit_text_comment).requestFocus();
                    }
                });

                vh.pointsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ObjectResponseListener<Gift> listener = new ObjectResponseListener<Gift>(getActivity(), new GiftsHandler(getActivity()), false);
                        String uri = GiftContract.RemoteGifts.buildLikeGiftUri(giftId).toString();
                        GsonRequest<Gift> stringRequest = new GsonRequest<Gift>(getActivity(), Request.Method.POST, uri, Gift.class, null,
                                listener,
                                listener);
                        RequestManager.addRequest(stringRequest, this);
                    }
                });

            }

            @Override
            public void visit(CommentViewHolder viewHolder) {
                String content = mergeCursor.getString(CommentQuery.COMMENT_CONTENT);
                String creator = mergeCursor.getString(CommentQuery.COMMENT_CREATOR);

                viewHolder.content.setText(content);
                viewHolder.creator.setText(creator);
            }
        }

    }

    private interface CommentQuery {

        int TOKEN = 0x1;

        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Gifts.VIEW_TYPE_COLUMN,
                GiftContract.Comments.COMMENT_ID,
                GiftContract.Comments.COMMENT_CONTENT,
                GiftContract.Comments.COMMENT_CREATOR,
        };

        int _ID = 0;
        int VIEW_TYPE = 1;
        int COMMENT_ID = 2;
        int COMMENT_CONTENT = 3;
        int COMMENT_CREATOR= 4;
    }


    private interface GiftQuery {

        int TOKEN = 0x2;

        String[] PROJECTION = {
                BaseColumns._ID,
                GiftContract.Gifts.VIEW_TYPE_COLUMN,
                GiftContract.Gifts.GIFT_ID,
                GiftContract.Gifts.GIFT_POINTS,
                GiftContract.Gifts.GIFT_TITLE,
                GiftContract.Gifts.GIFT_IMAGE,
                GiftContract.Gifts.GIFT_DESCRIPTION,
        };

        int _ID = 0;
        int VIEW_TYPE = 1;
        int GIFT_ID = 2;
        int GIFT_POINTS = 3;
        int GIFT_TITLE = 4;
        int GIFT_IMAGE = 5;
        int GIFT_DESCRIPTION = 6;
    }

}
