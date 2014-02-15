package com.elation.demo;

import android.app.Application;
import android.webkit.ConsoleMessage;

import java.util.ArrayList;

/**
 * Stores events in the Application Context
 */
public class EventStore extends Application {
    private ArrayList<ElationEvent> events = new ArrayList<ElationEvent>();
    private ArrayList<NetworkRequest> networkRequests = new ArrayList<NetworkRequest>();
    private ArrayList<NetworkRequest> networkRequestsPending = new ArrayList<NetworkRequest>();
    private ArrayList<ConsoleMessage> logs = new ArrayList<ConsoleMessage>();

    public ArrayList<ConsoleMessage> getConsoleMessages() {
        return logs;
    }

    public ArrayList<ElationEvent> getElationEventsList() {
        return events;
    }

    public ArrayList<NetworkRequest> getNetworkRequestList() {
        return networkRequests;
    }

    public ArrayList<NetworkRequest> getNetworkRequestPendingList() {
        return networkRequestsPending;
    }
}
