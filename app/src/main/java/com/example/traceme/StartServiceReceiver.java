package com.example.traceme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LocationUpdatesService.class);
        context.startService(service);
    }
}
