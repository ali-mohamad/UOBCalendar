package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private CalendarCardPager myCalendar;
    private ArrayList<Event> mEvent;
    private static String loadUrl = "http://178.62.81.25/calendar/v1/events";
    private ProgressDialog pDialog;
    private String jsonResponse = "";
    private static String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickCellCalendar();
        CalendarDbAdapter cDbAdapter = new CalendarDbAdapter(this);
        cDbAdapter.initializeDatabase("/data/data/lb.edu.balamand.uobcalendar/databases/");
        cDbAdapter.open();
        mEvent = cDbAdapter.getAllEvents();
        EventsAdapter.setEvents(mEvent);

        cDbAdapter.close();

        myCalendar.getCardPagerAdapter().notifyDataSetChanged();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading events...");
        pDialog.setCancelable(false);

        makeJsonArrayRequest();

    }

    private void makeJsonArrayRequest() {

        showpDialog();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,loadUrl,
               null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray events = (JSONArray) response.getJSONArray("events");
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject event = (JSONObject) events.getJSONObject(i);
                                mEvent.add(new Event(
                                        event.getString("title"),
                                        event.getString("description"),
                                        event.getString("thumb"),
                                        event.getString("from_date"),
                                        event.getString("to_date"),
                                        event.getInt("isHoliday")
                                ));

                            }
                            CalendarDbAdapter cDbAdapter = new CalendarDbAdapter(getApplicationContext());
                            cDbAdapter.initializeDatabase("/data/data/lb.edu.balamand.uobcalendar/databases/");
                            cDbAdapter.open();


                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();

        }

    }

    private void setupClickCellCalendar(){
       // myCalendar = (CalendarCard)findViewById(R.id.calendarCard1);
        myCalendar = ((CalendarCardPager)findViewById(R.id.calendarCard1));
        myCalendar.setOnCellItemClick(new OnCellItemClick() {
            @Override
            public void onCellClick(View v, CardGridItem item) {
                ArrayList<Event> intentEvents = getListofEvents(item);

                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra("LIST_EVENTS", intentEvents);
                intent.putExtra("TEXT_DAY",String.valueOf(item.getDate().get(Calendar.DAY_OF_MONTH)));
                intent.putExtra("TEXT_MONTH",item.getDate().getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.US));
                startActivity(intent);
            }
        });

}


    private ArrayList<Event> getListofEvents(CardGridItem item) {
        ArrayList<Event> v = new ArrayList<Event>();
        SimpleDateFormat sdp = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        for(Event event:mEvent){
            Date c = sdp.parse(event.get_from_date(), new ParsePosition(0));
            Calendar c2 = Calendar.getInstance();
            c2.setTime(c);
            if(Utils.isSameDay(item.getDate(),c2)) v.add(event);
        }
        return v;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_today:
                myCalendar.setCurrentItem(0);
                return true;
            case R.id.reload:
                reloadEvents();
                makeJsonArrayRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void reloadEvents() {
        mEvent.clear();
        CalendarDbAdapter cDbAdapter = new CalendarDbAdapter(this);
        cDbAdapter.initializeDatabase("/data/data/lb.edu.balamand.uobcalendar/databases/");
        cDbAdapter.open();
        mEvent = cDbAdapter.getAllEvents();
        EventsAdapter.setEvents(mEvent);

        cDbAdapter.close();

        myCalendar.getCardPagerAdapter().notifyDataSetChanged();
    }


}
