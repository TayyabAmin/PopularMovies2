package com.example.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.model.MovieData;
import com.example.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tayyab on 6/4/2017.
 * Adapter for loading movies in grid view
 */

public class MoviesAdapter extends ArrayAdapter {

    private final Context mContext;
    private final ArrayList<MovieData> arrayList;

    public MoviesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<MovieData> objects) {
        super(context, resource, objects);
        mContext = context;
        arrayList = objects;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    /*@Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_view, null);
            ImageView imageView = (ImageView) gridView.findViewById(R.id.iv_grid);
            final String URL = "http://image.tmdb.org/t/p/w185";
            String path = URL + arrayList.get(position).getPath();

            Picasso.with(mContext).load(path).into(imageView);
        }else {
            return convertView;
        }

        return gridView;
    }
}
