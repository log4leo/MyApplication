package com.zjy.wukazhifu.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zjy.wukazhifu.view.MyAlert;

public class JiesuantongdaoActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView fenrunTxt;
	private TextView shengfutongTxt;
	private TextView kuaijietongTxt;
	private LinearLayout fenrunLayout;
	private LinearLayout shengfutongLayout;
	private LinearLayout kuaijietongLayout;

	private String fenrun;
	private String shengfutong;
	private String kuaijietong;

	private String fenrunAccount;
	private String shengfutongAccount;
	private String kuaijietongAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiesuantongdao);
		initView();
		getTongdaoyue();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		fenrunTxt = (TextView) findViewById(R.id.fenrunTxt);
		shengfutongTxt = (TextView) findViewById(R.id.shengfutongTxt);
		kuaijietongTxt = (TextView) findViewById(R.id.kuaijietongTxt);

		fenrunLayout = (LinearLayout) findViewById(R.id.fenrunLayout);
		fenrunLayout.setOnClickListener(this);
		shengfutongLayout = (LinearLayout) findViewById(R.id.shengfutongLayout);
		shengfutongLayout.setOnClickListener(this);
		kuaijietongLayout = (LinearLayout) findViewById(R.id.kuaijietongLayout);
		kuaijietongLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.fenrunLayout:
			if (fenrun == null || fenrun.length() == 0 || "0".equals(fenrun)) {
				View contentView = LayoutInflater.from(JiesuantongdaoActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("没有可提现的余额！");
				MyAlert.alert(JiesuantongdaoActivity.this, contentView);
				return;
			}
			intent = new Intent(JiesuantongdaoActivity.this, TixianActivity.class);
			intent.putExtra("acBal", fenrun);
			intent.putExtra("account", fenrunAccount);
			startActivity(intent);
			break;
		case R.id.shengfutongLayout:
			if (shengfutong == null || shengfutong.length() == 0 || "0".equals(shengfutong)) {
				View contentView = LayoutInflater.from(JiesuantongdaoActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("没有可提现的余额！");
				MyAlert.alert(JiesuantongdaoActivity.this, contentView);
				return;
			}
			intent = new Intent(JiesuantongdaoActivity.this, TixianActivity.class);
			intent.putExtra("acBal", shengfutong);
			intent.putExtra("account", shengfutongAccount);
			startActivity(intent);
			break;
		case R.id.kuaijietongLayout:
			if (kuaijietong == null || kuaijietong.length() == 0 || "0".equals(kuaijietong)) {
				View contentView = LayoutInflater.from(JiesuantongdaoActivity.this).inflate(R.layout.alert_text1, null);
				TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
				txt1.setText("没有可提现的余额！");
				MyAlert.alert(JiesuantongdaoActivity.this, contentView);
				return;
			}
			intent = new Intent(JiesuantongdaoActivity.this, TixianActivity.class);
			intent.putExtra("acBal", kuaijietong);
			intent.putExtra("account", kuaijietongAccount);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getTongdaoyue() {
		String url = Constants.ADDR_tongdaoyue;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
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
								fenrunAccount = item.optString("account");
								fenrun = item.optString("acBal");
								fenrunTxt.setText("余额：" + ToolUtil.getMoney(fenrun, false));
							} else if (item.optString("account").contains("ShengFuTong")) {
								shengfutongAccount = item.optString("account");
								shengfutong = item.optString("acBal");
								shengfutongTxt.setText("余额：" + ToolUtil.getMoney(shengfutong, false));
							} else if (item.optString("account").contains("KuaiJieTong")) {
								kuaijietongAccount = item.optString("account");
								kuaijietong = item.optString("acBal");
								kuaijietongTxt.setText("余额：" + ToolUtil.getMoney(kuaijietong, false));
							}
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(JiesuantongdaoActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(JiesuantongdaoActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(JiesuantongdaoActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}