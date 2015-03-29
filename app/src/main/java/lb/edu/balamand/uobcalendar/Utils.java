package lb.edu.balamand.uobcalendar;

/**
 * Created by Lenovo on 3/25/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

public class Utils {
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSameDay(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) return true;
        else return false;
    }

    public static boolean isSameDay(CardGridItem i, Calendar c) {
        if ((i.getDayOfMonth() == c.get(Calendar.DAY_OF_MONTH)) &&
                (i.getDate().get(Calendar.MONTH) == c.get(Calendar.MONTH)) &&
                (i.getDate().get(Calendar.YEAR) == c.get(Calendar.YEAR))) {
            return true;
        } else return false;
    }
}
