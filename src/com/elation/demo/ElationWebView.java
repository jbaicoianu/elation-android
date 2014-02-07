package com.elation.demo;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.ConsoleMessage;
import android.webkit.WebResourceResponse;
import android.widget.ProgressBar;
import android.widget.ArrayAdapter;
import android.util.AttributeSet;
import android.net.Uri;
import java.util.ArrayList;


public class ElationWebView extends WebView {
  private class ElationWebViewClient extends WebViewClient {
    private boolean first = true;
    private ArrayAdapter uriAdapter;
    private ArrayAdapter elationEventsAdapter;
    private ArrayList<ElationEvent> events = new ArrayList<ElationEvent>();
    private ArrayList<Uri> uris = new ArrayList<Uri>();

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Uri uri = Uri.parse(url);
      if (uri.getScheme().equals("elation-event")) {
        ElationEvent event = new ElationEvent(uri);
        events.add(event);

        // FIXME - the WebViewClient should have no concept of adapters, and this isn't thread safe!
        if (elationEventsAdapter != null) {
          elationEventsAdapter.notifyDataSetChanged();
        }
        return true;
      }
      return false;
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
      Uri uri = Uri.parse(url);
      uris.add(uri);

      // FIXME - the WebViewClient should have no concept of adapters, and this isn't thread safe!
      if (uriAdapter != null) {
        uriAdapter.notifyDataSetChanged();
      }
      return null;
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
    public ArrayList<ElationEvent> getElationEventsList() {
      return events;
    }
    public ArrayList<Uri> getUriList() {
      return uris;
    }

    // FIXME - the WebViewClient should have no concept of adapters
    public void setElationEventsAdapter(ArrayAdapter nAdapter) {
      elationEventsAdapter = nAdapter;
    }
    public void setUriAdapter(ArrayAdapter nAdapter) {
      uriAdapter = nAdapter;
    }
  }
  private class ElationWebChromeClient extends WebChromeClient {
    private ProgressBar progress;
    private ArrayList<ConsoleMessage> logs = new ArrayList<ConsoleMessage>();
    private ArrayAdapter consoleMessageAdapter;

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
    public boolean onConsoleMessage(ConsoleMessage cm) {
      logs.add(cm);

      // FIXME - the WebChromeClient should have no concept of adapters, and this isn't thread safe!
      if (consoleMessageAdapter != null) {
        consoleMessageAdapter.notifyDataSetChanged();
      }
      return true;
    }

    public void setProgressBar(ProgressBar tProgress) {
      progress = tProgress;
    }
    public ArrayList<ConsoleMessage> getConsoleMessages() {
      return logs;
    }
    // FIXME - the WebChromeClient should have no concept of adapters
    public void setConsoleMessageAdapter(ArrayAdapter nAdapter) {
      consoleMessageAdapter = nAdapter;
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
  public ArrayList<ConsoleMessage> getConsoleMessages() {
    return webChromeClient.getConsoleMessages();
  }
  public ArrayList<ElationEvent> getElationEventsList() {
    return webViewClient.getElationEventsList();
  }
  public ArrayList<Uri> getUriList() {
    return webViewClient.getUriList();
  }
  // FIXME - the WebView should have no concept of adapters
  public void setConsoleMessageAdapter(ArrayAdapter adapter) {
    webChromeClient.setConsoleMessageAdapter(adapter);
  }
  public void setElationEventsAdapter(ArrayAdapter adapter) {
    webViewClient.setElationEventsAdapter(adapter);
  }
  public void setUriAdapter(ArrayAdapter adapter) {
    webViewClient.setUriAdapter(adapter);
  }
}

