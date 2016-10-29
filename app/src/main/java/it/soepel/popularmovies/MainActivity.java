package it.soepel.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import it.soepel.popularmovies.MovieContent.MovieItem;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new MovieFragment())
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(MovieItem item) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("it.soepel.popularmovies.detail.original_title", item.original_title);
        detailIntent.putExtra("it.soepel.popularmovies.detail.poster_path", item.poster_path);
        detailIntent.putExtra("it.soepel.popularmovies.detail.overview", item.overview);
        detailIntent.putExtra("it.soepel.popularmovies.detail.vote_average", item.vote_average);
        detailIntent.putExtra("it.soepel.popularmovies.detail.release_date", item.release_date);
        startActivity(detailIntent);
    }
}
