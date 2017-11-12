package com.zjy.wukazhifu.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.FileUtils;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.QRCodeUtil;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class ShoukuanActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private TextView shareTxt;
	private TextView saveTxt;

	private TextView msgTxt;
	private TextView amtTxt;
	private TextView resultTxt;
	private ImageView erweimaImg;

	private String busContent;
	private String prdordAmt;
	private String custName;
	private String prdOrdNo;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		custName = getIntent().getStringExtra("custName");
		prdordAmt = getIntent().getStringExtra("prdordAmt");
		busContent = getIntent().getStringExtra("busContent");
		prdOrdNo = getIntent().getStringExtra("prdOrdNo");
		type = getIntent().getStringExtra("type");

		if ("wx".equals(type)) {
			setContentView(R.layout.activity_shoukuan_wx);
		} else if ("zfb".equals(type)) {
			setContentView(R.layout.activity_shoukuan_zfb);
		}

		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		shareTxt = (TextView) findViewById(R.id.shareTxt);
		shareTxt.setOnClickListener(this);
		saveTxt = (TextView) findViewById(R.id.saveTxt);
		saveTxt.setOnClickListener(this);

		msgTxt = (TextView) findViewById(R.id.msgTxt);
		amtTxt = (TextView) findViewById(R.id.amtTxt);

		erweimaImg = (ImageView) findViewById(R.id.erweimaImg);

		resultTxt = (TextView) findViewById(R.id.resultTxt);
		resultTxt.setOnClickListener(this);

		String msg = "商家 <font color='#4FB640'>" + custName + "</font> 正向您发起一笔金额为<font color='#4FB640'>" + ToolUtil.getMoney2(prdordAmt, false) + "</font>元的微信收款，请用微信扫描以下二维码";
		if ("zfb".equals(type)) {
			msg = "商家 <font color='#28A6E1'>" + custName + "</font> 正向您发起一笔金额为<font color='#28A6E1'>" + ToolUtil.getMoney2(prdordAmt, false) + "</font>元的支付宝收款，请用支付宝扫描以下二维码";
		}
		msgTxt.setText(Html.fromHtml(msg));
		amtTxt.setText("￥" + ToolUtil.getMoney2(prdordAmt, false));
		MyVolley.getImage(busContent, erweimaImg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Bitmap mBitmap = QRCodeUtil.createQRImage(busContent, 800, 800);
				if (mBitmap != null) {
					ShoukuanActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							erweimaImg.setImageBitmap(mBitmap);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			intent = new Intent();
			intent.setClass(ShoukuanActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
			startActivity(intent);
			break;
		case R.id.shareTxt:
			shareWeb(getPic());
			break;
		case R.id.saveTxt:
			savePic(getPic());
			break;
		case R.id.resultTxt:
			intent = new Intent(ShoukuanActivity.this, ResultActivity.class);
			intent.putExtra("prdOrdNo", prdOrdNo);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(ShoukuanActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
		String fileName = path + File.separator + "images" + File.separator + System.currentTimeMillis() + ".jpg";
		FileUtils.savePic(this, bitmap, fileName);
		if (bitmap != null) {
			bitmap.recycle();
		}
		ToastHelper.toast(this, "图片保存成功");
	}

	private void shareWeb(Bitmap bitmap) {
		UMImage image = new UMImage(ShoukuanActivity.this, bitmap);
		new ShareAction(ShoukuanActivity.this).withMedia(image).withText("盛树宝盒").setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN).setCallback(new UMShareListener() {

			@Override
			public void onStart(SHARE_MEDIA arg0) {

			}

			@Override
			public void onResult(SHARE_MEDIA arg0) {
				ToastHelper.toast(ShoukuanActivity.this, "分享成功");
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