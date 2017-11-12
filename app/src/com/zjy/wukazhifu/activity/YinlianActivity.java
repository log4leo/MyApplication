package com.zjy.wukazhifu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.zjy.wukazhifu.R;

public class YinlianActivity extends Activity implements OnClickListener {
	private ImageView backImg;

	private WebView webView;
	private String html;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yinlian);
		html = getIntent().getStringExtra("html");
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		// webView.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
		// webView.getSettings().setDefaultFontSize(5);
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("https://mcashier.95516.com/mobile/authPay/callback.action")||url.startsWith("https://gateway.chinaepay.com/trans/frontTransResTokenURL")) {
					YinlianActivity.this.finish();
				} else {
					view.loadUrl(url);// 使用当前WebView处理跳转
				}
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			// 转向错误时的处理
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
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
