package com.elation.demo;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides fragments implementing the Observer interface with methods to register/deregister/notify
 * Relies on the assumption that all Observing fragments for a given Observable will be the same class
 */
public class AdapterObservable {
    private static AdapterObservable _adapterObservable;
    private ConcurrentHashMap<String, Observable> mObservables;

    private class ElationEventObservable extends Observable {
        public void changed() {
            setChanged();
        }
    }

    public static AdapterObservable newInstance() {
        if (_adapterObservable == null) {
            _adapterObservable = new AdapterObservable();
            _adapterObservable.mObservables = new ConcurrentHashMap<String, Observable>();
        }
        return _adapterObservable;
    }

    public void register(Observer observer) {
        String key = observer.getClass().getName();
        Observable observable;
        if (mObservables.containsKey(key)) {
            observable = mObservables.get(key);
            observable.addObserver(observer);
            Log.i(this.getClass().getName(), "Adding to Observable for:" + key);
        } else {
            observable = new ElationEventObservable();
            observable.addObserver(observer);
            mObservables.put(key, observable);
            Log.i(this.getClass().getName(), "new Observable for:" + key);
        }
        Log.i(this.getClass().getName(), "Registered " + observer.toString() + " " + key);
        Log.i(this.getClass().getName(), "Observers:" + mObservables.size());
    }

    public boolean unregister(Observer observer) {
        String key = observer.getClass().getName();
        if (mObservables.containsKey(key)) {
            mObservables.get(key).deleteObserver(observer);
            Log.i(this.getClass().getName(), "Unegistered " + observer.toString() + " " + key);
            Log.i(this.getClass().getName(), "Observers:" + mObservables.size());
            return true;
        } else return false;
    }

    /*
     * notifyObservers() == notifyObservers(null)
     *
     * Overloading, for great justice
     */
    public boolean notify(Class observable) {
        return notify(observable, null);
    }

    public boolean notify(Class observable, Object o) {
        /*
         * Class will always return a valid string
         */
        String key = observable.getName();
        if (observable == null || !mObservables.containsKey(key)) {
            Log.i(this.getClass().getName(), "No Observable registered for:" + key);
            return false;
        } else if (mObservables.get(key).countObservers() > 0) {
            ElationEventObservable mo = (ElationEventObservable) mObservables.get(key);
            mo.changed();
            mo.notifyObservers(o);
            Log.i(this.getClass().getName(), "Notified Observers:" + key + " with event " + o.toString());
            return true;
        }
        Log.i(this.getClass().getName(), "No Observers to Notify");
        return false;
    }
}
