package com.elation.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides classes implementing the Observer interface with methods to register/deregister/notify
 * for events by name
 */
public class ElationEventObservable {
    public static final String COM_ELATION_EVENT = "com.elation.event.";
    private static com.elation.demo.ElationEventObservable _elationEventObservable;
    private ConcurrentHashMap<String, Observable> mObservables;

    public static com.elation.demo.ElationEventObservable newInstance() {
        if (_elationEventObservable == null) {
            _elationEventObservable = new com.elation.demo.ElationEventObservable();
            _elationEventObservable.mObservables = new ConcurrentHashMap<String, Observable>();
        }
        return _elationEventObservable;
    }

    /*
     * Constructs intent from Elation event with intent action == com.elation.event.<type>
     */
    public static void sendEventIntent(Context context, ElationEvent event) {
        Intent intent = new Intent(COM_ELATION_EVENT + event.type);
        intent.putExtra("target", event.target);
        if (event.hasData())
            intent.putExtra("dataRaw", event.dataRaw);
        context.sendBroadcast(intent);
    }

    /*
     * Constructs Elation event from intent created with sendEventIntent
     */
    public static ElationEvent eventFromIntent(Intent intent) {
        ElationEvent event = new ElationEvent("");
        event.type = intent.getAction().replace(COM_ELATION_EVENT, "");
        event.target = intent.getStringExtra("target");
        event.dataRaw = intent.getStringExtra("dataRaw");
        return event;
    }

    /*
     *  Convenience method for registering for Elation events as actions
     *
     *  Allows ArrayList of events to register
     */
    public static void registerElationEventReceiver(Context context, BroadcastReceiver receiver, ArrayList<String> events) {
        IntentFilter filter = new IntentFilter();
        for (String event : events) {
            filter.addAction(COM_ELATION_EVENT + event);
        }

        context.registerReceiver(receiver, filter);
    }

    /*
    *  Convenience method for registering for Elation event as action
    */
    public static void registerElationEventReceiver(Context context, BroadcastReceiver receiver, String event) {
        context.registerReceiver(receiver, new IntentFilter(COM_ELATION_EVENT + event));
    }

    public void register(String key, Observer observer) {
        Observable observable;
        if (mObservables.containsKey(key)) {
            observable = mObservables.get(key);
            observable.addObserver(observer);
            Log.d(this.getClass().getName(), "Adding to Observable for:" + key);
        } else {
            observable = new EventObservable();
            observable.addObserver(observer);
            mObservables.put(key, observable);
            Log.d(this.getClass().getName(), "new Observable for:" + key);
        }
        Log.d(this.getClass().getName(), "Registered " + observer.toString() + " " + key);
        Log.d(this.getClass().getName(), "Observables:" + mObservables.size());
    }

    public boolean unregister(String key, Observer observer) {
        if (mObservables.containsKey(key)) {
            mObservables.get(key).deleteObserver(observer);
            Log.d(this.getClass().getName(), "Unegistered " + observer.toString() + " " + key);
            Log.d(this.getClass().getName(), "Observables:" + mObservables.size());
            return true;
        } else return false;
    }

    /*
     * notifyObservers() == notifyObservers(null)
     *
     * Overloading, for great justice
     */
    public boolean notify(String key) {
        return notify(key, null);
    }

    public boolean notify(String key, Object event) {
        if (key == null || !mObservables.containsKey(key)) {
            Log.d(this.getClass().getName(), "No Observable registered for:" + key);
            return false;
        } else if (mObservables.get(key).countObservers() > 0) {
            EventObservable mo = (EventObservable) mObservables.get(key);
            mo.changed();
            mo.notifyObservers(event);
            Log.d(this.getClass().getName(), "Notified Observers:" + key + " with event " + event.toString());
            return true;
        }
        Log.d(this.getClass().getName(), "No Observers to Notify");
        return false;
    }

    /*
     * Subclassed to get around protected method in Observable
     */
    private class EventObservable extends Observable {
        public void changed() {
            setChanged();
        }
    }
}
