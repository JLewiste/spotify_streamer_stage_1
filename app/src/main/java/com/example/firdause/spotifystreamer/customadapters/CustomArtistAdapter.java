package com.example.firdause.spotifystreamer.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firdause.spotifystreamer.models.ArtistParcelable;
import com.example.firdause.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by johanneslewiste on 6/24/15.
 */
public class CustomArtistAdapter extends ArrayAdapter<ArtistParcelable> {
    //For debugging purpose
    private final String LOG_TAG = CustomArtistAdapter.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private Context context;
    private int layoutResource;
    private ArrayList<ArtistParcelable> values;

    //Improve scrolling performance of ListView via ViewHolder pattern
    private static class ViewHolder {
        ImageView artistCover;
        TextView artistName;
    }

    //Constructor
    public CustomArtistAdapter(Context context, int layoutResource, ArrayList<ArtistParcelable> values) {
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

            convertView = layoutInflater.inflate(layoutResource, null);
            viewHolder.artistCover = (ImageView)convertView.findViewById(R.id.artist_cover_image_view);
            viewHolder.artistName = (TextView)convertView.findViewById(R.id.artist_name_text_view);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ArtistParcelable artistParcelable = values.get(position);

        viewHolder.artistName.setText(artistParcelable.getName());

        //If artist URL is available, load artist images via Picasso library
        if(artistParcelable.getUrl() != null) {
            Picasso.with(context).load(artistParcelable.getUrl())
                    .into(viewHolder.artistCover);
        }
        //If it fails, replace the artist images with a default picture
        else {
            Picasso.with(context).load(R.drawable.artist_icon).into(viewHolder.artistCover);
        }


        return convertView;
    }
}
