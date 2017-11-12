package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.KeyboardUtil;
import com.zjy.wukazhifu.view.KeyboardUtil.KeyboardListener;
import com.zjy.wukazhifu.view.LoadingDialog;

public class FenrunTixianActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText amtEdt;
	private TextView allTxt;
	private TextView acbalTxt;
	private TextView nextTxt;
	
	private double max = 0;
	private double min = 0;

	private String fenrun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fenruntixian);
		initView();
		getTongdaoyue();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		amtEdt = (EditText) findViewById(R.id.amtEdt);
		amtEdt.setInputType(InputType.TYPE_NULL);
		final KeyboardUtil keyboardUtil = new KeyboardUtil(FenrunTixianActivity.this, amtEdt, new KeyboardListener() {
			@Override
			public void completed() {
				String text = amtEdt.getText().toString();
				double num = 0;
				try {
					num = Double.parseDouble(text);
				} catch (Exception e) {
				}
				if (num <= max && num > min) {
					nextTxt.setEnabled(true);
					nextTxt.setBackgroundResource(R.drawable.blue_bg);
				} else {
					nextTxt.setEnabled(false);
					nextTxt.setBackgroundResource(R.drawable.gray_bg_round);
					ToastHelper.toast(FenrunTixianActivity.this, "输入的金额必须大于"+min+",小于等于"+max);
				}
			}
		});
		amtEdt.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (max ==0){
					ToastHelper.toast(FenrunTixianActivity.this, "没有可提现金额！");
				}else{
					keyboardUtil.show();
				}
				return false;
			}
		});
		nextTxt = (TextView) findViewById(R.id.nextTxt);
		nextTxt.setOnClickListener(this);
		nextTxt.setEnabled(false);
		nextTxt.setBackgroundResource(R.drawable.gray_bg_round);

		acbalTxt = (TextView) findViewById(R.id.acbalTxt);
		allTxt = (TextView) findViewById(R.id.allTxt);
		allTxt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.allTxt:
			String bal = ToolUtil.formatMoney(fenrun);
			amtEdt.setText(bal);
			amtEdt.setSelection(bal.length());
			nextTxt.setEnabled(true);
			nextTxt.setBackgroundResource(R.drawable.blue_bg);
			break;
		case R.id.nextTxt:
			tixian();
			break;
		default:
			break;
		}
	}

	private void getTongdaoyue() {
		String url = Constants.ADDR_tongdaoyue;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("account", "FenRun");
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray result = REP_BODY.optJSONArray("result");
						for (int i = 0; i < result.length(); i++) {
							JSONObject item = result.optJSONObject(i);
							if ("FenRun".equals(item.optString("account"))) {
								fenrun = item.optString("acBal");
								acbalTxt.setText("余额：" + ToolUtil.getMoney(fenrun, false));
								try {
									max = Double.parseDouble(ToolUtil.formatMoney(fenrun));
								} catch (Exception e) {
								}
							}
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(FenrunTixianActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(FenrunTixianActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(FenrunTixianActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void tixian() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_tixian;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("casType", "00");
		param.put("casAmt", amtEdt.getText().toString());
		param.put("account", "FenRun");
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("code");
					if ("000000".equals(RSPCOD)) {
						final AlertDialog dialog = new AlertDialog.Builder(FenrunTixianActivity.this).create();
						dialog.show();
						View view = LayoutInflater.from(FenrunTixianActivity.this).inflate(R.layout.alert_tixian, null);
						Window window = dialog.getWindow();
						dialog.setContentView(view);
						WindowManager.LayoutParams lp = window.getAttributes();
						window.setGravity(Gravity.CENTER);
						DisplayMetrics dm = FenrunTixianActivity.this.getResources().getDisplayMetrics();
						lp.width = (int) (dm.widthPixels * 0.8);
						window.setAttributes(lp);

						TextView custNameTxt = (TextView) view.findViewById(R.id.custNameTxt);
						TextView casDescTxt = (TextView) view.findViewById(R.id.casDescTxt);
						TextView casAmtTxt = (TextView) view.findViewById(R.id.casAmtTxt);
						TextView feeTxt = (TextView) view.findViewById(R.id.feeTxt);
						TextView serviceFeeTxt = (TextView) view.findViewById(R.id.serviceFeeTxt);
						TextView netrecAmtTxt = (TextView) view.findViewById(R.id.netrecAmtTxt);
						TextView cardNoTxt = (TextView) view.findViewById(R.id.cardNoTxt);

						custNameTxt.setText(REP_BODY.optString("custName"));
						casDescTxt.setText(ToolUtil.formatTime(REP_BODY.optString("casDesc")));
						casAmtTxt.setText(ToolUtil.getMoney(REP_BODY.optString("casAmt"), false));
						feeTxt.setText(ToolUtil.getMoney(REP_BODY.optString("fee"), false));
						serviceFeeTxt.setText(ToolUtil.getMoney(REP_BODY.optString("serviceFee"), false));
						netrecAmtTxt.setText(ToolUtil.getMoney(REP_BODY.optString("netrecAmt"), false));
						cardNoTxt.setText(REP_BODY.optString("cardNo"));

						TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
						confirmTxt.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								Intent intent = new Intent();
								intent.setClass(FenrunTixianActivity.this, WodeshouyiActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
								intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
								startActivity(intent);
							}
						});
						return;

					} else {
						String RSPMSG = REP_BODY.optString("msg");
						ToastHelper.toast(FenrunTixianActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(FenrunTixianActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(FenrunTixianActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}