package com.example.conor.sunshine.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SunshineSyncService extends Service {
    private static final Object SYNC_ADAPTER_LOCK = new Object();
    private static SunshineSyncAdapter SYNC_ADAPTER = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (SYNC_ADAPTER_LOCK) {
            if (SYNC_ADAPTER == null) {
                SYNC_ADAPTER = new SunshineSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return SYNC_ADAPTER.getSyncAdapterBinder();
    }
}