package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 3/29/2015.
 */
public class EventListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Event> eventItems;

    public EventListAdapter(EventActivity eventActivity, ArrayList<Event> mEventsList) {
        this.activity = eventActivity;
        this.eventItems = mEventsList;
    }

    @Override
    public int getCount() {
        return eventItems.size();
    }

    @Override
    public Object getItem(int position) {
        return eventItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_item, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView time = (TextView) convertView.findViewById(R.id.dateTime);

        // getting event data for the row
        Event m = eventItems.get(position);

        // title
        title.setText(m.get_title());

        // description
        description.setText(m.get_description());

        // time
        if(m.get_isHoliday() == 1) time.setText("");
        else
        time.setText(String.valueOf(m.get_from_date()));

        return convertView;
    }
}
