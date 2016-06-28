package cc.solart.turbo.simple.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int MOVIE = 0;
    private static final int MOVIE_ID = 1;
    private MyDatabaseHelper mDatabaseHelper;


    private static UriMatcher buildUriMatcher() {
        String content = MovieContract.CONTENT_AUTHORITY;
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(content, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(content, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MyDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:
                long _id = ContentUris.parseId(uri);
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        assert getContext() != null;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
