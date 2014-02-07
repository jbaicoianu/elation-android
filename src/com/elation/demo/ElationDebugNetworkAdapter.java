package com.elation.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.net.Uri;
import java.util.ArrayList;

public class ElationDebugNetworkAdapter extends ArrayAdapter<NetworkRequest> {

    private Context context;

    public ElationDebugNetworkAdapter(Context context, int textViewResourceId, ArrayList<NetworkRequest> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.debug_network_message, null);
        }

        NetworkRequest item = getItem(position);
        if (item != null) {
            TextView urlView = (TextView) view.findViewById(R.id.url);
            if (urlView != null) {
                urlView.setText(item.url);
            }
/*
            TextView timestampView = (TextView) view.findViewById(R.id.timestamp);
            if (timestampView != null) {
                if (item.finished) {
                  timestampView.setText(item.getLoadTime() + "ms");
                } else {
                  timestampView.setText("loading...");
                }
            }
*/
         }

        return view;
    }
}


