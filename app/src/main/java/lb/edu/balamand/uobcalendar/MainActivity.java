package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.os.Bundle;
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
        setToday();
        DatabaseHandler db = new DatabaseHandler(this);


        if(!getDatabasePath(db.getDatabaseName()).exists()) {

            Event e = new Event();
            e.set_title("test");
            e.set_description("desc");
            e.set_from_date("1");
            e.set_to_date("2");
            e.set_isHoliday(0);

            db.addEvent(e);
        }


        List<Event> v = db.getAllEvents();

        int nofEv = v.size();
        String numberOfEvent = String.valueOf(nofEv);
        Toast.makeText(getApplicationContext(), numberOfEvent , Toast.LENGTH_LONG).show();


    }

    private void setToday() {

    }

    private void setupClickCellCalendar(){
       // myCalendar = (CalendarCard)findViewById(R.id.calendarCard1);
        myCalendar = ((CalendarCardPager)findViewById(R.id.calendarCard1));
        myCalendar.setOnCellItemClick(new OnCellItemClick() {
            @Override
            public void onCellClick(View v, CardGridItem item) {
                Toast toast = Toast.makeText(getApplicationContext(),item.getDayOfMonth().toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}
