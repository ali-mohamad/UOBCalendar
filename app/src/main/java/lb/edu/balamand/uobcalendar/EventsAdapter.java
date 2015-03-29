package lb.edu.balamand.uobcalendar;

import android.text.format.DateUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 3/29/2015.
 */
public class EventsAdapter {

    private static List<Event> events;

    public static void setEvents(List<Event> events) {
        EventsAdapter.events = events;
    }

    public static List<Event> getEvents() {
        return events;
    }

    private void init(){

    }

    public static boolean getCheckAvailability(Calendar date) {
        if(events == null)
        return false;
        else {
            SimpleDateFormat sdp = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
            for(Event v:events) {
                Date c = sdp.parse(v.get_from_date(), new ParsePosition(0));
                if(c != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(c);
                    if(Utils.isSameDay(cal,date)) return true;
                }
                else continue;
            }
            return false;
        }
    }


}
