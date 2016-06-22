package cc.solart.turbo.simple.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL);"
        );

        // Insert sample data
        db.execSQL(
                "INSERT INTO " + MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_NAME + ") VALUES " +
                        "('独立日：卷土重来'), ('X战警：天启'), ('魔兽'), ('速度与激情8'), ('美国队长3'), ('钢铁侠2'), ('爱丽丝梦游仙境')," +
                        "('幻体：续命游戏'), ('愤怒的小鸟'), ('国家利益'), ('荒野猎人'), ('云中行走'), ('极盗者'), ('火星救援');"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
