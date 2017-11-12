package com.zjy.wukazhifu.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.FileUtils;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class ShareActivity extends Activity implements OnClickListener {
	private ImageView backImg;

	private TextView shareTxt;
	private TextView saveTxt;

	private WebView webView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		String custLogin = PreferenceHelper.getCustLogin();
		url = Constants.shareUrl+"&_r=" + new String(Base64.encode(custLogin.getBytes(), Base64.DEFAULT));

		initView();
	}

	private Bitmap getPic() {
		View view = this.getWindow().getDecorView();
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

	private void savePic(Bitmap bitmap) {
		String path = FileUtils.getAppPath();
		String fileName = path + File.separator + "images" + File.separator + System.currentTimeMillis()+".jpg";
		FileUtils.savePic(this,bitmap, fileName);
		if (bitmap != null) {
			bitmap.recycle();
		}
		ToastHelper.toast(this, "图片保存成功");
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		shareTxt = (TextView) findViewById(R.id.shareTxt);
		shareTxt.setOnClickListener(this);

		saveTxt = (TextView) findViewById(R.id.saveTxt);
		saveTxt.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView);
		//webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		  //开启 DOM 存储功能
		webView.getSettings().setDomStorageEnabled(true);
        //开启 数据库 存储功能
		webView.getSettings().setDatabaseEnabled(true);
        //开启 应用缓存 功能
		webView.getSettings().setAppCacheEnabled(true);
        
		webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		webView.getSettings().setBlockNetworkImage(false);
		// webView.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
		// webView.getSettings().setDefaultFontSize(5);
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
		case R.id.shareTxt:
			shareWeb(getPic());
			break;
		case R.id.saveTxt:
			savePic(getPic());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	private void shareWeb(Bitmap bitmap) {
		String custLogin = PreferenceHelper.getCustLogin();
		String url = Constants.ADDR_host+"p/H5app/shareRegister.web?_r=" + ToolUtil.getBase64(custLogin);
		UMImage thumb = new UMImage(ShareActivity.this, bitmap);
		UMWeb web = new UMWeb(url);
		web.setThumb(thumb);
		web.setDescription("盛树宝好友邀请");
		web.setTitle("盛树宝好友邀请");
		new ShareAction(ShareActivity.this).withMedia(web).setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN).setCallback(new UMShareListener() {

			@Override
			public void onStart(SHARE_MEDIA arg0) {

			}

			@Override
			public void onResult(SHARE_MEDIA arg0) {
				ToastHelper.toast(ShareActivity.this, "分享成功");
			}

			@Override
			public void onError(SHARE_MEDIA arg0, Throwable arg1) {

			}

			@Override
			public void onCancel(SHARE_MEDIA arg0) {

			}
		}).open();
	}
}
