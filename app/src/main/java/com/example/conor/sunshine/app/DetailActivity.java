package com.example.conor.sunshine.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.conor.sunshine.R;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        String forecastString = getIntent().getDataString();

        if (savedInstanceState == null) {
            Uri itemUri = Uri.parse(forecastString);
            ForecastDetailFragment forecastDetailFragment = ForecastDetailFragment.create(itemUri);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, forecastDetailFragment)
                    .commit();
        }

    }
}
