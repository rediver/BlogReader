package pl.wojteksmajda.blogreader;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainListActivity extends ListActivity {

//    protected String[] mBlogPostNames = {
//           "Lollypop",
//            "KitKat",
//            "Ice cream sandwith"
//    };

    protected String[] mBlogPostNames;
    public static final int NUM_OF_POSTS = 20;
    public static final String TAG = MainListActivity.class.getSimpleName();
    //duze litery zmiennych trzymaja constant values - KONWENCJA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

//      odpalenie async tasku
        if (isNetworkReady()) {
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        }


//        Resources resources = getResources();
//        mBlogPostNames = resources.getStringArray(R.array.android_names);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostNames);
//        setListAdapter(adapter);

        //Toast.makeText(this, LENGTH_LONG).show();
    }

    private boolean isNetworkReady() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvalible = false;
//      != i nie jest null
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvalible = true;
        } else {
            Toast.makeText(this, "Network is unavalible!", Toast.LENGTH_LONG).show();
        }
        return isAvalible;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    asynchroniczny task

      private class GetBlogPostsTask extends AsyncTask<Object, Void, String> {

          @Override
          protected String doInBackground(Object... objects) {

             int responseCode = -1;

             try {
                  URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUM_OF_POSTS);
//                Bad way, http connection
                  HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
                  connection.connect();
//                ustanowienie polaczenia
                  responseCode = connection.getResponseCode();

                 if (responseCode == HttpURLConnection.HTTP_OK) {

                   InputStream inputStream = connection.getInputStream();
                   Reader reader = new InputStreamReader(inputStream);
                   int contentLength = connection.getContentLength();
                   char[] charArray = new char[contentLength];
                   reader.read(charArray);

                   String responseData;
                   responseData = new String(charArray);

                   Log.v(TAG, responseData);

//                 utworzenie obiektu JSON
                   JSONObject jsonResponse = new JSONObject(responseData);
                   String status = jsonResponse.getString("status");

                   Log.v(TAG, status);

                   JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
                   for (int i = 0; i < jsonPosts.length(); i++ ) {
                       JSONObject jsonPost = jsonPosts.getJSONObject(i);
                       String title = jsonPost.getString("title");
                       Log.v(TAG, "Post " + i + ": " + title);
                   }



                 } else {
                   Log.i(TAG, "unsuccessful code:" + responseCode);
                 }

                  Log.i(TAG, "Code " + responseCode);

              }
              catch (MalformedURLException e) {
                  Log.e(TAG, "Exeption ..", e);
              }
              catch (IOException e) {
                  Log.e(TAG, "Exeption ..", e);
              }
              catch (Exception e) {
                  Log.e(TAG, "Exeption ..", e);
              }
              return "Code: " + responseCode;
          }

      }

}
