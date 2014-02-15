package com.elation.demo;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.*;
import android.widget.ProgressBar;

public class ElationWebView extends WebView {
    protected AdapterObservable mAdapterObservable;
    protected EventStore eventStore;

    private class ElationWebViewClient extends WebViewClient {
        private boolean first = true;

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
            eventStore.getNetworkRequestPendingList().add(req);
            return null;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            for (NetworkRequest req : eventStore.getNetworkRequestPendingList()) {
                if (req.url.equals(url)) {
                    req.setFinished();
                    mAdapterObservable.notify(ElationDebugNetworkFragment.class, req);
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (first) {
                //adapter.add("> elation.native.subscribe('*')");
                String jscode = "elation.onloads.add(function() { elation.native.subscribe('*'); });";
                WebViewJavascriptInjector.injectJavascript(view, jscode);
                first = false;
            }
            //TestElationEventsActivity.this.setTitle(view.getTitle());
        }

        private void addElationEvent(ElationEvent event) {
            eventStore.getElationEventsList().add(event);
            mAdapterObservable.notify(ElationDebugEventsFragment.class, event);
        }

        private void addNetworkRequest(NetworkRequest req) {
            eventStore.getNetworkRequestList().add(req);
            mAdapterObservable.notify(ElationDebugNetworkFragment.class, req);
        }
    }

    private class ElationWebViewJsInterface {
        // TODO - providing a native interface could provide us with a more efficient way of passing events
        @JavascriptInterface
        public boolean bridgeEvent() {
            return false;
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

        public boolean onConsoleMessage(ConsoleMessage msg) {
            this.addConsoleMessage(msg);
            return true;
        }

        public void setProgressBar(ProgressBar tProgress) {
            progress = tProgress;
        }

        public void addConsoleMessage(ConsoleMessage msg) {
            eventStore.getConsoleMessages().add(msg);
            mAdapterObservable.notify(ElationDebugConsoleFragment.class, msg);
        }
    }

    private ElationWebViewClient webViewClient;
    private ElationWebChromeClient webChromeClient;
    private ProgressBar progress;

    public ElationWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        eventStore = (EventStore) context.getApplicationContext();
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
}

