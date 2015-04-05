package lb.edu.balamand.uobcalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    private static final String SERVER_URL = "http://178.62.81.25/calendar/admin/register.php";
    private CalendarCardPager myCalendar;
    private ArrayList<Event> mEvent;
    private static String loadUrl = "http://178.62.81.25/calendar/v1/events";
    private ProgressDialog pDialog;
    private String jsonResponse = "";
    private static String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "466209783036";
    GoogleCloudMessaging gcm;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        if(checkPlayService()){
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getApplicationContext());

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        makeJsonArrayRequest();

    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);

                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();
                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }

        }.execute(null, null, null);

    }

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private void sendRegistrationIdToBackend() {
        Log.i(TAG, "registering device (regId = " + regid + ")");
        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regid);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }


    }
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;

    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayService();
    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void makeJsonArrayRequest() {

        showpDialog();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,loadUrl,
               null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray events = (JSONArray) response.getJSONArray("events");
                            for (int i = 0; i < events.length(); i++) {

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
        myCalendar.setOffscreenPageLimit(1);
        myCalendar.setOnCellItemClick(new OnCellItemClick() {
            @Override
            public void onCellClick(View v, CardGridItem item) {

                ArrayList<Event> intentEvents = getListofEvents(item);
                if(intentEvents.size() == 0) return;
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
