package com.elation.demo;

import java.util.Observable;
import java.util.Observer;

/**
 * Demonstrates how to register for Elation events from a Java class
 */
public class ElationEventHandler implements Observer {
    @Override
    public void update(Observable observable, Object data) {
        ElationEvent event = (ElationEvent) data;
        if (event.type.equals("myEvent")) {
            // DO SOMETHING
            System.out.println("myEvent");
        } else if (event.type.equals("myOtherEvent")) {
            // DO SOMETHING
            System.out.println("myOtherEvent");
        } else if (event.type.equals("yetAnotherEvent")) {
            // DO SOMETHING
            System.out.println("yetAnotherEvent");
        }
    }

    public ElationEventHandler() {
        ElationEventObservable observable = ElationEventObservable.newInstance();
        observable.register("myEvent", this);
        observable.register("myOtherEvent", this);
        observable.register("yetAnotherEvent", this);
    }

    @Override
    protected void finalize() throws Throwable {
        ElationEventObservable observable = ElationEventObservable.newInstance();
        observable.unregister("myEvent", this);
        observable.unregister("myOtherEvent", this);
        observable.unregister("yetAnotherEvent", this);
        super.finalize();
    }
}
