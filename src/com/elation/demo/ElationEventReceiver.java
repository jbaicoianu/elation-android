package com.elation.demo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Demonstrates how to make use of Elation events as Intents
 */
class ElationEventReceiver extends Service {
    public static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ElationEvent event = ElationEventObservable.eventFromIntent(intent);
            if (event.type.equals("myEvent")){
                //DO STUFF
            }
        }
    };

    @Override
    public void onCreate() {
        ElationEventObservable.registerElationEventReceiver(this, receiver, "myEvent");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
