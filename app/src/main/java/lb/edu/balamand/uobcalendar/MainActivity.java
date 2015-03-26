package lb.edu.balamand.uobcalendar;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends Activity implements OnItemClickListener {

    ListView listView;
    List<Item> arrayOfList;
    private static final String rssFeed = "https://dl.dropboxusercontent.com/u/63596028/ProjectNFC/listdata.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setTitle(R.string.title);

        //listView = (ListView) findViewById(R.id.listview);
        //listView.setOnItemClickListener(this);

        //LoadItemsAsync();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void LoadItemsAsync() {
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            new MyTask().execute(rssFeed);
        } else {
            showToast("No Network Connection!!!");
        }
    }
    public void showToast(String msg) {

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = arrayOfList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("url", item.getLink());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("desc", item.getDesc());
        startActivity(intent);
    }

    public void setAdapterToListview() {
        NewsRowAdapter objAdapter = new NewsRowAdapter(MainActivity.this,
                R.layout.row, arrayOfList);
        listView.setAdapter(objAdapter);
    }

    class MyTask extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            arrayOfList = new NamesParser().getData(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == arrayOfList || arrayOfList.size() == 0) {
                showToast("No data found from web!!!");
                MainActivity.this.finish();
            } else {
                setAdapterToListview();
            }

        }
    }
}
