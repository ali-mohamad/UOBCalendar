package lb.edu.balamand.uobcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 3/27/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

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

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," +
                KEY_DESCRIPTION + " TEXT," + KEY_THUMB + " TEXT, " + KEY_FROM_DATE + " TEXT," + KEY_TO_DATE + " TEXT," + KEY_IS_HOLIDAY +
                " INT" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.get_title());
        values.put(KEY_DESCRIPTION, event.get_description());
        values.put(KEY_THUMB, event.get_thumb());
        values.put(KEY_FROM_DATE, event.get_from_date());
        values.put(KEY_TO_DATE, event.get_to_date());
        values.put(KEY_IS_HOLIDAY, event.get_isHoliday());

        db.insert(TABLE_EVENTS,null,values);
        db.close();
    }
     public Event getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

         Cursor cursor = db.query(TABLE_EVENTS , new String[] {
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

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updateEvent(Event event) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.get_title());
        values.put(KEY_DESCRIPTION, event.get_description());
        values.put(KEY_THUMB, event.get_thumb());
        values.put(KEY_FROM_DATE, event.get_from_date());
        values.put(KEY_TO_DATE, event.get_to_date());
        values.put(KEY_IS_HOLIDAY, event.get_isHoliday());

        // updating row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(event.get_id()) });
    }

    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(event.get_id()) });
        db.close();
    }
}
