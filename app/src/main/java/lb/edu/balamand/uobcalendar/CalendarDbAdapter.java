package lb.edu.balamand.uobcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 3/29/2015.
 */
public class CalendarDbAdapter extends SQLiteOpenHelper {

    private String DATABASE_PATH = "/data/data/lb.edu.balamand.uobcalendar/";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbCalendar";
    private static final String TABLE_EVENTS = "events";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_THUMB = "thumb";
    private static final String KEY_FROM_DATE = "from_date";
    private static final String KEY_TO_DATE = "to_date";
    private static final String KEY_IS_HOLIDAY = "isHoliday";

    private SQLiteDatabase mDb;


    private final Context mContext;

    private boolean mCreateDatabase = false;
    private boolean mUpgradeDatabase = true;

    public CalendarDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.calendarDbVersion));
        this.mContext = context;
    }


    public void initializeDatabase(String path) {
        DATABASE_PATH = path;
        getWritableDatabase();

        if(mUpgradeDatabase) {
            mContext.deleteDatabase(DATABASE_NAME);
        }

        if(mCreateDatabase || mUpgradeDatabase) {
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    private void copyDatabase() throws IOException {
        close();

        InputStream input = mContext.getAssets().open(DATABASE_NAME);

        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream output = new FileOutputStream(outFileName);

        // Transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();

        getWritableDatabase().close();
    }

    public CalendarDbAdapter open() throws SQLException {
        mDb = getReadableDatabase();
        return this;
    }

    public void CleanUp() {
        mDb.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        mCreateDatabase = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgradeDatabase = true;
    }

    /**
     * Public helper methods
     */

    public Event getEvent(int id){


        Cursor cursor = mDb.query(TABLE_EVENTS , new String[] {
                        KEY_ID,
                        KEY_TITLE,
                        KEY_DESCRIPTION,
                        KEY_THUMB,
                        KEY_FROM_DATE,
                        KEY_TO_DATE,
                        KEY_IS_HOLIDAY }, KEY_ID + "=?" ,new String[]{
                        String.valueOf(id)  },null,null,null,null
        );
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Event event = new Event(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                Integer.parseInt(cursor.getString(6)));
        return event;
    }

    public ArrayList<Event> getAllEvents(Calendar cal){
        ArrayList<Event> v = new ArrayList<Event>();
        List<Event> lv = getAllEvents();
        SimpleDateFormat sdp = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        for(Event event:lv){
            Date c = sdp.parse(event.get_from_date(), new ParsePosition(0));
            Calendar c2 = Calendar.getInstance();
            c2.setTime(c);
            if(Utils.isSameDay(cal,c2)) v.add(event);
        }
        return v;
    }
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

       // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.set_id(Integer.parseInt(cursor.getString(0)));
                event.set_title(cursor.getString(1));
                event.set_description(cursor.getString(2));
                event.set_thumb(cursor.getString(3));
                event.set_from_date(cursor.getString(4));
                event.set_to_date(cursor.getString(5));
                event.set_isHoliday(Integer.parseInt(cursor.getString(6)));
                // Adding event to list
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        // return event list
        return eventList;
    }

    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
