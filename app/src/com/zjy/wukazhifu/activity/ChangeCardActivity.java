package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class ChangeCardActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private TextView phoneEdt;
	private EditText codeEdt;
	private TextView codeTxt;
	private TextView confirmTxt;

	private int time = 60;
	private Handler handler = new Handler();

	private String BANK_CARD_ID;
	private String inputCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		BANK_CARD_ID = intent.getStringExtra("BANK_CARD_ID");
		
		setContentView(R.layout.activity_changecard);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		phoneEdt = (TextView) findViewById(R.id.phoneEdt);
		codeEdt = (EditText) findViewById(R.id.codeEdt);
		phoneEdt.setText(PreferenceHelper.getCustLogin());
		codeTxt = (TextView) findViewById(R.id.codeTxt);
		codeTxt.setOnClickListener(this);
		confirmTxt = (TextView) findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.codeTxt:
			getCode();
			break;
		case R.id.confirmTxt:
			change();
			break;
		default:
			break;
		}
	}

	private void getCode() {
		final String account = phoneEdt.getText().toString().trim();
		/*if ("".equals(account)) {
			ToastHelper.toast(ChangeCardActivity.this,"手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(ChangeCardActivity.this,"手机号不正确！");
			return;
		}*/
		String url = Constants.ADDR_getcode;
		Map<String, String> param = new HashMap<String, String>();
		param.put("codeType", "05");
		param.put("custMobile", account);

		LoadingDialog.show(this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						codeTxt.setEnabled(false);
						codeTxt.setBackgroundResource(R.drawable.huoquyanzhengma2);
						handler.post(new Runnable() {
							@Override
							public void run() {
								codeTxt.setText(time + "秒后重新获取");
								if (time-- > 0) {
									handler.postDelayed(this, 1000);
								} else {
									time = 60;
									codeTxt.setEnabled(true);
									codeTxt.setText("重新获取");
									codeTxt.setBackgroundResource(R.drawable.blue_bg);
								}
							}
						});

						View contentView = LayoutInflater.from(ChangeCardActivity.this).inflate(R.layout.alert_text22, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("我们已经发送验证码到您的手机：");
						TextView txt2 = (TextView) contentView.findViewById(R.id.txt2);
						txt2.setText(account);
						MyAlert.alert(ChangeCardActivity.this, contentView);

					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ChangeCardActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ChangeCardActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(ChangeCardActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void change() {
		String account = phoneEdt.getText().toString().trim();
		inputCode = codeEdt.getText().toString().trim();
		/*if ("".equals(account)) {
			ToastHelper.toast(ChangeCardActivity.this,"手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(ChangeCardActivity.this,"手机号不存在！");
			return;
		}
		if ("".equals(inputCode)) {
			ToastHelper.toast(ChangeCardActivity.this,"验证码不能为空！");
			return;
		}*/

		String url = Constants.ADDR_changecard;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("codeType", "05");
		param.put("custMobile", account);
		param.put("msgCode", inputCode);

		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						Intent intent = new Intent(ChangeCardActivity.this, AddCardActivity.class);
						intent.putExtra("BANK_CARD_ID", BANK_CARD_ID);
						intent.putExtra("inputCode", inputCode);
						startActivity(intent);
						ChangeCardActivity.this.finish();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ChangeCardActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ChangeCardActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(ChangeCardActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}