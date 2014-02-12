package com.elation.demo;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Provides fragments implementing the Observer interface with methods to register/deregister/notify
 * Relies on the assumption that all Observing fragments for a given Observable will be the same class
 */
public class AdapterObservable {
    private static AdapterObservable _adapterObservable;
    private HashMap<Object, Observable> mObservables;

    public static AdapterObservable newInstance() {
        if (_adapterObservable == null) {
            _adapterObservable = new AdapterObservable();
            _adapterObservable.mObservables = new HashMap<Object, Observable>();
        }
        return _adapterObservable;
    }

    public void register(Observer observer) {
        if (mObservables.containsKey(observer.getClass()))
            mObservables.get(observer.getClass()).addObserver(observer);
        else {
            Observable o = new Observable();
            o.addObserver(observer);
            mObservables.put(observer.getClass(), o);
        }
    }

    public boolean unregister(Observer observer) {
        if (mObservables.containsKey(observer.getClass())) {
            mObservables.get(observer.getClass()).deleteObserver(observer);
            return true;
        } else return false;
    }

    /*
     * notifyObservers() == notifyObservers(null)
     */
    public boolean notify(Class adapter) {
        return notify(adapter, null);
    }

    public boolean notify(Class adapter, Object o) {
        if (adapter == null || !mObservables.containsKey(adapter))
            return false;
        else
            mObservables.get(adapter).notifyObservers(o);

        return true;
    }
}
