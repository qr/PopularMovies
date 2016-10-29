package it.soepel.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent i = getActivity().getIntent();

        String originalTitle = getString(R.string.title_placeholder);
        Double voteAverage   = 0.0;
        Date   releaseDate   = new Date();
        String overview      = getString(R.string.overview_placeholder);
        String posterPath    = null;

        if (
                null != i
                && i.hasExtra("it.soepel.popularmovies.detail.original_title")
                && i.hasExtra("it.soepel.popularmovies.detail.poster_path")
                && i.hasExtra("it.soepel.popularmovies.detail.overview")
                && i.hasExtra("it.soepel.popularmovies.detail.vote_average")
                && i.hasExtra("it.soepel.popularmovies.detail.release_date")
                ) {
            originalTitle     = i.getStringExtra("it.soepel.popularmovies.detail.original_title");
            posterPath = i.getStringExtra("it.soepel.popularmovies.detail.poster_path");
            overview   = i.getStringExtra("it.soepel.popularmovies.detail.overview");
            voteAverage       = i.getDoubleExtra("it.soepel.popularmovies.detail.vote_average", 0);
            releaseDate       = (Date)i.getSerializableExtra("it.soepel.popularmovies.detail.release_date");
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleView       = (TextView) view.findViewById(R.id.text_title);
        TextView userRatingView  = (TextView) view.findViewById(R.id.text_user_rating);
        TextView releaseDateView = (TextView) view.findViewById(R.id.text_release_date);
        TextView overviewView    = (TextView) view.findViewById(R.id.text_overview);
        ImageView imageView      = (ImageView) view.findViewById(R.id.detail_image);

        titleView.setText(originalTitle);
        userRatingView.setText(getString(R.string.user_rating, voteAverage.toString()));
        overviewView.setText(overview);

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        String formatedDate = dateFormat.format(releaseDate);
        releaseDateView.setText(getString(R.string.release_date, formatedDate));

        if (null != posterPath) {
            Picasso.with(getContext()).load(generateURL(posterPath).toString()).placeholder(R.drawable.loading).fit().centerCrop().into(imageView);
        }

        return view;
    }

    protected URL generateURL(String poster_path) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("original");
        String urlString = builder.build().toString();
        urlString += poster_path;
        URL url;
        try {
            url = new URL(urlString);
            Log.v(LOG_TAG, urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }
}
