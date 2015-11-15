package gmu.cs.cs477.courseproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    private EditText post;
    private TextView counter;
    private Button postButton;
    private RelativeLayout input_wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        post = (EditText) findViewById(R.id.post_details);
        counter = (TextView) findViewById(R.id.chars_left);
        postButton = (Button) findViewById(R.id.post_button);
        input_wrapper = (RelativeLayout) findViewById(R.id.input_wrapper);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                counter.setText(200 - post.getText().length() + " left");
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Try to post
                String postText = post.getText().toString();
                if (!postText.equals("")) {
                    postText.replace('\n', ' ');
                    onBackPressed();
                    Toast.makeText(getApplicationContext(), "Post created", Toast.LENGTH_SHORT).show();
                }
            }
        });
        post.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!post.getText().toString().equals("")) {
                    new AlertDialog.Builder(CreatePostActivity.this)
                            .setMessage("Do you want to discard post?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    NavUtils.navigateUpFromSameTask(CreatePostActivity.this);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
