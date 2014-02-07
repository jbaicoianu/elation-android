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
        ElationEvent event = new ElationEvent(uri);
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
        return true;
      }
      return false;
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
      NetworkRequest req = new NetworkRequest(url);

      networkRequests.add(req);
      networkRequestsPending.add(req);

      // FIXME - the WebViewClient should have no concept of adapters or the UI thread
      if (networkRequestAdapter != null) {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
          @Override
          public void run() {
            networkRequestAdapter.notifyDataSetChanged();
          }
        });
      }
      WebResourceResponse foo = super.shouldInterceptRequest(view, url);
      return foo;
    }
    @Override
    public void onLoadResource(WebView view, String url) {
      for (NetworkRequest req : networkRequestsPending) {
        if (req.url.equals(url)) {
          req.setFinished();

          if (networkRequestAdapter != null) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
              @Override
              public void run() {
                networkRequestAdapter.notifyDataSetChanged();
              }
            });
          }
        }
      }
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

