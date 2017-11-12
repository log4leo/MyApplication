package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.zjy.wukazhifu.view.LoadingDialog;

public class YueActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private TextView yueTxt;
	private TextView recordTxt;
	private TextView jiesuanTxt;
	private TextView totleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yue);
		initView();
	}

	@Override
	protected void onResume() {
		getYue();
		getShoukuan();
		super.onResume();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		yueTxt = (TextView) findViewById(R.id.yueTxt);
		totleTxt = (TextView) findViewById(R.id.totleTxt);

		recordTxt = (TextView) findViewById(R.id.recordTxt);
		recordTxt.setOnClickListener(this);
		jiesuanTxt = (TextView) findViewById(R.id.jiesuanTxt);
		jiesuanTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.recordTxt:
			intent = new Intent(YueActivity.this, ShoukuanTixianActivity.class);
			startActivity(intent);
			break;
		case R.id.jiesuanTxt:
			intent = new Intent(YueActivity.this, JiesuantongdaoActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getYue() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_yue;
		Map<String, String> param = new HashMap<String, String>();
		param.put("acType", "01");
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
						ToastHelper.toast(YueActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(YueActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(YueActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getShoukuan() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_shoukuan;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String result = REP_BODY.optString("result");
						totleTxt.setText(ToolUtil.getMoney(result, false));
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(YueActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(YueActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(YueActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

}