package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private CalendarCardPager myCalendar;
    private ArrayList<Event> mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickCellCalendar();
        CalendarDbAdapter cDbAdapter = new CalendarDbAdapter(this);
        cDbAdapter.initializeDatabase("/data/data/lb.edu.balamand.uobcalendar/databases/");
        cDbAdapter.open();
        EventsAdapter.setEvents(cDbAdapter.getAllEvents());

        cDbAdapter.close();

        myCalendar.getCardPagerAdapter().notifyDataSetChanged();
    }


    private void setupClickCellCalendar(){
       // myCalendar = (CalendarCard)findViewById(R.id.calendarCard1);
        myCalendar = ((CalendarCardPager)findViewById(R.id.calendarCard1));
        myCalendar.setOnCellItemClick(new OnCellItemClick() {
            @Override
            public void onCellClick(View v, CardGridItem item) {

                mEvent = getListofEvents(item);
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra("LIST_EVENTS", mEvent);
                intent.putExtra("TEXT_DAY",String.valueOf(item.getDate().get(Calendar.DAY_OF_MONTH)));
                intent.putExtra("TEXT_MONTH",item.getDate().getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.US));
                startActivity(intent);
            }
        });

}

    private ArrayList<Event> getListofEvents(CardGridItem item) {

        ArrayList<Event> listOfEvents;
        CalendarDbAdapter cDbAdapter = new CalendarDbAdapter(this);
        cDbAdapter.initializeDatabase("/data/data/lb.edu.balamand.uobcalendar/databases/");
        cDbAdapter.open();
        listOfEvents = cDbAdapter.getAllEvents(item.getDate());
        cDbAdapter.close();
        return listOfEvents;
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }






}
