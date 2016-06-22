package cc.solart.turbo.simple;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.solart.turbo.TurboRecyclerView;
import cc.solart.turbo.simple.adapter.MovieCursorAdapter;
import cc.solart.turbo.simple.provider.MovieContract;

public class CursorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private LoaderManager mLoaderManager;
    private MovieCursorAdapter mCursorAdapter;
    private TurboRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor);
        mRecyclerView = (TurboRecyclerView) findViewById(R.id.rv_list);
        mLoaderManager = getSupportLoaderManager();

        mCursorAdapter = new MovieCursorAdapter(this, null);
        View header = LayoutInflater.from(this).inflate(R.layout.item_header, (ViewGroup) mRecyclerView.getParent(), false);
        mCursorAdapter.addHeaderView(header);
        mRecyclerView.setAdapter(mCursorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI, new String[]{
                        MovieContract.MovieEntry._ID,
                        MovieContract.MovieEntry.COLUMN_NAME
                }, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                mCursorAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID:
                mCursorAdapter.swapCursor(null);
                break;
        }
    }
}
