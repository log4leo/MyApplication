package com.zjy.wukazhifu.activity;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjy.wukazhifu.R;

public class ADActivity extends Activity implements OnClickListener {
	private ImageView backImg;
	private TextView titleTxt;

	private WebView webView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad);
		url = getIntent().getStringExtra("url");
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		titleTxt= (TextView) findViewById(R.id.titleTxt);
		
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 开启 DOM 存储功能
		webView.getSettings().setDomStorageEnabled(true);
		// 开启 数据库 存储功能
		webView.getSettings().setDatabaseEnabled(true);
		// 开启 应用缓存 功能
		webView.getSettings().setAppCacheEnabled(true);

		webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		// webView.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
		// webView.getSettings().setDefaultFontSize(5);
		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				titleTxt.setText(title);
			}
		};

		// 设置setWebChromeClient对象
		webView.setWebChromeClient(wvcc);

		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			// 转向错误时的处理
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		default:
			break;
		}
	}
}
