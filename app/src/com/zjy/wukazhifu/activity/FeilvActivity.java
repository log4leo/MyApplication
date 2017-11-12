package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
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

public class FeilvActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView classTxt;
	private TextView wxTxt;
	private TextView zfbTxt;
	private TextView ylTxt;

	private Map<String, Map<String, String>> dataMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feilv);
		initView();
		initData();
		feilv();
	}

	private void initData() {
		dataMap = new HashMap<String, Map<String, String>>();
		Map<String, String> map40 = new HashMap<String, String>();
		Map<String, String> map50 = new HashMap<String, String>();
		Map<String, String> map60 = new HashMap<String, String>();
		Map<String, String> map70 = new HashMap<String, String>();
		map40.put("wx", "0.49%");
		map40.put("zfb", "0.49%");
		map40.put("yl", "0.44%");
		map50.put("wx", "0.42%");
		map50.put("zfb", "0.42%");
		map50.put("yl", "0.40%");
		map60.put("wx", "0.36%");
		map60.put("zfb", "0.36%");
		map60.put("yl", "0.36%");
		map70.put("wx", "0.36%");
		map70.put("zfb", "0.36%");
		map70.put("yl", "0.36%");
		dataMap.put("40", map40);
		dataMap.put("50", map50);
		dataMap.put("60", map60);
		dataMap.put("70", map70);
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		classTxt = (TextView) findViewById(R.id.classTxt);
		wxTxt = (TextView) findViewById(R.id.wxTxt);
		zfbTxt = (TextView) findViewById(R.id.zfbTxt);
		ylTxt = (TextView) findViewById(R.id.ylTxt);
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

	private void feilv() {
		String url = Constants.ADDR_feilv;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String merclass = REP_BODY.optString("merclass");
						if ("40".equals(merclass)) {
							classTxt.setText("小二");
						} else if ("50".equals(merclass)) {
							classTxt.setText("掌柜");
						} else if ("60".equals(merclass)) {
							classTxt.setText("东家");
						}else if ("70".equals(merclass)) {
							classTxt.setText("合伙人");
						}
						Map<String, String> map = dataMap.get(merclass);
						wxTxt.setText(map.get("wx"));
						zfbTxt.setText(map.get("zfb"));
						ylTxt.setText(map.get("yl"));
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(FeilvActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(FeilvActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(FeilvActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}