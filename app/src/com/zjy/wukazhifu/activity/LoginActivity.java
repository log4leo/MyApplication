package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.zjy.wukazhifu.util.PostJsonObjectRequest.CookieUtil;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;

public class LoginActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText accountEdt;
	private EditText pwdEdt;
	private ImageView kejianImg;
	private TextView forgetpwdTxt;
	private TextView registerTxt;
	private TextView loginTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}

	private void initView() {
		accountEdt = (EditText) findViewById(R.id.accountEdt);
		pwdEdt = (EditText) findViewById(R.id.pwdEdt);
		kejianImg = (ImageView) findViewById(R.id.kejianImg);
		forgetpwdTxt = (TextView) findViewById(R.id.forgetpwdTxt);
		registerTxt = (TextView) findViewById(R.id.registerTxt);
		loginTxt = (TextView) findViewById(R.id.loginTxt);
		backImg = (ImageView) findViewById(R.id.backImg);
		
		accountEdt.setText(PreferenceHelper.getCustLogin());

		kejianImg.setOnClickListener(this);
		forgetpwdTxt.setOnClickListener(this);
		registerTxt.setOnClickListener(this);
		loginTxt.setOnClickListener(this);
		backImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
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
		case R.id.forgetpwdTxt:
			intent = new Intent(LoginActivity.this, ForgetpwdActivity.class);
			startActivity(intent);
			break;
		case R.id.registerTxt:
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.loginTxt:
			login();
			break;
		case R.id.backImg:
			finish();
			break;
		default:
			break;
		}
	}

	private void login() {
		String account = accountEdt.getText().toString().trim();
		String pwd = pwdEdt.getText().toString().trim();
		if (!ToolUtil.pwdValidation(pwd)) {
			ToastHelper.toast(LoginActivity.this, "请输入正确的6-16数字字符密码！");
			return;
		}
		/*if ("".equals(account)) {
			ToastHelper.toast(LoginActivity.this,"手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(LoginActivity.this,"手机号不存在！");
			return;
		}
		if ("".equals(pwd)) {
			ToastHelper.toast(LoginActivity.this,"密码不能为空！");
			return;
		}*/
		String url = Constants.ADDR_login;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custMobile", account);
		param.put("custPwd", pwd);
		LoadingDialog.show(LoginActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String custId = REP_BODY.optString("custId");
						String custLogin = REP_BODY.optString("custLogin");
						PreferenceHelper.setCustId(custId);
						PreferenceHelper.setCustLogin(custLogin);
						PreferenceHelper.setLogin(true);
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
					}/* else if("000102".equals(RSPCOD)) {
						ToastHelper.toast(LoginActivity.this,"登录密码输入错误，请重新输入");
					} else if("000206".equals(RSPCOD)){
						ToastHelper.toast(LoginActivity.this,"该手机号码不存在");
					}  else if("000101".equals(RSPCOD)){
						ToastHelper.toast(LoginActivity.this,"您的账号已经被冻结，请联系客服");
					}*/else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(LoginActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(LoginActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(LoginActivity.this,"无法连接服务器");
			}
		}, new CookieUtil() {

			@Override
			public void getCookie(String cookie) {
				PreferenceHelper.setCookie(cookie);
			}
		});
		MyVolley.addRequest(request);
	}
}