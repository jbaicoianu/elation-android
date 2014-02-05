package com.elation.demo;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.util.AttributeSet;
import android.net.Uri;


public class ElationWebView extends WebView {
  private class ElationWebViewClient extends WebViewClient {
    private boolean first = true;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Uri uri = Uri.parse(url);
      if (uri.getScheme().equals("elation-event")) {
        //ElationEvent event = new ElationEvent(uri);

        return true;
      }
      return false;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
      if (first) {
        //adapter.add("> elation.native.subscribe('*')");
        view.loadUrl("javascript:(function() { elation.native.subscribe('*'); })()");
        first = false;
      }
      //TestElationEventsActivity.this.setTitle(view.getTitle());
    }
  }
  private class ElationWebChromeClient extends WebChromeClient {
    private ProgressBar progress;

    public void onProgressChanged(WebView view, int value) {
      if (progress != null) {
        progress.setIndeterminate((value == 0));
        if (value < 100 && progress.getVisibility() == ProgressBar.GONE) {
          progress.setVisibility(ProgressBar.VISIBLE);
        }
        progress.setProgress(Math.max(1, value));
        if (value == 100) {
          progress.setVisibility(ProgressBar.GONE);
        }
      }
      //TestElationEventsActivity.this.setTitle(view.getTitle());
    }
    /*
    public boolean onConsoleMessage(ConsoleMessage cm) {
      Log.d("MyApplication", cm.message() + " -- From line "
                           + cm.lineNumber() + " of "
                           + cm.sourceId() );
      return true;
    }
    */

    public void setProgressBar(ProgressBar tProgress) {
      progress = tProgress;
    }
  }

  private ElationWebViewClient webViewClient;
  private ElationWebChromeClient webChromeClient;
  private ProgressBar progress;

  public ElationWebView(Context context, AttributeSet attrs) {
    super(context, attrs);

    webViewClient = new ElationWebViewClient();
    this.setWebViewClient(webViewClient);

    webChromeClient = new ElationWebChromeClient();
    this.setWebChromeClient(webChromeClient);

    // enable JavaScript
    WebSettings webSettings = this.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
  }
  public void setProgressBar(ProgressBar tProgress) {
    progress = tProgress;
    if (webChromeClient != null) {
      webChromeClient.setProgressBar(progress);
    }
  }
}

