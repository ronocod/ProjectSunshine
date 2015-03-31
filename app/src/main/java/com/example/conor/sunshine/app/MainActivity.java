package com.example.conor.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.conor.sunshine.R;

public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String location;
    private boolean inTwoPaneMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        location = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            inTwoPaneMode = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, DetailFragment.create(getIntent().getData()), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            inTwoPaneMode = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String latestLocation = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (latestLocation != null && !latestLocation.equals(location)) {
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != forecastFragment) {
                forecastFragment.onLocationChanged();
            }
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != detailFragment) {
                detailFragment.onLocationChanged(latestLocation);
            }
            location = latestLocation;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_location) {
            Uri geoUri = Uri.parse("geo:0,0")
                    .buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoUri);
            if (intent.resolveActivity(getPackageManager()) == null) {
                Log.w(LOG_TAG, "No intent handler for uri: " + geoUri);
                Toast.makeText(this, "You don't have an app installed that can view this location", Toast.LENGTH_SHORT).show();
                return true;
            }
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dateUri) {

        if (inTwoPaneMode) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, DetailFragment.create(dateUri), DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }
}
