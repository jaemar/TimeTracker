package limex.timetracker.com.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import limex.timetracker.com.models.Tracker;

/**
 * Created by limex on 9/16/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String TAG = DatabaseHelper.class.getCanonicalName();
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "timetracker.db";
    private static final String COLUMN_KEY_ID = "id";

    private static final String TABLE_TRACKER = "trackers";
    private static final String COLUMN_DATE = "logged_at";
    private static final String COLUMN_IN = "log_in";
    private static final String COLUMN_OUT = "log_out";

    private static final String CREATE_TRACKER_TABLE = "CREATE TABLE " +
            TABLE_TRACKER + "("
            + COLUMN_KEY_ID + " INTEGER PRIMARY KEY, " + COLUMN_DATE
            + " TEXT, " + COLUMN_IN + " TEXT, " + COLUMN_OUT
            + " TEXT " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRACKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void loggedIn() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, getDate());
        values.put(COLUMN_IN , getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TRACKER, null, values);
        db.close();
    }

    public void loggedOut(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OUT, getTime());

        db.update(TABLE_TRACKER, values, "id = ?", new String[]{Integer.toString(id)});
        db.close();
    }

    public Boolean checkIfNewDay() {
        Boolean new_day = true;

        StringBuffer query = new StringBuffer();
        query.append("Select * from ")
                .append(TABLE_TRACKER)
                .append(" where ")
                .append(COLUMN_DATE)
                .append(" = '")
                .append(getDate())
                .append("'");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), null);

        if (cursor.moveToFirst()) {
            new_day = false;
        }

        db.close();
        return new_day;
    }

    public int getId() {
        int id = 0;

        StringBuffer query = new StringBuffer();
        query.append("Select * from ")
                .append(TABLE_TRACKER)
                .append(" where ")
                .append(COLUMN_DATE)
                .append(" = '")
                .append(getDate())
                .append("'");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return id;
    }

    public List<Tracker> getTrackerList() {
        List<Tracker> trackerList = new ArrayList<Tracker>();
        String query = "Select * from " + TABLE_TRACKER
                + " order by " + COLUMN_KEY_ID + " desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Tracker list = new Tracker();
                    list.date = cursor.getString(1);
                    list.in = cursor.getString(2);
                    list.out = cursor.getString(3);
                    trackerList.add(list);
                }while (cursor.moveToNext());
            }
        } else {
            trackerList = null;
        }

        db.close();
        return trackerList;
    }

    public Boolean checkIfAlreadyLoggedIn() {
        Boolean logged_in = false;
        StringBuffer query = new StringBuffer();
        query.append("Select * from ")
                .append(TABLE_TRACKER)
                .append(" where ")
                .append(COLUMN_DATE)
                .append(" = '")
                .append(getDate())
                .append("'")
                .append(" and ")
                .append(COLUMN_IN)
                .append(" IS NULL");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), null);

        if (cursor.moveToFirst()) {
            logged_in = true;
        }

        db.close();
        return logged_in;
    }

    public Boolean checkIfAlreadyLoggedOut() {
        Boolean logged_out = false;
        StringBuffer query = new StringBuffer();
        query.append("Select * from ")
                .append(TABLE_TRACKER)
                .append(" where ")
                .append(COLUMN_DATE)
                .append(" = '")
                .append(getDate())
                .append("'")
                .append(" and ")
                .append(COLUMN_OUT)
                .append(" IS NULL");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), null);

        if (cursor.moveToFirst()) {
            logged_out = false;
        } else {
            logged_out = true;
        }

        db.close();
        return logged_out;
    }

    public void removeRecord(String date) {
        StringBuffer query = new StringBuffer();
        query.append("delete from ")
                .append(TABLE_TRACKER)
                .append(" where ")
                .append(COLUMN_DATE)
                .append(" = '")
                .append(date)
                .append("'");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query.toString());
        db.close();
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_TRACKER);
        db.close();
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
