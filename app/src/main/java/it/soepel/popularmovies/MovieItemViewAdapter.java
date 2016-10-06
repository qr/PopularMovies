package it.soepel.popularmovies;

import android.content.Context;
import android.net.Uri;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import it.soepel.popularmovies.MovieContent.MovieItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import it.soepel.popularmovies.MovieFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MovieItem} and makes a call to the
 * specified {@link }.
 */
public class MovieItemViewAdapter extends RecyclerView.Adapter<MovieItemViewAdapter.ViewHolder> {

    private final String LOG_TAG = MovieItemViewAdapter.class.getSimpleName();
    private List<MovieItem> mValues;
    private final MovieFragment.OnListFragmentInteractionListener mListener;
    private Context mContext;

    public MovieItemViewAdapter(List<MovieItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues   = items;
        mListener = listener;
        mContext  = context;
    }

    public void updateList(List<MovieItem> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Picasso.with(mContext).load(generateURL(holder.mItem.poster_path).toString()).placeholder(R.drawable.loading).fit().centerCrop().into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    protected URL generateURL(String poster_path) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w780");
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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public MovieItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.content);
        }
    }
}
