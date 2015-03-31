package com.example.conor.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conor.sunshine.R;

import static com.example.conor.sunshine.app.data.WeatherContract.LocationEntry;
import static com.example.conor.sunshine.app.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ForecastDetailFragment.class.getSimpleName();

    private static final String[] COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_DEGREES,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LATITUDE,
            LocationEntry.COLUMN_LONGITUDE
    };
    private static final String KEY_URI = "KEY_URI";
    private static final int LOADER_ID = 1;

    private Uri forecastUri;
    private ShareActionProvider shareActionProvider;
    private TextView dayText;
    private TextView dateText;
    private TextView highText;
    private TextView lowText;
    private TextView humidityText;
    private TextView windText;
    private TextView pressureText;
    private TextView descriptionText;
    private ImageView iconView;

    public static ForecastDetailFragment create(Uri itemUri) {
        ForecastDetailFragment fragment = new ForecastDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_URI, itemUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        forecastUri = getArguments().getParcelable(KEY_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast_detail, container, false);
        dayText = (TextView) rootView.findViewById(R.id.detail_day_text);
        dateText = (TextView) rootView.findViewById(R.id.detail_date_text);
        highText = (TextView) rootView.findViewById(R.id.detail_high_text);
        lowText = (TextView) rootView.findViewById(R.id.detail_low_text);
        humidityText = (TextView) rootView.findViewById(R.id.detail_humidity_text);
        windText = (TextView) rootView.findViewById(R.id.detail_wind_text);
        pressureText = (TextView) rootView.findViewById(R.id.detail_pressure_text);
        pressureText = (TextView) rootView.findViewById(R.id.detail_pressure_text);
        descriptionText = (TextView) rootView.findViewById(R.id.detail_description_text);
        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(String forecast) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                forecast + " #Sunshine");
        return shareIntent;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by date.
        return new CursorLoader(getActivity(),
                forecastUri,
                COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        long date = data.getLong(data.getColumnIndex(WeatherEntry.COLUMN_DATE));
        dayText.setText(Utility.getDayName(getActivity(), date));
        dateText.setText(Utility.getFormattedMonthDay(getActivity(), date));

        boolean isMetric = Utility.isMetric(getActivity());
        long high = data.getLong(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP));
        highText.setText(Utility.formatTemperature(getActivity(), high, isMetric));
        long low = data.getLong(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP));
        lowText.setText(Utility.formatTemperature(getActivity(), low, isMetric));

        float humidity = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
        humidityText.setText(getActivity().getString(R.string.format_humidity, humidity));
        float wind = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
        float degrees = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES));
        windText.setText(Utility.getFormattedWind(getActivity(), wind, degrees));
        float pressure = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
        pressureText.setText(getActivity().getString(R.string.format_pressure, pressure));

        String forecastString = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
        descriptionText.setText(forecastString);

        int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent(forecastString));

        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dayText.setText("No Forecast");
    }
}
