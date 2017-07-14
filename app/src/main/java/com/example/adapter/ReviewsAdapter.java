package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.model.ReviewData;
import com.example.popularmoviesstage2.R;

import java.util.ArrayList;

/**
 * Created by Tayyab on 6/30/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<ReviewData> arrayList;
    class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView reviewAuthor, reviewContent;
        public ViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = (TextView) itemView.findViewById(R.id.review_author);
            reviewContent = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    public ReviewsAdapter(Context context, ArrayList<ReviewData> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_item_view, parent, false);

        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.reviewAuthor.setText(arrayList.get(position).getAuthor());
        holder.reviewContent.setText(arrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
