package it.soepel.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MovieContent {

    private Context mContext;

    private MovieItemViewAdapter mAdapter;

    public MovieContent(Context context) {
        mContext = context;
    }

    public List<MovieItem> items = new ArrayList<MovieItem>();

    public Integer getCount() {
        return items.size();
    }

    public void updateItems(MovieItemViewAdapter adapter) {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(mContext.getString(R.string.top_rated));
        mAdapter = adapter;
    }

    public static class MovieItem {
        public final Integer id;
        public final String poster_path;
        public final Boolean adult;
        public final String overview;
        public final Date release_date;
        public final String original_title;
        public final String original_language;
        public final String title;
        public final String backdrop_path;
        public final Double popularity;
        public final Integer vote_count;
        public final Boolean video;
        public final Double vote_average;

        /**
         * Constructor without genres
         * @param id identifier from themoviedb
         * @param poster_path path to the poster picture
         * @param adult boolean indicating adult content
         * @param overview synposis of the movie
         * @param release_date the release date of the movie
         * @param original_title the title in the original language
         * @param original_language the original language
         * @param title title in english
         * @param backdrop_path path to the backdrop picture
         * @param popularity boolean indicating popularity
         * @param vote_count number of votes
         * @param video boolean indicating if there is a video available
         * @param vote_average the vote average
         */
        public MovieItem(Integer id, String poster_path, Boolean adult,
                         String overview, Date release_date, String original_title,
                         String original_language, String title, String backdrop_path,
                         Double popularity, Integer vote_count, Boolean video,
                         Double vote_average) {
            this.id                = id;
            this.poster_path       = poster_path;
            this.adult             = adult;
            this.overview          = overview;
            this.release_date      = release_date;
            this.original_title    = original_title;
            this.original_language = original_language;
            this.title             = title;
            this.backdrop_path     = backdrop_path;
            this.popularity        = popularity;
            this.vote_count        = vote_count;
            this.video             = video;
            this.vote_average      = vote_average;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, List<MovieItem>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<MovieItem> movieItems) {
            mAdapter.updateList(movieItems);
        }

        @Override
        protected List<MovieItem> doInBackground(String... params) {
            if (1 != params.length) {
                return null;
            }

            List<MovieItem> items = new ArrayList<MovieItem>();
            String moviesJsonStr = fetchJson(params[0]);

            if (null != moviesJsonStr) {
                items = parseItems(moviesJsonStr);
            }

            return items;
        }

        private List<MovieItem> parseItems(String jsonString) {
            List<MovieItem> items = new ArrayList<MovieItem>();

            final String TMD_RESULTS           = "results";
            final String TMD_ID                = "id";
            final String TMD_POSTER_PATH       = "poster_path";
            final String TMD_ADULT             = "adult";
            final String TMD_OVERVIEW          = "overview";
            final String TMD_RELEASE_DATE      = "release_date";
            final String TMD_ORIGINAL_TITLE    = "original_title";
            final String TMD_ORIGINAL_LANGUAGE = "original_language";
            final String TMD_TITLE             = "title";
            final String TMD_BACKDROP_PATH     = "backdrop_path";
            final String TMD_POPULARITY        = "popularity";
            final String TMD_VOTE_COUNT        = "vote_count";
            final String TMD_VIDEO             = "video";
            final String TMD_VOTE_AVERAGE      = "vote_average";

            try {
                JSONObject moviesJson = new JSONObject(jsonString);
                JSONArray resultsArray = moviesJson.getJSONArray(TMD_RESULTS);

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movieJsonObject = resultsArray.getJSONObject(i);

                    Date date = format.parse(movieJsonObject.getString(TMD_RELEASE_DATE));

                    MovieItem item = new MovieItem(
                        movieJsonObject.getInt(TMD_ID),
                        movieJsonObject.getString(TMD_POSTER_PATH),
                        movieJsonObject.getBoolean(TMD_ADULT),
                        movieJsonObject.getString(TMD_OVERVIEW),
                        date,
                        movieJsonObject.getString(TMD_ORIGINAL_TITLE),
                        movieJsonObject.getString(TMD_ORIGINAL_LANGUAGE),
                        movieJsonObject.getString(TMD_TITLE),
                        movieJsonObject.getString(TMD_BACKDROP_PATH),
                        movieJsonObject.getDouble(TMD_POPULARITY),
                        movieJsonObject.getInt(TMD_VOTE_COUNT),
                        movieJsonObject.getBoolean(TMD_VIDEO),
                        movieJsonObject.getDouble(TMD_VOTE_AVERAGE)
                    );

                    items.add(item);
                }

            } catch (JSONException|ParseException e) {
                Log.e(LOG_TAG, "Error", e);
            }

            return items;
        }

        private String fetchJson(String listType) {
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonString = null;

            try {
                URL movieUrl = getMovieURL(listType);

                urlConnection = (HttpsURLConnection) movieUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonString = buffer.toString();

                Log.v(LOG_TAG, jsonString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return jsonString;
        }

        private URL getMovieURL(String listType) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(listType)
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
            String urlString = builder.build().toString();
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
}
