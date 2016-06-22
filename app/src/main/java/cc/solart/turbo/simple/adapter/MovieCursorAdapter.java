package cc.solart.turbo.simple.adapter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.solart.turbo.BaseCursorAdapter;
import cc.solart.turbo.BaseViewHolder;
import cc.solart.turbo.simple.R;
import cc.solart.turbo.simple.provider.MovieContract;

/**
 * -------------------------------------------------------------------------
 * Author: imilk
 * Create:  12:55
 * -------------------------------------------------------------------------
 * Describe:
 * -------------------------------------------------------------------------
 * Changes:
 * -------------------------------------------------------------------------
 * 12 : Create by imilk
 * -------------------------------------------------------------------------
 */
public class MovieCursorAdapter extends BaseCursorAdapter<MovieCursorAdapter.MovieHolder>{


    /**
     * Constructor that disallows control over auto-requery. As an alternative,
     * use {@link LoaderManager}/{@link android.support.v4.app.LoaderManager}
     * with a {@link CursorLoader}/{@link android.support.v4.content.CursorLoader}
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public MovieCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected void convert(MovieHolder holder, Cursor cursor) {
        if(cursor!=null){
            holder.textView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME)));
        }
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(inflateItemView(R.layout.item_simple,parent));
    }

    class MovieHolder extends BaseViewHolder{
        TextView textView;
        public MovieHolder(View view) {
            super(view);
            textView = findViewById(R.id.simple_text);
        }
    }
}
