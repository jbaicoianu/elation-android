package com.elation.demo;

import android.os.Build;
import android.webkit.WebView;

/**
 * Created by zanshin on 2/13/14.
 */
public abstract class WebViewJavascriptInjector {
    public abstract void inject(WebView webview, String jscode);

    private static WebViewJavascriptInjector _injector;

    public static void injectJavascript(WebView webview, String jscode) {
        if (_injector == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                _injector = new KitKatInjector();
            else
                _injector = new LegacyInjector();
        }

        _injector.inject(webview, jscode);
    }
}
