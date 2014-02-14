package com.elation.demo;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by zanshin on 2/13/14.
 */
public class KitKatInjector extends WebViewJavascriptInjector {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void inject(WebView webview, String jscode) {
        webview.evaluateJavascript(jscode, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }
}
