package com.zjy.wukazhifu.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.zjy.wukazhifu.view.LoadingDialog;

public class CardActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private LinearLayout wbdLayout;
	private TextView nextTxt;

	private LinearLayout cardLayout;
	private TextView cardNameTxt;
	private TextView cardNoTxt;
	private TextView genggaiTxt;
	private TextView statusTxt;
	
	private String BANK_CARD_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
		initView();
		jsk();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		wbdLayout = (LinearLayout) findViewById(R.id.wbdLayout);
		nextTxt = (TextView) findViewById(R.id.nextTxt);
		nextTxt.setOnClickListener(this);

		cardLayout = (LinearLayout) findViewById(R.id.cardLayout);
		cardNameTxt = (TextView) findViewById(R.id.cardNameTxt);
		cardNoTxt = (TextView) findViewById(R.id.cardNoTxt);
		statusTxt = (TextView) findViewById(R.id.statusTxt);
		genggaiTxt = (TextView) findViewById(R.id.genggaiTxt);
		genggaiTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.genggaiTxt:
			intent = new Intent(CardActivity.this, ChangeCardActivity.class);
			intent.putExtra("BANK_CARD_ID", BANK_CARD_ID);
			startActivity(intent);
			break;
		case R.id.nextTxt:
			intent = new Intent(CardActivity.this, AddCardActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void jsk() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_jsk;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray bankCardList = REP_BODY.optJSONArray("bankCardList");
						JSONArray bankCardUnAuditList = REP_BODY.optJSONArray("bankCardUnAuditList");
						if (bankCardList.length() == 0 && bankCardUnAuditList.length() == 0) {
							// 未绑定
							wbdLayout.setVisibility(View.VISIBLE);
						} else if (bankCardUnAuditList.length() != 0) {
							// 审核中
							cardLayout.setVisibility(View.VISIBLE);
							statusTxt.setText("审核中");
							genggaiTxt.setVisibility(View.GONE);
							JSONObject card = bankCardUnAuditList.getJSONObject(0);
							cardNameTxt.setText(card.optString("issnam"));
							cardNoTxt.setText(card.optString("cardNo"));

						} else {
							// 已绑定
							cardLayout.setVisibility(View.VISIBLE);
							statusTxt.setText("已绑定");
							JSONObject card = bankCardList.getJSONObject(0);
							cardNameTxt.setText(card.optString("issnam"));
							cardNoTxt.setText(card.optString("cardNo"));
							BANK_CARD_ID = card.optString("BANK_CARD_ID");
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(CardActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(CardActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(CardActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}