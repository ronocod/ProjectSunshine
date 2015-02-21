package com.example.conor.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        String forecastString = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (savedInstanceState == null) {
            ForecastDetailFragment forecastDetailFragment = new ForecastDetailFragment(forecastString);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, forecastDetailFragment)
                    .commit();
        }

    }
}
