package com.elation.demo;

//import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.webkit.ConsoleMessage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ElationDebugConsoleFragment extends android.support.v4.app.Fragment implements Observer {
  private ListView consoleList;
  private EditText consoleInput;
  private ArrayAdapter consoleListAdapter;
  private ArrayList<ConsoleMessage> consoleEntries;
  private ElationWebView webview;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.debug_console_fragment, container, false);
    consoleList = (ListView) view.findViewById(R.id.debug_console_list);
    consoleInput = (EditText) view.findViewById(R.id.debug_console_input);

    if (consoleListAdapter == null) {
      // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
      webview = ((ElationDemoActivity) getActivity()).getWebView();
      webview.mAdapterObservable.register(this);
      consoleEntries = webview.getConsoleMessages();

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
          self.webview.injectJavascript(v.getText().toString());
          v.setText("");
          return true;
        }
      });
    }

    return view;
  }
  @Override
  public void onDestroy() {
    webview.mAdapterObservable.unregister(this);
    super.onDestroy();
  }

  @Override
  public void update(Observable observable, Object data) {
    consoleListAdapter.notifyDataSetChanged();
  }
}
