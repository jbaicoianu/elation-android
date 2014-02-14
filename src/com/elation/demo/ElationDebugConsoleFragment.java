package com.elation.demo;

//import android.app.Fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ElationDebugConsoleFragment extends android.support.v4.app.Fragment implements Observer {
    private ListView consoleList;
    private EditText consoleInput;
    private ArrayAdapter consoleListAdapter;
    private ArrayList<ConsoleMessage> consoleEntries;
    private ElationWebView webview;
    private EventStore eventStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debug_console_fragment, container, false);
        consoleList = (ListView) view.findViewById(R.id.debug_console_list);
        consoleInput = (EditText) view.findViewById(R.id.debug_console_input);
        eventStore = (EventStore) getActivity().getApplicationContext();

        if (consoleListAdapter == null) {
            // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
            webview = ((ElationDemoActivity) getActivity()).getWebView();
            webview.mAdapterObservable.register(this);
            consoleEntries = eventStore.getConsoleMessages();

            consoleListAdapter = new ElationDebugConsoleMessageAdapter(getActivity(), R.layout.debug_console_message, consoleEntries);
        }
        if (consoleList != null) {
            consoleList.setAdapter(consoleListAdapter);
        }
        if (consoleInput != null) {
            final ElationDebugConsoleFragment self = this;
            consoleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    WebViewJavascriptInjector.injectJavascript(self.webview, v.getText().toString());
                    v.setText("");
                    return true;
                }
            });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        webview.mAdapterObservable.unregister(this);
        super.onDestroyView();
    }

    @Override
    public void update(Observable observable, Object data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                consoleListAdapter.notifyDataSetChanged();
            }
        });
    }
}
