package com.zjy.wukazhifu.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.activity.CardActivity;
import com.zjy.wukazhifu.activity.LoginActivity;
import com.zjy.wukazhifu.activity.MainActivity;
import com.zjy.wukazhifu.activity.ShimingActivity;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class MyFragment extends Fragment implements OnClickListener {
	private View view;

	private ImageView headImg;
	private TextView classTxt;
	private TextView accountTxt;
	private TextView referrerTxt;
	private TextView smrzTxt;
	private TextView jskglTxt;
	private TextView versionTxt;

	private LinearLayout smrzLayout;
	private LinearLayout jskglLayout;
	private LinearLayout zdkfLayout;
	private LinearLayout tcdlLayout;

	private String kefu = "4000007077";
	private String custStatus;
	private String cardBundingStatus;

	public boolean inThispage = false;

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (inThispage) {
				my();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_my, container, false);
		initView();
		my();
		return view;
	}

	@Override
	public void onResume() {
		if(((MainActivity)getActivity()).getCurPage()==2){
			inThispage = true;
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		inThispage = false;
		super.onPause();
	}

	private void initView() {
		headImg = (ImageView) view.findViewById(R.id.headImg);
		classTxt = (TextView) view.findViewById(R.id.classTxt);
		accountTxt = (TextView) view.findViewById(R.id.accountTxt);
		referrerTxt = (TextView) view.findViewById(R.id.referrerTxt);
		smrzTxt = (TextView) view.findViewById(R.id.smrzTxt);
		jskglTxt = (TextView) view.findViewById(R.id.jskglTxt);
		versionTxt = (TextView) view.findViewById(R.id.versionTxt);

		versionTxt.setText("V" + ToolUtil.getLocalVersionName(getActivity()));

		smrzLayout = (LinearLayout) view.findViewById(R.id.smrzLayout);
		jskglLayout = (LinearLayout) view.findViewById(R.id.jskglLayout);
		zdkfLayout = (LinearLayout) view.findViewById(R.id.zdkfLayout);
		tcdlLayout = (LinearLayout) view.findViewById(R.id.tcdlLayout);

		smrzLayout.setOnClickListener(this);
		jskglLayout.setOnClickListener(this);
		zdkfLayout.setOnClickListener(this);
		tcdlLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.smrzLayout:
			if ("1".equals(custStatus)) {
				ToastHelper.toast(getActivity(), "您的实名认证正在审核中！");
			} else if ("2".equals(custStatus)) {
				ToastHelper.toast(getActivity(), "您已完成实名认证！");
			} else {
				intent = new Intent(getActivity(), ShimingActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.jskglLayout:
			intent = new Intent(getActivity(), CardActivity.class);
			startActivity(intent);
			break;
		case R.id.zdkfLayout:
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + kefu));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		case R.id.tcdlLayout:
			View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_text1, null);
			TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
			txt1.setText("确定要退出吗？");
			MyAlert.confirm(getActivity(), contentView, new OnClickListener() {
				@Override
				public void onClick(View v) {
					MyAlert.dismiss();
					PreferenceHelper.setCookie("");
					PreferenceHelper.setLogin(false);
					PreferenceHelper.setCustLogin("");
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			});
			break;
		default:
			break;
		}
	}

	public void my() {
		if (!PreferenceHelper.getLogin()) {
			return;
		}
		LoadingDialog.show(getActivity());
		String url = Constants.ADDR_my;
		Map<String, String> param = new HashMap<String, String>();
		param.put("custLogin", PreferenceHelper.getCustLogin());
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");

					if ("000000".equals(RSPCOD)) {
						String merclass = REP_BODY.optString("merclass");
						String custLogin = REP_BODY.optString("custLogin");
						String referrer = REP_BODY.optString("referrer");
						custStatus = REP_BODY.optString("custStatus");
						cardBundingStatus = REP_BODY.optString("cardBundingStatus");
						int cardNum = REP_BODY.optInt("cardNum");

						if ("40".equals(merclass)) {
							headImg.setImageResource(R.drawable.huanjin);
							classTxt.setText("小二");
						} else if ("50".equals(merclass)) {
							headImg.setImageResource(R.drawable.bojin);
							classTxt.setText("掌柜");
						} else if ("60".equals(merclass)) {
							headImg.setImageResource(R.drawable.zuanshi);
							classTxt.setText("东家");
						} else if ("70".equals(merclass)) {
							headImg.setImageResource(R.drawable.hehuoren);
							classTxt.setText("合伙人");
						}
						String account = custLogin.substring(0, 3) + "****" + custLogin.substring(7);
						accountTxt.setText(account);
						if (referrer.length() > 0) {
							referrerTxt.setText(referrer);
							referrerTxt.setTextColor(getResources().getColor(R.color.blue));
						}
						if ("0".equals(custStatus)) {
							smrzTxt.setText("未完善");
							smrzTxt.setTextColor(getResources().getColor(R.color.cccccc));
						} else if ("1".equals(custStatus)) {
							smrzTxt.setText("审核中");
							smrzTxt.setTextColor(getResources().getColor(R.color.blue));
						} else if ("2".equals(custStatus)) {
							smrzTxt.setText("已认证");
							smrzTxt.setTextColor(getResources().getColor(R.color.blue));
						} else if ("3".equals(custStatus)) {
							smrzTxt.setText("重新认证");
							smrzTxt.setTextColor(getResources().getColor(R.color.blue));
						}
						if ("0".equals(cardBundingStatus)) {
							jskglTxt.setText("未绑定");
							jskglTxt.setTextColor(getResources().getColor(R.color.cccccc));
						} else if ("1".equals(cardBundingStatus)) {
							jskglTxt.setText("审核中");
							jskglTxt.setTextColor(getResources().getColor(R.color.blue));
						} else if ("2".equals(cardBundingStatus)) {
							jskglTxt.setText("已绑定");
							jskglTxt.setTextColor(getResources().getColor(R.color.blue));
						} else if ("3".equals(cardBundingStatus)) {
							jskglTxt.setText("未通过");
							jskglTxt.setTextColor(getResources().getColor(R.color.blue));
						}
						if ("1".equals(custStatus) || "1".equals(cardBundingStatus)) {
							handler.postDelayed(runnable, 30000);
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(), RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(), "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}
