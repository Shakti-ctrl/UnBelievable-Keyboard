package juloo.keyboard2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TypingMasterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        // Loading the web version of Open-Typer as a typing tutor section
        webView.loadUrl("https://open-typer.github.io/");
        setContentView(webView);
    }
}
