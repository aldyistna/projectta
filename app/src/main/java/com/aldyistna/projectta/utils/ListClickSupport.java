package com.aldyistna.projectta.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aldyistna.projectta.R;

public class ListClickSupport {
    private final RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                onItemClickListener.onItemClicked(recyclerView, holder.getAdapterPosition(), view);
            }
        }
    };

    private final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                return onItemLongClickListener.onItemLongClicked(recyclerView, holder.getAdapterPosition(), view);
            }
            return false;
        }
    };

    private final RecyclerView.OnChildAttachStateChangeListener attachListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(@NonNull View view) {
            if (onItemClickListener != null){
                view.setOnClickListener(onClickListener);
            }

            if (onItemLongClickListener != null) {
                view.setOnLongClickListener(onLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(@NonNull View view) {

        }
    };

    private ListClickSupport(RecyclerView recyclerViewl) {
        recyclerView = recyclerViewl;
        recyclerView.setTag(R.id.list_click_support, this);
        recyclerView.addOnChildAttachStateChangeListener(attachListener);
    }

    public static ListClickSupport addTo(RecyclerView rv) {
        ListClickSupport support = (ListClickSupport) rv.getTag(R.id.list_click_support);
        if (support == null) {
            support = new ListClickSupport(rv);
        }
        return support;
    }

    public static ListClickSupport removeFrom(RecyclerView rv) {
        ListClickSupport support = (ListClickSupport) rv.getTag(R.id.list_click_support);
        if (support != null) {
            support.detach(rv);
        }
        return support;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    private void detach(RecyclerView rv) {
        rv.removeOnChildAttachStateChangeListener(attachListener);
        rv.setTag(R.id.list_click_support, null);
    }

    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int pos, View view);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, int pos, View view);
    }
}
