package com.zjy.wukazhifu.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.Constants;

public class ShengjiActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private LinearLayout feilvLayout;

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shengji);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		feilvLayout = (LinearLayout) findViewById(R.id.feilvLayout);
		feilvLayout.setOnClickListener(this);

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
		webView.loadUrl(Constants.shengjiUrl);
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
		case R.id.feilvLayout:
			Intent intent = new Intent(ShengjiActivity.this, FeilvActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
}