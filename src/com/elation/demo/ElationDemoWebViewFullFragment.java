package com.elation.demo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;

import com.elation.demo.ElationWebView;


public class ElationDemoWebViewFullFragment extends Fragment {
  ElationWebView webview;
  ProgressBar progress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_webview_full, container, false);
    //TextView blargh = (TextView) view.findViewById(R.id.blargh);

    progress = (ProgressBar) view.findViewById(R.id.content_progress);

    webview = (ElationWebView) view.findViewById(R.id.content_webview);
    webview.setProgressBar(progress);
    webview.loadUrl("http://elation-framework.com");

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

