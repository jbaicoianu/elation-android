package com.elation.demo;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.ConsoleMessage;
import android.webkit.WebResourceResponse;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;
import android.widget.ArrayAdapter;
import android.util.AttributeSet;
import android.net.Uri;
import java.util.ArrayList;

import android.app.Activity;

public class ElationWebView extends WebView {
  private class ElationWebViewClient extends WebViewClient {
    private boolean first = true;
    private ArrayList<ElationEvent> events = new ArrayList<ElationEvent>();
    private ArrayList<NetworkRequest> networkRequests = new ArrayList<NetworkRequest>();
    private ArrayList<NetworkRequest> networkRequestsPending = new ArrayList<NetworkRequest>();

    // FIXME - the WebViewClient should have no concept of adapters
    private ArrayAdapter elationEventsAdapter;
    private ArrayAdapter networkRequestAdapter;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Uri uri = Uri.parse(url);
      if (uri.getScheme().equals("elation-event")) {
        NetworkRequest req = new NetworkRequest(url);
        this.addNetworkRequest(req, view);

        ElationEvent event = new ElationEvent(uri);
        this.addElationEvent(event, view);
        return true;
      }
      return false;
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
      NetworkRequest req = new NetworkRequest(url);

      networkRequestsPending.add(req);

      this.addNetworkRequest(req, view);
      return null;
    }
    @Override
    public void onLoadResource(WebView view, String url) {
      for (NetworkRequest req : networkRequestsPending) {
        if (req.url.equals(url)) {
          req.setFinished();

/*
          if (networkRequestAdapter != null) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
              @Override
              public void run() {
                networkRequestAdapter.notifyDataSetChanged();
              }
            });
          }
*/
        }
      }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      if (first) {
        //adapter.add("> elation.native.subscribe('*')");
        view.evaluateJavascript("elation.onloads.add(function() { elation.native.subscribe('*'); });", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) { }
        });
        first = false;
      }
      //TestElationEventsActivity.this.setTitle(view.getTitle());
    }
    public ArrayList<ElationEvent> getElationEventsList() {
      return events;
    }
    public ArrayList<NetworkRequest> getNetworkRequestList() {
      return networkRequests;
    }

    // FIXME - the WebViewClient should have no concept of adapters
    public void setElationEventsAdapter(ArrayAdapter nAdapter) {
      elationEventsAdapter = nAdapter;
    }
    public void setNetworkRequestAdapter(ArrayAdapter nAdapter) {
      networkRequestAdapter = nAdapter;
    }

    private void addNetworkRequest(NetworkRequest req, View view) {
      networkRequests.add(req);

      // FIXME - the WebViewClient should have no concept of adapters, views, or the UI thread
      if (networkRequestAdapter != null) {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
          @Override
          public void run() {
            networkRequestAdapter.notifyDataSetChanged();
          }
        });
      }
    }
    private void addElationEvent(ElationEvent event, View view) {
      events.add(event);

      // FIXME - the WebViewClient should have no concept of adapters or the UI thread
      if (elationEventsAdapter != null) {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
          @Override
          public void run() {
            elationEventsAdapter.notifyDataSetChanged();
          }
        });
      }
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

      // FIXME - the WebChromeClient should have no concept of adapters
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
  private class ElationWebViewJsInterface {
    // TODO - providing a native interface could provide us with a better way of passing events than via iframe loads
    @JavascriptInterface
    public boolean bridgeEvent() { return false; }
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

    this.addJavascriptInterface(new ElationWebViewJsInterface(), "elationNative");
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
  public ArrayList<NetworkRequest> getNetworkRequestList() {
    return webViewClient.getNetworkRequestList();
  }
  // FIXME - the WebView should have no concept of adapters
  public void setConsoleMessageAdapter(ArrayAdapter adapter) {
    webChromeClient.setConsoleMessageAdapter(adapter);
  }
  public void setElationEventsAdapter(ArrayAdapter adapter) {
    webViewClient.setElationEventsAdapter(adapter);
  }
  public void setNetworkRequestAdapter(ArrayAdapter adapter) {
    webViewClient.setNetworkRequestAdapter(adapter);
  }
}

