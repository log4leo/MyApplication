package com.zjy.wukazhifu.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Card;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;

public class TongdaoDmfActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private TextView noTxt;
	private TextView amtTxt;
	private EditText cardEdt;
	private EditText phoneEdt;

	private TextView selectCardTxt;

	private LinearLayout dmfLayout;

	private WebView webView;

	private String prdordNo;
	private String prdordAmt;
	private String custName;
	private List<Card> dataList = new ArrayList<Card>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tongdaodmf);

		prdordNo = getIntent().getStringExtra("prdordNo");
		prdordAmt = getIntent().getStringExtra("prdordAmt");

		initView();
		getCustInfo();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		selectCardTxt = (TextView) findViewById(R.id.selectCardTxt);
		selectCardTxt.setOnClickListener(this);

		noTxt = (TextView) findViewById(R.id.noTxt);
		amtTxt = (TextView) findViewById(R.id.amtTxt);
		noTxt.setText(prdordNo);
		amtTxt.setText(ToolUtil.getMoney2(prdordAmt, true));
		cardEdt = (EditText) findViewById(R.id.cardEdt);
		phoneEdt = (EditText) findViewById(R.id.phoneEdt);

		dmfLayout = (LinearLayout) findViewById(R.id.dmfLayout);
		dmfLayout.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		// webView.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
		// webView.getSettings().setDefaultFontSize(5);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			// 转向错误时的处理
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.selectCardTxt:
			getCard();
			break;
		case R.id.dmfLayout:
			String card = cardEdt.getText().toString().trim();
			String phone = phoneEdt.getText().toString().trim();
			if ("".equals(card)) {
				ToastHelper.toast(TongdaoDmfActivity.this, "银行卡号不能为空！");
				return;
			}
			if ("".equals(phone)) {
				ToastHelper.toast(TongdaoDmfActivity.this, "手机号不能为空！");
				return;
			}

			final AlertDialog dialog = new AlertDialog.Builder(TongdaoDmfActivity.this).create();
			dialog.show();
			View view = LayoutInflater.from(TongdaoDmfActivity.this).inflate(R.layout.alert_fukuandmf, null);
			Window window = dialog.getWindow();
			dialog.setContentView(view);
			WindowManager.LayoutParams lp = window.getAttributes();
			window.setGravity(Gravity.CENTER);
			DisplayMetrics dm = TongdaoDmfActivity.this.getResources().getDisplayMetrics();
			lp.width = (int) (dm.widthPixels * 0.8);
			window.setAttributes(lp);

			TextView casAmtTxt = (TextView) view.findViewById(R.id.casAmtTxt);
			TextView casDescTxt = (TextView) view.findViewById(R.id.casDescTxt);
			TextView cardNoTxt = (TextView) view.findViewById(R.id.cardNoTxt);
			TextView custNameTxt = (TextView) view.findViewById(R.id.custNameTxt);
			TextView cardTxt = (TextView) view.findViewById(R.id.cardTxt);

			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			custNameTxt.setText(custName);
			casDescTxt.setText(sdf.format(d));
			casAmtTxt.setText("￥ " + ToolUtil.getMoney2(prdordAmt, false));
			cardNoTxt.setText(prdordNo);
			cardTxt.setText(cardEdt.getText());

			TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
			cancleTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
			confirmTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					getErweima();
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
						ToastHelper.toast(TongdaoDmfActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoDmfActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(TongdaoDmfActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getCard() {
		LoadingDialog.show(this);
		String url = Constants.ADDR_cardhistory;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("account", PreferenceHelper.getCustLogin());
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray cardList = REP_BODY.optJSONArray("cardList");
						Gson gson = new Gson();
						dataList = gson.fromJson(cardList.toString(), new TypeToken<List<Card>>() {
						}.getType());

						if (dataList != null && dataList.size() > 0) {

							final AlertDialog dialog = new AlertDialog.Builder(TongdaoDmfActivity.this).create();
							dialog.show();
							View view = LayoutInflater.from(TongdaoDmfActivity.this).inflate(R.layout.alert_list, null);
							Window window = dialog.getWindow();
							dialog.setContentView(view);
							WindowManager.LayoutParams lp = window.getAttributes();
							window.setGravity(Gravity.CENTER);
							DisplayMetrics dm = TongdaoDmfActivity.this.getResources().getDisplayMetrics();
							lp.width = (int) (dm.widthPixels * 0.8);
							window.setAttributes(lp);

							ListView listView = (ListView) view.findViewById(R.id.listView);
							listView.setAdapter(new CardAdapter(TongdaoDmfActivity.this));
							listView.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									Card card = dataList.get(position);
									cardEdt.setText(card.getCardNo());
									phoneEdt.setText(card.getPhone());
									dialog.dismiss();
								}
							});
						} else {
							ToastHelper.toast(TongdaoDmfActivity.this, "没有银行卡的历史记录");
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(TongdaoDmfActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoDmfActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(TongdaoDmfActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void getErweima() {
		String card = cardEdt.getText().toString().trim();
		String phone = phoneEdt.getText().toString().trim();

		String url = Constants.ADDR_erweima;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("clearType", "00");
		param.put("cardNo", card);
		param.put("payType", "03");
		param.put("payAmt", prdordAmt);
		param.put("frontUrl", "https://th5app.shugenpay.com/p/H5app/transfer.web");
		param.put("buyPhone", phone);
		param.put("prdOrdNo", prdordNo);
		LoadingDialog.show(TongdaoDmfActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {

						String contentType = REP_BODY.optString("contentType");
						String orderStatus = REP_BODY.optString("orderStatus");
						if ("03".equals(contentType)) {// 需要绑卡
							final String busContent = REP_BODY.optString("busContent");

							View contentView = LayoutInflater.from(TongdaoDmfActivity.this).inflate(R.layout.alert_text1, null);
							TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
							txt1.setText("您还未绑卡，请绑定银行卡才可支付！");
							MyAlert.confirm(TongdaoDmfActivity.this, contentView, new OnClickListener() {
								@Override
								public void onClick(View v) {
									MyAlert.dismiss();
									Intent intent = new Intent(TongdaoDmfActivity.this, YinlianActivity.class);
									intent.putExtra("html", busContent);
									startActivity(intent);
									//webView.loadDataWithBaseURL(null, busContent, "text/html", "utf-8", null);
								}
							});
						} else if ("04".equals(orderStatus)) {// 绑卡成功
							final AlertDialog dialog = new AlertDialog.Builder(TongdaoDmfActivity.this).create();
							dialog.show();
							View view = LayoutInflater.from(TongdaoDmfActivity.this).inflate(R.layout.alert_confirm_dmf, null);
							Window window = dialog.getWindow();
							window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
							dialog.setContentView(view);
							WindowManager.LayoutParams lp = window.getAttributes();
							window.setGravity(Gravity.CENTER);
							DisplayMetrics dm = TongdaoDmfActivity.this.getResources().getDisplayMetrics();
							lp.width = (int) (dm.widthPixels * 0.8);
							window.setAttributes(lp);

							final EditText edt = (EditText) view.findViewById(R.id.edt);
							TextView cancleTxt = (TextView) view.findViewById(R.id.cancleTxt);
							cancleTxt.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									InputMethodManager imm = (InputMethodManager) TongdaoDmfActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
									if (imm.isActive()) {
										imm.hideSoftInputFromWindow(edt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
									}
									dialog.dismiss();

								}
							});
							TextView confirmTxt = (TextView) view.findViewById(R.id.confirmTxt);
							confirmTxt.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									String code = edt.getText().toString();
									if (code.length() > 0) {
										code(code);
										InputMethodManager imm = (InputMethodManager) TongdaoDmfActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
										if (imm.isActive()) {
											imm.hideSoftInputFromWindow(edt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
										}
										dialog.dismiss();
									} else {
										ToastHelper.toast(TongdaoDmfActivity.this, "请输入验证码！");
									}
								}
							});
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(TongdaoDmfActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoDmfActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(TongdaoDmfActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void code(String code) {
		String url = Constants.ADDR_code;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("custOrderNo", prdordNo);
		param.put("smsCode", code);
		LoadingDialog.show(TongdaoDmfActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						ToastHelper.toast(TongdaoDmfActivity.this, "支付成功");
						Intent intent = new Intent();
						intent.setClass(TongdaoDmfActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
						startActivity(intent);
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(TongdaoDmfActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(TongdaoDmfActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(TongdaoDmfActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class CardAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private CardAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_card, null);
				holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
				holder.noTxt = (TextView) convertView.findViewById(R.id.noTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Card data = dataList.get(position);

			String str1 = data.getBnkName() + "(" + data.getBnkCode() + ")";
			String str2 = "*" + data.getCardNo().substring(data.getCardNo().length() - 4);
			holder.nameTxt.setText(str1);
			holder.noTxt.setText(str2);
			return convertView;
		}

		class ViewHolder {
			public TextView nameTxt;
			public TextView noTxt;
		}
	}
}