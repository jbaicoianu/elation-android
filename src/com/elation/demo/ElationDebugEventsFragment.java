package com.elation.demo;

//import android.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.database.DataSetObserver;
//import android.webkit.ConsoleMessage;
import java.util.ArrayList;

public class ElationDebugEventsFragment extends android.support.v4.app.Fragment {
  private ListView eventsList;
  private ArrayAdapter eventsListAdapter;
  private ArrayList<ElationEvent> events;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.debug_events_fragment, container, false);
    eventsList = (ListView) view.findViewById(R.id.debug_events_list);

    final Context context = getActivity();
    if (eventsListAdapter == null) {
      // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
      ElationWebView webview = ((ElationDemoActivity) getActivity()).getWebView();
      events = webview.getElationEventsList();
      eventsListAdapter = new ElationDebugEventsAdapter(getActivity(), R.layout.debug_events_message, events);
      
/*
      eventsListAdapter.registerDataSetObserver(new DataSetObserver() {
        @Override
        public void onChanged() {
          Toast.makeText(context, "fuck", Toast.LENGTH_SHORT).show();
        }
      });
*/

      webview.setElationEventsAdapter(eventsListAdapter);
    }
    eventsList.setAdapter(eventsListAdapter);
    return view;
  }
}

