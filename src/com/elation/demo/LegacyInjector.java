package com.elation.demo;

import android.webkit.WebView;

/**
 * Created by zanshin on 2/13/14.
 */
public class LegacyInjector extends WebViewJavascriptInjector {
    @Override
    public void inject(WebView webview, String jscode) {
        webview.loadUrl("javascript:(function() {"+jscode);
    }
}
