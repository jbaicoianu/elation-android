package com.elation.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ElationDebugEventsAdapter extends ArrayAdapter<ElationEvent> {

    private Context context;

    public ElationDebugEventsAdapter(Context context, int textViewResourceId, ArrayList<ElationEvent> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.debug_events_message, null);
        }

        ElationEvent event = getItem(position);
        if (event != null) {
            // My layout has only one TextView
            TextView eventType = (TextView) view.findViewById(R.id.event_type);
            if (eventType != null) {
                eventType.setText(event.type);
            }
            TextView eventTarget = (TextView) view.findViewById(R.id.event_target);
            if (eventTarget != null) {
                if (event.target != null) {
                  eventTarget.setText(event.target);
                }
            }
            TextView eventData = (TextView) view.findViewById(R.id.event_data);
            if (eventData != null) {
                if (event.hasData()) {
                  eventData.setText(event.dataRaw);
                } else {
                  eventData.setText("(no data)");
                }
            }
         }

        return view;
    }
}


