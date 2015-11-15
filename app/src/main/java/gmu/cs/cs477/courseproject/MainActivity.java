package gmu.cs.cs477.courseproject;



import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static gmu.cs.cs477.courseproject.Constants.*;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout refreshLayout;
    private ListView postsList;
    private PostAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        postsList = (ListView) findViewById(R.id.postsList);
        refreshLayout.setOnRefreshListener(this);
        fab = (FloatingActionButton) findViewById(R.id.actionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });
        postsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewPostActivity.class);
                intent.putExtra(POST_TEXT, adapter.getPostText(position));
                intent.putExtra(POST_TIME, DateUtils.getRelativeTimeSpanString(adapter.getPostTime(position).getTime()));
                startActivity(intent);
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                loadData();
            }
        });
    }

    @Override
    public void onRefresh() {
            loadData();
    }

    private void loadData(){
        PostsLoader loader = new PostsLoader();
        loader.execute();
    }


    /**
     * An AsyncTask class to retrieve and load listview with posts
     */
    private class PostsLoader extends AsyncTask<Void, Void, ArrayList<Post>> implements LocationListener {
        private Location lastKnownLocation = null;
        private String error = null;
        @Override
        protected void onPreExecute(){
            if (!isGPSEnabled()){
                Toast.makeText(getApplicationContext(), "GPS is disabled", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                this.cancel(true);
                return;
            }

            if (!isInternetEnabled()){
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                this.cancel(true);
                return;
            }

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } catch(SecurityException se){
            }
        }

        private boolean isGPSEnabled(){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        private boolean isInternetEnabled(){
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        // Get Posts
        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            // Wait for GPS location
            Long t = Calendar.getInstance().getTimeInMillis();
            while ( lastKnownLocation== null && Calendar.getInstance().getTimeInMillis()-t<30000) {
                try{ Thread.sleep(100);}
                catch (InterruptedException ie){}
            };

            ArrayList<Post> posts = new ArrayList<>();
            for (int i =0; i < 100; i++){
                posts.add(new Post(i, "Post number: " + i, new Date()));
            }
            return posts;
        }

        // Update the list view
        @Override
        protected void onPostExecute(ArrayList<Post> result) {
            if (error != null){
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                return;
            }
            adapter = new PostAdapter(result);
            postsList.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
            if (lastKnownLocation != null) {
                Toast.makeText(getApplicationContext(), getLastKnownAddress(), Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getApplicationContext(), "Could not retrieve location", Toast.LENGTH_SHORT).show();
            }
        }

        private String getLastKnownAddress(){
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e){
                return "Could not get address";
            }
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            return address + " " + city + ", " + state + ", "  + country + " " + postalCode;
        }


        @Override
        public void onLocationChanged(Location location) {
            lastKnownLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
