package com.elation.demo;

import java.util.Observable;
import java.util.Observer;

/**
 * Demonstrates how to register for Elation events from a Java class
 */
public class ElationEventHandler implements Observer {
    @Override
    public void update(Observable observable, Object data) {
        /*
         *  Simply cast the Observable interface's data as an ElationEvent
         */
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
        ElationEventManager manager = ElationEventManager.newInstance();
        manager.register("myEvent", this);
        manager.register("myOtherEvent", this);
        manager.register("yetAnotherEvent", this);
    }

    @Override
    protected void finalize() throws Throwable {
        ElationEventManager manager = ElationEventManager.newInstance();
        manager.unregister("myEvent", this);
        manager.unregister("myOtherEvent", this);
        manager.unregister("yetAnotherEvent", this);
        super.finalize();
    }
}
