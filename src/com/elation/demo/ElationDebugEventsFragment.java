package com.elation.demo;

//import android.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

//import android.webkit.ConsoleMessage;

public class ElationDebugEventsFragment extends android.support.v4.app.Fragment implements Observer {
    private ListView eventsList;
    private ArrayAdapter eventsListAdapter;
    private ArrayList<ElationEvent> events;
    private ElationWebView webview;
    private EventStore eventStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debug_events_fragment, container, false);
        eventsList = (ListView) view.findViewById(R.id.debug_events_list);

        final Context context = getActivity();
        eventStore = (EventStore) context.getApplicationContext();
        if (eventsListAdapter == null) {
            // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
            webview = ((ElationDemoActivity) getActivity()).getWebView();
            events = new ArrayList<ElationEvent>();
            events.addAll(eventStore.getElationEventsList());
            eventsListAdapter = new ElationDebugEventsAdapter(getActivity(),R.layout.debug_events_message, events);
        }
        webview.mElationEventObservable.register(ElationDebugEventsFragment.class.getName(), this);
        eventsList.setAdapter(eventsListAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        webview.mElationEventObservable.unregister(ElationDebugEventsFragment.class.getName(), this);
        super.onPause();
    }

    @Override
    public void update(Observable observable, Object data) {
        final ElationEvent event = (ElationEvent) data;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                events.add(event);
                eventsListAdapter.notifyDataSetChanged();
            }
        });
    }
}

