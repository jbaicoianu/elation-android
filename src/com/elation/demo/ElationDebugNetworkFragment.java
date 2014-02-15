package com.elation.demo;

//import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ElationDebugNetworkFragment extends android.support.v4.app.Fragment implements Observer {
    private ListView networkList;
    private ArrayAdapter networkListAdapter;
    private ArrayList<NetworkRequest> networkEntries;
    private EventStore eventStore;
    private ElationWebView webview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debug_network_fragment, container, false);
        networkList = (ListView) view.findViewById(R.id.debug_network_list);
        eventStore = (EventStore) getActivity().getApplicationContext();

        if (networkListAdapter == null) {
            // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
            webview = ((ElationDemoActivity) getActivity()).getWebView();
            networkEntries = new ArrayList<NetworkRequest>();
            networkEntries.addAll(eventStore.getNetworkRequestList());
            networkListAdapter = new ElationDebugNetworkAdapter(getActivity(), R.layout.debug_network_message, networkEntries);
        }
        webview.mAdapterObservable.register(this);
        networkList.setAdapter(networkListAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        webview.mAdapterObservable.unregister(this);
        super.onPause();
    }

    @Override
    public void update(Observable observable, Object data) {
        final NetworkRequest request = (NetworkRequest) data;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkEntries.add(request);
                networkListAdapter.notifyDataSetChanged();
            }
        });
    }
}

