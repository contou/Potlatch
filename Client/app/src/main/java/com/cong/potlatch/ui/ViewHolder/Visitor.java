package com.cong.potlatch.ui.ViewHolder;

/**
 * Created by cong on 11/15/14.
 */
public interface Visitor {
    void visit(GiftViewHolder viewHolder);
    void visit(CommentViewHolder viewHolder);
}
