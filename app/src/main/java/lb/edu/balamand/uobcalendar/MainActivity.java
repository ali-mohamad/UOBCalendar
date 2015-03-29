package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private CalendarCardPager myCalendar;
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
               // Toast toast = Toast.makeText(getApplicationContext(),item.getDayOfMonth().toString(), Toast.LENGTH_LONG);
               // toast.show();
            }
        });

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
