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
                    rootView.addView(overlayWebView);
    
                    overlayWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            // Page has finished loading, make the overlay visible
                            overlayWebView.setVisibility(View.VISIBLE);
                            callbackContext.success();
                        }
                    });
    
                    WebSettings webSettings = overlayWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                }
                overlayWebView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
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
