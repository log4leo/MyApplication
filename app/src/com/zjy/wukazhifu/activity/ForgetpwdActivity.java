package com.zjy.wukazhifu.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
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

public class ForgetpwdActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText accountEdt;
	private EditText codeEdt;
	private ImageView kejianImg;
	private EditText pwdEdt;
	private TextView codeTxt;
	private TextView resetTxt;

	private int time = 60;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpwd);
		initView();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		accountEdt = (EditText) findViewById(R.id.accountEdt);
		codeEdt = (EditText) findViewById(R.id.codeEdt);
		pwdEdt = (EditText) findViewById(R.id.pwdEdt);
		kejianImg = (ImageView) findViewById(R.id.kejianImg);
		codeTxt = (TextView) findViewById(R.id.codeTxt);
		resetTxt = (TextView) findViewById(R.id.resetTxt);

		backImg.setOnClickListener(this);
		kejianImg.setOnClickListener(this);
		codeTxt.setOnClickListener(this);
		resetTxt.setOnClickListener(this);
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
		case R.id.resetTxt:
			uppwd();
			break;
		default:
			break;
		}
	}

	private void getCode() {
		final String account = accountEdt.getText().toString().trim();
		/*if ("".equals(account)) {
			ToastHelper.toast(ForgetpwdActivity.this,"手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(ForgetpwdActivity.this,"手机号不存在！");
			return;
		}*/
		String url = Constants.ADDR_getcode;
		Map<String, String> param = new HashMap<String, String>();
		param.put("codeType", "04");
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

						View contentView = LayoutInflater.from(ForgetpwdActivity.this).inflate(R.layout.alert_text22, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("我们已经发送验证码到您的手机：");
						TextView txt2 = (TextView) contentView.findViewById(R.id.txt2);
						txt2.setText(account);
						MyAlert.alert(ForgetpwdActivity.this, contentView);

					}/* else if ("000206".equals(RSPCOD)) {
						ToastHelper.toast(ForgetpwdActivity.this,"该手机号未注册，请重新输入");
					}*/ else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ForgetpwdActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ForgetpwdActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(ForgetpwdActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void uppwd() {
		String account = accountEdt.getText().toString().trim();
		String pwd = pwdEdt.getText().toString().trim();
		String code = codeEdt.getText().toString().trim();
		if (!ToolUtil.pwdValidation(pwd)) {
			ToastHelper.toast(ForgetpwdActivity.this, "请输入正确的6-16数字字符密码！");
			return;
		}
		/*if ("".equals(account)) {
			ToastHelper.toast(ForgetpwdActivity.this,"手机号不能为空！");
			return;
		} else if (!ToolUtil.phoneValidation(account)) {
			ToastHelper.toast(ForgetpwdActivity.this,"手机号不存在！");
			return;
		}
		if ("".equals(pwd)) {
			ToastHelper.toast(ForgetpwdActivity.this,"密码不能为空！");
			return;
		}else if (pwd.length()<6||pwd.length()>16) {
			ToastHelper.toast(ForgetpwdActivity.this,"密码位数需要在6~16位！");
			return;
		}
		
		if ("".equals(code)) {
			ToastHelper.toast(ForgetpwdActivity.this,"验证码不能为空！");
			return;
		}*/
		String url = Constants.ADDR_uppwd;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custMobile", account);
		param.put("custPwd", pwd);
		param.put("custPwd2", pwd);
		param.put("codeType", "04");
		param.put("msgCode", code);

		PostJsonObjectRequest request = new PostJsonObjectRequest(url, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						View contentView = LayoutInflater.from(ForgetpwdActivity.this).inflate(R.layout.alert_text1, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("登录密码重置成功！");
						MyAlert.alert(ForgetpwdActivity.this, contentView, new OnClickListener() {
							@Override
							public void onClick(View v) {
								MyAlert.dismiss();
								ForgetpwdActivity.this.finish();
							}
						});
					}/* else if ("001102".equals(RSPCOD)) {
						ToastHelper.toast(ForgetpwdActivity.this,"校验码错误，请重新输入");
					} else if ("001103".equals(RSPCOD)) {
						ToastHelper.toast(ForgetpwdActivity.this,"校验码已失效，请重新获取");
					}*/ else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ForgetpwdActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ForgetpwdActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(ForgetpwdActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}