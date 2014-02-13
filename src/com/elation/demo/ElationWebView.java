package com.elation.demo;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.*;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class ElationWebView extends WebView {
    protected AdapterObservable mAdapterObservable;
    private class ElationWebViewClient extends WebViewClient {
        private boolean first = true;
        private ArrayList<ElationEvent> events = new ArrayList<ElationEvent>();
        private ArrayList<NetworkRequest> networkRequests = new ArrayList<NetworkRequest>();
        private ArrayList<NetworkRequest> networkRequestsPending = new ArrayList<NetworkRequest>();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equals("elation-event")) {
                ElationEvent event = new ElationEvent(uri);
                this.addElationEvent(event);
                return true;
            }
            return false;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            NetworkRequest req = new NetworkRequest(url);
            this.addNetworkRequest(req);
            networkRequestsPending.add(req);
            return null;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            for (NetworkRequest req : networkRequestsPending) {
                if (req.url.equals(url)) {
                    req.setFinished();
                    mAdapterObservable.notify(ElationDebugNetworkAdapter.class);
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (first) {
                //adapter.add("> elation.native.subscribe('*')");
                String jscode = "elation.onloads.add(function() { elation.native.subscribe('*'); });";
                ((ElationWebView) view).injectJavascript(jscode);
                first = false;
            }
            //TestElationEventsActivity.this.setTitle(view.getTitle());
        }

        private void addElationEvent(ElationEvent event) {
            events.add(event);
            mAdapterObservable.notify(ElationDebugEventsAdapter.class);
        }
        private void addNetworkRequest(NetworkRequest req) {
            networkRequests.add(req);
            mAdapterObservable.notify(ElationDebugNetworkAdapter.class);
        }

        public ArrayList<ElationEvent> getElationEventsList() {
            return events;
        }

        public ArrayList<NetworkRequest> getNetworkRequestList() {
            return networkRequests;
        }
    }

    private class ElationWebViewJsInterface {
        // TODO - providing a native interface could provide us with a more efficient way of passing events
        @JavascriptInterface
        public boolean bridgeEvent() { return false; }
    }

    private class ElationWebChromeClient extends WebChromeClient {
        private ProgressBar progress;
        private ArrayList<ConsoleMessage> logs = new ArrayList<ConsoleMessage>();

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

        public boolean onConsoleMessage(ConsoleMessage msg) {
            this.addConsoleMessage(msg);
            return true;
        }

        public void setProgressBar(ProgressBar tProgress) {
            progress = tProgress;
        }

        public ArrayList<ConsoleMessage> getConsoleMessages() {
            return logs;
        }

        public void addConsoleMessage(ConsoleMessage msg) {
            logs.add(msg);
            mAdapterObservable.notify(ElationDebugConsoleFragment.class);
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

        // Enable JavaScript and DOM storage
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Register native JS interface
        this.addJavascriptInterface(new ElationWebViewJsInterface(), "elationNative");

        // Get new instance of AdapterObservable
        mAdapterObservable = AdapterObservable.newInstance();
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

    public void injectJavascript(String jscode) {
        this.evaluateJavascript(jscode, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) { }
        });
    }
}

