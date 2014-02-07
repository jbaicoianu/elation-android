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
import android.net.Uri;
import java.util.ArrayList;

public class ElationDebugNetworkFragment extends android.support.v4.app.Fragment {
  private ListView networkList;
  private ArrayAdapter networkListAdapter;
  private ArrayList<Uri> networkEntries;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.debug_network_fragment, container, false);
    networkList = (ListView) view.findViewById(R.id.debug_network_list);

    if (networkListAdapter == null) {
      // onCreateView is called every time this fragment is loaded in a tab, but we can reuse most of these objects
      ElationWebView webview = ((ElationDemoActivity) getActivity()).getWebView();
      networkEntries = webview.getUriList();

      networkListAdapter = new ElationDebugNetworkAdapter(getActivity(), R.layout.debug_network_message, networkEntries);
      
      webview.setUriAdapter(networkListAdapter);
    }
    networkList.setAdapter(networkListAdapter);

    return view;
  }
}

