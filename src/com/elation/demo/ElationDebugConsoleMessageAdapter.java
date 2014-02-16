package com.elation.demo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.webkit.ConsoleMessage;
import java.util.ArrayList;
import java.util.HashMap;

public class ElationDebugConsoleMessageAdapter extends ArrayAdapter<ConsoleMessage> {

    private Context context;

    private HashMap<ConsoleMessage.MessageLevel, Integer> messageColors = new HashMap<ConsoleMessage.MessageLevel, Integer>();;

    public ElationDebugConsoleMessageAdapter(Context context, int textViewResourceId, ArrayList<ConsoleMessage> items) {
        super(context, textViewResourceId, items);
        this.context = context;

        messageColors.put(ConsoleMessage.MessageLevel.DEBUG, Color.CYAN);
        messageColors.put(ConsoleMessage.MessageLevel.ERROR, Color.RED);
        messageColors.put(ConsoleMessage.MessageLevel.LOG, Color.GRAY);
        messageColors.put(ConsoleMessage.MessageLevel.WARNING, Color.YELLOW);
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
                ConsoleMessage.MessageLevel msglvl = item.messageLevel();
                if (messageColors.containsKey(msglvl)) {
                  message.setTextColor(messageColors.get(msglvl));
                } else {
                  message.setTextColor(Color.GRAY);
                }
            }
            TextView location = (TextView) view.findViewById(R.id.location);
            if (location != null) {
                location.setText(item.sourceId() + ":" + item.lineNumber());
            }
         }

        return view;
    }
}

