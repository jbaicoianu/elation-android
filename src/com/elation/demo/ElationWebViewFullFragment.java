package com.elation.demo;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;

import com.elation.demo.ElationWebView;


public class ElationWebViewFullFragment extends Fragment {
  ElationWebView webview;
  ProgressBar progress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.webview_full_fragment, container, false);

    progress = (ProgressBar) view.findViewById(R.id.content_progress);

    webview = (ElationWebView) view.findViewById(R.id.content_webview);
    webview.setProgressBar(progress);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    String hostname = prefs.getString("config_web_hostname", getActivity().getString(R.string.config_web_hostname_default));
    String startpage = prefs.getString("config_web_startpage", getActivity().getString(R.string.config_web_startpage_default));
    String cobrand = prefs.getString("config_cobrand", getActivity().getString(R.string.config_cobrand_default));

    String url = "http://" + hostname + startpage;
    if (!cobrand.equals("")) {
      url += "?cobrand=" + cobrand;
    }

    webview.loadUrl(url);

    return view;
  }
  /*
  private OnKeyListener backbuttonListener = new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
          webview.goBack();
          return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return false;
      }

  };
  */

}

