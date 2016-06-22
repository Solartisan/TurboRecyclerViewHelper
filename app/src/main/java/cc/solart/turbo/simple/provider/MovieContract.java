package cc.solart.turbo.simple.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    // Content authority is a name for the entire content provider
    // similar to a domain and its website. This string is guaranteed to be unique.
    public static final String CONTENT_AUTHORITY = "cc.solart.turbo.simple";

    // Use the content authority to provide the base
    // of all URIs
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path for URI to a movie
    public static final String PATH_MOVIE = "movie";

    /**
     * Class that defines the schema of the Movie table.
     */
    public static final class MovieEntry implements BaseColumns {
        // Uri to access all movies
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // MIME types of Movie queries
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_MOVIE;

        // Schema
        public static final String TABLE_NAME = "movieTable";
        public static final String COLUMN_NAME = "movieName";

        // Builds a URI for an individual movie.
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
