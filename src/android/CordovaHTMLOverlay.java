package com.curbngo.htmloverlay;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CordovaHTMLOverlay extends CordovaPlugin {

    private WebView overlayWebView;
    private String htmlStr;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            htmlStr = args.getString(0);
            showOverlay(callbackContext);
            return true;
        } else if (action.equals("hide")) {
            hideOverlay();
            callbackContext.success();
            return true;
        }
        return false;
    }

    private void showOverlay(final CallbackContext callbackContext) {
        final Context context = cordova.getActivity().getApplicationContext();
        final ViewGroup rootView = cordova.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (overlayWebView == null) {
                    overlayWebView = new WebView(context);
                    overlayWebView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    
                    overlayWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    overlayWebView.setVisibility(View.VISIBLE);
                                    callbackContext.success();
                                }
                            }, 100);
                        }
                    });

                    WebSettings webSettings = overlayWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
    
                    // Load the HTML content
                    overlayWebView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
                    
                    rootView.addView(overlayWebView);
                    overlayWebView.setVisibility(View.INVISIBLE); // Start with WebView invisible
                } else {
                    // WebView already exists, make it visible
                    overlayWebView.setVisibility(View.VISIBLE);
                    callbackContext.success();
                }
            }
        });
    }

    private void hideOverlay() {
        if (overlayWebView != null) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    overlayWebView.setVisibility(View.GONE);

                    // Remove the WebView from the parent view to release resources
                    ViewGroup rootView = cordova.getActivity().getWindow().getDecorView()
                            .findViewById(android.R.id.content);
                    rootView.removeView(overlayWebView);

                    overlayWebView.destroy();
                    overlayWebView = null;
                }
            });
        }
    }
}
