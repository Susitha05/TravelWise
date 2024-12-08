package com.example.travelwise.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelwise.R;

public class MapsActivity extends AppCompatActivity {

    private WebView mapwebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maps);

        // Initialize WebView
        mapwebView = findViewById(R.id.webview);
        mapwebView.setWebViewClient(new myWebClient());
        WebSettings webSettings = mapwebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Get location data passed from the previous activity
        String fromLocation = getIntent().getStringExtra("FROM_LOCATION");
        String toLocation = getIntent().getStringExtra("TO_LOCATION");

        if (fromLocation != null && toLocation != null) {
            // Build a dynamic URL with the location data if map.html supports parameters
            String url = "file:///android_asset/map.html?from=" + fromLocation + "&to=" + toLocation;
            mapwebView.loadUrl(url);
        } else {
            // Handle the case where location data is missing
            mapwebView.loadUrl("file:///android_asset/map.html");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Custom WebViewClient to handle page navigation
    private class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // Optionally, you could show a progress bar or handle loading events here
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Handle URL loading in the WebView itself
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // Handle WebView back navigation
        if (mapwebView.canGoBack()) {
            mapwebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
