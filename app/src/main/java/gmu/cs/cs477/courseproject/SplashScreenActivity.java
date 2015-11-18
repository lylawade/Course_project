package gmu.cs.cs477.courseproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity implements GPSClient {
    boolean created = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        ImageView iv = (ImageView) findViewById(R.id.loadingAnimation);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkGPS();
            }
        }, 1000);
    }

    private void checkGPS() {
        new GPSLocator(getApplicationContext(), this).execute();
    }

    private void leave() {
        Intent intent = new Intent(this, PostsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (created) {
            checkGPS();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGPSDisabled() {
        new AlertDialog.Builder(SplashScreenActivity.this)
                .setMessage("GPS is disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        created = true;
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.isGPSEnabled(SplashScreenActivity.this)) {
                            checkGPS();
                        } else {
                            leave();
                        }
                    }
                }).show();
    }

    @Override
    public void onGPSEnabled() {
    }

    @Override
    public void onLocationFound() {
        leave();
    }

    @Override
    public void onLocationNotFound() {
        leave();
    }
}
