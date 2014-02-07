package com.elation.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.net.Uri;
import java.util.ArrayList;

public class ElationDebugNetworkAdapter extends ArrayAdapter<Uri> {

    private Context context;

    public ElationDebugNetworkAdapter(Context context, int textViewResourceId, ArrayList<Uri> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.debug_network_message, null);
        }

        Uri item = getItem(position);
        if (item != null) {
            TextView uriView = (TextView) view.findViewById(R.id.uri);
            if (uriView != null) {
                uriView.setText(item.toString());
            }
         }

        return view;
    }
}


