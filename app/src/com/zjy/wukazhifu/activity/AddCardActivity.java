package com.zjy.wukazhifu.activity;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.feezu.liuli.timeselector.Utils.ScreenUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.City;
import com.zjy.wukazhifu.entity.Province;
import com.zjy.wukazhifu.util.BitmapAndStringUtils;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.FileUtils;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.view.LoadingDialog;
import com.zjy.wukazhifu.view.MyAlert;
import com.zjy.wukazhifu.view.choose.SelectOneDialog;
import com.zjy.wukazhifu.view.choose.SelectTwoDialog;
import com.zjy.wukazhifu.view.choose.myinterface.SelectAddressInterface;

public class AddCardActivity extends Activity implements OnClickListener {
	private static final int YHK = 1;// 银行卡拍照
	private static final int BANK = 2;// 分行

	private ImageView backImg;

	private TextView bankTxt;
	private TextView areaTxt;
	private TextView bank2Txt;
	private TextView cardnoTxt;
	private TextView addTxt;
	private ImageView cardPicImg;
	private String[] banks;
	private String[] provinces;
	private Map<String, String[]> dataMap;

	private Bitmap mBitmap;
	private String cnapsCode;

	private String BANK_CARD_ID;
	private String inputCode;

	private String picPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcard);

		Intent intent = getIntent();
		BANK_CARD_ID = intent.getStringExtra("BANK_CARD_ID");
		inputCode = intent.getStringExtra("inputCode");

		initView();
		bank();
		initCityData();
	}

	private void initCityData() {
		try {
			InputStream is = getAssets().open("city.txt");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String text = new String(buffer, "UTF-8");
			Gson gson = new Gson();
			List<Province> dataList = gson.fromJson(text, new TypeToken<List<Province>>() {
			}.getType());
			provinces = new String[dataList.size()];
			dataMap = new HashMap<String, String[]>();
			for (int i = 0; i < dataList.size(); i++) {
				Province p = dataList.get(i);
				String provinceName = p.getProName();
				List<City> citys = p.getCitys();

				provinces[i] = provinceName;
				String[] cityArray = new String[citys.size()];
				for (int j = 0; j < citys.size(); j++) {
					cityArray[j] = citys.get(j).getCityName();
				}
				dataMap.put(provinceName, cityArray);
			}
		} catch (Exception e) {
		}
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		bankTxt = (TextView) findViewById(R.id.bankTxt);
		bankTxt.setOnClickListener(this);
		areaTxt = (TextView) findViewById(R.id.areaTxt);
		areaTxt.setOnClickListener(this);
		bank2Txt = (TextView) findViewById(R.id.bank2Txt);
		bank2Txt.setOnClickListener(this);
		cardnoTxt = (TextView) findViewById(R.id.cardnoTxt);

		cardPicImg = (ImageView) findViewById(R.id.cardPicImg);
		cardPicImg.setOnClickListener(this);

		addTxt = (TextView) findViewById(R.id.addTxt);
		addTxt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.bankTxt:
			SelectOneDialog dialog1 = new SelectOneDialog(AddCardActivity.this, banks, new SelectAddressInterface() {
				@Override
				public void setAreaString(String area) {
					bankTxt.setText(area);
				}
			});
			dialog1.showDialog();
			break;
		case R.id.areaTxt:
			SelectTwoDialog dialog2 = new SelectTwoDialog(AddCardActivity.this, provinces, dataMap, new SelectAddressInterface() {
				@Override
				public void setAreaString(String area) {
					areaTxt.setText(area);
				}
			});
			dialog2.showDialog();
			break;
		case R.id.bank2Txt:
			String bank = bankTxt.getText().toString().trim();
			if (bank.length() == 0) {
				ToastHelper.toast(AddCardActivity.this, "请先选择开户银行！");
				return;
			}
			String area = areaTxt.getText().toString().trim();
			if (area.length() == 0) {
				ToastHelper.toast(AddCardActivity.this, "请先选择省份地区！");
				return;
			}
			String[] areas = area.split(" ");
			String sheng = areas[0];
			String shi = areas[1];
			intent = new Intent(AddCardActivity.this, BankActivity.class);
			intent.putExtra("bank", bank);
			intent.putExtra("sheng", sheng);
			intent.putExtra("shi", shi);
			startActivityForResult(intent, BANK);
			break;
		case R.id.cardPicImg:
			final Dialog dialog = new Dialog(AddCardActivity.this, R.style.MyDialogStyleBottom);
			dialog.setCancelable(true);
			dialog.setContentView(R.layout.dlg_yhkzm);
			Window window = dialog.getWindow();
			window.setGravity(Gravity.BOTTOM);
			WindowManager.LayoutParams lp = window.getAttributes();
			int width = ScreenUtil.getInstance(AddCardActivity.this).getScreenWidth();
			lp.width = width;
			window.setAttributes(lp);
			dialog.show();
			TextView photoTxt = (TextView) dialog.findViewById(R.id.photoTxt);
			photoTxt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					picPath = FileUtils.getAppPath() + File.separator + "images" + File.separator + System.currentTimeMillis() + ".jpg";
					File dir = new File(FileUtils.getAppPath() + File.separator + "images");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picPath)));
					startActivityForResult(intent, YHK);
					dialog.dismiss();
				}
			});
			break;
		case R.id.addTxt:
			add();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case YHK:
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Bitmap bm = BitmapFactory.decodeFile(picPath);
							if (bm != null) {
								mBitmap = FileUtils.scalePic(bm, 600);
								bm.recycle();
							}
						} catch (Exception e) {
						}
						AddCardActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (mBitmap != null) {
									cardPicImg.setImageBitmap(mBitmap);
								}
							}
						});
					}
				}).start();
				break;
			case BANK:
				cnapsCode = data.getStringExtra("cnapsCode");
				bank2Txt.setText(data.getStringExtra("subBranch"));
				break;
			default:
				break;
			}
		}
	}

	private void bank() {
		String url = Constants.ADDR_bank;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray array = REP_BODY.optJSONArray("bankCardList");
						banks = new String[array.length()];
						for (int i = 0; i < array.length(); i++) {
							banks[i] = array.getString(i);
						}
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(AddCardActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(AddCardActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(AddCardActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	private void add() {
		String bank = bankTxt.getText().toString().trim();
		String area = areaTxt.getText().toString().trim();
		String bank2 = bank2Txt.getText().toString().trim();
		String cardno = cardnoTxt.getText().toString().trim();
		if (bank.length() == 0) {
			ToastHelper.toast(AddCardActivity.this, "请先选择开户银行！");
			return;
		}
		if (area.length() == 0) {
			ToastHelper.toast(AddCardActivity.this, "请先选择省份地区！");
			return;
		}
		if (bank2.length() == 0) {
			ToastHelper.toast(AddCardActivity.this, "请先选择支行名称！");
			return;
		}
		if (cardno.length() == 0) {
			ToastHelper.toast(AddCardActivity.this, "请先输入银行卡号！");
			return;
		}
		if (mBitmap == null) {
			ToastHelper.toast(AddCardActivity.this, "请先拍照！");
			return;
		}

		String cardFront = BitmapAndStringUtils.convertIconToString(mBitmap, 60);
		int length =  cardFront.length();
		String url = Constants.ADDR_addbank;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("bankName", bank);
		param.put("openCity", area);
		param.put("subBankName", bank2);
		param.put("cnapsCode", cnapsCode);
		param.put("cardNo", cardno);
		param.put("cardFront", cardFront);
		param.put("provinceId", "");
		param.put("cityId", "");
		if (BANK_CARD_ID != null) {
			param.put("operType", "2");
			param.put("bankCardId", BANK_CARD_ID);
			param.put("inputCode", inputCode);
		} else {
			param.put("operType", "1");
		}
		LoadingDialog.show(AddCardActivity.this);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						View contentView = LayoutInflater.from(AddCardActivity.this).inflate(R.layout.alert_text1, null);
						TextView txt1 = (TextView) contentView.findViewById(R.id.txt1);
						txt1.setText("您的结算卡信息已提交成功，请耐心等待审核结果！");
						MyAlert.alert(AddCardActivity.this, contentView, new OnClickListener() {
							@Override
							public void onClick(View v) {
								MyAlert.dismiss();
								Intent intent = new Intent();
								intent.setClass(AddCardActivity.this, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
								intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
								intent.putExtra("page", 2);
								startActivity(intent);
							}
						});
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(AddCardActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(AddCardActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(AddCardActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}