package com.elation.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.webkit.ConsoleMessage;
import java.util.ArrayList;

public class ElationDebugConsoleMessageAdapter extends ArrayAdapter<ConsoleMessage> {

    private Context context;

    public ElationDebugConsoleMessageAdapter(Context context, int textViewResourceId, ArrayList<ConsoleMessage> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.debug_console_message, null);
        }

        ConsoleMessage item = getItem(position);
        if (item != null) {
            // My layout has only one TextView
            TextView message = (TextView) view.findViewById(R.id.message);
            if (message != null) {
                message.setText(item.message());
            }
            TextView location = (TextView) view.findViewById(R.id.location);
            if (location != null) {
                location.setText(item.sourceId() + ":" + item.lineNumber());
            }
         }

        return view;
    }
}

