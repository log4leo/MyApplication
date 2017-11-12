package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.zjy.wukazhifu.view.KeyboardUtil;
import com.zjy.wukazhifu.view.KeyboardUtil.KeyboardListener;
import com.zjy.wukazhifu.view.LoadingDialog;

public class JiaoyiActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	
	private EditText amtEdt;
	private TextView nextTxt;

	private double max = 50000;
	private double min = 110;

	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiaoyi);
		type = getIntent().getStringExtra("type");
		if ("dmf".equals(type)) {
			min = 100;
		}
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		amtEdt = (EditText) findViewById(R.id.amtEdt);
		amtEdt.setInputType(InputType.TYPE_NULL);
		final KeyboardUtil keyboardUtil = new KeyboardUtil(JiaoyiActivity.this, amtEdt, new KeyboardListener() {
			@Override
			public void completed() {
				String text = amtEdt.getText().toString();
				double num = 0;
				try {
					num = Double.parseDouble(text);
				} catch (Exception e) {
				}
				if (num <= max && num >= min) {
					nextTxt.setEnabled(true);
					nextTxt.setBackgroundResource(R.drawable.blue_bg);
				} else {
					nextTxt.setEnabled(false);
					nextTxt.setBackgroundResource(R.drawable.gray_bg_round);
					ToastHelper.toast(JiaoyiActivity.this, "输入的金额必须大于等于"+min+",小于等于"+max);
				}
			}
		});
		amtEdt.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				keyboardUtil.show();
				return false;
			}
		});
		nextTxt = (TextView) findViewById(R.id.nextTxt);
		nextTxt.setOnClickListener(this);
		nextTxt.setEnabled(false);
		nextTxt.setBackgroundResource(R.drawable.gray_bg_round);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.nextTxt:
			dingdan();
			break;
		default:
			break;
		}
	}

	private void dingdan() {
		LoadingDialog.show(this);
		final String prdordAmt = amtEdt.getText().toString();
		String url = Constants.ADDR_dingdan;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("prdOrdType", "01");
		param.put("prdordAmt", prdordAmt);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						if ("dmf".equals(type)) {
							Intent intent = new Intent(JiaoyiActivity.this, TongdaoDmfActivity.class);
							intent.putExtra("prdordNo", REP_BODY.optString("prdordNo"));
							intent.putExtra("prdordAmt", prdordAmt);
							startActivity(intent);
						} else {
							Intent intent = new Intent(JiaoyiActivity.this, TongdaoActivity.class);
							intent.putExtra("prdordNo", REP_BODY.optString("prdordNo"));
							intent.putExtra("prdordAmt", prdordAmt);
							intent.putExtra("type", type);
							startActivity(intent);
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(JiaoyiActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(JiaoyiActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(JiaoyiActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}