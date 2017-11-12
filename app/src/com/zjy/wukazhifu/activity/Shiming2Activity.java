package com.zjy.wukazhifu.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.feezu.liuli.timeselector.Utils.ScreenUtil;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.BitmapAndStringUtils;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.FileUtils;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class Shiming2Activity extends Activity implements OnClickListener {

	private static final int ZM = 1;// 正面拍照
	private static final int FM = 2;// 反面拍照
	private static final int SC = 3;// 手持拍照

	private ImageView backImg;

	private LinearLayout sfzzmLayout;
	private LinearLayout sfzfmLayout;
	private LinearLayout sczjLayout;

	private ImageView sfzzmImg;
	private ImageView sfzfmImg;
	private ImageView sczjImg;

	private TextView commitTxt;

	private Bitmap zmBitmap;
	private Bitmap fmBitmap;
	private Bitmap scBitmap;

	private String custName;
	private String certificateNo;

	private String picPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shiming2);
		Intent intent = getIntent();
		custName = intent.getStringExtra("name");
		certificateNo = intent.getStringExtra("sfzh");
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		sfzzmImg = (ImageView) findViewById(R.id.sfzzmImg);
		sfzfmImg = (ImageView) findViewById(R.id.sfzfmImg);
		sczjImg = (ImageView) findViewById(R.id.sczjImg);

		sfzzmLayout = (LinearLayout) findViewById(R.id.sfzzmLayout);
		sfzfmLayout = (LinearLayout) findViewById(R.id.sfzfmLayout);
		sczjLayout = (LinearLayout) findViewById(R.id.sczjLayout);
		sfzzmLayout.setOnClickListener(this);
		sfzfmLayout.setOnClickListener(this);
		sczjLayout.setOnClickListener(this);

		commitTxt = (TextView) findViewById(R.id.commitTxt);
		commitTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.sfzzmLayout:
			openDialog(R.layout.dlg_sfzzm, ZM);
			break;
		case R.id.sfzfmLayout:
			openDialog(R.layout.dlg_sfzfm, FM);
			break;
		case R.id.sczjLayout:
			openDialog(R.layout.dlg_sczj, SC);
			break;
		case R.id.commitTxt:
			smrz();
			break;
		default:
			break;
		}
	}

	private void openDialog(int viewId, final int index) {
		final Dialog dialog = new Dialog(Shiming2Activity.this, R.style.MyDialogStyleBottom);
		dialog.setCancelable(true);
		dialog.setContentView(viewId);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		int width = ScreenUtil.getInstance(Shiming2Activity.this).getScreenWidth();
		lp.width = width;
		window.setAttributes(lp);
		dialog.show();
		TextView photoTxt = (TextView) dialog.findViewById(R.id.photoTxt);
		photoTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				picPath = FileUtils.getAppPath() + File.separator + "images" + File.separator + System.currentTimeMillis() + ".jpg";
				File dir = new File(FileUtils.getAppPath() + File.separator + "images");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picPath)));
				startActivityForResult(intent, index);
				dialog.dismiss();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ZM:
				Bitmap zmbmp = BitmapFactory.decodeFile(picPath);
				if (zmbmp == null) {
					zmbmp = (Bitmap) data.getExtras().get("data");
				}
				if (zmbmp != null) {
					zmBitmap = FileUtils.scalePic(zmbmp, 600);
					zmbmp.recycle();
					sfzzmImg.setImageBitmap(zmBitmap);
				}
				break;
			case FM:
				Bitmap fmbmp = BitmapFactory.decodeFile(picPath);
				if (fmbmp == null) {
					fmbmp = (Bitmap) data.getExtras().get("data");
				}
				if (fmbmp != null) {
					fmBitmap = FileUtils.scalePic(fmbmp, 600);
					fmbmp.recycle();
					sfzfmImg.setImageBitmap(fmBitmap);
				}
				break;
			case SC:
				Bitmap scbmp = BitmapFactory.decodeFile(picPath);
				if (scbmp == null) {
					scbmp = (Bitmap) data.getExtras().get("data");
				}
				if (scbmp != null) {
					scBitmap = FileUtils.scalePic(scbmp, 600);
					scbmp.recycle();
					sczjImg.setImageBitmap(scBitmap);
				}
				break;
			default:
				break;
			}
		}
	}

	private void smrz() {
		if (zmBitmap == null) {
			View contentView = LayoutInflater.from(Shiming2Activity.this).inflate(R.layout.alert_text1, null);
			TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
			txt1.setText("身份证正面照不能为空！");
			MyAlert.alert(Shiming2Activity.this, contentView);
			return;
		}
		if (fmBitmap == null) {
			View contentView = LayoutInflater.from(Shiming2Activity.this).inflate(R.layout.alert_text1, null);
			TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
			txt1.setText("身份证反面照不能为空！");
			MyAlert.alert(Shiming2Activity.this, contentView);
			return;
		}
		if (scBitmap == null) {
			View contentView = LayoutInflater.from(Shiming2Activity.this).inflate(R.layout.alert_text1, null);
			TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
			txt1.setText("手持证件照不能为空！");
			MyAlert.alert(Shiming2Activity.this, contentView);
			return;
		}
		String zmString = BitmapAndStringUtils.convertIconToString(zmBitmap, 60);
		String fmString = BitmapAndStringUtils.convertIconToString(fmBitmap, 60);
		String sczjString = BitmapAndStringUtils.convertIconToString(scBitmap, 60);

		String url = Constants.ADDR_smrz;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custName", custName);
		param.put("certificateNo", certificateNo);
		param.put("operType", "1");
		param.put("certificateType ", "01");
		param.put("cardFront", zmString);
		param.put("cardBack", fmString);
		param.put("cardHandheld", sczjString);
		String cookie = PreferenceHelper.getCookie();

		LoadingDialog.show(Shiming2Activity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						View contentView = LayoutInflater.from(Shiming2Activity.this).inflate(R.layout.alert_text1, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("您的认证信息已提交成功，请耐心等待审核结果！");
						MyAlert.alert(Shiming2Activity.this, contentView, new OnClickListener() {
							@Override
							public void onClick(View v) {
								MyAlert.dismiss();
								Intent intent = new Intent();
								intent.setClass(Shiming2Activity.this, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
								intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
								intent.putExtra("page", 2);
								startActivity(intent);
							}
						});
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(Shiming2Activity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(Shiming2Activity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(Shiming2Activity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}