package com.example.firdause.spotifystreamer.searchartist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.toptracks.TopTracksActivity;
import com.example.firdause.spotifystreamer.toptracks.TopTracksActivityFragment;
import com.example.firdause.spotifystreamer.customadapters.CustomArtistAdapter;
import com.example.firdause.spotifystreamer.models.ArtistParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchMainActivityFragment extends Fragment implements SearchView.OnQueryTextListener,
        android.support.v7.widget.SearchView.OnCloseListener {
    //For debugging purpose
    private final String LOG_TAG = SearchMainActivityFragment.class.getSimpleName();

    //Initialize Views
    CustomArtistAdapter adapter;
    ListView listView;
    SearchView searchView;

    private final String ARTISTS_KEY = "artists_key";
    private final String QUERY_ONGOING = "query_ongoing";
    private final String QUERY_ARTIST = "query_artist";

    //Manage rotation
    private ArrayList<ArtistParcelable> artistParcelable = new ArrayList<>();
    private boolean queryOngoing = false;
    private String artistQuery;

    //Define the minimum artist image size
    private final int MIN_SIZE = 200;


    //Constructor
    public SearchMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_main, container, false);

        //Setup search view
        searchView = (SearchView) rootView.findViewById(R.id.artist_search_view);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        //Setup custom adapter for artist list view
        adapter = new CustomArtistAdapter(getActivity(), R.layout.list_artists, new ArrayList<ArtistParcelable>());


        //Save values and update the CustomArtistAdapter
        if (savedInstanceState != null) {
            queryOngoing = savedInstanceState.getBoolean(QUERY_ONGOING);
            artistQuery = savedInstanceState.getString(QUERY_ARTIST);
            artistParcelable = savedInstanceState.getParcelableArrayList(ARTISTS_KEY);

            if (artistParcelable != null) {
                adapter.clear();
                for (ArtistParcelable someArtist : artistParcelable) {
                    adapter.add(someArtist);
                }
            }

        }

        //Setup and inject the CustomArtistAdapter into the ListView
        listView = (ListView) rootView.findViewById(R.id.artist_list_view);
        listView.setAdapter(adapter);

        //Display artist's top ten tracks when user clicks on the artist!
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idArtist = adapter.getItem(position).getId();
                String nameArtist = adapter.getItem(position).getName();

                //Start TopTracks Activity!
                Intent intent = new Intent(getActivity(), TopTracksActivity.class);
                intent.putExtra(TopTracksActivityFragment.ID_ARTIST, idArtist);
                intent.putExtra(TopTracksActivityFragment.NAME_ARTIST, nameArtist);

                //Start top tracks activity
                startActivity(intent);

            }
        });

        //Load up and cache default image for artist
        Picasso.with(getActivity()).load(R.drawable.artist_icon).fetch();

        return rootView;
    }


    //Method to search for artist
    public void searchForArtist(String artist) {

        //Performing query now!
        queryOngoing = true;

        //Start the Spotify API
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        spotify.searchArtists(artist, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {

                if (artistsPager != null && artistsPager.artists != null &&
                        artistsPager.artists.items != null &&
                        artistsPager.artists.items.size() > 0) {

                    List<Artist> artists = artistsPager.artists.items;

                    //Retain query results on rotation
                    ArrayList<ArtistParcelable> tempArtists = new ArrayList<>();
                    ArtistParcelable someArtist;
                    for (Artist artist : artists) {
                        List<Image> images = artist.images;
                        String url = getImageURL(images, MIN_SIZE);
                        someArtist = new ArtistParcelable(artist.id, url, artist.name);
                        tempArtists.add(someArtist);
                    }
                    artistParcelable = tempArtists;

                    //Done with query
                    queryOngoing = false;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchView.clearFocus();
                            adapter.clear();
                            for (ArtistParcelable someArtist : artistParcelable) {
                                adapter.add(someArtist);
                            }
                        }
                    });
                }

                //If no matching artists were found, display a toast message as a warning!!!
                else {

                    //Done with query
                    queryOngoing = false;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.no_artist_found, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Done with query
                queryOngoing = false;

                getActivity().runOnUiThread((new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }));
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // if query did not finish run it again
        if (queryOngoing) {
            searchForArtist(artistQuery);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String letters) {
        // save value in appropriate variable
        artistQuery = letters;
        if (artistQuery.length() < 1) {
            adapter.clear();
            listView.setSelectionAfterHeaderView();
        } else {
            searchForArtist(artistQuery);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String letters) {
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARTISTS_KEY, artistParcelable);
        outState.putBoolean(QUERY_ONGOING, queryOngoing);
        outState.putString(QUERY_ARTIST, artistQuery);
    }


}
