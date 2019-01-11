package com.wrms.vodafone.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.wrms.vodafone.R;

/**
 * Created by Admin on 11-10-2017.
 */
public class MoreInfoActivity extends AppCompatActivity {

    private WebView mWebview ;
    String url = null;

    String pos=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info_activity);

        url = getIntent().getStringExtra("url");
        pos = getIntent().getStringExtra("pos");

        mWebview  =(WebView)findViewById(R.id.webView1);
        mWebview.getSettings().setBuiltInZoomControls(true);


        ImageView backBTN = (ImageView)findViewById(R.id.backBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (url!=null) {

            if (pos.equalsIgnoreCase("0")) {
                mWebview.loadUrl(url);
                mWebview.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                        viewx.loadUrl(urlx);
                        return false;
                    }
                });
            }else {
                mWebview.getSettings().setJavaScriptEnabled(true);
                String pdf =url;
                mWebview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
                finish();
            }
        }

    }

}
