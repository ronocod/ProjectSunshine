package com.example.conor.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.conor.sunshine.R;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, new DetailFragment())
                    .commit();
        }
    }
}
