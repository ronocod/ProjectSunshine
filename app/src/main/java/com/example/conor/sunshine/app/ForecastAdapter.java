package com.example.conor.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conor.sunshine.R;
import com.example.conor.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
    }

    // This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
    // string.
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastListFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastListFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastListFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastListFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = R.layout.list_item_forecast;
        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder holder = (ViewHolder) view.getTag();


        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        int viewType = getItemViewType(cursor.getPosition());
        int resource = Utility.getIconResourceForWeatherCondition(weatherId);
        if (viewType == VIEW_TYPE_TODAY) {
            resource = Utility.getArtResourceForWeatherCondition(weatherId);
        }
        // Use placeholder image for now
        holder.iconView.setImageResource(resource);

        // TODO Read date from cursor
        long date = cursor.getLong(ForecastListFragment.COL_WEATHER_DATE);
        holder.dateView.setText(Utility.formatDate(date));

        // TODO Read weather forecast from cursor
        String forecast = cursor.getString(ForecastListFragment.COL_WEATHER_DESC);
        holder.descriptionView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastListFragment.COL_WEATHER_MAX_TEMP);
        holder.highTempView.setText(Utility.formatTemperature(view.getContext(), high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastListFragment.COL_WEATHER_MIN_TEMP);
        holder.lowTempView.setText(Utility.formatTemperature(view.getContext(), low, isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}