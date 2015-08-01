package com.example.firdause.spotifystreamer.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firdause.spotifystreamer.models.TopTracksParcelable;
import com.example.firdause.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by johanneslewiste on 6/22/15.
 */
public class CustomTopTracksAdapter extends ArrayAdapter<TopTracksParcelable> {
    //For debugging purpose
    private final String LOG_TAG = CustomTopTracksAdapter.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private Context context;
    private int layoutResource;
    private ArrayList<TopTracksParcelable> values;

    //Improve scrolling performance of ListView via ViewHolder pattern
    private static class ViewHolder {
        ImageView albumCover;
        TextView albumTitle;
        TextView tracksTitle;
    }

    //Constructor
    public CustomTopTracksAdapter(Context context, int layoutResource, ArrayList<TopTracksParcelable> values) {
        super(context, layoutResource, values);
        this.context = context;
        this.layoutResource = layoutResource;
        this.values = values;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            /*
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_top_tracks, parent, false);
            */

            convertView = layoutInflater.inflate(layoutResource, null);
            viewHolder.albumCover = (ImageView)convertView.findViewById(R.id.album_cover_image_view);
            viewHolder.albumTitle = (TextView)convertView.findViewById(R.id.album_title_text_view);
            viewHolder.tracksTitle = (TextView)convertView.findViewById(R.id.tracks_title_text_view);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TopTracksParcelable topTracksParcelable = values.get(position);

        viewHolder.albumTitle.setText(topTracksParcelable.getName());
        viewHolder.tracksTitle.setText(topTracksParcelable.getAlbum());

        //If tracks URL is available, load album images via Picasso library
        if(topTracksParcelable.getSmallUrl() != null) {
            Picasso.with(context).load(topTracksParcelable.getSmallUrl()).into(viewHolder.albumCover);

        }
        //If it fails, replace the album images with a default picture
        else {
            Picasso.with(context).load(R.drawable.album_cover_icon);
        }

        return convertView;

    }
}
