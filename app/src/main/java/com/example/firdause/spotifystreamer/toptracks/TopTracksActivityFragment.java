package com.example.firdause.spotifystreamer.toptracks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.customadapters.CustomTopTracksAdapter;
import com.example.firdause.spotifystreamer.models.TopTracksParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {
    //For debugging purpose
    private final String LOG_TAG = TopTracksActivityFragment.class.getSimpleName();

    //Initialize views
    CustomTopTracksAdapter adapter;
    ListView listView;

    String nameArtist;
    String idArtist;

    private final String TRACKS_KEY = "tracks_key";
    private final String QUERY_ONGOING = "query_ongoing";
    public static final String ID_ARTIST = "idArtist";
    public static final String NAME_ARTIST = "nameArtist";

    //Manage rotation
    private ArrayList<TopTracksParcelable> topTracksParcelable;
    private boolean queryOngoing = false;

    //Define the album image (small and big size)
    private final int SMALL_IMAGE = 200;
    private final int BIG_IMAGE = 640;

    //Constructor
    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        //Setup custom adapter for top tracks list view
        adapter = new CustomTopTracksAdapter(getActivity(), R.layout.list_top_tracks, new ArrayList<TopTracksParcelable>());

        if (savedInstanceState != null) {
            queryOngoing = savedInstanceState.getBoolean(QUERY_ONGOING);
            topTracksParcelable = savedInstanceState.getParcelableArrayList(TRACKS_KEY);

            if (topTracksParcelable != null) {
                adapter.clear();
                for (TopTracksParcelable someTracks : topTracksParcelable) {
                    adapter.add(someTracks);
                }
            }
        }

        //Setup and inject CustomTopTracksAdapter into the ListView
        listView = (ListView) rootView.findViewById(R.id.tracks_list_view);
        listView.setAdapter(adapter);

        Intent intent = getActivity().getIntent();
        nameArtist = intent.getStringExtra(NAME_ARTIST);
        idArtist = intent.getStringExtra(ID_ARTIST);

        if (queryOngoing || savedInstanceState == null) {
            searchForTopTracks(idArtist);

        }

        //Load up and cache default image for album
        Picasso.with(getActivity()).load(R.drawable.album_cover_icon).fetch();

        return rootView;
    }


    //Method to search for artist's top tracks!
    public void searchForTopTracks(String idArtist) {

        //Performing query now!
        queryOngoing = true;

        //Start the Spotify Api
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> map = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String country = sharedPreferences.getString(
                getString(R.string.country_key),
                getString(R.string.country_default));

        map.put("country", country);

        spotify.getArtistTopTrack(idArtist, map, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                if (tracks.tracks.size() > 0 && tracks != null && tracks.tracks != null) {

                    List<Track> realTracks = tracks.tracks;

                    //Retain query results on rotation
                    ArrayList<TopTracksParcelable> tempTracks = new ArrayList<>();
                    TopTracksParcelable someTracks;

                    for(Track track : realTracks) {
                        List<Image> images = track.album.images;
                        String smallUrl = getImageURL(images, SMALL_IMAGE);
                        String bigUrl = getImageURL(images, BIG_IMAGE);

                        //Preview URL is meant for Spotify Stage 2, ignore it at the moment
                        someTracks = new TopTracksParcelable(smallUrl, bigUrl, track.preview_url,
                                track.name, track.album.name, nameArtist);
                        tempTracks.add(someTracks);
                    }
                    topTracksParcelable = tempTracks;

                    //Done with query
                    queryOngoing = false;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            for (TopTracksParcelable someTracks : topTracksParcelable) {
                                adapter.add(someTracks);
                            }
                        }
                    });
                }
                else {
                    //Done with query
                    queryOngoing = false;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Done with query
                queryOngoing = false;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    //Method to grab image urls
    public String getImageURL(List<Image> images, int minWidth) {
        int size = images.size();
        if (size > 0) {

            ListIterator iterator = images.listIterator(size);
            while (iterator.hasPrevious()) {
                Image image = (Image) iterator.previous();
                if (image.width >= minWidth) {
                    return image.url;
                }
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACKS_KEY, topTracksParcelable);
        outState.putBoolean(QUERY_ONGOING, queryOngoing);
    }


}
