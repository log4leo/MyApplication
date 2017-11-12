package com.zjy.wukazhifu.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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
import com.zjy.wukazhifu.view.LoadingDialog;

public class TongdaoActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private ImageView typeImg;

	private TextView typeTxt;
	private TextView noTxt;
	private TextView amtTxt;

	private LinearLayout wxLayout;
	private LinearLayout zfbLayout;

	private String prdordNo;
	private String prdordAmt;
	private String custName;

	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tongdao);

		prdordNo = getIntent().getStringExtra("prdordNo");
		prdordAmt = getIntent().getStringExtra("prdordAmt");
		type = getIntent().getStringExtra("type");

		initView();
		getCustInfo();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		typeImg = (ImageView) findViewById(R.id.typeImg);

		noTxt = (TextView) findViewById(R.id.noTxt);
		amtTxt = (TextView) findViewById(R.id.amtTxt);
		noTxt.setText(prdordNo);
		amtTxt.setText(ToolUtil.getMoney2(prdordAmt, true));
		typeTxt = (TextView) findViewById(R.id.typeTxt);

		wxLayout = (LinearLayout) findViewById(R.id.wxLayout);
		wxLayout.setOnClickListener(this);
		zfbLayout = (LinearLayout) findViewById(R.id.zfbLayout);
		zfbLayout.setOnClickListener(this);
		if ("wx".equals(type)) {
			typeImg.setImageResource(R.drawable.wxddzf);
			typeTxt.setText("微信支付");
			wxLayout.setVisibility(View.VISIBLE);
			zfbLayout.setVisibility(View.GONE);
		} else if ("zfb".equals(type)) {
			typeImg.setImageResource(R.drawable.zfbddzf);
			typeTxt.setText("支付宝");
			zfbLayout.setVisibility(View.VISIBLE);
			wxLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.wxLayout:
		case R.id.zfbLayout:
			final AlertDialog dialog = new AlertDialog.Builder(TongdaoActivity.this).create();
			dialog.show();
			View view = LayoutInflater.from(TongdaoActivity.this).inflate(R.layout.alert_fukuan, null);
			Window window = dialog.getWindow();
			dialog.setContentView(view);
			WindowManager.LayoutParams lp = window.getAttributes();
			window.setGravity(Gravity.CENTER);
			DisplayMetrics dm = TongdaoActivity.this.getResources().getDisplayMetrics();
			lp.width = (int) (dm.widthPixels * 0.8);
			window.setAttributes(lp);

			TextView casAmtTxt = (TextView) view.findViewById(R.id.casAmtTxt);
			TextView casDescTxt = (TextView) view.findViewById(R.id.casDescTxt);
			TextView cardNoTxt = (TextView) view.findViewById(R.id.cardNoTxt);
			TextView custNameTxt = (TextView) view.findViewById(R.id.custNameTxt);
			TextView typeTxt = (TextView) view.findViewById(R.id.typeTxt);
			if ("wx".equals(type)) {
				typeTxt.setText("微信支付");
			} else if ("zfb".equals(type)) {
				typeTxt.setText("支付宝");
			}

			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			custNameTxt.setText(custName);
			casDescTxt.setText(sdf.format(d));
			casAmtTxt.setText("￥"+ToolUtil.getMoney2(prdordAmt, false));
			cardNoTxt.setText(prdordNo);

			TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
			cancleTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			final TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
			confirmTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getErweima(confirmTxt);
				}
			});
			break;
		default:
			break;
		}
	}

	private void getCustInfo() {
		String url = Constants.ADDR_custinfo;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("account", PreferenceHelper.getCustLogin());
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						custName = REP_BODY.optString("custName");
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(TongdaoActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(TongdaoActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getErweima(final TextView confirmTxt) {
		String url = Constants.ADDR_erweima;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("prdOrdNo", prdordNo);
		if ("wx".equals(type)) {
			param.put("payType", "04");
		} else if ("zfb".equals(type)) {
			param.put("payType", "05");
		}
		param.put("payAmt", prdordAmt);
		
		LoadingDialog.show(TongdaoActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						String busContent = REP_BODY.optString("busContent");
						String prdOrdNo = REP_BODY.optString("prdOrdNo");
						Intent intent = new Intent(TongdaoActivity.this, ShoukuanActivity.class);
						intent.putExtra("custName", custName);
						intent.putExtra("prdordAmt", prdordAmt);
						intent.putExtra("prdOrdNo", prdOrdNo);
						intent.putExtra("busContent", busContent);
						intent.putExtra("type", type);
						startActivity(intent);
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(TongdaoActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(TongdaoActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}