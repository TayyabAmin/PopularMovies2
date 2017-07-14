package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.model.TrailerData;
import com.example.popularmoviesstage2.R;

import java.util.ArrayList;

/**
 * Created by Tayyab on 6/29/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<TrailerData> arrayList;
    final private ListItemClickListener mOnItemClickListener;
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView trailerName;
        public ViewHolder(View itemView) {
            super(itemView);
            trailerName = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int checkedItem = getAdapterPosition();
            mOnItemClickListener.onListItemClicked(checkedItem);
        }
    }

    public interface ListItemClickListener {
        void onListItemClicked (int checkedItemIndex);
    }

    public TrailersAdapter(Context context, ArrayList<TrailerData> arrayList, ListItemClickListener listener) {
        this.mContext = context;
        this.arrayList = arrayList;
        this.mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.trailer_item_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.trailerName.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
