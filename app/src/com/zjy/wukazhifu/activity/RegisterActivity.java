package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class RegisterActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText accountEdt;
	private EditText codeEdt;
	private ImageView kejianImg;
	private EditText pwdEdt;
	private EditText referrerEdt;
	private TextView codeTxt;
	private TextView registerTxt;

	private TextView xieyiTxt;

	private int time = 60;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		accountEdt = (EditText) findViewById(R.id.accountEdt);
		codeEdt = (EditText) findViewById(R.id.codeEdt);
		pwdEdt = (EditText) findViewById(R.id.pwdEdt);
		referrerEdt = (EditText) findViewById(R.id.referrerEdt);
		kejianImg = (ImageView) findViewById(R.id.kejianImg);
		codeTxt = (TextView) findViewById(R.id.codeTxt);
		registerTxt = (TextView) findViewById(R.id.registerTxt);

		backImg.setOnClickListener(this);
		kejianImg.setOnClickListener(this);
		codeTxt.setOnClickListener(this);
		registerTxt.setOnClickListener(this);

		xieyiTxt = (TextView) findViewById(R.id.xieyiTxt);
		xieyiTxt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.kejianImg:
			int inputType = pwdEdt.getInputType();
			if (129 == inputType) {
				pwdEdt.setInputType(InputType.TYPE_CLASS_TEXT);
				kejianImg.setImageResource(R.drawable.eyeopen);
			} else {
				pwdEdt.setInputType(129);
				kejianImg.setImageResource(R.drawable.eyeclose);
			}
			break;
		case R.id.codeTxt:
			getCode();
			break;
		case R.id.registerTxt:
			register();
			break;
		case R.id.xieyiTxt:
			Intent intent = new Intent(RegisterActivity.this, XieyiActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getCode() {
		final String account = accountEdt.getText().toString().trim();
	/*	if ("".equals(account)) {
			ToastHelper.toast(RegisterActivity.this, "手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(RegisterActivity.this, "手机号不存在！");
			return;
		}*/
		String url = Constants.ADDR_getcode;
		Map<String, String> param = new HashMap<String, String>();
		param.put("codeType", "01");
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
									codeTxt.setBackgroundResource(R.drawable.huoquyanzhengma);
								}
							}
						});

						View contentView = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.alert_text22, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("我们已经发送验证码到您的手机：");
						TextView txt2 = (TextView) contentView.findViewById(R.id.txt2);
						txt2.setText(account);
						MyAlert.alert(RegisterActivity.this, contentView);

					} /*else if ("000202".equals(RSPCOD)) {
						ToastHelper.toast(RegisterActivity.this, "该手机号已存在");
					} */else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(RegisterActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(RegisterActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(RegisterActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void register() {
		final String account = accountEdt.getText().toString().trim();
		String pwd = pwdEdt.getText().toString().trim();
		String code = codeEdt.getText().toString().trim();
		String referrer = referrerEdt.getText().toString().trim();
		if (!ToolUtil.pwdValidation(pwd)) {
			ToastHelper.toast(RegisterActivity.this, "请输入正确的6-16数字字符密码！");
			return;
		}
		/*if ("".equals(account)) {
			ToastHelper.toast(RegisterActivity.this, "手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(RegisterActivity.this, "手机号不存在！");
			return;
		}
		if ("".equals(pwd)) {
			ToastHelper.toast(RegisterActivity.this, "密码不能为空！");
			return;
		} else if (pwd.length() < 6 || pwd.length() > 16) {
			ToastHelper.toast(RegisterActivity.this, "密码位数需要在6~16位！");
			return;
		}
		if ("".equals(code)) {
			ToastHelper.toast(RegisterActivity.this, "验证码不能为空！");
			return;
		}*/
		String url = Constants.ADDR_register;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custMobile", account);
		param.put("custPwd", pwd);
		param.put("custPwd2", pwd);
		param.put("codeType", "01");
		if (referrer.length() > 0) {
			param.put("referrer", referrer);
		}
		param.put("msgCode", code);

		LoadingDialog.show(RegisterActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						View contentView = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.alert_text2, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText(account);
						TextView txt2 = (TextView) contentView.findViewById(R.id.txt2);
						txt2.setText("注册成功！");
						MyAlert.alert(RegisterActivity.this, contentView, new OnClickListener() {
							@Override
							public void onClick(View v) {
								MyAlert.dismiss();
								RegisterActivity.this.finish();
							}
						});
					} /*else if ("001102".equals(RSPCOD)) {
						ToastHelper.toast(RegisterActivity.this, "校验码错误，请重新输入");
					} else if ("001103".equals(RSPCOD)) {
						ToastHelper.toast(RegisterActivity.this, "校验码已失效，请重新获取");
					} else if ("900001".equals(RSPCOD)) {
						ToastHelper.toast(RegisterActivity.this, "推荐人不存在，请重新输入");
					} */else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(RegisterActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(RegisterActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(RegisterActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}