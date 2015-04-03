package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 3/29/2015.
 */
public class EventActivity extends Activity {


    ListView mListView;

    ArrayList<Event> mEventsList;
    EventListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_list);

        Intent i = getIntent();
        mEventsList = (ArrayList<Event>) i.getSerializableExtra("LIST_EVENTS");

        TextView day = (TextView)findViewById(R.id.Day);
        TextView month = (TextView)findViewById(R.id.Month);

        day.setText(i.getStringExtra("TEXT_DAY"));
        month.setText(i.getStringExtra("TEXT_MONTH"));

        ListView mListView = (ListView) findViewById(R.id.eventlistview);
        adapter= new EventListAdapter(this,mEventsList);
        mListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        //Toast.makeText(this, mEventsList.get(0).get_title(), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
