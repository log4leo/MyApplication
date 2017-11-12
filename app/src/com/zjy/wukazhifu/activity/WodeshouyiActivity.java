package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class WodeshouyiActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private TextView yueTxt;
	private TextView zuoriTxt;
	private TextView jinriTxt;
	private TextView recordTxt;
	private TextView jiesuanTxt;
	private TextView totleTxt;
	private TextView dangyueTxt;
	private TextView numTxt;

	private ImageView hongbaoImg;
	private TextView hongbaoTxt;

	private AlertDialog dialog;
	private boolean open;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wodeshouyi);
		open = getIntent().getBooleanExtra("open", false);
		initView();
	}

	@Override
	protected void onResume() {
		getYue();
		getFenrun();
		// getJiaoyi();
		getHongbao();
		super.onResume();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		yueTxt = (TextView) findViewById(R.id.yueTxt);
		zuoriTxt = (TextView) findViewById(R.id.zuoriTxt);
		jinriTxt = (TextView) findViewById(R.id.jinriTxt);
		totleTxt = (TextView) findViewById(R.id.totleTxt);
		dangyueTxt = (TextView) findViewById(R.id.dangyueTxt);
		numTxt = (TextView) findViewById(R.id.numTxt);

		recordTxt = (TextView) findViewById(R.id.recordTxt);
		recordTxt.setOnClickListener(this);
		jiesuanTxt = (TextView) findViewById(R.id.jiesuanTxt);
		jiesuanTxt.setOnClickListener(this);

		hongbaoImg = (ImageView) findViewById(R.id.hongbaoImg);
		hongbaoImg.setOnClickListener(this);

		hongbaoTxt = (TextView) findViewById(R.id.hongbaoTxt);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	private void shareWeb(Bitmap bitmap) {
		String custLogin = PreferenceHelper.getCustLogin();
		String url = Constants.ADDR_host + "p/H5app/shareRegister.web?_r=" + ToolUtil.getBase64(custLogin);
		UMImage thumb = new UMImage(WodeshouyiActivity.this, bitmap);
		UMWeb web = new UMWeb(url);
		web.setThumb(thumb);
		web.setDescription("盛树宝领红包");
		web.setTitle("盛树宝领红包");
		new ShareAction(WodeshouyiActivity.this).withMedia(web).setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN).setCallback(new UMShareListener() {

			@Override
			public void onStart(SHARE_MEDIA arg0) {

			}

			@Override
			public void onResult(SHARE_MEDIA arg0) {
				ToastHelper.toast(WodeshouyiActivity.this, "分享成功", new OnClickListener() {
					@Override
					public void onClick(View v) {
						MyAlert.dismiss();
						getYue();
						getFenrun();
						getHongbao();
					}
				});
			}

			@Override
			public void onError(SHARE_MEDIA arg0, Throwable arg1) {

			}

			@Override
			public void onCancel(SHARE_MEDIA arg0) {

			}
		}).open();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.hongbaoImg:
			if (hongbaoTxt.getText().length() == 0 || "0".equals(hongbaoTxt.getText())) {
				ToastHelper.toast(WodeshouyiActivity.this, "没有可领的红包");
				return;
			}
			open();
			break;
		case R.id.recordTxt:
			intent = new Intent(WodeshouyiActivity.this, ShouyiActivity.class);
			startActivity(intent);
			break;
		case R.id.jiesuanTxt:
			String yue = yueTxt.getText().toString();
			double jine = 0;
			try {
				jine = Double.parseDouble(yue);
			} catch (Exception e) {
			}
			if (jine == 0) {
				ToastHelper.toast(WodeshouyiActivity.this, "没有可提现金额！");
			} else {
				intent = new Intent(WodeshouyiActivity.this, FenrunTixianActivity.class);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	private void open() {
		dialog = new AlertDialog.Builder(WodeshouyiActivity.this).create();
		dialog.show();
		View view = LayoutInflater.from(WodeshouyiActivity.this).inflate(R.layout.alert_hongbao, null);
		Window window = dialog.getWindow();
		dialog.setContentView(view);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER);
		DisplayMetrics dm = WodeshouyiActivity.this.getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.8);
		window.setAttributes(lp);

		ImageView closeImg = (ImageView) view.findViewById(R.id.closeImg);
		ImageView openImg = (ImageView) view.findViewById(R.id.openImg);

		closeImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		openImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linghongbao();
			}
		});
	}

	private void getYue() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_yue;
		Map<String, String> param = new HashMap<String, String>();
		param.put("acType", "02");
		param.put("account", "FenRun");
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String acBal = REP_BODY.optString("acBal");
						yueTxt.setText(ToolUtil.getMoney(acBal, false));
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(WodeshouyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(WodeshouyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(WodeshouyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getFenrun() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_fenrun;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONObject RSPBODY = REP_BODY.optJSONObject("RSPBODY");
						String yesterdaySum = RSPBODY.optString("yesterdaySum");
						String todaySum = RSPBODY.optString("todaySum");
						String sharSum = RSPBODY.optString("sharSum");
						zuoriTxt.setText(ToolUtil.getMoney(yesterdaySum, false));
						jinriTxt.setText(ToolUtil.getMoney(todaySum, false));
						totleTxt.setText(ToolUtil.getMoney(sharSum, false));
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(WodeshouyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(WodeshouyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(WodeshouyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getJiaoyi() {
		String url = Constants.ADDR_jiaoyi;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String salesNum = REP_BODY.optString("salesNum");
						String salesSum = REP_BODY.optString("salesSum");
						dangyueTxt.setText(ToolUtil.getMoney(salesSum, false));
						numTxt.setText(salesNum);
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(WodeshouyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(WodeshouyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(WodeshouyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getHongbao() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_hongbao;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String redNum00 = REP_BODY.optString("redNum00");
						if (redNum00 == null || redNum00.length() == 0 || "0".equals(redNum00)) {
							redNum00 = "0";
							hongbaoTxt.setText(redNum00);
						} else {
							hongbaoTxt.setText(redNum00);
							if (open) {
								open = false;
								open();
							}
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(WodeshouyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(WodeshouyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(WodeshouyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void linghongbao() {
		String url = Constants.ADDR_linghongbao;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String redAchieveAmt = REP_BODY.optString("redAchieveAmt");

						View view = LayoutInflater.from(WodeshouyiActivity.this).inflate(R.layout.alert_hongbaodakai, null);
						Window window = dialog.getWindow();
						dialog.setContentView(view);
						WindowManager.LayoutParams lp = window.getAttributes();
						window.setGravity(Gravity.CENTER);
						DisplayMetrics dm = WodeshouyiActivity.this.getResources().getDisplayMetrics();
						lp.width = (int) (dm.widthPixels * 0.8);
						window.setAttributes(lp);
						dialog.show();

						TextView amtTxt = (TextView) view.findViewById(R.id.amtTxt);
						amtTxt.setText(ToolUtil.getMoney(redAchieveAmt, false));

						ImageView closeImg = (ImageView) view.findViewById(R.id.closeImg);
						ImageView shareImg = (ImageView) view.findViewById(R.id.shareImg);

						closeImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								getYue();
								getFenrun();
								getHongbao();
							}
						});

						shareImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								Bitmap bp = getPic(dialog.getWindow());
								shareWeb(bp);
							}
						});

					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(WodeshouyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(WodeshouyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(WodeshouyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private Bitmap getPic(Window window) {
		View view = window.getDecorView();
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

}