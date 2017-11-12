package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
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
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;

public class ResultActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView refreshTxt;

	private TextView noTxt;
	private TextView typeTxt;
	private TextView amtTxt;
	private TextView timeTxt;
	private TextView statusTxt;

	private String prdOrdNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		prdOrdNo = getIntent().getStringExtra("prdOrdNo");
		
		initView();
		getData();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		refreshTxt = (TextView) findViewById(R.id.refreshTxt);
		refreshTxt.setOnClickListener(this);

		noTxt = (TextView) findViewById(R.id.noTxt);
		typeTxt = (TextView) findViewById(R.id.typeTxt);
		amtTxt = (TextView) findViewById(R.id.amtTxt);
		timeTxt = (TextView) findViewById(R.id.timeTxt);
		statusTxt = (TextView) findViewById(R.id.statusTxt);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.refreshTxt:
			getData();
			break;
		default:
			break;
		}
	}

	private String getType(String code) {
		String type = "";
		if ("03".equals(code)) {
			type = "快捷支付";
		} else if ("04".equals(code)) {
			type = " 微信支付";
		} else if ("05".equals(code)) {
			type = "支付宝";
		}
		return type;
	}

	private String getStatus(String code) {
		// 00||06 未处理。01 成功。02 失败。03||04||05||07||08 处理中。
		String status = "";
		if ("00".equals(code) || "06".equals(code)) {
			status = "未处理";
		} else if ("01".equals(code)) {
			status = "成功";
		} else if ("02".equals(code)) {
			status = " 失败";
		} else if ("03".equals(code) || "04".equals(code) || "05".equals(code) || "07".equals(code) || "08".equals(code)) {
			status = " 处理中";
		}
		return status;
	}
	
	private void getData() {
		String url = Constants.ADDR_order;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("orderNo", prdOrdNo);
		LoadingDialog.show(ResultActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray tranList = REP_BODY.optJSONArray("tranList");
						JSONObject order = tranList.getJSONObject(0);
						noTxt.setText(order.optString("prdordno"));
						typeTxt.setText(getType(order.optString("PAYTYPE")));
						amtTxt.setText(ToolUtil.getMoney(order.optString("ordamt"), false));
						timeTxt.setText(ToolUtil.formatTime(order.optString("ordtime")));
						statusTxt.setText(getStatus(order.optString("ordstatus")));

					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ResultActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ResultActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(ResultActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}