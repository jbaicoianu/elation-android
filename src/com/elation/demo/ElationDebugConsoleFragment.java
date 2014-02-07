package com.elation.demo;

//import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.webkit.ConsoleMessage;
import java.util.ArrayList;

public class ElationDebugConsoleFragment extends android.support.v4.app.Fragment {
  private ListView consoleList;
  private ArrayAdapter consoleListAdapter;
  private ArrayList<ConsoleMessage> consoleEntries;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.debug_console_fragment, container, false);
    consoleList = (ListView) view.findViewById(R.id.debug_console_list);

    if (consoleListAdapter == null) {
      // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
      ElationWebView webview = ((ElationDemoActivity) getActivity()).getWebView();
      consoleEntries = webview.getConsoleMessages();

      //consoleListAdapter = new ArrayAdapter(getActivity(), R.layout.debug_console_list_entry, R.id.label, consoleEntries);
      consoleListAdapter = new ElationDebugConsoleMessageAdapter(getActivity(), R.layout.debug_console_message, consoleEntries);
      
      webview.setConsoleMessageAdapter(consoleListAdapter);
    }
    consoleList.setAdapter(consoleListAdapter);

    return view;
  }
}
