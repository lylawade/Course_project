package gmu.cs.cs477.courseproject;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Date;

import static gmu.cs.cs477.courseproject.Constants.*;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
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
        refreshLayout.setColorSchemeColors(Color.rgb(135, 206, 250), Color.rgb(135, 206, 235), Color.rgb(0, 191, 255));
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

    private void loadData() {
        new GPSLocator(this, new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                PostsLoader loader = new PostsLoader();
                loader.execute();
            }
        }, new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }).execute();
    }

    /**
     * An AsyncTask class to retrieve and load listview with posts
     */
    private class PostsLoader extends AsyncTask<Void, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            if (!Utils.isInternetEnabled(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                this.cancel(true);
                return;
            }
        }

        // Get Posts
        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            ArrayList<Post> posts = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                posts.add(new Post(i, "Post number: " + i, new Date()));
            }
            return posts;
        }

        // Update the list view
        @Override
        protected void onPostExecute(@NonNull final ArrayList<Post> result) {
            adapter = new PostAdapter(result);
            postsList.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
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
