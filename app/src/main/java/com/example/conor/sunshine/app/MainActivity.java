package com.example.conor.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.conor.sunshine.R;

public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FORECAST_LIST_FRAGMENT_TAG = "ForecastListFragment";
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        location = Utility.getPreferredLocation(this);

        MediaStore.Audio.Media.INTERNAL_CONTENT_URI

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastListFragment(), FORECAST_LIST_FRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String latestLocation = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (latestLocation != null && !latestLocation.equals(location)) {
            ForecastListFragment fragment = (ForecastListFragment) getSupportFragmentManager().findFragmentByTag(FORECAST_LIST_FRAGMENT_TAG);
            if (null != fragment) {
                fragment.onLocationChanged();
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
            Uri geoUri = Uri.parse("geo:0,0").buildUpon().appendQueryParameter("q", location).build();
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

}
