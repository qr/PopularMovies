package it.soepel.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent i = getActivity().getIntent();

        String originalTitle = getString(R.string.title_placeholder);
        Double voteAverage   = 0.0;
        Date   releaseDate   = new Date();

        if (
                null != i
                && i.hasExtra("it.soepel.popularmovies.detail.original_title")
                && i.hasExtra("it.soepel.popularmovies.detail.poster_path")
                && i.hasExtra("it.soepel.popularmovies.detail.overview")
                && i.hasExtra("it.soepel.popularmovies.detail.vote_average")
                && i.hasExtra("it.soepel.popularmovies.detail.release_date")
                ) {
            originalTitle     = i.getStringExtra("it.soepel.popularmovies.detail.original_title");
            String posterPath = i.getStringExtra("it.soepel.popularmovies.detail.poster_path");
            String overview   = i.getStringExtra("it.soepel.popularmovies.detail.overview");
            voteAverage       = i.getDoubleExtra("it.soepel.popularmovies.detail.vote_average", 0);
            releaseDate       = (Date)i.getSerializableExtra("it.soepel.popularmovies.detail.release_date");
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleView       = (TextView) view.findViewById(R.id.text_title);
        TextView userRatingView  = (TextView) view.findViewById(R.id.text_user_rating);
        TextView releaseDateView = (TextView) view.findViewById(R.id.text_release_date);

        titleView.setText(originalTitle);
        userRatingView.setText(getString(R.string.user_rating, voteAverage.toString()));

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        String formatedDate = dateFormat.format(releaseDate);
        releaseDateView.setText(getString(R.string.release_date, formatedDate));

        return view;
    }
}
